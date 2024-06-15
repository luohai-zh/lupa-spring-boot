package com.my.netty.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;


public class MyMqttDecoder extends ReplayingDecoder<MyMqttDecoder.DecoderState> {

    private static final int DEFAULT_MAX_BYTES_IN_MESSAGE = 8092;

    /**
     * States of the decoder.
     * We start at READ_FIXED_HEADER, followed by
     * READ_VARIABLE_HEADER and finally READ_PAYLOAD.
     */
    enum DecoderState {
        READ_FIXED_HEADER,
        READ_VARIABLE_HEADER,
        READ_PAYLOAD,
        BAD_MESSAGE,
    }

    private MqttFixedHeader mqttFixedHeader;
    private Object variableHeader;
    private int bytesRemainingInVariablePart;

    private final int maxBytesInMessage;

    public MyMqttDecoder() {
        this(DEFAULT_MAX_BYTES_IN_MESSAGE);
    }

    public MyMqttDecoder(int maxBytesInMessage) {
        super(MyMqttDecoder.DecoderState.READ_FIXED_HEADER);
        this.maxBytesInMessage = maxBytesInMessage;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        switch (state()) {
            case READ_FIXED_HEADER: try {
                mqttFixedHeader = decodeFixedHeader(ctx, buffer);
                bytesRemainingInVariablePart = mqttFixedHeader.remainingLength();
                checkpoint(MyMqttDecoder.DecoderState.READ_VARIABLE_HEADER);
                // fall through
            } catch (Exception cause) {
                out.add(invalidMessage(cause));
                return;
            }

            case READ_VARIABLE_HEADER:  try {
                final MyMqttDecoder.Result<?> decodedVariableHeader = decodeVariableHeader(ctx, buffer, mqttFixedHeader);
                variableHeader = decodedVariableHeader.value;
                if (bytesRemainingInVariablePart > maxBytesInMessage) {
                    throw new TooLongFrameException("too large message: " + bytesRemainingInVariablePart + " bytes");
                }
                bytesRemainingInVariablePart -= decodedVariableHeader.numberOfBytesConsumed;
                checkpoint(MyMqttDecoder.DecoderState.READ_PAYLOAD);
                // fall through
            } catch (Exception cause) {
                out.add(invalidMessage(cause));
                return;
            }

            case READ_PAYLOAD: try {
                final MyMqttDecoder.Result<?> decodedPayload =
                        decodePayload(
                                ctx,
                                buffer,
                                mqttFixedHeader.messageType(),
                                bytesRemainingInVariablePart,
                                variableHeader);
                bytesRemainingInVariablePart -= decodedPayload.numberOfBytesConsumed;
                if (bytesRemainingInVariablePart != 0) {
                    throw new DecoderException(
                            "non-zero remaining payload bytes: " +
                                    bytesRemainingInVariablePart + " (" + mqttFixedHeader.messageType() + ')');
                }
                checkpoint(MyMqttDecoder.DecoderState.READ_FIXED_HEADER);
                MqttMessage message = MqttMessageFactory.newMessage(
                        mqttFixedHeader, variableHeader, decodedPayload.value);
                mqttFixedHeader = null;
                variableHeader = null;
                out.add(message);
                break;
            } catch (Exception cause) {
                out.add(invalidMessage(cause));
                return;
            }

            case BAD_MESSAGE:
                // Keep discarding until disconnection.
                buffer.skipBytes(actualReadableBytes());
                break;

            default:
                // Shouldn't reach here.
                throw new Error();
        }
    }

    private MqttMessage invalidMessage(Throwable cause) {
        checkpoint(MyMqttDecoder.DecoderState.BAD_MESSAGE);
        return MqttMessageFactory.newInvalidMessage(mqttFixedHeader, variableHeader, cause);
    }

    /**
     * Decodes the fixed header. It's one byte for the flags and then variable bytes for the remaining length.
     *
     * @param buffer the buffer to decode from
     * @return the fixed header
     */
    private static MqttFixedHeader decodeFixedHeader(ChannelHandlerContext ctx, ByteBuf buffer) {
        short b1 = buffer.readUnsignedByte();

        MqttMessageType messageType = MqttMessageType.valueOf(b1 >> 4);
        boolean dupFlag = (b1 & 0x08) == 0x08;
        int qosLevel = (b1 & 0x06) >> 1;
        boolean retain = (b1 & 0x01) != 0;

        int remainingLength = 0;
        int multiplier = 1;
        short digit;
        int loops = 0;
        do {
            digit = buffer.readUnsignedByte();
            remainingLength += (digit & 127) * multiplier;
            multiplier *= 128;
            loops++;
        } while ((digit & 128) != 0 && loops < 4);

        // MQTT protocol limits Remaining Length to 4 bytes
        if (loops == 4 && (digit & 128) != 0) {
            throw new DecoderException("remaining length exceeds 4 digits (" + messageType + ')');
        }
        MqttFixedHeader decodedFixedHeader =
                new MqttFixedHeader(messageType, dupFlag, MqttQoS.valueOf(qosLevel), retain, remainingLength);
        return MyMqttCodecUtil.validateFixedHeader(ctx, MyMqttCodecUtil.resetUnusedFields(decodedFixedHeader));
    }

    /**
     * Decodes the variable header (if any)
     * @param buffer the buffer to decode from
     * @param mqttFixedHeader MqttFixedHeader of the same message
     * @return the variable header
     */
    private MyMqttDecoder.Result<?> decodeVariableHeader(ChannelHandlerContext ctx, ByteBuf buffer, MqttFixedHeader mqttFixedHeader) {
        switch (mqttFixedHeader.messageType()) {
            case CONNECT:
                return decodeConnectionVariableHeader(ctx, buffer);

            case CONNACK:
                return decodeConnAckVariableHeader(ctx, buffer);

            case UNSUBSCRIBE:
            case SUBSCRIBE:
            case SUBACK:
            case UNSUBACK:
                return decodeMessageIdAndPropertiesVariableHeader(ctx, buffer);

            case PUBACK:
            case PUBREC:
            case PUBCOMP:
            case PUBREL:
                return decodePubReplyMessage(buffer);

            case PUBLISH:
                return decodePublishVariableHeader(ctx, buffer, mqttFixedHeader);

            case DISCONNECT:
            case AUTH:
                return decodeReasonCodeAndPropertiesVariableHeader(buffer);

            case PINGREQ:
            case PINGRESP:
                // Empty variable header
                return new MyMqttDecoder.Result<Object>(null, 0);
            default:
                //shouldn't reach here
                throw new DecoderException("Unknown message type: " + mqttFixedHeader.messageType());
        }
    }

    private static MyMqttDecoder.Result<MqttConnectVariableHeader> decodeConnectionVariableHeader(
            ChannelHandlerContext ctx,
            ByteBuf buffer) {
        final MyMqttDecoder.Result<String> protoString = decodeString(buffer);
        int numberOfBytesConsumed = protoString.numberOfBytesConsumed;

        final byte protocolLevel = buffer.readByte();
        numberOfBytesConsumed += 1;

        MqttVersion version = MqttVersion.fromProtocolNameAndLevel(protoString.value, protocolLevel);
        MyMqttCodecUtil.setMqttVersion(ctx, version);

        final int b1 = buffer.readUnsignedByte();
        numberOfBytesConsumed += 1;

        final int keepAlive = decodeMsbLsb(buffer);
        numberOfBytesConsumed += 2;

        final boolean hasUserName = (b1 & 0x80) == 0x80;
        final boolean hasPassword = (b1 & 0x40) == 0x40;
        final boolean willRetain = (b1 & 0x20) == 0x20;
        final int willQos = (b1 & 0x18) >> 3;
        final boolean willFlag = (b1 & 0x04) == 0x04;
        final boolean cleanSession = (b1 & 0x02) == 0x02;
        if (version == MqttVersion.MQTT_3_1_1 || version == MqttVersion.MQTT_5) {
            final boolean zeroReservedFlag = (b1 & 0x01) == 0x0;
            if (!zeroReservedFlag) {
                // MQTT v3.1.1: The Server MUST validate that the reserved flag in the CONNECT Control Packet is
                // set to zero and disconnect the Client if it is not zero.
                // See http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html#_Toc385349230
                throw new DecoderException("non-zero reserved flag");
            }
        }

        final MqttProperties properties;
        if (version == MqttVersion.MQTT_5) {
            final MyMqttDecoder.Result<MqttProperties> propertiesResult = decodeProperties(buffer);
            properties = propertiesResult.value;
            numberOfBytesConsumed += propertiesResult.numberOfBytesConsumed;
        } else {
            properties = MqttProperties.NO_PROPERTIES;
        }

        final MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(
                version.protocolName(),
                version.protocolLevel(),
                hasUserName,
                hasPassword,
                willRetain,
                willQos,
                willFlag,
                cleanSession,
                keepAlive,
                properties);
        return new MyMqttDecoder.Result<MqttConnectVariableHeader>(mqttConnectVariableHeader, numberOfBytesConsumed);
    }

    private static MyMqttDecoder.Result<MqttConnAckVariableHeader> decodeConnAckVariableHeader(
            ChannelHandlerContext ctx,
            ByteBuf buffer) {
        final MqttVersion mqttVersion = MyMqttCodecUtil.getMqttVersion(ctx);
        final boolean sessionPresent = (buffer.readUnsignedByte() & 0x01) == 0x01;
        byte returnCode = buffer.readByte();
        int numberOfBytesConsumed = 2;

        final MqttProperties properties;
        if (mqttVersion == MqttVersion.MQTT_5) {
            final MyMqttDecoder.Result<MqttProperties> propertiesResult = decodeProperties(buffer);
            properties = propertiesResult.value;
            numberOfBytesConsumed += propertiesResult.numberOfBytesConsumed;
        } else {
            properties = MqttProperties.NO_PROPERTIES;
        }

        final MqttConnAckVariableHeader mqttConnAckVariableHeader =
                new MqttConnAckVariableHeader(MqttConnectReturnCode.valueOf(returnCode), sessionPresent, properties);
        return new MyMqttDecoder.Result<MqttConnAckVariableHeader>(mqttConnAckVariableHeader, numberOfBytesConsumed);
    }

    private static MyMqttDecoder.Result<MqttMessageIdAndPropertiesVariableHeader> decodeMessageIdAndPropertiesVariableHeader(
            ChannelHandlerContext ctx,
            ByteBuf buffer) {
        final MqttVersion mqttVersion = MyMqttCodecUtil.getMqttVersion(ctx);
        final int packetId = decodeMessageId(buffer);

        final MqttMessageIdAndPropertiesVariableHeader mqttVariableHeader;
        final int mqtt5Consumed;

        if (mqttVersion == MqttVersion.MQTT_5) {
            final MyMqttDecoder.Result<MqttProperties> properties = decodeProperties(buffer);
            mqttVariableHeader = new MqttMessageIdAndPropertiesVariableHeader(packetId, properties.value);
            mqtt5Consumed = properties.numberOfBytesConsumed;
        } else {
            mqttVariableHeader = new MqttMessageIdAndPropertiesVariableHeader(packetId,
                    MqttProperties.NO_PROPERTIES);
            mqtt5Consumed = 0;
        }

        return new MyMqttDecoder.Result<MqttMessageIdAndPropertiesVariableHeader>(mqttVariableHeader,
                2 + mqtt5Consumed);
    }

    private MyMqttDecoder.Result<MqttPubReplyMessageVariableHeader> decodePubReplyMessage(ByteBuf buffer) {
        final int packetId = decodeMessageId(buffer);

        final MqttPubReplyMessageVariableHeader mqttPubAckVariableHeader;
        final int consumed;
        final int packetIdNumberOfBytesConsumed = 2;
        if (bytesRemainingInVariablePart > 3) {
            final byte reasonCode = buffer.readByte();
            final MyMqttDecoder.Result<MqttProperties> properties = decodeProperties(buffer);
            mqttPubAckVariableHeader = new MqttPubReplyMessageVariableHeader(packetId,
                    reasonCode,
                    properties.value);
            consumed = packetIdNumberOfBytesConsumed + 1 + properties.numberOfBytesConsumed;
        } else if (bytesRemainingInVariablePart > 2) {
            final byte reasonCode = buffer.readByte();
            mqttPubAckVariableHeader = new MqttPubReplyMessageVariableHeader(packetId,
                    reasonCode,
                    MqttProperties.NO_PROPERTIES);
            consumed = packetIdNumberOfBytesConsumed + 1;
        } else {
            mqttPubAckVariableHeader = new MqttPubReplyMessageVariableHeader(packetId,
                    (byte) 0,
                    MqttProperties.NO_PROPERTIES);
            consumed = packetIdNumberOfBytesConsumed;
        }

        return new MyMqttDecoder.Result<MqttPubReplyMessageVariableHeader>(mqttPubAckVariableHeader, consumed);
    }

    private MyMqttDecoder.Result<MqttReasonCodeAndPropertiesVariableHeader> decodeReasonCodeAndPropertiesVariableHeader(
            ByteBuf buffer) {
        final byte reasonCode;
        final MqttProperties properties;
        final int consumed;
        if (bytesRemainingInVariablePart > 1) {
            reasonCode = buffer.readByte();
            final MyMqttDecoder.Result<MqttProperties> propertiesResult = decodeProperties(buffer);
            properties = propertiesResult.value;
            consumed = 1 + propertiesResult.numberOfBytesConsumed;
        } else if (bytesRemainingInVariablePart > 0) {
            reasonCode = buffer.readByte();
            properties = MqttProperties.NO_PROPERTIES;
            consumed = 1;
        } else {
            reasonCode = 0;
            properties = MqttProperties.NO_PROPERTIES;
            consumed = 0;
        }
        final MqttReasonCodeAndPropertiesVariableHeader mqttReasonAndPropsVariableHeader =
                new MqttReasonCodeAndPropertiesVariableHeader(reasonCode, properties);

        return new MyMqttDecoder.Result<MqttReasonCodeAndPropertiesVariableHeader>(
                mqttReasonAndPropsVariableHeader,
                consumed);
    }

    private MyMqttDecoder.Result<MqttPublishVariableHeader> decodePublishVariableHeader(
            ChannelHandlerContext ctx,
            ByteBuf buffer,
            MqttFixedHeader mqttFixedHeader) {
        final MqttVersion mqttVersion = MyMqttCodecUtil.getMqttVersion(ctx);
        final MyMqttDecoder.Result<String> decodedTopic = decodeString(buffer);
        if (!MyMqttCodecUtil.isValidPublishTopicName(decodedTopic.value)) {
            throw new DecoderException("invalid publish topic name: " + decodedTopic.value + " (contains wildcards)");
        }
        int numberOfBytesConsumed = decodedTopic.numberOfBytesConsumed;

        int messageId = -1;
        if (mqttFixedHeader.qosLevel().value() > 0) {
            messageId = decodeMessageId(buffer);
            numberOfBytesConsumed += 2;
        }

        final MqttProperties properties;
        if (mqttVersion == MqttVersion.MQTT_5) {
            final MyMqttDecoder.Result<MqttProperties> propertiesResult = decodeProperties(buffer);
            properties = propertiesResult.value;
            numberOfBytesConsumed += propertiesResult.numberOfBytesConsumed;
        } else {
            properties = MqttProperties.NO_PROPERTIES;
        }

        final MqttPublishVariableHeader mqttPublishVariableHeader =
                new MqttPublishVariableHeader(decodedTopic.value, messageId, properties);
        return new MyMqttDecoder.Result<MqttPublishVariableHeader>(mqttPublishVariableHeader, numberOfBytesConsumed);
    }

    /**
     * @return messageId with numberOfBytesConsumed is 2
     */
    private static int decodeMessageId(ByteBuf buffer) {
        final int messageId = decodeMsbLsb(buffer);
        if (!MyMqttCodecUtil.isValidMessageId(messageId)) {
            throw new DecoderException("invalid messageId: " + messageId);
        }
        return messageId;
    }

    /**
     * Decodes the payload.
     *
     * @param buffer the buffer to decode from
     * @param messageType  type of the message being decoded
     * @param bytesRemainingInVariablePart bytes remaining
     * @param variableHeader variable header of the same message
     * @return the payload
     */
    private static MyMqttDecoder.Result<?> decodePayload(
            ChannelHandlerContext ctx,
            ByteBuf buffer,
            MqttMessageType messageType,
            int bytesRemainingInVariablePart,
            Object variableHeader) {
        switch (messageType) {
            case CONNECT:
                return decodeConnectionPayload(buffer, (MqttConnectVariableHeader) variableHeader);

            case SUBSCRIBE:
                return decodeSubscribePayload(buffer, bytesRemainingInVariablePart);

            case SUBACK:
                return decodeSubackPayload(buffer, bytesRemainingInVariablePart);

            case UNSUBSCRIBE:
                return decodeUnsubscribePayload(buffer, bytesRemainingInVariablePart);

            case UNSUBACK:
                return decodeUnsubAckPayload(ctx, buffer, bytesRemainingInVariablePart);

            case PUBLISH:
                return decodePublishPayload(buffer, bytesRemainingInVariablePart);

            default:
                // unknown payload , no byte consumed
                return new MyMqttDecoder.Result<Object>(null, 0);
        }
    }

    private static MyMqttDecoder.Result<MqttConnectPayload> decodeConnectionPayload(
            ByteBuf buffer,
            MqttConnectVariableHeader mqttConnectVariableHeader) {
        final MyMqttDecoder.Result<String> decodedClientId = decodeString(buffer);
        final String decodedClientIdValue = decodedClientId.value;
        final MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(mqttConnectVariableHeader.name(),
                (byte) mqttConnectVariableHeader.version());
        if (!MyMqttCodecUtil.isValidClientId(mqttVersion, decodedClientIdValue)) {
            throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + decodedClientIdValue);
        }
        int numberOfBytesConsumed = decodedClientId.numberOfBytesConsumed;

        MyMqttDecoder.Result<String> decodedWillTopic = null;
        byte[] decodedWillMessage = null;

        final MqttProperties willProperties;
        if (mqttConnectVariableHeader.isWillFlag()) {
            if (mqttVersion == MqttVersion.MQTT_5) {
                final MyMqttDecoder.Result<MqttProperties> propertiesResult = decodeProperties(buffer);
                willProperties = propertiesResult.value;
                numberOfBytesConsumed += propertiesResult.numberOfBytesConsumed;
            } else {
                willProperties = MqttProperties.NO_PROPERTIES;
            }
            decodedWillTopic = decodeString(buffer, 0, 32767);
            numberOfBytesConsumed += decodedWillTopic.numberOfBytesConsumed;
            decodedWillMessage = decodeByteArray(buffer);
            numberOfBytesConsumed += decodedWillMessage.length + 2;
        } else {
            willProperties = MqttProperties.NO_PROPERTIES;
        }
        MyMqttDecoder.Result<String> decodedUserName = null;
        byte[] decodedPassword = null;
        if (mqttConnectVariableHeader.hasUserName()) {
            decodedUserName = decodeString(buffer);
            numberOfBytesConsumed += decodedUserName.numberOfBytesConsumed;
        }
        if (mqttConnectVariableHeader.hasPassword()) {
            decodedPassword = decodeByteArray(buffer);
            numberOfBytesConsumed += decodedPassword.length + 2;
        }

        final MqttConnectPayload mqttConnectPayload =
                new MqttConnectPayload(
                        decodedClientId.value,
                        willProperties,
                        decodedWillTopic != null ? decodedWillTopic.value : null,
                        decodedWillMessage != null ? decodedWillMessage : null,
                        decodedUserName != null ? decodedUserName.value : null,
                        decodedPassword != null ? decodedPassword : null);
        return new MyMqttDecoder.Result<MqttConnectPayload>(mqttConnectPayload, numberOfBytesConsumed);
    }

    private static MyMqttDecoder.Result<MqttSubscribePayload> decodeSubscribePayload(
            ByteBuf buffer,
            int bytesRemainingInVariablePart) {
        final List<MqttTopicSubscription> subscribeTopics = new ArrayList<MqttTopicSubscription>();
        int numberOfBytesConsumed = 0;
        while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
            final MyMqttDecoder.Result<String> decodedTopicName = decodeString(buffer);
            numberOfBytesConsumed += decodedTopicName.numberOfBytesConsumed;
            //See 3.8.3.1 Subscription Options of MQTT 5.0 specification for optionByte details
            final short optionByte = buffer.readUnsignedByte();

            MqttQoS qos = MqttQoS.valueOf(optionByte & 0x03);
            boolean noLocal = ((optionByte & 0x04) >> 2) == 1;
            boolean retainAsPublished = ((optionByte & 0x08) >> 3) == 1;
            MqttSubscriptionOption.RetainedHandlingPolicy retainHandling = MqttSubscriptionOption.RetainedHandlingPolicy.valueOf((optionByte & 0x30) >> 4);

            final MqttSubscriptionOption subscriptionOption = new MqttSubscriptionOption(qos,
                    noLocal,
                    retainAsPublished,
                    retainHandling);

            numberOfBytesConsumed++;
            subscribeTopics.add(new MqttTopicSubscription(decodedTopicName.value, subscriptionOption));
        }
        return new MyMqttDecoder.Result<MqttSubscribePayload>(new MqttSubscribePayload(subscribeTopics), numberOfBytesConsumed);
    }

    private static MyMqttDecoder.Result<MqttSubAckPayload> decodeSubackPayload(
            ByteBuf buffer,
            int bytesRemainingInVariablePart) {
        final List<Integer> grantedQos = new ArrayList<Integer>(bytesRemainingInVariablePart);
        int numberOfBytesConsumed = 0;
        while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
            int reasonCode = buffer.readUnsignedByte();
            numberOfBytesConsumed++;
            grantedQos.add(reasonCode);
        }
        return new MyMqttDecoder.Result<MqttSubAckPayload>(new MqttSubAckPayload(grantedQos), numberOfBytesConsumed);
    }

    private static MyMqttDecoder.Result<MqttUnsubAckPayload> decodeUnsubAckPayload(
            ChannelHandlerContext ctx,
            ByteBuf buffer,
            int bytesRemainingInVariablePart) {
        final List<Short> reasonCodes = new ArrayList<Short>(bytesRemainingInVariablePart);
        int numberOfBytesConsumed = 0;
        while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
            short reasonCode = buffer.readUnsignedByte();
            numberOfBytesConsumed++;
            reasonCodes.add(reasonCode);
        }
        return new MyMqttDecoder.Result<MqttUnsubAckPayload>(new MqttUnsubAckPayload(reasonCodes), numberOfBytesConsumed);
    }

    private static MyMqttDecoder.Result<MqttUnsubscribePayload> decodeUnsubscribePayload(
            ByteBuf buffer,
            int bytesRemainingInVariablePart) {
        final List<String> unsubscribeTopics = new ArrayList<String>();
        int numberOfBytesConsumed = 0;
        while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
            final MyMqttDecoder.Result<String> decodedTopicName = decodeString(buffer);
            numberOfBytesConsumed += decodedTopicName.numberOfBytesConsumed;
            unsubscribeTopics.add(decodedTopicName.value);
        }
        return new MyMqttDecoder.Result<MqttUnsubscribePayload>(
                new MqttUnsubscribePayload(unsubscribeTopics),
                numberOfBytesConsumed);
    }

    private static MyMqttDecoder.Result<ByteBuf> decodePublishPayload(ByteBuf buffer, int bytesRemainingInVariablePart) {
        ByteBuf b = buffer.readRetainedSlice(bytesRemainingInVariablePart);
        return new MyMqttDecoder.Result<ByteBuf>(b, bytesRemainingInVariablePart);
    }

    private static MyMqttDecoder.Result<String> decodeString(ByteBuf buffer) {
        return decodeString(buffer, 0, Integer.MAX_VALUE);
    }

    private static MyMqttDecoder.Result<String> decodeString(ByteBuf buffer, int minBytes, int maxBytes) {
        int size = decodeMsbLsb(buffer);
        int numberOfBytesConsumed = 2;
        if (size < minBytes || size > maxBytes) {
            buffer.skipBytes(size);
            numberOfBytesConsumed += size;
            return new MyMqttDecoder.Result<String>(null, numberOfBytesConsumed);
        }
        String s = buffer.toString(buffer.readerIndex(), size, CharsetUtil.UTF_8);
        buffer.skipBytes(size);
        numberOfBytesConsumed += size;
        return new MyMqttDecoder.Result<String>(s, numberOfBytesConsumed);
    }

    /**
     *
     * @return the decoded byte[], numberOfBytesConsumed = byte[].length + 2
     */
    private static byte[] decodeByteArray(ByteBuf buffer) {
        int size = decodeMsbLsb(buffer);
        byte[] bytes = new byte[size];
        buffer.readBytes(bytes);
        return bytes;
    }

    // packing utils to reduce the amount of garbage while decoding ints
    private static long packInts(int a, int b) {
        return (((long) a) << 32) | (b & 0xFFFFFFFFL);
    }

    private static int unpackA(long ints) {
        return (int) (ints >> 32);
    }

    private static int unpackB(long ints) {
        return (int) ints;
    }

    /**
     *  numberOfBytesConsumed = 2. return decoded result.
     */
    private static int decodeMsbLsb(ByteBuf buffer) {
        int min = 0;
        int max = 65535;
        short msbSize = buffer.readUnsignedByte();
        short lsbSize = buffer.readUnsignedByte();
        int result = msbSize << 8 | lsbSize;
        if (result < min || result > max) {
            result = -1;
        }
        return result;
    }

    /**
     * See 1.5.5 Variable Byte Integer section of MQTT 5.0 specification for encoding/decoding rules
     *
     * @param buffer the buffer to decode from
     * @return result pack with a = decoded integer, b = numberOfBytesConsumed. Need to unpack to read them.
     * @throws DecoderException if bad MQTT protocol limits Remaining Length
     */
    private static long decodeVariableByteInteger(ByteBuf buffer) {
        int remainingLength = 0;
        int multiplier = 1;
        short digit;
        int loops = 0;
        do {
            digit = buffer.readUnsignedByte();
            remainingLength += (digit & 127) * multiplier;
            multiplier *= 128;
            loops++;
        } while ((digit & 128) != 0 && loops < 4);

        if (loops == 4 && (digit & 128) != 0) {
            throw new DecoderException("MQTT protocol limits Remaining Length to 4 bytes");
        }
        return packInts(remainingLength, loops);
    }

    private static final class Result<T> {

        private final T value;
        private final int numberOfBytesConsumed;

        Result(T value, int numberOfBytesConsumed) {
            this.value = value;
            this.numberOfBytesConsumed = numberOfBytesConsumed;
        }
    }

    private static MyMqttDecoder.Result<MqttProperties> decodeProperties(ByteBuf buffer) {
        final long propertiesLength = decodeVariableByteInteger(buffer);
        int totalPropertiesLength = unpackA(propertiesLength);
        int numberOfBytesConsumed = unpackB(propertiesLength);

        MqttProperties decodedProperties = new MqttProperties();
        while (numberOfBytesConsumed < totalPropertiesLength) {
            long propertyId = decodeVariableByteInteger(buffer);
            final int propertyIdValue = unpackA(propertyId);
            numberOfBytesConsumed += unpackB(propertyId);
            MqttProperties.MqttPropertyType propertyType = MqttProperties.MqttPropertyType.valueOf(propertyIdValue);
            switch (propertyType) {
                case PAYLOAD_FORMAT_INDICATOR:
                case REQUEST_PROBLEM_INFORMATION:
                case REQUEST_RESPONSE_INFORMATION:
                case MAXIMUM_QOS:
                case RETAIN_AVAILABLE:
                case WILDCARD_SUBSCRIPTION_AVAILABLE:
                case SUBSCRIPTION_IDENTIFIER_AVAILABLE:
                case SHARED_SUBSCRIPTION_AVAILABLE:
                    final int b1 = buffer.readUnsignedByte();
                    numberOfBytesConsumed++;
                    decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, b1));
                    break;
                case SERVER_KEEP_ALIVE:
                case RECEIVE_MAXIMUM:
                case TOPIC_ALIAS_MAXIMUM:
                case TOPIC_ALIAS:
                    final int int2BytesResult = decodeMsbLsb(buffer);
                    numberOfBytesConsumed += 2;
                    decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, int2BytesResult));
                    break;
                case PUBLICATION_EXPIRY_INTERVAL:
                case SESSION_EXPIRY_INTERVAL:
                case WILL_DELAY_INTERVAL:
                case MAXIMUM_PACKET_SIZE:
                    final int maxPacketSize = buffer.readInt();
                    numberOfBytesConsumed += 4;
                    decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, maxPacketSize));
                    break;
                case SUBSCRIPTION_IDENTIFIER:
                    long vbIntegerResult = decodeVariableByteInteger(buffer);
                    numberOfBytesConsumed += unpackB(vbIntegerResult);
                    decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, unpackA(vbIntegerResult)));
                    break;
                case CONTENT_TYPE:
                case RESPONSE_TOPIC:
                case ASSIGNED_CLIENT_IDENTIFIER:
                case AUTHENTICATION_METHOD:
                case RESPONSE_INFORMATION:
                case SERVER_REFERENCE:
                case REASON_STRING:
                    final MyMqttDecoder.Result<String> stringResult = decodeString(buffer);
                    numberOfBytesConsumed += stringResult.numberOfBytesConsumed;
                    decodedProperties.add(new MqttProperties.StringProperty(propertyIdValue, stringResult.value));
                    break;
                case USER_PROPERTY:
                    final MyMqttDecoder.Result<String> keyResult = decodeString(buffer);
                    final MyMqttDecoder.Result<String> valueResult = decodeString(buffer);
                    numberOfBytesConsumed += keyResult.numberOfBytesConsumed;
                    numberOfBytesConsumed += valueResult.numberOfBytesConsumed;
                    decodedProperties.add(new MqttProperties.UserProperty(keyResult.value, valueResult.value));
                    break;
                case CORRELATION_DATA:
                case AUTHENTICATION_DATA:
                    final byte[] binaryDataResult = decodeByteArray(buffer);
                    numberOfBytesConsumed += binaryDataResult.length + 2;
                    decodedProperties.add(new MqttProperties.BinaryProperty(propertyIdValue, binaryDataResult));
                    break;
                default:
                    //shouldn't reach here
                    throw new DecoderException("Unknown property type: " + propertyType);
            }
        }

        return new MyMqttDecoder.Result<MqttProperties>(decodedProperties, numberOfBytesConsumed);
    }
}

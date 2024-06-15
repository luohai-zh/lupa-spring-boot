package com.my.netty.mqtt;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class MyMqttCodecUtil {

    private static final char[] TOPIC_WILDCARDS = {'#', '+'};
    private static final int MIN_CLIENT_ID_LENGTH = 1;
    private static final int MAX_CLIENT_ID_LENGTH = 23;

    static final AttributeKey<MqttVersion> MQTT_VERSION_KEY = AttributeKey.valueOf("NETTY_CODEC_MQTT_VERSION");

    static MqttVersion getMqttVersion(ChannelHandlerContext ctx) {
        Attribute<MqttVersion> attr = ctx.channel().attr(MQTT_VERSION_KEY);
        MqttVersion version = attr.get();
        if (version == null) {
            return MqttVersion.MQTT_3_1_1;
        }
        return version;
    }

    static void setMqttVersion(ChannelHandlerContext ctx, MqttVersion version) {
        Attribute<MqttVersion> attr = ctx.channel().attr(MQTT_VERSION_KEY);
        attr.set(version);
    }

    static boolean isValidPublishTopicName(String topicName) {
        // publish topic name must not contain any wildcard
        for (char c : TOPIC_WILDCARDS) {
            if (topicName.indexOf(c) >= 0) {
                return false;
            }
        }
        return true;
    }

    static boolean isValidMessageId(int messageId) {
        return messageId != 0;
    }

    static boolean isValidClientId(MqttVersion mqttVersion, String clientId) {
        if (mqttVersion == MqttVersion.MQTT_3_1) {
            return clientId != null;
//            return clientId != null && clientId.length() >= MIN_CLIENT_ID_LENGTH &&
//                    clientId.length() <= MAX_CLIENT_ID_LENGTH;
        }
        if (mqttVersion == MqttVersion.MQTT_3_1_1 || mqttVersion == MqttVersion.MQTT_5) {
            // In 3.1.3.1 Client Identifier of MQTT 3.1.1 and 5.0 specifications, The Server MAY allow ClientId’s
            // that contain more than 23 encoded bytes. And, The Server MAY allow zero-length ClientId.
            return clientId != null;
        }
        throw new IllegalArgumentException(mqttVersion + " is unknown mqtt version");
    }

    static MqttFixedHeader validateFixedHeader(ChannelHandlerContext ctx, MqttFixedHeader mqttFixedHeader) {
        switch (mqttFixedHeader.messageType()) {
            case PUBREL:
            case SUBSCRIBE:
            case UNSUBSCRIBE:
                if (mqttFixedHeader.qosLevel() != MqttQoS.AT_LEAST_ONCE) {
                    throw new DecoderException(mqttFixedHeader.messageType().name() + " message must have QoS 1");
                }
                return mqttFixedHeader;
            case AUTH:
                if (MyMqttCodecUtil.getMqttVersion(ctx) != MqttVersion.MQTT_5) {
                    throw new DecoderException("AUTH message requires at least MQTT 5");
                }
                return mqttFixedHeader;
            default:
                return mqttFixedHeader;
        }
    }

    static MqttFixedHeader resetUnusedFields(MqttFixedHeader mqttFixedHeader) {
        switch (mqttFixedHeader.messageType()) {
            case CONNECT:
            case CONNACK:
            case PUBACK:
            case PUBREC:
            case PUBCOMP:
            case SUBACK:
            case UNSUBACK:
            case PINGREQ:
            case PINGRESP:
            case DISCONNECT:
                if (mqttFixedHeader.isDup() ||
                        mqttFixedHeader.qosLevel() != MqttQoS.AT_MOST_ONCE ||
                        mqttFixedHeader.isRetain()) {
                    return new MqttFixedHeader(
                            mqttFixedHeader.messageType(),
                            false,
                            MqttQoS.AT_MOST_ONCE,
                            false,
                            mqttFixedHeader.remainingLength());
                }
                return mqttFixedHeader;
            case PUBREL:
            case SUBSCRIBE:
            case UNSUBSCRIBE:
                if (mqttFixedHeader.isRetain()) {
                    return new MqttFixedHeader(
                            mqttFixedHeader.messageType(),
                            mqttFixedHeader.isDup(),
                            mqttFixedHeader.qosLevel(),
                            false,
                            mqttFixedHeader.remainingLength());
                }
                return mqttFixedHeader;
            default:
                return mqttFixedHeader;
        }
    }

    private MyMqttCodecUtil() { }
}

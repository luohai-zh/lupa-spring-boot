package com.lupa.utils.exception;

public enum ExceptionCodeEnum implements ExceptionHandler {
    // 数据操作错误定义
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 默认失败
     */
    FAILED(500, "操作失败"),
    ERROR(520, "未知错误"),

    /**
     * 参数错误 （1001~1999）
     */
    PRAM_NOT_MATCH(1001, "参数不正确"),
    NUMBER_FORMAT_FAILED(1004, "请输入正确的数字"),
    VALIDATE_FAILED(1002, "参数检验失败"),
    ADD_ERROR(1005, "添加操作失败"),
    DELETE_ERROR(1006, "删除操作失败"),
    SELECT_ERROR(1007, "查询操作失败"),
    UPDATE_ERROR(1008, "更新操作失败"),
    PARAM_NOT_VALID(1001, "用户ID无效,请输入正确的用户ID"),
    PARAM_IS_BLANK(1002, "设备名为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),


    /**
     * 账户错误 （2001~2999）
     */
    USER_IS_EXIST(2006, "该手机号已经注册"),
    USER_NOTFOUND(2007, "用户名错误"),
    PASSWORD_ERROR(2010, "密码错误"),
    /**
     * 业务错误  （10001~19999）
     */

    TIMESTAMP_ERROR(10001, "时间戳错误"),
    DEVICE_IS_EXIST(10004, "该设备名称重复，请更换名字"),
    DEVICE_IS_EXISTID(10015, "该用户不存在,或已被删除"),
    THRESHOLD_USER_ERROR(10002, "该设备不属于你，无法添加阈值"),
    DEVICE_CONTROL_ERROR(10003, "同一个设备下有重复控制字段"),
    AUTHCODE_ERROR(10005, "验证码错误"),
    NO_AUTHCODE(10006, "请发送验证码"),
    PHEONE_IS_REGISTER(10007, "该手机号未注册,请先注册"),
    PHONE_IS_NULL(10008, "用户名为空为空，请添加用户名"),
    ACCESS_IS_EXIST(10009, "Access信息已存在，无法再次生成"),
    ACCESSSECRET_ERROR(10010, "ACCESS密钥错误"),
    USAGECOUNT_ERROR(10011, "请求接口次数为零"),
    ACCESSINFO_NOTFOUND(10012, "ACCESS信息为空"),
    ACCESSINFO_ERROR(10013, "ACCESS信息错误"),
    NOPAY_OR_NOCANCEL(10014, "请完成支付或取消订单"),
    WEIXINLOGIN_ERROR(10015, "微信登录失败"),
    WEIXINLOGIN_ERROR_MY(10016, "该设备不存在,或暂未添加"),
    WEIXINLOGIN_ERROR_DEL(10017, "主键ID不正确,请输入正确的ID"),
    WEIXINLOGIN_ERROR_MYSQL(10018,"数据库中暂无相关数据,请勿重复操作"),
    WEIXINLOGIN_ERROR_DEVICE(10019,"该条数据不存在或以被删除"),
    WEIXINLOGIN_ERROR_DEVICENAME(10020,"设备名为空,请填写设备名"),
    WEIXINLOGIN_ERROR_DEVICENEW(10021,"改设备的所有历史数据以全部删除,请勿重复操作"),
    OfflineReminder_IS_EXIST(10005, "该地区您已经添加过提醒，无需重复添加"),
    USERNAME_IS_EXIST(100022,"该用户名以存在,请更换用户名")
    ;

    private long code;
    private String message;

    private ExceptionCodeEnum(long code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

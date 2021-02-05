package com.blog.enums;


import java.util.Arrays;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/2/3 9:38
 */

public enum ResultMsg {
    PARMS_NOT_NULL("参数不能为空"),
    INCORRECT_ACCOUNT_PASSWORD("账号/密码错误"),
    FAILED_TO_DELETE_TOKEN("删除token失败"),
    FAILED_TO_DELETE_USER_INFORMATION("删除用户信息失败"),
    FAILED_TO_DELETE("删除失败"),
    SAVE_FAILED("保存失败"),
    MESSAGE_FAILED_TO_SEND("短信发送失败"),
    NOT_LOGIN("未登录"),
    SMS_VERIFICATION_CODE_ERROR("短信验证码错误"),
    MESSAGE_HAS_SENT_PLEASE_TRY_AGAIN_IN_ONE_MINUTE("短信已发送,请一分钟后再试"),
    ;


    ResultMsg(String msg) {
        this.msg = msg;
    }
    private String msg;

    public String getMsg() {
        return msg;
    }
}

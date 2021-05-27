package com.blog.enums;

/**
 * 缓存常量
 *
 * <pre>
 * 枚举命名以小模块名打头（如：核心-APP模块使用：CORE_APP_XXXXX）放在一起
 * 键（至少两层）：
 *  a、第一层区分模块，固定三位长度：
 *     1、第一位固定X，
 *     2、第二位是大模块标志0-5（
 *              0：系统公共
 *              1：基础模块【XAPP-BASE】
 *              2：共通模块【XAPP-COMN】
 *              3：核心模块【XAPP-CORE】
 *              4：网关模块【XAPP-GATE】
 *              5：借贷模块【XAPP-LOAN】
 *       ）
 * 【示例】:
 *      CORE_APP_XXXX("MER:XXX:%s")，核心APP的某个缓存键
 * </pre>
 */
public enum CacheKey {


    /**
     * 用户模块
     */
    // 用户注册并发锁，key为phone_appCode，value为锁定时的系统时间
    BLOG_USER_REGISTER_LOCK("MER:CORE:USER:REGISTER:LOCKER:%s_%s"),
    BLOG_USER_SMS_CODE_REGISTER("MER:USER:SMS:REGISTER:%s"),//用户注册存放短信验证码(手机号)
    BLOG_USER_SMS_CODE_LOGIN("MER:USER:SMS:LOGIN:%s"),//用户登录存放短信验证码(手机号)
    BLOG_USER_SMS_RESET_PWD("MER:USER:SMS:RESET_PWD:%s"),//用户重设密码存放短信验证码(手机号)
    BLOG_USER_SMS_CODE_LOGIN_IMG_CODE_TIMES("MER:USER:IMG_CODE:%s"),//用户短信登录发送短信图形验证码次数
    BLOG_USER_SMS_CODE_LOGIN_WITH_REG("MER:USER:SMS:LOGIN_WITH_REG:%s"),//用户注册带自动登录存放短信验证码(手机号)
    BLOG_USER_LOGIN_TOKEN("MER:USER:LOGIN:TOKEN:%s"),//用户登录时候缓存生成的token的key
    BLOG_USER_LOGIN_TOKEN_USER("MER:USER:LOGIN:TOKEN:USER:%s"),//用户登录成功缓存当前用户信息key
    BLOG_USER_SMS_LOCKED_VEST_PHONES("MER:USER:SMS:LOCKED:VEST_%s"),//马甲固定短信验证码手机号配置，键为马甲Code，值为以英文逗号分隔的手机号码列表

    USER_INFO("MER:USER:%s"), // 用户信息
    USER_WORK_INFO("MER:USER:WORK:%s"), // 工作信息
    USER_CARD_INFO("MER:USER:CARD:%s"), // 卡信息
    USER_CONTACTS_INFO("MER:USER:CONTACTS:%s"), // 联系人信息

    BLOG_PAGE_LIST("blog:page:list"),
    BLOG_PAGE_SETNX("blog:page:setnx"),
    /**
     * SMS 模块缓存
     */
    //缓存有效期-马甲短信场景配置
    BLOG_SMS_SETTING_CONFIG_VALID_VEST_BUSI_LIST("MER:LOAN:SMS:SETTING:CONFIG_VALID:VEST_BUSI_LIST"),
    //缓存有效期-账户列表
    BLOG_SMS_SETTING_CONFIG_VALID_ACCOUNT_LIST("MER:LOAN:SMS:SETTING:CONFIG_VALID:ACCOUNT_LIST"),
    //缓存有效期-短信业务码详情
    BLOG_SMS_SETTING_CONFIG_VALID_SERVICE_INFO("MER:LOAN:SMS:SETTING:CONFIG_VALID:SERVICE_INFO"),
    //马甲短信场景配置，值为MerchantSmsVestServiceConfigVo集合
    BLOG_SMS_CONFIG_VEST_BUSI_LIST("MER:LOAN:SMS:CONFIG:VEST_BUSI_LIST:%s"),//马甲Code
    //账户列表，值为MerchantSmsAccountVo集合
    BLOG_SMS_CONFIG_ACCOUNT_LIST("MER:LOAN:SMS:CONFIG:ACCOUNT_LIST"),
    //短信业务码详情，值为MerchantSmsServiceConfigVo对象
    BLOG_SMS_CONFIG_SERVICE_INFO("MER:LOAN:SMS:CONFIG:SERVICE_INFO:%s"),//短信业务码
    ;


    CacheKey(String key) {
        this.key = key;
    }

    private String key;

    public String getKey() {
        return key;
    }

    /**
     * 根据keys数组逐个替换KEY中%s生成KEY
     *
     * @param keys
     * @return
     */
    public String getKey(String... keys) {
        return String.format(key, keys);
    }
}

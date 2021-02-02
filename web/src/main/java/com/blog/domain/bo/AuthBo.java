package com.blog.domain.bo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "认证用户请求实体")
public class AuthBo implements Serializable {

	private static final long serialVersionUID = -5258552132842005102L;

	/**
	 * 用户令牌
	 */
	private String token;

	/**
	 * 用户账号
	 */
	private String username;

	/**
	 * 用户密码
	 */
	private String password;

	/**
	 * 图形验证码
	 */
	//private String codeImg;

	/**
	 * 短信验证码
	 */
	private String smsCode;
}

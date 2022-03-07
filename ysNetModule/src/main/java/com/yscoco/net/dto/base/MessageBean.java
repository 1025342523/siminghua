package com.yscoco.net.dto.base;

import java.io.Serializable;


/**
 * 作者：karl.wei
 * 创建日期： 2017/7/31 0031 19:23
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：消息体基类
 */
public class MessageBean<T> implements Serializable {

	private static final long serialVersionUID = -6179718895675023989L;

	/**
	 * 返回状态码
	 */
	private String code;

	/**
	 * 返回消息
	 */
	private String msg;

	public MessageBean() {
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}

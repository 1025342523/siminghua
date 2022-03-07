package com.yscoco.net.dto.base;

import java.util.List;


/**
 * 作者：karl.wei
 * 创建日期： 2017/7/31 0031 19:23
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：子对象消息体，对象形式
 */
public class ListMessageBean<E> extends MessageBean {

	private static final long serialVersionUID = 3821068611680729123L;

	/**
	 * 返回列表
	 */
	private List<E> data;

	public ListMessageBean() {
		super();
	}

	public List<E> getData() {
		return data;
	}

	public void setData(List<E> data) {
		this.data = data;
	}
}

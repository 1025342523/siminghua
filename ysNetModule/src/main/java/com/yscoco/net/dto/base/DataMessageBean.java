package com.yscoco.net.dto.base;


/**
 * 作者：karl.wei
 * 创建日期： 2017/7/31 0031 19:23
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：子对象消息体，对象形式
 */
public class DataMessageBean<E> extends MessageBean {

    private static final long serialVersionUID = 7499049188052671251L;

    /**
     * 返回对象
     */
    private E data;

    public DataMessageBean() {
        super();
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataMessageBean{" +
                "data=" + data +
                '}';
    }
}

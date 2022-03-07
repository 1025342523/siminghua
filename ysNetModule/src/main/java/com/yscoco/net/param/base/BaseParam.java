package com.yscoco.net.param.base;

import java.io.Serializable;

/**
 * 作者：karl.wei
 * 创建日期： 2017/7/30 0030 22:15
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：参数基类
 */

public abstract class BaseParam implements Serializable {
    public String url;
    public abstract String getAES();
}

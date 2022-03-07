package com.yscoco.net.dto.base;
/**
 * 作者：karl.wei
 * 创建日期： 2017/7/31 0031 19:23
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：URl对应DTO基类
 */

public class BaseUrlBean extends BaseBean {
    private String urls;/*链接地址*/
    private String urlType;/*链接类型*/
    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }
}

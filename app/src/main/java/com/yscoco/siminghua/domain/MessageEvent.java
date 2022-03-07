package com.yscoco.siminghua.domain;

/**
 * Created by ZhangZeZhi on 2018\9\21 0021.
 */

public class MessageEvent {

    private int code;
    private Object obj;

    public int getCode() {
        return code;
    }

    public MessageEvent setCode(int code) {
        this.code = code;
        return this;
    }

    public Object getObj() {
        return obj;
    }

    public MessageEvent setObj(Object obj) {
        this.obj = obj;
        return this;
    }
    
}

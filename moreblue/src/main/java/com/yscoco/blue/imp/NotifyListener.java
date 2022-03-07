package com.yscoco.blue.imp;

/**
 * 作者：karl.wei
 * 创建日期： 2017/8/16 0016 09:19
 * 邮箱：karl.wei@yscoco.com
 * 类介绍：通知信息回调方法
 * 里面可自由实现需要处理后的结果方法体
 */

public interface NotifyListener {
    /**
     * 获取到设备传输过来数据
     */
    void change(byte[]  changeByte);
    /*电量值*/
    void battery(int battery);

}

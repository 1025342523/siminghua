package com.yscoco.net;

/**
 * 作者：karl.wei
 * 创建日期： 2018/7/23 0023
 * 邮箱：karl.wei@yscoco.com
 * QQ：2736764338
 * 类介绍：网络配置
 */
public class NetConfig {
    public final static int CONNECT_TIMEOUT = 180000;
    public final static int READ_TIMEOUT = 180000;
    public final static int WRITE_TIMEOUT = 180000;
    private static boolean isLocation = false;
    public static String URL_ROOT = null;

    static {
        if (isLocation) {
            URL_ROOT = "http://39.108.162.192:8080/tire/";
//            URL_ROOT = "http://121.46.4.24:8600/tire/";
        } else {
            URL_ROOT = "http://39.108.162.192:8080/tire/";
//            URL_ROOT = "http://121.46.4.24:8600/tire/";
        }
    }
}

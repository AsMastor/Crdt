package cn.xu.config;

import java.util.*;

public class Config {
    public static final String toServerTopic = "serverUp";
    public static final String fromServerTopic = "serverDown";
    public static final String toOtherServersTopic = "otherServers";
    public static final String startSycUpTopic = "startSycUp";
    public static final String startSycDownTopic = "startSycUpDown";
    public static final String startSycEndSymbol = "sycEnd";
    public static final int Null = -1;
    public static final String msgSplitter = "&";
    public static final String clockSplitter = "#";
    public static final String operationSplitter = "%";
    public static final String invalidSymbol = "~";
    public static final List<String> mqttBrokers;
    static {
        mqttBrokers = new ArrayList<>();
//        mqttBrokers.add ("tcp://47.94.214.44:1883");    // 阿里云北京
//        mqttBrokers.add ("tcp://47.108.181.239:1883");  // 阿里云成都
//        mqttBrokers.add ("tcp://139.224.9.246:1883");   // 阿里云上海
        mqttBrokers.add ("tcp://0.0.0.0:1883");         // 本机
        mqttBrokers.add("tcp://broker-cn.emqx.io:1883");        // 公用免费的mqtt服务器（有流量限制）
//        mqttBrokers.add ("tcp://mqtt.eclipseprojects.io:1883"); // 公用免费的mqtt服务器（有流量限制）
//        mqttBrokers.add("tcp://test.mosquitto.org:1883");           // 公用免费的mqtt服务器（有流量限制）
    }
}

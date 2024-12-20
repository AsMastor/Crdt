package cn.xu.config;

import java.util.*;

public class Config {
    public static final String toServerTopic = "serverUp";
    public static final String fromServerTopic = "serverDown";
    public static final String toOtherServersTopic = "otherServers";
    public static final int Null = -1;
    public static final String msgSplitter = "&";
    public static final String clockSplitter = "#";
    public static final String operationSplitter = "%";
    public static final List<String> mqttBrokers;
    static {
        mqttBrokers = new ArrayList<>();
        mqttBrokers.add ("tcp://8.146.204.142:1883");   // 阿里云北京
        mqttBrokers.add ("tcp://8.156.64.76:1883");     // 阿里云成都
        mqttBrokers.add ("tcp://111.231.82.68:1883");   // 腾讯云上海
        mqttBrokers.add ("tcp://0.0.0.0:1883");         // 本机
    }
}

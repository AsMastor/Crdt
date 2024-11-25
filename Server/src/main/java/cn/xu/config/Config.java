package cn.xu.config;

import java.util.*;

public class Config {
    public static final String toServerTopic = "serverUp";
    public static final String fromServerTopic = "serverDown";
    public static final String msgSplitter = "&";
    public static final String clockSplitter = "#";
    public static final String operationSplitter = "%";
    public static final List<String> mqttBrokers;
    static {
        mqttBrokers = new ArrayList<>();
        mqttBrokers.add ("tcp://0.0.0.0:1883");
        mqttBrokers.add ("tcp://8.146.204.142:1883");
        mqttBrokers.add ("tcp://mqtt.eclipseprojects.io:1883");
    }
}

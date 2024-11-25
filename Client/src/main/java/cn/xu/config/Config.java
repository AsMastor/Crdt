package cn.xu.config;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static final String mqttBroker = "tcp://0.0.0.0:1883";
    public static final String toServerTopic = "serverUp";
    public static final String fromServerTopic = "serverDown";
    public static final String msgSplitter = "&";
    public static final String clockSplitter = "#";
    public static final String operationSplitter = "%";
    public static final boolean showClock = false;
    public static final int emptyGcb = 0;
    public static final int Null = -1;
    public static final int RTTBaseS = 10;  // 多边时钟：边缘服务器之间的RTT
    public static final int RTTBaseM = 20;  // 多边时钟：客户端和边缘服务器之间的RTT
    public static final int RTTBaseL = 40;  // 精简时钟、向量时钟：客户端和服务器之间的RTT
    public static final int RTTRangePercentage = 20;    // 上下浮动百分比：0 ~ 100
    public static final List<String> mqttBrokers;
    static {
        mqttBrokers = new ArrayList<>();
        mqttBrokers.add ("tcp://111.231.82.68:1883");   // 腾讯云上海
        mqttBrokers.add ("tcp://8.146.204.142:1883");   // 阿里云北京
        mqttBrokers.add ("tcp://8.156.64.76:1883");     // 阿里云成都
        mqttBrokers.add ("tcp://0.0.0.0:1883");         // 本机
    }
}

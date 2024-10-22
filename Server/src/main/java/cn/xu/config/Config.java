package cn.xu.config;

public class Config {
    public static final String mqttBroker = "tcp://0.0.0.0:1883";
    public static final String toServerTopic = "serverUp";
    public static final String fromServerTopic = "serverDown";
    public static final String msgSplitter = "&";
    public static final String clockSplitter = "#";
    public static final String operationSplitter = "%";
}

package cn.xu;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Mqtt {
    private final String topic = "control";
    private final int qos = 1;
    private MqttClient client;

    public Mqtt() {
        // 先初始化mqtt客户端client
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        try {
            client = new MqttClient("tcp://0.0.0.0:1883", "host", new MemoryPersistence());
            client.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        MqttMessage message = new MqttMessage("start".getBytes());
        message.setQos(qos);
        try {
            // 发布消息
            client.publish(topic, message);
            //System.out.println("Message published");
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }
}

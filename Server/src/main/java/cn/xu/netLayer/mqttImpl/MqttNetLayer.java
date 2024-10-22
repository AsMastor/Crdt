package cn.xu.netLayer.mqttImpl;

import cn.xu.backGround.BackGround;
import cn.xu.netLayer.NetLayer;
import cn.xu.config.Config;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttNetLayer implements NetLayer {
    private MqttClient client;
    private MyMqttCallback myMqttCallback;
    private final String publishTopic;
    private final int qos = 1;

    public MqttNetLayer(String nodeId, String subscribeTopic, String publishTopic) {
        // 先初始化mqtt客户端client
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        this.publishTopic = publishTopic;
        try {
            client = new MqttClient(Config.mqttBroker, nodeId, new MemoryPersistence());
            //System.out.println("Connecting to broker: " + Config.mqttBroker);
            client.connect(connOpts);
            //System.out.println("Connected");
        } catch (MqttException me) {
            me.printStackTrace();
        }
        // 订阅对应的主题
        try {
            myMqttCallback = new MyMqttCallback();
            client.setCallback(myMqttCallback);
            client.subscribe(subscribeTopic, qos);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    @Override
    public void setBackGround(BackGround backGround) {
        myMqttCallback.setBackGround(backGround);
    }

    private class MyMqttCallback implements MqttCallback {
        private BackGround backGround;

        MyMqttCallback() {}

        void setBackGround(BackGround backGround) {
            this.backGround = backGround;
        }

        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("Connection lost: " + cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String msg = new String(message.getPayload());
            //System.out.println("Received message from topic '" + topic + "': " + msg);
            backGround.msgIn(msg);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            // 消息发送完成回调
            //System.out.println("Callback complete");
        }
    }

    @Override
    public void asyncSend(String msgStr) {
        //System.out.println("Publishing message: " + msg.toString());
        MqttMessage message = new MqttMessage(msgStr.getBytes());
        message.setQos(qos);
        try {
            // 发布消息
            client.publish(publishTopic, message);
            //System.out.println("Message published");
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }
}

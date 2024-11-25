package cn.xu.netLayer.mqttImpl;

import cn.xu.backGround.BackGround;
import cn.xu.netLayer.NetLayer;
import cn.xu.config.Config;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class MqttNetLayer implements NetLayer {
    private MqttClient client;
    private List<MqttClient> clientOnOtherEdge;
    private MyMqttCallback myMqttCallback;
    private final String downTopic;
    private final int qos = 1;

    public MqttNetLayer(int nodeId, String upTopic, String downTopic, int eNum) {
        // 先给订阅主题加料
        upTopic = upTopic.concat(String.valueOf(nodeId));
        this.downTopic = downTopic.concat(String.valueOf(nodeId));
        // 先初始化mqtt客户端client
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        // 创建并订阅
        try {
            client = new MqttClient(Config.mqttBrokers.get(nodeId), "server#".concat(String.valueOf(nodeId)), new MemoryPersistence());
            //System.out.println("Connecting to broker: " + Config.mqttBroker);
            client.connect(connOpts);
            //System.out.println("Connected");
            myMqttCallback = new MyMqttCallback();
            client.setCallback(myMqttCallback);
            client.subscribe(upTopic, qos);
        } catch (MqttException me) {
            me.printStackTrace();
        }
        // 服务器端独有的，订阅每个边缘服务器的主题
        clientOnOtherEdge = new ArrayList<>();
        for (int i = 0; i < eNum; i++) {
            if (i == nodeId) {
                continue;
            }
            // 订阅其他服务的下行数据
            try {
                MqttClient tempClient = new MqttClient(Config.mqttBrokers.get(i), "server#".concat(String.valueOf(nodeId)), new MemoryPersistence());
                clientOnOtherEdge.add(tempClient);
                tempClient.connect(connOpts);
                myMqttCallback = new MyMqttCallback();
                tempClient.setCallback(myMqttCallback);
                tempClient.subscribe(downTopic.concat(String.valueOf(i)), qos);    // 这里订阅的是其他边缘服务器器的下行主题
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
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
            try {
                throw cause;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String msg = new String(message.getPayload());
            System.out.println("Received message from topic '" + topic + "': " + msg);
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
            client.publish(downTopic, message);
            //System.out.println("Message published");
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }
}

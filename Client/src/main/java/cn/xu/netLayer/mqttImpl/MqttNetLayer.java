package cn.xu.netLayer.mqttImpl;

import cn.xu.backGround.BackGround;
import cn.xu.netLayer.NetLayer;
import cn.xu.config.Config;
import cn.xu.utils.TestUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class MqttNetLayer implements NetLayer {
    private MqttClient client;
    private String sycEndClientId;
    private MqttClient startSycClient;
    private MyMqttCallback myMqttCallback;
    private final String publishTopic;
    private final int qos = 1;
    private Semaphore semaphore;
    private final int RTTBase, RTTRange;
    private final Random random;

    public MqttNetLayer(String nodeId, int serverId, String subscribeTopic, String publishTopic, int RTTBase, Random random, Semaphore semaphore) {
        this(nodeId, serverId, subscribeTopic, publishTopic, RTTBase, random);
        this.semaphore = semaphore;
    }

    public MqttNetLayer(String nodeId, int serverId, String subscribeTopic, String publishTopic, int RTTBase, Random random) {
        this.sycEndClientId = Config.startSycEndSymbol + nodeId;
        this.random = new Random(random.nextInt());
        this.RTTBase = RTTBase;
        RTTRange = RTTBase * Config.RTTRangePercentage / 100;
        // 订阅主题先加料
        subscribeTopic = subscribeTopic.concat(String.valueOf(serverId));
        publishTopic = publishTopic.concat(String.valueOf(serverId));
        // 先初始化mqtt客户端client
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setMaxInflight(1000);
        this.publishTopic = publishTopic;
        // 创建客户端并订阅对应主题
        try {
            client = new MqttClient(Config.mqttBrokers.get(serverId), nodeId, new MemoryPersistence());
            client.connect(connOpts);
            myMqttCallback = new MyMqttCallback(RTTBase, RTTRange, random);
            client.setCallback(myMqttCallback);
            client.subscribe(subscribeTopic, qos);
        } catch (MqttException me) {
            me.printStackTrace();
        }
        // 创建开始启动客户端，并订阅对应主题
        try {
            startSycClient = new MqttClient(Config.mqttBrokers.get(serverId), nodeId + "startSyc", new MemoryPersistence());
            startSycClient.connect(connOpts);
            startSycClient.setCallback(myMqttCallback);
            startSycClient.subscribe(Config.startSycDownTopic, qos);
        } catch (MqttException me) {
            me.printStackTrace();
        }
        // 创建客户端，订阅控制主题
        try {
            MqttClient ctrClient = new MqttClient(Config.mqttBrokers.get(0), nodeId + "ctr", new MemoryPersistence());
            ctrClient.connect(connOpts);
            ctrClient.setCallback(new ControlCallback());
            ctrClient.subscribe(Config.controlTopic);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    @Override
    public void setBackGround(BackGround backGround) {
        myMqttCallback.setBackGround(backGround);
    }

    private class ControlCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable throwable) {

        }

        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            semaphore.release();
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    }

    private class MyMqttCallback implements MqttCallback {
        private BackGround backGround;
        private final int RTTBase;
        private final int RTTRange;
        private final Random random;

        MyMqttCallback(int RTTBase, int RTTRange, Random random) {
            this.RTTBase = RTTBase;
            this.RTTRange = RTTRange;
            this.random = random;
        }

        void setBackGround(BackGround backGround) {
            this.backGround = backGround;
        }

        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("Connection lost: " + cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            // 模拟网络延时，故意暂停一段时间
            TestUtils.randomHRTT(RTTBase, RTTRange, random);
            String msg = new String(message.getPayload());
            //System.out.println("Received message from topic '" + topic + "': " + msg);
            // TODO: 让边缘服务器来决定结束不合理，会导致其他正在startsyc的客户端同样停止，后续改进为客户端自己判断停止startsyc
            if (msg.startsWith(Config.startSycEndSymbol)) {
                if (msg.equals(sycEndClientId)) {
                    startSycClient.disconnect();
                    System.out.println("syc end");
                }
                return;
            }
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
        // 模拟网络延时，故意暂停一段时间
        TestUtils.randomHRTT(RTTBase, RTTRange, random);
        try {
            // 发布消息
            client.publish(publishTopic, message);
            //System.out.println("Message published");
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    @Override
    public void startSyc() {
        MqttMessage message = new MqttMessage(sycEndClientId.getBytes());
        message.setQos(qos);
        try {
            startSycClient.publish(Config.startSycUpTopic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

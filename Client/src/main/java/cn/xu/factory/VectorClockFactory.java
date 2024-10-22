package cn.xu.factory;

import cn.xu.backGround.BackGround;
import cn.xu.backGround.ClientBackGround;
import cn.xu.clockLayer.ClockLayer;
import cn.xu.clockLayer.SimplifyClockLayer;
import cn.xu.clockLayer.VectorClockLayer;
import cn.xu.config.Config;
import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.MvMap;
import cn.xu.netLayer.NetLayer;
import cn.xu.netLayer.mqttImpl.MqttNetLayer;

import java.util.Random;

public class VectorClockFactory implements Factory{
    private final int nId;
    private final int nodeNum;
    private Random random;

    public VectorClockFactory(int nId, int nodeNum, Random random) {
        this.nId = nId;
        this.nodeNum = nodeNum;
        this.random = random;
    }

    @Override
    public AwSet buildAwSet() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)),
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseL, random);
        BackGround backGround = new ClientBackGround();
        ClockLayer clockLayer = new VectorClockLayer(nId, nodeNum);
        AwSet awSet = new AwSet();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(awSet);
        clockLayer.setNetLayer(netLayer);
        clockLayer.setBackGround(backGround);
        awSet.setClockLayer(clockLayer);
        // 成功创建并返回
        System.out.println("VectorClockClient ".concat(String.valueOf(nId)).concat(" start..."));
        return awSet;
    }

    @Override
    public MvMap buildMvMap() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)),
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseL, random);
        BackGround backGround = new ClientBackGround();
        ClockLayer clockLayer = new VectorClockLayer(nId, nodeNum);
        MvMap mvMap = new MvMap();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(mvMap);
        clockLayer.setNetLayer(netLayer);
        clockLayer.setBackGround(backGround);
        mvMap.setClockLayer(clockLayer);
        // 成功创建并返回
        System.out.println("VectorClockClient ".concat(String.valueOf(nId)).concat(" start..."));
        return mvMap;
    }
}

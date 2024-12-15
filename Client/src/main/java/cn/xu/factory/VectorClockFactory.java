package cn.xu.factory;

import cn.xu.backGround.BackGround;
import cn.xu.backGround.ClientBackGround;
import cn.xu.clockLayer.ClockLayer;
import cn.xu.clockLayer.SimplifyClockLayer;
import cn.xu.clockLayer.VectorClockLayer;
import cn.xu.config.Config;
import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.LwwMap;
import cn.xu.crdtObject.MvMap;
import cn.xu.netLayer.NetLayer;
import cn.xu.netLayer.mqttImpl.MqttNetLayer;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class VectorClockFactory implements Factory{
    private final int nId;
    private final int nodeNum;
    private final int eId;
    private Random random;
    private Semaphore semaphore;

    public VectorClockFactory(int nId, int nodeNum, int eId, Random random, Semaphore semaphore) {
        this.nId = nId;
        this.nodeNum = nodeNum;
        this.eId = eId;
        this.random = random;
        this.semaphore = semaphore;
    }

    @Override
    public AwSet buildAwSet() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseS, random, semaphore);
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
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseS, random, semaphore);
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

    @Override
    public LwwMap buildLwwMap() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseS, random, semaphore);
        BackGround backGround = new ClientBackGround();
        ClockLayer clockLayer = new VectorClockLayer(nId, nodeNum);
        LwwMap lwwMap = new LwwMap();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(lwwMap);
        clockLayer.setNetLayer(netLayer);
        clockLayer.setBackGround(backGround);
        lwwMap.setClockLayer(clockLayer);
        // 成功创建并返回
        System.out.println("VectorClockClient ".concat(String.valueOf(nId)).concat(" start..."));
        return lwwMap;
    }
}

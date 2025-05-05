package cn.xu.factory;

import cn.xu.backGround.BackGround;
import cn.xu.backGround.ClientBackGround;
import cn.xu.clockLayer.ClockLayer;
import cn.xu.clockLayer.MultiEdgeClockLayer;
import cn.xu.config.Config;
import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.LogList;
import cn.xu.crdtObject.LwwMap;
import cn.xu.crdtObject.MvMap;
import cn.xu.netLayer.NetLayer;
import cn.xu.netLayer.mqttImpl.MqttNetLayer;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class MultiEdgeClockFactory implements Factory{
    private final int nId, eId, eNum;
    private Random random;
    private Semaphore semaphore;

    public MultiEdgeClockFactory(int nId, int eId, int eNum, Random random, Semaphore semaphore) {
        this.nId = nId;
        this.eId = eId;
        this.eNum = eNum;
        this.random = random;
        this.semaphore = semaphore;
    }

    @Override
    public AwSet buildAwSet() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseS, random, semaphore);
        BackGround backGround = new ClientBackGround();
        ClockLayer clockLayer = new MultiEdgeClockLayer(nId, eNum);
        AwSet awSet = new AwSet();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(awSet);
        clockLayer.setNetLayer(netLayer);
        awSet.setClockLayer(clockLayer);
        // 启动同步
        netLayer.startSyc();
        // 成功创建并返回
        System.out.println("MultiEdgeClockClient ".concat(String.valueOf(nId)).concat(" start..."));
        return awSet;
    }

    @Override
    public MvMap buildMvMap() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseS, random, semaphore);
        BackGround backGround = new ClientBackGround();
        ClockLayer clockLayer = new MultiEdgeClockLayer(nId, eNum);
        MvMap mvMap = new MvMap();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(mvMap);
        clockLayer.setNetLayer(netLayer);
        mvMap.setClockLayer(clockLayer);
        // 启动同步
        netLayer.startSyc();
        // 成功创建并返回
        System.out.println("MultiEdgeClockClient ".concat(String.valueOf(nId)).concat(" start..."));
        return mvMap;
    }

    @Override
    public LwwMap buildLwwMap() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseS, random, semaphore);
        BackGround backGround = new ClientBackGround();
        ClockLayer clockLayer = new MultiEdgeClockLayer(nId, eNum);
        LwwMap lwwMap = new LwwMap();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(lwwMap);
        clockLayer.setNetLayer(netLayer);
        lwwMap.setClockLayer(clockLayer);
        // 启动同步
        netLayer.startSyc();
        // 成功创建并返回
        System.out.println("MultiEdgeClockClient ".concat(String.valueOf(nId)).concat(" start..."));
        return lwwMap;
    }

    @Override
    public LogList buildLogList() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseS, random, semaphore);
        BackGround backGround = new ClientBackGround();
        ClockLayer clockLayer = new MultiEdgeClockLayer(nId, eNum);
        LogList logList = new LogList();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(logList);
        clockLayer.setNetLayer(netLayer);
        logList.setClockLayer(clockLayer);
        // 启动同步
        netLayer.startSyc();
        // 成功创建并返回
        System.out.println("MultiEdgeClockClient ".concat(String.valueOf(nId)).concat(" start..."));
        return logList;
    }
}

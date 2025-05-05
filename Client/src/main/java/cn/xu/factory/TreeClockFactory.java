package cn.xu.factory;

import cn.xu.backGround.BackGround;
import cn.xu.backGround.ClientBackGround;
import cn.xu.clockLayer.ClockLayer;
import cn.xu.clockLayer.TreeClockLayer;
import cn.xu.clockLayer.VectorClockLayer;
import cn.xu.config.Config;
import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.LogList;
import cn.xu.crdtObject.LwwMap;
import cn.xu.crdtObject.MvMap;
import cn.xu.netLayer.NetLayer;
import cn.xu.netLayer.mqttImpl.MqttNetLayer;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class TreeClockFactory implements Factory {
    private final int nId;
    private final int nodeNum;
    private final int eId;
    private Random random;
    private Semaphore semaphore;

    public TreeClockFactory(int nId, int nodeNum, int eId, Random random, Semaphore semaphore) {
        this.nId = nId;
        this.nodeNum = nodeNum;
        this.eId = eId;
        this.random = random;
        this.semaphore = semaphore;
    }

    @Override
    public AwSet buildAwSet() {
        return null;
    }

    @Override
    public MvMap buildMvMap() {
        return null;
    }

    @Override
    public LwwMap buildLwwMap() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseS, random, semaphore);
        BackGround backGround = new ClientBackGround();
        ClockLayer clockLayer = new TreeClockLayer(nId, nodeNum);
        LwwMap lwwMap = new LwwMap();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(lwwMap);
        clockLayer.setNetLayer(netLayer);
        clockLayer.setBackGround(backGround);
        lwwMap.setClockLayer(clockLayer);
        // 成功创建并返回
        System.out.println("TreeClockClient ".concat(String.valueOf(nId)).concat(" start..."));
        return lwwMap;
    }

    @Override
    public LogList buildLogList() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseS, random, semaphore);
        BackGround backGround = new ClientBackGround();
        ClockLayer clockLayer = new TreeClockLayer(nId, nodeNum);
        LogList logList = new LogList();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(logList);
        clockLayer.setNetLayer(netLayer);
        clockLayer.setBackGround(backGround);
        logList.setClockLayer(clockLayer);
        // 成功创建并返回
        System.out.println("TreeClockClient ".concat(String.valueOf(nId)).concat(" start..."));
        return logList;
    }
}

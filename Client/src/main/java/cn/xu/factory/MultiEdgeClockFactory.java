package cn.xu.factory;

import cn.xu.backGround.BackGround;
import cn.xu.backGround.ClientBackGround;
import cn.xu.clockLayer.ClockLayer;
import cn.xu.clockLayer.MultiEdgeClockLayer;
import cn.xu.config.Config;
import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.MvMap;
import cn.xu.netLayer.NetLayer;
import cn.xu.netLayer.mqttImpl.MqttNetLayer;

import java.util.Random;

public class MultiEdgeClockFactory implements Factory{
    private final int nId, eId, eNum;
    private Random random;

    public MultiEdgeClockFactory(int nId, int eId, int eNum, Random random) {
        this.nId = nId;
        this.eId = eId;
        this.eNum = eNum;
        this.random = random;
    }

    @Override
    public AwSet buildAwSet() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseL, random);
        BackGround backGround = new ClientBackGround();
        ClockLayer clockLayer = new MultiEdgeClockLayer(nId, eNum);
        AwSet awSet = new AwSet();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(awSet);
        clockLayer.setNetLayer(netLayer);
        awSet.setClockLayer(clockLayer);
        // 成功创建并返回
        System.out.println("MultiEdgeClockClient ".concat(String.valueOf(nId)).concat(" start..."));
        return awSet;
    }

    @Override
    public MvMap buildMvMap() {
        NetLayer netLayer = new MqttNetLayer("client#".concat(String.valueOf(nId)), eId,
                Config.fromServerTopic, Config.toServerTopic, Config.RTTBaseL, random);
        BackGround backGround = new ClientBackGround();
        ClockLayer clockLayer = new MultiEdgeClockLayer(nId, eNum);
        MvMap mvMap = new MvMap();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(mvMap);
        clockLayer.setNetLayer(netLayer);
        mvMap.setClockLayer(clockLayer);
        // 成功创建并返回
        System.out.println("MultiEdgeClockClient ".concat(String.valueOf(nId)).concat(" start..."));
        return mvMap;
    }
}

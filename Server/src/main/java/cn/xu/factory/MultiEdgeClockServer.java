package cn.xu.factory;

import cn.xu.backGround.BackGround;
import cn.xu.backGround.ServerBackGround;
import cn.xu.clockLayer.ClockLayer;
import cn.xu.clockLayer.MultiEdgeClockLayer;
import cn.xu.clockLayer.SimplifyClockLayer;
import cn.xu.config.Config;
import cn.xu.netLayer.NetLayer;
import cn.xu.netLayer.mqttImpl.MqttNetLayer;

public class MultiEdgeClockServer implements Server{
    private final int nId;  // 本边服务器的id，范围：0 至 nNum - 1
    private final int nNum; // 边缘服务器的数量

    public MultiEdgeClockServer(int nId, int nNum) {
        this.nId = nId;
        this.nNum = nNum;
    }

    @Override
    public void start() {
        NetLayer netLayer = new MqttNetLayer(nId, Config.toServerTopic, Config.fromServerTopic, nNum);
        BackGround backGround = new ServerBackGround();
        ClockLayer clockLayer = new MultiEdgeClockLayer(nId);
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setNetLayer(netLayer);
        System.out.println("MultiEdgeClockServer ".concat(String.valueOf(nId).concat(" start...")));
    }
}

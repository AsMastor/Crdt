package cn.xu.factory;

import cn.xu.backGround.BackGround;
import cn.xu.backGround.ServerBackGround;
import cn.xu.clockLayer.ClockLayer;
import cn.xu.clockLayer.SimplifyClockLayer;
import cn.xu.config.Config;
import cn.xu.netLayer.NetLayer;
import cn.xu.netLayer.mqttImpl.MqttNetLayer;

public class SimplifyClockServer implements Server{
    private final int nId;

    public SimplifyClockServer(int nId) {
        this.nId = nId;
    }

    @Override
    public void start() {
        NetLayer netLayer = new MqttNetLayer("server#".concat(String.valueOf(nId)),
                Config.toServerTopic, Config.fromServerTopic);
        BackGround backGround = new ServerBackGround();
        ClockLayer clockLayer = new SimplifyClockLayer();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setNetLayer(netLayer);
        System.out.println("SimplifyClockServer ".concat(String.valueOf(nId).concat(" start...")));
    }
}

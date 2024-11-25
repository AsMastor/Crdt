package cn.xu.factory;

import cn.xu.backGround.BackGround;
import cn.xu.backGround.ServerBackGround;
import cn.xu.clockLayer.ClockLayer;
import cn.xu.clockLayer.VectorClockLayer;
import cn.xu.config.Config;
import cn.xu.netLayer.NetLayer;
import cn.xu.netLayer.mqttImpl.MqttNetLayer;

public class VectorClockServer implements Server{
    private final int nId;

    public VectorClockServer(int nId) {
        this.nId = nId;
    }

    @Override
    public void start() {
        NetLayer netLayer = new MqttNetLayer(nId, Config.toServerTopic, Config.fromServerTopic, 0);
        BackGround backGround = new ServerBackGround();
        ClockLayer clockLayer = new VectorClockLayer();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setNetLayer(netLayer);
        System.out.println("VectorClockServer ".concat(String.valueOf(nId).concat(" start...")));
    }
}

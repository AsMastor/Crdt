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
    private final int nNum; // 边缘服务器的数量

    public VectorClockServer(int nId, int nNum) {
        this.nId = nId;
        this.nNum = nNum;
    }

    @Override
    public void start() {
        NetLayer netLayer = new MqttNetLayer(nId, Config.toServerTopic, Config.fromServerTopic, nNum);
        BackGround backGround = new ServerBackGround();
        ClockLayer clockLayer = new VectorClockLayer();
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setNetLayer(netLayer);
        System.out.println("VectorClockServer ".concat(String.valueOf(nId).concat(" start...")));
    }
}

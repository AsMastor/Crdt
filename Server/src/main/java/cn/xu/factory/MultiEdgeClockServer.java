package cn.xu.factory;

import cn.xu.backGround.BackGround;
import cn.xu.backGround.ServerBackGround;
import cn.xu.clockLayer.ClockLayer;
import cn.xu.clockLayer.MultiEdgeClockLayer;
import cn.xu.config.Config;
import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.LogList;
import cn.xu.crdtObject.LwwMap;
import cn.xu.netLayer.NetLayer;
import cn.xu.netLayer.mqttImpl.MqttNetLayer;
import lombok.Data;

@Data
public class MultiEdgeClockServer implements Server{
    private final int nId;  // 本边服务器的id，范围：0 至 nNum - 1
    private final int nNum; // 边缘服务器的数量
    private ClockLayer clockLayer;
    private MqttNetLayer netLayer;

    public MultiEdgeClockServer(int nId, int nNum) {
        this.nId = nId;
        this.nNum = nNum;
    }

    @Override
    public void start() {
        netLayer = new MqttNetLayer(nId, Config.toServerTopic, Config.fromServerTopic, nNum);
        BackGround backGround = new ServerBackGround();
        clockLayer = new MultiEdgeClockLayer(nId);
        // 依赖关系套嵌其中
        netLayer.setBackGround(backGround);
        backGround.setClockLayer(clockLayer);
        clockLayer.setNetLayer(netLayer);
        System.out.println("MultiEdgeClockServer ".concat(String.valueOf(nId).concat(" start...")));
    }

    public void setLwwMap() {
        LwwMap lwwMap = new LwwMap(500);
        lwwMap.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(lwwMap);
        netLayer.setCrdtObject(lwwMap);
        lwwMap.setNetLayer(netLayer);
    }

    public void setAwSet() {
        AwSet awSet = new AwSet(0);
        awSet.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(awSet);
        netLayer.setCrdtObject(awSet);
        awSet.setNetLayer(netLayer);
    }

    public void setLogList() {
        LogList logList = new LogList(0);
        logList.setClockLayer(clockLayer);
        clockLayer.setCrdtLayer(logList);
        netLayer.setCrdtObject(logList);
        logList.setNetLayer(netLayer);
    }
}

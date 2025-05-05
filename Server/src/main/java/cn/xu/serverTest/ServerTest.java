package cn.xu.serverTest;

import cn.xu.clockLayer.MultiEdgeClockLayer;
import cn.xu.factory.MultiEdgeClockServer;
import cn.xu.factory.Server;
import cn.xu.factory.SimplifyClockServer;
import cn.xu.factory.VectorClockServer;
import cn.xu.pojo.clock.MultiEdgeClock;
import cn.xu.utils.IOUtils;
import cn.xu.utils.TestUtils;
import cn.xu.utils.WidthCnt;

import java.util.Timer;
import java.util.TimerTask;

public class ServerTest {
    static int edgeId;
    static int edgeNum = 2;

    public static void testVector(int eId, int eNum) {
        edgeId = eId;
        edgeNum = eNum;
        asVectorClockServer();
    }

    public static void testMultiEdge(int eId, int eNum) {
        edgeId = eId;
        edgeNum = eNum;
        asMultiEdgeClockServer();
    }

    private static void asVectorClockServer() {
        Server server = new VectorClockServer(edgeId, edgeNum);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    private static void asMultiEdgeClockServer() {
        MultiEdgeClockServer server = new MultiEdgeClockServer(edgeId, edgeNum);
        server.start();

        // 设置对应的数据结构层
        //server.setLwwMap();
        server.setAwSet();
        //server.setLogList();

        // 主线程无限期等待
        TestUtils.foreverSleep();
    }
}

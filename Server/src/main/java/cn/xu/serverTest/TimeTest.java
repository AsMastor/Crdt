package cn.xu.serverTest;

import cn.xu.factory.MultiEdgeClockServer;
import cn.xu.factory.Server;
import cn.xu.factory.SimplifyClockServer;
import cn.xu.factory.VectorClockServer;
import cn.xu.utils.IOUtils;
import cn.xu.utils.TestUtils;

public class TimeTest {
    static int edgeId;

    public static void test() {
        init();
        //asVectorClockServer();
        //asSimplifyClockServer();
        asMultiEdgeClockServer();
    }

    private static void init() {
        IOUtils.readData();
        edgeId = IOUtils.get("edgeId");
    }

    private static void asVectorClockServer() {
        int edgeNum = 3;
        Server server = new VectorClockServer(edgeId, edgeNum);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    //@serverTest
    private static void asSimplifyClockServer() {
        Server server = new SimplifyClockServer(edgeId);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    private static void asMultiEdgeClockServer() {
        int edgeNum = 3;
        Server server = new MultiEdgeClockServer(edgeId, edgeNum);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    private static void singleTestMultiEdgeClockServer() {
        int eNum = 3;
        for (int i = 0; i < eNum; i++) {
            Server server = new MultiEdgeClockServer(i, eNum);
            server.start();
        }
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }
}

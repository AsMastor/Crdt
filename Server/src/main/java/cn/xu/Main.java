package cn.xu;

import cn.xu.factory.MultiEdgeClockServer;
import cn.xu.factory.Server;
import cn.xu.factory.SimplifyClockServer;
import cn.xu.factory.VectorClockServer;
import cn.xu.utils.TestUtils;

public class Main {
    private static void testVectorClockServer() {
        Server server = new VectorClockServer(0);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    //@Test
    private static void testSimplifyClockServer() {
        Server server = new SimplifyClockServer(0);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    private static void AsMultiEdgeClockServer() {
        int edgeId = 1;
        int edgeNum = 3;
        Server server = new MultiEdgeClockServer(edgeId, edgeNum);
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    private static void testMultiEdgeClockServer() {
        int eNum = 3;
        for (int i = 0; i < eNum; i++) {
            Server server = new MultiEdgeClockServer(i, eNum);
            server.start();
        }
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    public static void main(String[] args) {
        //testVectorClockServer();
        testSimplifyClockServer();
        //testMultiEdgeClockServer();
    }
}
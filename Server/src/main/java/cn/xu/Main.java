package cn.xu;

import cn.xu.factory.MultiEdgeClockServer;
import cn.xu.factory.Server;
import cn.xu.factory.SimplifyClockServer;
import cn.xu.factory.VectorClockServer;
import cn.xu.utils.TestUtils;

public class Main {
    public static void main(String[] args) {
        //asVectorClockServer(Integer.parseInt(args[0]));
        //asSimplifyClockServer(Integer.parseInt(args[0]));
        asMultiEdgeClockServer(Integer.parseInt(args[0]));
    }

    private static void asVectorClockServer(int edgeId) {
        int edgeNum = 3;
        Server server = new VectorClockServer(edgeId, edgeNum);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    //@Test
    private static void asSimplifyClockServer(int edgeId) {
        Server server = new SimplifyClockServer(edgeId);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    private static void asMultiEdgeClockServer(int edgeId) {
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
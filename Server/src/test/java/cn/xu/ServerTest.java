package cn.xu;

import cn.xu.factory.MultiEdgeClockServer;
import cn.xu.factory.Server;
import cn.xu.factory.SimplifyClockServer;
import cn.xu.factory.VectorClockServer;
import cn.xu.utils.TestUtils;
import org.junit.Test;

public class ServerTest {
    //@Test
    public void testVectorClockServer() {
        Server server = new VectorClockServer(0, 0);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    //@Test
    public void testSimplifyClockServer() {
        Server server = new SimplifyClockServer(0);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    //@Test
    public void testMultiEdgeClockServer() {
        int eNum = 2;
        for (int i = 0; i < eNum; i++) {
            Server server = new MultiEdgeClockServer(i, eNum);
            server.start();
        }
        //Server server = new MultiEdgeClockServer(1, 4);
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }
}

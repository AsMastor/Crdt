package cn.xu;

import cn.xu.factory.Server;
import cn.xu.factory.SimplifyClockServer;
import cn.xu.factory.VectorClockServer;
import cn.xu.utils.TestUtils;
import org.junit.Test;

public class ServerTest {
    @Test
    public void testVectorClockServer() {
        Server server = new VectorClockServer(1);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }

    @Test
    public void testSimplifyClockServer() {
        Server server = new SimplifyClockServer(1);
        server.start();
        // 主线程无限期等待
        TestUtils.foreverSleep();
    }
}

package cn.xu.utils;

public class TestUtils {
    public static void foreverSleep() {
        while (true) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

package cn.xu.utils;

import java.util.Random;

public class TestUtils {
    public static String randomStr(Random random, int charRange) {
        char x = (char) ('A' + Math.abs(random.nextInt()) % charRange);
        return String.valueOf(x);
    }

    public static void randomSleep(Random random, int sleepTime, int sleepTimeRange) {
        try {
            Thread.sleep(sleepTime + random.nextInt() % sleepTimeRange);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

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

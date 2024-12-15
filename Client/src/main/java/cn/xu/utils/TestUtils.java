package cn.xu.utils;

import java.util.Random;

public class TestUtils {
    public static String randomStr(Random random, int charRange) {
        char x = (char) ('A' + Math.abs(random.nextInt()) % charRange);
        return String.valueOf(x);
    }

    public static void randomSleep(Random random, int sleepTime, int sleepTimeRange) {
        int trueSleepTime = sleepTime;
        if (sleepTimeRange != 0) {
            trueSleepTime = sleepTime + random.nextInt() % sleepTimeRange;
        }
        try {
            Thread.sleep(trueSleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void randomHRTT(int RTTBase, int RTTRange, Random random) {
        try {
            int time = RTTBase / 2 + random.nextInt() % RTTRange;
            Thread.sleep(time);
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

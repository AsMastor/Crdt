package cn.xu.utils;

import lombok.Getter;

public class TimeCounter {
    long startTime;
    long endTime;
    static long alTime = 0;
    @Getter
    static long alNode = 0;

    public void start() {
        startTime = System.nanoTime();
    }

    public void end() {
        endTime = System.nanoTime();
    }

    public String time() {
        long nsTime = endTime - startTime;
        alTime += nsTime;
        alNode++;
        double msTime = (double) nsTime / 1000000;
        return " Time: " + msTime + "ms";
    }

    public static String averageTime() {
        double msTime = (double) (alTime / alNode) / 1000000;
        return " Average Time: " + msTime + "ms";
    }
}

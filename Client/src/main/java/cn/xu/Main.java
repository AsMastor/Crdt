package cn.xu;

import cn.xu.clientTest.TimeTest;
import cn.xu.pojo.clock.MultiEdgeClock;
import cn.xu.pojo.clock.VevtorClock;
import com.alibaba.fastjson.JSON;

public class Main {
    public static void main(String[] args) {
        TimeTest.test();
        //testFastJson();
    }

    static void testFastJson() {
        VevtorClock clock = new VevtorClock(new int[] {1,2,3,4,5}, new int[] {5,4,3,2,1}, 0);
        String s = JSON.toJSONString(clock);
        System.out.println(s);
        VevtorClock clock2 = JSON.parseObject(s, VevtorClock.class);
        System.out.println(clock2);
    }
}
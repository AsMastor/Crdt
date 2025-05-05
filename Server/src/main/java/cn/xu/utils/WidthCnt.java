package cn.xu.utils;

public class WidthCnt {
    int cnt;

    public WidthCnt() {
        cnt = 0;
    }

    public void add(int num) {
        cnt += num;
    }

    @Override
    public String toString() {
        return String.valueOf(cnt * 2);
    }
}

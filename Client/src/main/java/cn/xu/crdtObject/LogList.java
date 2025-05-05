package cn.xu.crdtObject;

import cn.xu.clockLayer.ClockLayer;
import cn.xu.config.Config;
import cn.xu.netLayer.NetLayer;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;
import cn.xu.pojo.clock.MultiEdgeClock;
import cn.xu.pojo.operation.OpType;
import cn.xu.pojo.operation.Operation;
import cn.xu.utils.Pair;
import lombok.Setter;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public class LogList implements CrdtObject {
    private LinkedList<Pair<String, Clock>> list;

    private CountDownLatch latch;   // 每来一个其他消息和自己产生操作时就--，以此来得知所有操作都结束的时刻
    private ClockLayer clockLayer;

    public LogList() {
        list = new LinkedList<>();
    }

    @Override
    public void setCountDownLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void setClockLayer(ClockLayer clockLayer) {
        this.clockLayer = clockLayer;
    }

    @Override
    public void msgIn(Msg msg) {
        if (msg.getOp() == null) {
            latch.countDown();
            return;
        }
        synchronized (this) {
            Operation op = msg.getOp();
            Clock clock = msg.getClock();
            if (op.getOpType().equals(OpType.ADD)) {
                String content = op.getParams()[0];
                realAdd(content, clock);
            } else if (op.getOpType().equals(OpType.REMOVE)) {
                String content = op.getParams()[0];
                MultiEdgeClock removeClock = MultiEdgeClock.deSerialized(content);
                list.removeIf(pair -> pair.getV().equals(removeClock));
            }
            latch.countDown();
        }
    }

    @Override
    public void ownMsgIn(Msg msg) {
        Operation op = msg.getOp();
        Clock clock = msg.getClock();
        if (op.getOpType().equals(OpType.ADD)) {
            // replace 本地这个操作
            for (Pair<String, Clock> pair : list) {
                Clock thisClock = pair.getV();
                if (thisClock.equals(clock)) {
                    thisClock.replaceBy(clock);
                    break;
                }
            }
        }
    }

    public void add(String content) {
        Operation op = new Operation(OpType.ADD, content);
        Clock clock;
        synchronized (this) {
            clock = clockLayer.generateNextClock();
            realAdd(content, clock);
        }
        clockLayer.msgOut(new Msg(op, clock));
        latch.countDown();
    }

    private void realAdd(String content, Clock clock) {
        boolean added = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getV().totalBigger(clock)) {
                list.add(i, new Pair<>(content, clock));
                added = true;
                break;
            }
        }
        if (!added) {
            list.addLast(new Pair<>(content, clock));
        }
    }

    public boolean removeRecent() {
        synchronized (this) {
            if (!list.isEmpty()) {
                Clock removeClock = list.peekLast().getV();
                list.removeLast();
                Operation op = new Operation(OpType.REMOVE, removeClock.serialized());
                Clock clock = clockLayer.generateNextClock();
                clockLayer.msgOut(new Msg(op, clock));
                latch.countDown();
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, Clock> pair : list) {
            sb.append(pair.getK());
            if (Config.showClock) {
                sb.append("[").append(pair.getV()).append("]");
            }
            sb.append("->");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}

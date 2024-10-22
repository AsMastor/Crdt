package cn.xu.crdtObject;

import cn.xu.clockLayer.ClockLayer;
import cn.xu.config.Config;
import cn.xu.pojo.Msg;
import cn.xu.pojo.clock.Clock;
import cn.xu.pojo.operation.OpType;
import cn.xu.pojo.operation.Operation;
import cn.xu.utils.Pair;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class MvMap implements CrdtObject{
    private CountDownLatch latch;   // 每来一个其他消息和自己产生操作时就--，以此来得知所有操作都结束的时刻
    private HashMap<String, LinkedList<Pair<Clock, String>>> existMap;
    private ClockLayer clockLayer;

    public MvMap() {
        existMap = new HashMap<>();
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
        //System.out.println("Received Msg From Others: " + msg.toString());
        Operation op = msg.getOp();
        String key = op.getParams()[0];
        Clock clock = msg.getClock();
        if (op.getOpType().equals(OpType.ADD)) {
            String val;
            // 如果操作不存在 val，则表示发送的 val 值为 ""
            if (op.getParams().length == 1) {
                val = "";
            } else {
                val = op.getParams()[1];
            }
            if (existMap.containsKey(key)) {
                // 删除掉因果序小于当前操作时钟的时钟
                LinkedList<Pair<Clock, String>> list = existMap.get(key);
                list.removeIf(clock1 -> clock1.getK().compare(clock) == -1);
                // 再添加上新来的时钟+val
                list.add(new Pair<>(clock, val));
            } else {
                // 创建新的key value对
                LinkedList<Pair<Clock, String>> tempList = new LinkedList<>();
                tempList.add(new Pair<>(clock, val));
                existMap.put(key, tempList);
            }
        } else if (op.getOpType().equals(OpType.REMOVE)) {
            if (existMap.containsKey(key)) {
                // 删除掉因果序小于当前操作时钟的时钟
                LinkedList<Pair<Clock, String>> list = existMap.get(key);
                list.removeIf(clock1 -> clock1.getK().compare(clock) == -1);
                // 如果 list 为空就删掉 key
                if (list.isEmpty()) {
                    existMap.remove(key);
                }
            }
        }
        latch.countDown();
    }

    @Override
    public void ownMsgIn(Msg msg) {
        Operation op = msg.getOp();
        Clock clock = msg.getClock();
        if (op.getOpType().equals(OpType.REMOVE)) {
            // redundant
        } else if (op.getOpType().equals(OpType.ADD)) {
            String key = op.getParams()[0];
            // replace 本地这个操作
            for (Pair<Clock, String> pair : existMap.getOrDefault(key, new LinkedList<>())) {
                Clock thisClock = pair.getK();
                if (thisClock.equals(clock)) {
                    thisClock.replaceBy(clock);
                    break;
                }
            }
        }
    }

    public void add(String key, String val) {
        Operation op = new Operation(OpType.ADD, key, val);
        Clock clock;
        // 这里需要用CrdtObject来同步：消息到来的线程+CrdtObject的用户操作线程
        synchronized (this) {
            clock = clockLayer.generateNextClock();
            LinkedList<Pair<Clock, String>> temp = new LinkedList<>();
            temp.add(new Pair<>(clock, val));
            existMap.put(key, temp);
        }
        clockLayer.msgOut(new Msg(op, clock));
        latch.countDown();
    }

    /**
     * @return
     *  true: 删除成功
     *  false：删除失败：尝试删除的 key 值不存在
     */
    public boolean remove(String key) {
        // 这里需要用CrdtObject来同步：消息到来的线程+CrdtObject的用户操作线程
        synchronized (this) {
            if (existMap.containsKey(key)) {
                existMap.remove(key);
                Operation op = new Operation(OpType.REMOVE, key);
                Clock clock = clockLayer.generateNextClock();
                clockLayer.msgOut(new Msg(op, clock));
                latch.countDown();
                return true;
            }
        }
        return false;
    }

    public Map<String, LinkedList<String>> query() {
        Map<String, LinkedList<String>> re = new HashMap<>();
        synchronized (this) {
            for (String key : existMap.keySet()) {
                LinkedList<String> temp = new LinkedList<>();
                for (Pair<Clock, String> pair : existMap.get(key)) {
                    temp.add(pair.getV());
                }
                re.put(key, temp);
            }
        }
        return re;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        // 这里需要用CrdtObject来同步：消息到来的线程+CrdtObject的用户操作线程
        synchronized (this) {
            for (String e : existMap.keySet()) {
                sb.append(e).append(":[");
                for (Pair<Clock, String> pair : existMap.get(e)) {
                    if (Config.showClock) {
                        sb.append(pair.getK());
                        if (!pair.getV().isEmpty()) {
                            sb.append("-");
                        }
                    }
                    sb.append(pair.getV()).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("], ");
            }
            if (!existMap.isEmpty()) {
                sb.delete(sb.length() - 2, sb.length());
            }
        }
        sb.append("}");
        return sb.toString();
    }
}

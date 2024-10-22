package cn.xu.pojo.operation;

public enum OpType {
    ADD(0),
    REMOVE(1),
    SET(2);

    OpType(int i) {}

    public static OpType getEnum(int i) {
        for (OpType opType : OpType.values()) {
            if (opType.ordinal() == i) {
                return opType;
            }
        }
        return null;
    }
}

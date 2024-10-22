package cn.xu.pojo.operation;

import cn.xu.config.Config;
import lombok.Data;

@Data
public class Operation {
    OpType opType;
    String[] params;

    public Operation(OpType opType, String param0) {
        String[] params = new String[1];
        params[0] = param0;
        this.opType = opType;
        this.params = params;
    }

    public Operation(OpType opType, String param0, String param1) {
        String[] params = new String[2];
        params[0] = param0;
        params[1] = param1;
        this.opType = opType;
        this.params = params;
    }

    public Operation(OpType opType, String[] params) {
        this.opType = opType;
        this.params = params;
    }

    // 反序列化
    public Operation(String opStr) {
        String[] opStrs = opStr.split(Config.operationSplitter);
        opType = OpType.getEnum(Integer.parseInt(opStrs[0]));
        if (opType == null) {
            throw new RuntimeException("Illegal OpType");
        }
        params = new String[opStrs.length - 1];
        System.arraycopy(opStrs, 1, params, 0, opStrs.length - 1);
    }

    public String serialized() {
        // opType^param0^param1
        StringBuilder result = new StringBuilder(String.valueOf(opType.ordinal()));
        for (int i = 0; i < params.length; i++) {
            result.append(Config.operationSplitter).append(params[i]);
        }
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opType.name());
        for (int i = 0; i < params.length; i++) {
            sb.append(" ").append(params[i]);
        }
        return sb.toString();
    }
}

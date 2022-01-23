package smart.enums;

public enum MsgAction {
        // 处理成功
        ACCEPT,
        // 可以重试的错误
        RETRY,
        // 无需重试的错误
        REJECT;
    }

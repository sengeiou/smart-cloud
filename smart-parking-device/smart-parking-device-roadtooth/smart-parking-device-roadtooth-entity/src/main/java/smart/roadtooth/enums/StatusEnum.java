package smart.roadtooth.enums;

/**
 * 业务消息类型
 */
public enum StatusEnum {

    STATUS_IN(0, "进场"),
    STATUS_OUT(1, "出场"),
    STATUS_RANGE_ALARM(2, "距离异常"),
    STATUS_CLEAR_RANGE_ALARM(3, "解除距离异常"),
    STATUS_MODIFY_LICENSE_PLATE(4, "修改车牌");

    private int code;
    private String message;

    StatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 根据状态code获取枚举名称
     *
     * @return
     */
    public static String getMessageByCode(Integer code) {
        for (StatusEnum status : StatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status.message;
            }
        }
        return null;
    }

    /**
     * 根据状态code获取枚举值
     *
     * @return
     */
    public static StatusEnum getByCode(Integer code) {
        for (StatusEnum status : StatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}

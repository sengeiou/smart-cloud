package smart.geomagnetism.enums;

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
    private String name;

    StatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

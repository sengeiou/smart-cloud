package smart.emnus;


/**
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:56
 */
public enum TimetaskTypes  {

    /**
     * 执行一次
     */
    One(1, "执行一次"),
    /**
     * 重复执行
     */
    Two(2, "重复执行"),
    /**
     * 调度明细
     */
    Three(3, "调度明细"),
    /**
     * 调度任务
     */
    Four(4, "调度任务");

    private int code;
    private String message;

    TimetaskTypes(int code, String message) {
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
}

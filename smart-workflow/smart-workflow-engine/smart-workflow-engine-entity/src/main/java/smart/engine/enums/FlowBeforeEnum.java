package smart.engine.enums;

/**
 * task节点的状态
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
public enum FlowBeforeEnum {
    //没有经过
    Futility("-1", "没有经过"),
    //经过节点
    Pass("0", "经过节点"),
    //当前节点
    Present("1", "当前节点"),
    //未完成
    Undone("2", "未完成");

    private String code;
    private String message;

    FlowBeforeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

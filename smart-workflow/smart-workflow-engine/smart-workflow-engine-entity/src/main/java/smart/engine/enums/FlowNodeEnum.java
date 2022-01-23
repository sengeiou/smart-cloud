package smart.engine.enums;


/**
 * 流程节点状态
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public enum FlowNodeEnum {
    //进行节点
    Process("0", "进行节点"),
    //驳回开始
    Reject("-1", "驳回开始"),
    //无用节点
    Futility("-2", "无用节点");

    private String code;
    private String message;

    FlowNodeEnum(String code, String message) {
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

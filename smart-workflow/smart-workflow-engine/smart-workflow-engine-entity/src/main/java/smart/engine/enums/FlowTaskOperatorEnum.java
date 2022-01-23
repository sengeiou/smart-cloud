package smart.engine.enums;

/**
 * 经办对象
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public enum FlowTaskOperatorEnum {
    //发起者主管
    LaunchCharge("1","发起者主管"),
    //部门经理
    DepartmentCharge("2","部门经理"),
    //发起者本人
    InitiatorMe("3","发起者本人"),
    //指定用户
    AppointUser("4","指定用户"),
    //指定岗位
    AppointPosition("5","指定岗位"),
    //或签
    Fixedapprover("6","或签"),
    //加签
    FreeApprover("7","加签"),
    //会签
    FixedJointlyApprover("8","会签");

    private String code;
    private String message;

    FlowTaskOperatorEnum(String code, String message) {
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

    /**
     * 根据状态code获取枚举名称
     *
     * @return
     */
    public static String getMessageByCode(String code) {
        for (FlowTaskOperatorEnum status : FlowTaskOperatorEnum.values()) {
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
    public static FlowTaskOperatorEnum getByCode(String code) {
        for (FlowTaskOperatorEnum status : FlowTaskOperatorEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}

package smart.engine.model.flowengine.shuntjson.childnode;

import lombok.Data;

import java.util.List;

/**
 * 解析引擎
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:12
 */
@Data
public class Properties {

    /**condition属性**/
    private Boolean isDefault;
    private String priority;
    private List<ProperCond> conditions;

    /**approver属性**/
    private String title;
    /**发起人**/
    private String[] initiator;
    /**发起岗位**/
    private String[] initiatePos;
    /**批准人**/
    private String[] approvers;
    /**批准岗位**/
    private String[] approverPos;
    /**经办对象**/
    private String assigneeType;
    /**字段**/
    private List<FormOperates> formOperates;
    /**传阅人**/
    private String[] circulatePosition;
    /**传阅部门**/
    private String[] circulateUser;
    /**流程进度**/
    private String progress;
    /**驳回步骤 1.上一步骤 0.返回开始**/
    private String rejectStep;
    /**备注**/
    private String description;
    /**节点事件**/
    /**是否开启节点事件**/
    private Boolean hasApproverfunc;
    /**节点的url**/
    private String approverInterfaceUrl;
    /**节点url的类型(POST、GET)**/
    private String approverInterfaceType;
    /**开始事件**/
    /**是否开启开始事件**/
    private Boolean hasInitfunc;
    /**开始的url**/
    private String initInterfaceUrl;
    /**开始url的类型(POST、GET)**/
    private String initInterfaceType;
    /**结束事件**/
    /**是否开启结束事件**/
    private Boolean hasEndfunc;
    /**结束的url**/
    private String endInterfaceUrl;
    /**结束url的类型(POST、GET)**/
    private String endInterfaceType;
    /**节点撤回事件**/
    private Boolean hasRecallFunc;
    private String recallInterfaceUrl;
    /**发起撤回事件**/
    private Boolean hasFlowRecallFunc;
    private String flowRecallInterfaceUrl;
    /**定时器**/
    /**天**/
    private Integer day = 0;
    /**时**/
    private Integer hour = 0;
    /**分**/
    private Integer minute = 0;
    /**秒**/
    private Integer second = 0;

}

package smart.engine.model.flowbefore;

import smart.engine.model.flowengine.shuntjson.childnode.FormOperates;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:18
 */
@Data
public class FlowBeforeInfoVO {
    private Long freeApprover;
    private FlowTaskEntityInfoModel flowTaskInfo;
    private List<FlowTaskNodeEntityInfoModel> flowTaskNodeList;
    private List<FlowTaskOperatorEntityInfoModel> flowTaskOperatorList;
    private List<FlowTaskOperatorRecordEntityInfoModel> flowTaskOperatorRecordList;
    private String flowFormInfo;
    private List<FormOperates> formOperates = new ArrayList<>();
}

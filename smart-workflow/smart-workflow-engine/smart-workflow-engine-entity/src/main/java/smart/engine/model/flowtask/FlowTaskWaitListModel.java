package smart.engine.model.flowtask;

import lombok.Data;

import java.util.Date;

/**
 *
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:17
 */
@Data
public class FlowTaskWaitListModel {

    private String id;
    private String processId;
    private String enCode;
    private String fullName;
    private Integer flowUrgent;
    private String flowId;
    private String flowCode;
    private String flowName;
    private String flowCategory;
    private Date startTime;
    private Date endTime;
    private String thisStep;
    private String thisStepId;
    private Integer status;
    private Integer completion;
    private String creatorUserId;
    private Date creatorTime;
    private String handleId;
    private String lastModifyUserId;
    private Date lastModifyTime;
    private String nodePropertyJson;
}

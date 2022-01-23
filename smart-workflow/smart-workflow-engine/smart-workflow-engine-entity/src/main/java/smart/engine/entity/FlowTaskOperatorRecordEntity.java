package smart.engine.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 流程经办记录
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("flow_taskoperatorrecord")
public class FlowTaskOperatorRecordEntity {
    /**
     * 节点流转主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 节点编码
     */
    @TableField("F_NODECODE")
    private String nodeCode;

    /**
     * 节点名称
     */
    @TableField("F_NODENAME")
    private String nodeName;

    /**
     * 经办状态 0-拒绝、1-同意、2-提交、3-撤回、4-终止
     */
    @TableField("F_HANDLESTATUS")
    private Integer handleStatus;

    /**
     * 经办人员
     */
    @TableField("F_HANDLEID")
    private String handleId;

    /**
     * 经办时间
     */
    @TableField("F_HANDLETIME")
    private Date handleTime;

    /**
     * 经办理由
     */
    @TableField("F_HANDLEOPINION")
    private String handleOpinion;

    /**
     * 经办主键
     */
    @TableField(value="F_TASKOPERATORID",fill = FieldFill.UPDATE)
    private String taskOperatorId;

    /**
     * 节点主键
     */
    @TableField(value="F_TASKNODEID",fill = FieldFill.UPDATE)
    private String taskNodeId;

    /**
     * 任务主键
     */
    @TableField("F_TASKID")
    private String taskId;
}

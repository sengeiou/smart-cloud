package smart.engine.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 流程节点
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("flow_tasknode")
public class FlowTaskNodeEntity {
    /**
     * 节点实例主键
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
     * 节点类型
     */
    @TableField("F_NODETYPE")
    private String nodeType;

    /**
     * 节点属性Json
     */
    @TableField("F_NODEPROPERTYJSON")
    private String nodePropertyJson;

    /**
     * 上一节点 1.上一步骤 0.返回开始
     */
    @TableField("F_NODEUP")
    private String nodeUp;

    /**
     * 下一节点
     */
    @TableField("F_NODENEXT")
    private String nodeNext;

    /**
     * 是否完成
     */
    @TableField("F_COMPLETION")
    private Integer completion;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 创建时间
     */
    @TableField(value = "F_CREATORTIME",fill = FieldFill.INSERT)
    private Date creatorTime;

    /**
     * 任务主键
     */
    @TableField("F_TASKID")
    private String taskId;

    /**
     * 状态 0.新流程 -1.驳回开始
     */
    @TableField("F_State")
    private String state;
}

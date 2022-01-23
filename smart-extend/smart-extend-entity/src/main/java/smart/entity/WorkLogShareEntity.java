package smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * 工作日志分享
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("ext_worklogshare")
public class WorkLogShareEntity  {
    /**
     * 工作日志明细主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 日志主键
     */
    @TableField("F_WORKLOGID")
    private String workLogId;

    /**
     * 共享人员
     */
    @TableField("F_SHAREUSERID")
    private String shareUserId;

    /**
     * 共享时间
     */
    @TableField("F_SHARETIME")
    private Date shareTime;

}

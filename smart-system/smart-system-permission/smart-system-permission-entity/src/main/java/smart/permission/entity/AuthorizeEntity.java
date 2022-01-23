package smart.permission.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 操作权限
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("base_authorize")
public class AuthorizeEntity {
    /**
     * 权限主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 项目类型
     */
    @TableField("F_ITEMTYPE")
    private String itemType;

    /**
     * 项目主键
     */
    @TableField("F_ITEMID")
    private String itemId;

    /**
     * 对象类型
     */
    @TableField("F_OBJECTTYPE")
    private String objectType;

    /**
     * 对象主键
     */
    @TableField("F_OBJECTID")
    private String objectId;

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
     * 创建用户
     */
    @TableField(value = "F_CREATORUSERID",fill = FieldFill.INSERT)
    private String creatorUserId;
}

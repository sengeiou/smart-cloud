package smart.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 数据连接
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("base_dblink")
public class DbLinkEntity {
    /**
     * 连接主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 连接名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 连接驱动
     */
    @TableField("F_DBTYPE")
    private String dbType;

    /**
     * 主机名称
     */
    @TableField("F_HOST")
    private String host;

    /**
     * 端口
     */
    @TableField("F_PORT")
    private String port;

    /**
     * 用户
     */
    @TableField("F_USERNAME")
    private String userName;

    /**
     * 密码
     */
    @TableField("F_PASSWORD")
    private String password;

    /**
     * 服务名称
     */
    @TableField("F_SERVICENAME")
    private String serviceName;

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
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

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

    /**
     * 修改时间
     */
    @TableField(value = "F_LASTMODIFYTIME",fill = FieldFill.UPDATE)
    private Date lastModifyTime;

    /**
     * 修改用户
     */
    @TableField(value = "F_LASTMODIFYUSERID",fill = FieldFill.UPDATE)
    private String lastModifyUserId;

    /**
     * 删除标志
     */
    @TableField("F_DELETEMARK")
    private Integer deleteMark;

    /**
     * 删除时间
     */
    @TableField("F_DELETETIME")
    private Date deleteTime;

    /**
     * 删除用户
     */
    @TableField("F_DELETEUSERID")
    private String deleteUserId;
}

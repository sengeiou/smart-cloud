package smart.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 租户信息
 *
 * @author SmartCloud项目开发组
 * @version V1.2.191207
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("base_tenant")
public class TenantEntity {
    /**
     * 租户主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 姓名
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 密码
     */
    @TableField("F_PASSWORD")
    private String password;

    /**
     * 公司
     */
    @TableField("F_COMPANYNAME")
    private String companyName;

    /**
     * 过期时间
     */
    @TableField("F_EXPIRESTIME")
    private String expiresTime;

    /**
     * 连接驱动
     */
    @TableField("F_DBTYPE")
    private String dbType;

    /**
     * 主机地址
     */
    @TableField("F_DBHOST")
    private String dbHost;

    /**
     * 端口
     */
    @TableField("F_DBPORT")
    private String dbPort;

    /**
     * 用户
     */
    @TableField("F_DBUSERNAME")
    private String dbUserName;

    /**
     * 密码
     */
    @TableField("F_DBPASSWORD")
    private String dbPassword;

    /**
     * 服务名
     */
    @TableField("F_DBSERVICENAME")
    private String dbServiceName;

    /**
     * ip
     */
    @TableField("F_IPADDRESS")
    private String ipAddress;

    /**
     * ip城市
     */
    @TableField("F_IPADDRESSNAME")
    private String ipAddressName;

    /**
     * 来源
     */
    @TableField("F_SOURCEWEBSITE")
    private String sourceWebsite;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序
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

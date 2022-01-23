package smart;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * baseTenant
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Data
@TableName("base_tenant")
public class BaseTenantEntity  {

    @TableId("F_ID")
    private String id;

    @TableField("F_ENCODE")
    private String enCode;

    @TableField("F_FULLNAME")
    private String fullName;

    @TableField("F_COMPANYNAME")
    private String comPanyName;

    @TableField("F_EXPIRESTIME")
    private Date expiresTime;

    @TableField("F_DBNAME")
    private String dbserviceName;

    @TableField("F_IPADDRESS")
    private String iPAddress;

    @TableField("F_IPADDRESSNAME")
    private String iPAddressName;

    @TableField("F_SOURCEWEBSITE")
    private String sourceWebsite;

    @TableField("F_DESCRIPTION")
    private String descriPtion;

    @TableField("F_SORTCODE")
    private String sortcode;

    @TableField("F_ENABLEDMARK")
    private String enabledMark;

    @TableField("F_CREATORTIME")
    private Date creatorTime;

    @TableField("F_CREATORUSERID")
    private String creatorUserId;

    @TableField("F_LASTMODIFYTIME")
    private Date lastModifyTime;

    @TableField("F_LASTMODIFYUSERID")
    private String lastmodifyUserId;

    @TableField("F_DELETEMARK")
    private String deleteMark;

    @TableField("F_DELETETIME")
    private Date deleteTime;

    @TableField("F_DELETEUSERID")
    private String deleteUserid;
}

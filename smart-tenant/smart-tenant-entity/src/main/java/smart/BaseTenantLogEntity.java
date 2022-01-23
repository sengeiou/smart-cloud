package smart;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * baseTenantlog
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Data
@TableName("base_tenantlog")
public class BaseTenantLogEntity {


    @TableId("F_ID")
    private String id;

    @TableField("F_TENANTID")
    private String tenantId;

    @TableField("F_LOGINACCOUNT")
    private String loginAccount;

    @TableField("F_LOGINIPADDRESS")
    private String loginIpaddress;

    @TableField("F_LOGINIPADDRESSNAME")
    private String loginIpaddressName;

    @TableField("F_LOGINSOURCEWEBSITE")
    private String loginSourceBebsite;

    @TableField("F_LOGINTIME")
    private Date loginTime;

    @TableField("F_DESCRIPTION")
    private String descriPtion;
}

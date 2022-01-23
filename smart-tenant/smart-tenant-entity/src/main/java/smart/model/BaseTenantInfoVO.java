package smart.model;


import lombok.Data;

/**
 *
 * BaseTenant模型
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Data
public class BaseTenantInfoVO {
    private String id;

    private String enCode;

    private String fullName;

    private String companyName;

    private Long expiresTime;

    private String description;
}

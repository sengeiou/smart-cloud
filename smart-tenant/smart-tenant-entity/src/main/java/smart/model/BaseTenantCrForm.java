package smart.model;

import lombok.Data;


/**
 *
 * BaseTenant模型
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Data
public class BaseTenantCrForm  {
    private String enCode;

    private String fullName;

    private String companyName;

    private long expiresTime;

    private String description;

}

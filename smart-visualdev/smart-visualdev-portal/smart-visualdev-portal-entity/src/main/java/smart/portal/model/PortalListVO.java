package smart.portal.model;

import lombok.Data;

/**
 *
 *
 * @version V3.0.0
 * @copyright 智慧停车公司
 * @author 管理员/admin
 * @date 2020-10-21 14:23:30
 */
@Data
public class PortalListVO{
    private String id;
    private String fullName;
    private String enCode;
    private String description;
    private long creatorTime;
    private String creatorUser;
    private String category;
    private long lastmodifytime;
    private String lastmodifyuser;
    private Integer enabledMark;
    private Long sortCode;
}

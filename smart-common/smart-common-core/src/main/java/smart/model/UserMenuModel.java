package smart.model;

import smart.util.treeutil.SumTree;
import lombok.Data;

/**
 * 用户DTO
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
public class UserMenuModel extends SumTree {
    private String id;
    private String fullName;
    private Integer isButtonAuthorize;
    private Integer isColumnAuthorize;
    private Integer isDataAuthorize;
    private String enCode;
    private String parentId;
    private String icon;
    private String urlAddress;
    private String linkTarget;
    private Integer type;
    private Integer enabledMark;
    private Long sortCode;
    private String category;
    private String description;
    private String propertyJson;
}

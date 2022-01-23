package smart.util.type;

import lombok.Data;

/**
 * 权限类型常量表
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Data
public class AuthorizeType {
    /**
     * 用户权限
     */
    public static final String USER = "User";
    /**
     * 岗位权限
     */
    public static final String POSITION = "Position";
    /**
     * 角色权限
     */
    public static final String ROLE = "Role";
    /**
     * 按钮权限
     */
    public static final String BUTTON = "button";
    /**
     * 菜单权限
     */
    public static final String MODULE = "module";
    /**
     * 列表权限
     */
    public static final String COLUMN = "column";
    /**
     * 数据权限
     */
    public static final String RESOURCE = "resource";
}

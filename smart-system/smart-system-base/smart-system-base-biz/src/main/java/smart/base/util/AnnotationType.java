package smart.base.util;

import lombok.Data;

/**
 * 数据接口支持注解类型
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
public class AnnotationType {
    /**
     * USER 当前登陆者id
     */
    public static final String USER = "@user";
    /**
     * 当前登陆者部门id
     */
    public static final String DEPARTMENT = "@department";
    /**
     * 当前登陆者组织id
     */
    public static final String ORGANIZE = "@organize";
    /**
     * 当前登录者岗位id
     */
    public static final String POSTION = "@postion";

}

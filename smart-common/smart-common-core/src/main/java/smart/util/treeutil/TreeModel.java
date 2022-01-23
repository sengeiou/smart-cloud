package smart.util.treeutil;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 需要实现树的类可以继承该类，手写set方法，在设定本身属性值时同时设置该类中的相关属性
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:56
 */
@Data
public class TreeModel<T> {
    private String id;
    private String fullName;
    private String parentId;
    private Boolean hasChildren = true;
    private String icon;
    private List<TreeModel<T>> children = new ArrayList<>();
}

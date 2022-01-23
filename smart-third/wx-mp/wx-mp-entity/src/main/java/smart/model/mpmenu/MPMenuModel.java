package smart.model.mpmenu;

import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class MPMenuModel extends SumTree {
    private String parentId;
    //菜单Key
    private String key;
    //菜单名称
    private String fullName;
    //菜单类型
    private String type;
    //页面地址
    private String url;
    //文字内容
    private String content;
    //排序码
    private Long sortCode;
}

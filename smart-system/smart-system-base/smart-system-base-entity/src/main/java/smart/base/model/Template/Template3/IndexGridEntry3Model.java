package smart.base.model.Template.Template3;

import lombok.Data;

import java.util.List;

/**
 * 子表列表
 */
@Data
public class IndexGridEntry3Model {
    //标题
    private String title;
    //表名
    private String table;
    //类的名称（添加的字段）
    private String className;
    //查询的主键(添加的字段)
    private DbTableRelation3Model dbTableRelation;
    // 列表字段
    private List<IndexGridField3Model> gridTableFieldList;
}

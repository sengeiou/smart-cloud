package smart.base.model.Template.Template3;

import lombok.Data;

/**
 * 列表字段
 */
@Data
public class IndexGridField3Model {
    //字段
    private String field;
    //列名
    private String colName;
    //对齐
    private String align;
    //宽度
    private String width;
    //隐藏
    private int isHide;
    //搜索
    private int allowSearch;
    //格式化项目：dictionary、department、position、user、userAccount、userName、dateTime、date、time、switchs
    private String formatItem;
    //格式化内容：dictionaryValue
    private String formatValue;
}

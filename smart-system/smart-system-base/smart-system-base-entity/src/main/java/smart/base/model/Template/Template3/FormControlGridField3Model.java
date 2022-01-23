package smart.base.model.Template.Template3;

import lombok.Data;

/**
 * 编辑表格字段
 */
@Data
public class FormControlGridField3Model {
    //列名
    private String colName;
    //字段
    private String field;
    //控件：input、select、date、checkbox、label
    private String control;
    //对齐
    private String align;
    //宽度
    private String width;
}

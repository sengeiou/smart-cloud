package smart.base.model.Template6;


import lombok.Data;

/**
 * 列表字段
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16
 */
@Data
public class ColumnListField {
    /**
     * 字段
     */
    private String prop;
    /**
     * 列名
     */
    private String label;
    /**
     * 对齐
     */
    private String align;
    /**
     * 宽度
     */
    private String width;

}

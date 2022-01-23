package smart.engine.model.flowdynamic;

import lombok.Data;

import java.util.List;

/**
 * 解析引擎
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:19
 */
@Data
public class FormColumnTableModel {

    /**json原始名称**/
    private String tableModel;
    /**表名称**/
    private String tableName;
    /**标题**/
    private String label;
    /**宽度**/
    private Integer span;
    /**是否显示标题**/
    private boolean showTitle;
    /**子表的属性**/
    private List<FormColumnModel> childList;
    /**app子表属性**/
    private String fieLdsModel;
}

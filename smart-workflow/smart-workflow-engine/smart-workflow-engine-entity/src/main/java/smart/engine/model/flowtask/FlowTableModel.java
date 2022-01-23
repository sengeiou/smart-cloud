package smart.engine.model.flowtask;

import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:17
 */
@Data
public class FlowTableModel {

    private String relationField;
    private String relationTable;
    private String table;
    private String tableName;
    private String tableField;
    private String typeId;
    private List<FlowFieldsModel> fields;

}

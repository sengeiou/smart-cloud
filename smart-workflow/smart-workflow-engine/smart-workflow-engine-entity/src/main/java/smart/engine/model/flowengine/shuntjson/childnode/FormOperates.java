package smart.engine.model.flowengine.shuntjson.childnode;

import lombok.Data;

/**
 * 解析引擎
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:12
 */
@Data
public class FormOperates {
    /**可读**/
    private boolean read;
    /**名称**/
    private String name;
    /**字段**/
    private String id;
    /**可写**/
    private boolean write;
}

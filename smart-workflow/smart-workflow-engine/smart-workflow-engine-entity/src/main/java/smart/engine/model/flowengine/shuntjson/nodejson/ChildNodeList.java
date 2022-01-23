package smart.engine.model.flowengine.shuntjson.nodejson;

import smart.engine.model.flowengine.shuntjson.childnode.ChildNode;
import smart.engine.model.flowengine.shuntjson.childnode.Properties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析引擎
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:12
 */
@Data
public class ChildNodeList {
    /**节点属性**/
    private Properties properties;
    /**自定义属性**/
    private Custom custom;
    /**流程节点id**/
    private String taskNodeId;
    /**流程任务id**/
    private String taskId;
    /**下一级定时器属性**/
    private DateProperties timer;
    /**定时器所有**/
    private List<DateProperties> timerAll = new ArrayList<>();
    /**分流合流**/
    private String conditionType;
    private List<ChildNode> flowList;
}

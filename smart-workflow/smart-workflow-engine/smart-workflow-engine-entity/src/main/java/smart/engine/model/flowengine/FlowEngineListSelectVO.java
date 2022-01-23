package smart.engine.model.flowengine;

import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:17
 */
@Data
public class FlowEngineListSelectVO {

    private String id;
    private String fullName;
    private Boolean hasChildren;
    private List<FlowEngineListSelectVO> children;
}

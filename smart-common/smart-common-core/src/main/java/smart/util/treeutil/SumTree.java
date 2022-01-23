package smart.util.treeutil;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:51
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SumTree<T> {
    private String id;
    private String parentId;
    private Boolean hasChildren;
    private List<SumTree<T>> children;
}

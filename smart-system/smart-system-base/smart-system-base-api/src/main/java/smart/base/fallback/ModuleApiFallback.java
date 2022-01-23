package smart.base.fallback;

import smart.base.ModuleApi;
import smart.base.entity.ModuleEntity;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * 调用系统菜单Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class ModuleApiFallback implements ModuleApi {
    @Override
    public List<ModuleEntity> getList() {
        return null;
    }
}

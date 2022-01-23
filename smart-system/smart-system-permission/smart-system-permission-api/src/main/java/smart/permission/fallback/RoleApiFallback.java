package smart.permission.fallback;

import smart.base.ActionResult;
import smart.permission.RoleApi;
import smart.permission.entity.RoleEntity;
import org.springframework.stereotype.Component;

/**
 * 获取角色信息Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class RoleApiFallback implements RoleApi {
    @Override
    public ActionResult<RoleEntity> getInfoByRole(String roleId) {
        return null;
    }
}

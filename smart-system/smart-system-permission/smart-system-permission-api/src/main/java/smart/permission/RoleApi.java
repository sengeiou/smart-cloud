package smart.permission;

import smart.permission.fallback.RoleApiFallback;
import smart.permission.entity.RoleEntity;
import smart.utils.FeignName;
import smart.base.ActionResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 获取角色信息Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME , fallback = RoleApiFallback.class, path = "/Permission/Role")
public interface RoleApi {
    /**
     * 通过account返回角色实体
     *
     * @param roleId
     * @return
     */
    @GetMapping("/getInfo/{roleId}")
    ActionResult<RoleEntity> getInfoByRole(@PathVariable("roleId") String roleId);
}

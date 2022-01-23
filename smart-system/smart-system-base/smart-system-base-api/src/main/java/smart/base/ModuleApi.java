package smart.base;

import smart.base.entity.ModuleEntity;
import smart.base.fallback.ModuleApiFallback;
import smart.utils.FeignName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
/**
 * 调用系统菜单Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME , fallback = ModuleApiFallback.class, path = "/Base/Menu")
public interface ModuleApi {

    /**
     * 列表
     *
     * @return
     */
    @GetMapping("/getList")
    List<ModuleEntity> getList();

}

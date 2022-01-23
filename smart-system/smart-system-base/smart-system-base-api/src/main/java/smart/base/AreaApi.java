package smart.base;

import smart.base.entity.ProvinceEntity;
import smart.base.fallback.AreaApiFallback;
import smart.base.ActionResult;
import smart.utils.FeignName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 获取行政区划Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME , fallback = AreaApiFallback.class, path = "/Base/Area")
public interface AreaApi {
    /**
     * 获取行政区划列表
     * @param id
     * @return
     */
    @GetMapping("/getList/{id}")
    ActionResult<List<ProvinceEntity>> getList(@PathVariable("id") String id);
}

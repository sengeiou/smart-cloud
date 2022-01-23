package smart.base;

import smart.utils.FeignName;
import smart.base.fallback.DataInterFaceApiFallback;
import smart.base.ActionResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * 调用数据接口Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME , fallback = DataInterFaceApiFallback.class, path = "/Base/DataInterface")
public interface DataInterFaceApi {

    /**
     * 访问接口
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/Actions/Response")
    ActionResult infoToId(@PathVariable("id") String id);

}

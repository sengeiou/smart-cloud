package smart.base;

import smart.base.fallback.DeviceApiFallback;
import smart.model.device.DeviceInfoVO;
import smart.utils.FeignName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 获取设备信息Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME , fallback = DeviceApiFallback.class, path = "/Device")
public interface DeviceApi {
    /**
     * 获取行政区划列表
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    ActionResult<DeviceInfoVO> getInfo(@PathVariable("id") String id);
}

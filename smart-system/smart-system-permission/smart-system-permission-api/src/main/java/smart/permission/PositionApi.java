package smart.permission;

import smart.permission.fallback.PositionApiFallback;
import smart.permission.entity.PositionEntity;
import smart.permission.model.position.PositionInfoVO;
import smart.utils.FeignName;
import smart.base.ActionResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 获取岗位信息Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME , fallback = PositionApiFallback.class, path = "/Permission/Position")
public interface PositionApi {

    /**
     * 获取所有岗位
     * @return
     */
    @GetMapping("/getListAll")
    ActionResult<List<PositionEntity>> getListAll();

    /**
     * 获取岗位信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    ActionResult<PositionInfoVO> getInfo(@PathVariable("id") String id);

}

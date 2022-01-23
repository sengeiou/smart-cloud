package smart.permission;

import smart.permission.entity.UserRelationEntity;
import smart.permission.fallback.UserRelationApiFallback;
import smart.utils.FeignName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 获取用户关系Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME , fallback = UserRelationApiFallback.class, path = "/Permission/UserRelation")
public interface UserRelationApi {

    /**
     * 获取岗位
     *
     * @return
     */
    @GetMapping("/getList/{userId}")
    List<UserRelationEntity> getList(@PathVariable("userId") String userId);

    /**
     * 获取岗位
     *
     * @return
     */
    @GetMapping("/getObjectList/{objectId}")
    List<UserRelationEntity> getObjectList(@PathVariable("objectId") String objectId);
}

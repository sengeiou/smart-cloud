package smart.permission.fallback;

import smart.permission.UserRelationApi;
import smart.permission.entity.UserRelationEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 获取用户关系Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class UserRelationApiFallback implements UserRelationApi {
    @Override
    public List<UserRelationEntity> getList(String userId) {
        return null;
    }

    @Override
    public List<UserRelationEntity> getObjectList(String objectId) {
        return null;
    }



}

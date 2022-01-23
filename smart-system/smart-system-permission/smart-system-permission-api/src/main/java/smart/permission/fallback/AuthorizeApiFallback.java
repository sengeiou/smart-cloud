package smart.permission.fallback;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import smart.permission.AuthorizeApi;
import smart.permission.entity.AuthorizeEntity;
import smart.permission.model.authorize.AuthorizeVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 获取权限信息Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class AuthorizeApiFallback implements AuthorizeApi {
    @Override
    public AuthorizeVO getEntity(boolean isCache) {
        return null;
    }

    @Override
    public List<AuthorizeEntity> getListByObjectId(String objectId) {
        return null;
    }

    @Override
    public void remove(QueryWrapper<AuthorizeEntity> queryWrapper) {

    }
}

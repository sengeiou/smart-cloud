package smart.permission.fallback;

import smart.permission.OrganizeApi;
import smart.permission.entity.OrganizeEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 获取组织信息Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class OrganizeApiFallback implements OrganizeApi {
    @Override
    public OrganizeEntity getById(String organizeId) {
        return null;
    }

    @Override
    public List<OrganizeEntity> getList() {
        return null;
    }
}

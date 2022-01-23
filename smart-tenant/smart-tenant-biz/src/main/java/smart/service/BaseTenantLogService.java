package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.BaseTenantLogEntity;

/**
 * baseTenantlog
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
public interface BaseTenantLogService extends IService<BaseTenantLogEntity> {

    /**
     * 获取信息
     * @param id
     * @return
     */
    BaseTenantLogEntity getInfo(String id);

    /**
     * 删除
     * @param entity
     */
    void delete(BaseTenantLogEntity entity);

    /**
     * 新建
     * @param entity
     */
    void create(BaseTenantLogEntity entity);

    /**
     * 修改
     * @param id
     * @param entity
     * @return
     */
    boolean update(String id, BaseTenantLogEntity entity);

    void logAsync(String id, String encode);
}

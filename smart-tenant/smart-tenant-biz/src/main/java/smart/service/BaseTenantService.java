package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.BaseTenantEntity;
import smart.model.BaseTenantDeForm;
import smart.model.BaseTenantPage;

import java.util.List;

/**
 * baseTenant
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
public interface BaseTenantService extends IService<BaseTenantEntity> {

    /**
     * 获取列表
     * @param baseTenantPage
     * @return
     */
    List<BaseTenantEntity> getList(BaseTenantPage baseTenantPage);

    /**
     * 获取信息
     * @param id
     * @return
     */
    BaseTenantEntity getInfo(String id);

    /**
     * 获取编码
     * @param id
     * @return
     */
    BaseTenantEntity getEnCode(String id);

    /**
     * 删除
     * @param entity
     * @param deForm
     */
    void delete(BaseTenantEntity entity, BaseTenantDeForm deForm);

    /**
     * 创建
     * @param entity
     */
    void create(BaseTenantEntity entity);

    /**
     * 修改
     * @param id
     * @param entity
     * @return
     */
    boolean update( String id, BaseTenantEntity entity);

    /**
     * 创建数据库
     * @param dbName
     */
    void createDb(String dbName);

}

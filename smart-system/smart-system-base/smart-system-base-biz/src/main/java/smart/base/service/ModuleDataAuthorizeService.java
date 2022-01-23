package smart.base.service;


import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.entity.ModuleDataAuthorizeEntity;

import java.util.List;

/**
 * 数据权限配置
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface ModuleDataAuthorizeService extends IService<ModuleDataAuthorizeEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<ModuleDataAuthorizeEntity> getList();

    /**
     * 列表
     *
     * @param moduleId 功能主键
     * @return
     */
    List<ModuleDataAuthorizeEntity> getList(String moduleId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    ModuleDataAuthorizeEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(ModuleDataAuthorizeEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, ModuleDataAuthorizeEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(ModuleDataAuthorizeEntity entity);

    /**
     * 上移
     *
     * @param id 主键值
     * @return
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     * @return
     */
    boolean next(String id);
}

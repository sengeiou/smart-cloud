package smart.base.service;


import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.Pagination;
import smart.base.entity.ModuleButtonEntity;

import java.util.List;

/**
 * 按钮权限
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface ModuleButtonService extends IService<ModuleButtonEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<ModuleButtonEntity> getList();

    /**
     * 列表
     *
     * @param moduleId 功能主键
     * @return
     */
    List<ModuleButtonEntity> getList(String moduleId);

    /**
     * 列表(带关键字的)
     *
     * @param moduleId 功能主键
     * @param pagination
     * @return
     */
    List<ModuleButtonEntity> getList(String moduleId, Pagination pagination);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    ModuleButtonEntity getInfo(String id);

    /**
     * 验证名称
     *
     * @param moduleId 功能主键
     * @param fullName 名称
     * @param id       主键值
     * @return
     */
    boolean isExistByFullName(String moduleId, String fullName, String id);

    /**
     * 验证编码
     *
     * @param moduleId 功能主键
     * @param enCode   编码
     * @param id       主键值
     * @return
     */
    boolean isExistByEnCode(String moduleId, String enCode, String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(ModuleButtonEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, ModuleButtonEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(ModuleButtonEntity entity);
}

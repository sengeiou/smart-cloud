package smart.base.service;


import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.Pagination;
import smart.base.entity.ModuleColumnEntity;

import java.util.List;

/**
 * 列表权限
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */

public interface ModuleColumnService extends IService<ModuleColumnEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<ModuleColumnEntity> getList();

    /**
     * 列表
     *
     * @param moduleId 功能主键
     * @param pagination
     * @return
     */
    List<ModuleColumnEntity> getList(String moduleId, Pagination pagination);

    /**
     * 列表
     * @param bindTable 绑定表格Id
     * @return
     */
    List<ModuleColumnEntity> getListByBindTable(String bindTable);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    ModuleColumnEntity getInfo(String id);

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
    void create(ModuleColumnEntity entity);

    /**
     * 创建
     * @param entitys 实体对象
     */
    void create(List<ModuleColumnEntity> entitys);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, ModuleColumnEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(ModuleColumnEntity entity);

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

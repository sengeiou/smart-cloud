package smart.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.Page;
import smart.permission.entity.RoleEntity;

import java.util.List;

/**
 * 系统角色
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface RoleService extends IService<RoleEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<RoleEntity> getList();

    /**
     * 列表
     *
     * @param page 条件
     * @return
     */
    List<RoleEntity> getList(Page page);

    /**
     * 列表
     *
     * @param userId 用户主键
     * @return
     */
    List<RoleEntity> getListByUserId(String userId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    RoleEntity getInfo(String id);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return
     */
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(RoleEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, RoleEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(RoleEntity entity);


    /**
     * 获取名称
     * @return
     */
    List<RoleEntity> getRoleName(List<String> id);
}

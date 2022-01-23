package smart.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.permission.entity.OrganizeEntity;

import java.util.List;

/**
 * 组织机构
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface OrganizeService extends IService<OrganizeEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<OrganizeEntity> getList();

    /**
     * 获取redis存储的部门信息
     *
     * @return
     */
     List<OrganizeEntity> getOrgRedisList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    OrganizeEntity getInfo(String id);

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
    void create(OrganizeEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, OrganizeEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(OrganizeEntity entity);

    /**
     * 上移
     *
     * @param id 主键值
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     * @return
     */
    boolean next(String id);

    /**
     * 判断是否允许删除
     *
     * @param id 主键值
     * @return
     */
    boolean allowdelete(String id);

    /**
     * 获取名称
     * @param id
     * @return
     */
    List<OrganizeEntity> getOrganizeName(List<String> id);

}

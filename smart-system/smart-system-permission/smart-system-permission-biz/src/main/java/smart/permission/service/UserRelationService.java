package smart.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.permission.entity.UserRelationEntity;

import java.util.List;

/**
 * 用户关系
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface UserRelationService extends IService<UserRelationEntity> {

    /**
     * 根据用户主键获取列表
     *
     * @param userId 用户主键
     * @return
     */
    List<UserRelationEntity> getListByUserId(String userId);

    /**
     * 根据对象主键获取列表
     *
     * @param objectId 对象主键
     * @return
     */
    List<UserRelationEntity> getListByObjectId(String objectId);

    /**
     * 根据对象主键获取列表
     *
     * @param objectId 对象主键
     * @return
     */
    List<UserRelationEntity> getListByObjectIdAll(List<String> objectId);

    /**
     * 根据对象主键删除数据
     *
     * @param objectId 对象主键
     * @return
     */
    void deleteListByObjectId(String objectId);

    /**
     * 根据对象主键和用户删除数据
     *
     * @param objectType 对象类型
     * @return
     */
    void deleteListByObjTypeAndUserId(String objectType,String userId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    UserRelationEntity getInfo(String id);

    /**
     * 创建
     *
     * @param objectId 对象主键
     * @param entitys  实体对象
     */
    void save(String objectId, List<UserRelationEntity> entitys);

    /**
     * 删除
     *
     * @param ids 主键值
     */
    void delete(String[] ids);
}

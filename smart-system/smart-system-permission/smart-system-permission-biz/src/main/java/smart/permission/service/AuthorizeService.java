package smart.permission.service;


import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.UserInfo;
import smart.permission.entity.AuthorizeEntity;
import smart.permission.model.authorize.AuthorizeVO;
import smart.permission.model.authorize.SaveAuthForm;
import smart.permission.model.authorize.SaveBatchForm;

import java.util.List;

/**
 * 操作权限
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface AuthorizeService extends IService<AuthorizeEntity> {

    /**
     * 获取权限（菜单、按钮、列表）
     *
     * @param isAdmin 是否管理员
     * @param userId  用户主键
     * @return
     */
    AuthorizeVO getAuthorize(boolean isAdmin, String userId);

    /**
     * 获取权限（菜单、按钮、列表）
     *
     * @param isCache 是否存在redis
     * @return
     */
    AuthorizeVO getAuthorize(boolean isCache);

    /**
     * 创建
     *
     * @param objectId      对象主键
     * @param authorizeList 实体对象
     */
    void save(String objectId, List<AuthorizeEntity> authorizeList);

    /**
     * 创建
     *
     * @param saveBatchForm    对象主键
     */
    void saveBatch(SaveBatchForm saveBatchForm);

    /**
     * 根据用户id获取列表
     *
     * @param isAdmin 是否管理员
     * @param userId  用户主键
     * @return
     */
    List<AuthorizeEntity> getListByUserId(boolean isAdmin, String userId);

    /**
     * 根据对象Id获取列表
     *
     * @param objectId 对象主键
     * @return
     */
    List<AuthorizeEntity> getListByObjectId(String objectId);

    /**
     * 根据对象Id获取列表
     *
     * @param objectType 对象主键
     * @return
     */
    List<AuthorizeEntity> getListByObjectAndItem(String itemId, String objectType);

    List<AuthorizeEntity> getListByItemId(String itemId);

    void saveAuth(String itemId, SaveAuthForm saveAuthForm );

    Object getCondition(Object obj, UserInfo userInfo, String moduleId);

    String getConditionSql(UserInfo userInfo, String moduleId);
}

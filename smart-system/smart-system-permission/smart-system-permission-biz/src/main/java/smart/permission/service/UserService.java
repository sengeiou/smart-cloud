package smart.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.Pagination;
import smart.base.UserInfo;
import smart.exception.LoginException;
import smart.permission.entity.UserEntity;
import smart.permission.model.user.UserAllModel;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 用户信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface UserService extends IService<UserEntity> {

    /**
     * 列表
     *
     * @param pagination 条件
     * @return
     */
    List<UserEntity> getList(Pagination pagination, String organizeId);

    /**
     * 列表
     *
     * @return
     */
    List<UserEntity> getList();

    /**
     * 列表
     *
     * @param positionId 岗位Id
     * @return
     */
    List<UserEntity> getListByPositionId(String positionId);

    /**
     * 列表
     *
     * @param managerId 主管Id
     * @return
     */
    List<UserEntity> getListByManagerId(String managerId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    UserEntity getInfo(String id);


    /**
     * 信息(返回:名称+账户)
     * @param id
     * @return
     */
    String getUserName(String id);

    /**
     * 验证账户
     *
     * @param account 账户
     * @return
     */
    boolean isExistByAccount(String account);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(UserEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, UserEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(UserEntity entity);

    /**
     * 修改密码
     *
     * @param entity 实体对象
     */
    void updatePassword(UserEntity entity);

    /**
     * 设置常用菜单
     *
     * @param id     主键值
     * @param menuId 菜单主键
     */
    void settingMenu(String id, String menuId);

    /**
     * 查询用户名称
     * @param id 主键值
     * @return
     */
    List<UserEntity> getUserName(List<String> id);

    /**
     * 获取所有用户信息
     * @return
     */
    List<UserAllModel> getAll();
    /**
     * 从数据库获取所有用户信息
     * @return
     */
    List<UserAllModel> getDbUserAll();

    /**
     * 通过account返回user实体
     *
     * @param account 账户
     * @return
     */
    UserEntity checkLogin(String account);

    /**
     * 判断当前账号能否登陆
     * @param account
     * @param password
     * @return
     * @throws LoginException
     */
    UserEntity isExistUser(String account, String password) throws LoginException;

    /**
     * 获取UserInfo
     * @param userInfo
     * @param userEntity
     * @return
     */
    UserInfo userInfo(UserInfo userInfo, UserEntity userEntity);

}

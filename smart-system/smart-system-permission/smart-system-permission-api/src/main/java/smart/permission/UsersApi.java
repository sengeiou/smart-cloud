package smart.permission;

import smart.exception.LoginException;
import smart.permission.entity.UserEntity;
import smart.permission.fallback.UsersApiFallback;
import smart.permission.model.user.UserAllModel;
import smart.permission.model.user.UserInfoVO;
import smart.utils.FeignName;
import smart.base.ActionResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 获取用户信息Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME, fallback = UsersApiFallback.class, path = "/Permission/Users")
public interface UsersApi {
    /**
     * 获取所有用户
     *
     * @return
     */
    @GetMapping("/modelAll")
    ActionResult<List<UserAllModel>> getAll();


    /**
     * 通过account获取userInfo
     *
     * @param account
     * @return
     */
    @GetMapping("/checkUser/{account}/{dbId}/{dbName}")
    UserEntity checkUser(@PathVariable("account") String account, @PathVariable("dbId") String dbId, @PathVariable("dbName") String dbName);

    /**
     * 获取下属
     *
     * @param userId
     * @return
     */
    @GetMapping("/getListByManagerId/{userId}")
    List<UserEntity> getListByManagerId(@PathVariable("userId") String userId);

    /**
     * 获取用户信息
     *
     * @param id 主键值
     * @return
     */
    @GetMapping("/{id}")
    ActionResult<UserInfoVO> getInfo(@PathVariable("id") String id);

    /**
     * 获取岗位用户
     *
     * @param userId
     * @return
     */
    @GetMapping("/getListByPositionId/{userId}")
    List<UserEntity> getListByPositionId(@PathVariable("userId") String userId);

    /**
     * 列表
     *
     * @return
     */
    @GetMapping("/getUserList")
    List<UserEntity> getUserList();

    /**
     * 直接从数据库获取所有用户信息（不过滤冻结账号）
     *
     * @return
     */
    @GetMapping("/getDbUserAll")
    List<UserAllModel> getDbUserAll();

    /**
     * 信息
     *
     * @param userId 主键值
     * @return
     */
    @GetMapping("/getInfoById/{userId}")
    UserEntity getInfoById(@PathVariable("userId") String userId);

    /**
     * 通过id修改
     *
     * @param userEntity
     */
    @GetMapping("/updateById")
    void updateById(UserEntity userEntity);

    /**
     * 验证账号是否可以使用
     *
     * @param account
     * @param password
     * @return
     * @throws LoginException
     */
    @GetMapping("/isExistUser/{account}/{password}/{tenantId}/{dbName}")
    UserEntity isExistUser(@PathVariable("account") String account, @PathVariable("password") String password, @PathVariable("tenantId") String tenantId, @PathVariable("dbName") String dbName) throws LoginException;

}

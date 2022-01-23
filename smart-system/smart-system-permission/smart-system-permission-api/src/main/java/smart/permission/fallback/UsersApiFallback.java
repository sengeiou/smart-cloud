package smart.permission.fallback;

import smart.exception.LoginException;
import smart.permission.UsersApi;
import smart.base.ActionResult;
import smart.permission.entity.UserEntity;
import smart.permission.model.user.UserAllModel;
import smart.permission.model.user.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 获取用户信息Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
@Slf4j
public class UsersApiFallback implements UsersApi {
    @Override
    public ActionResult<List<UserAllModel>> getAll() {
        return null;
    }

    @Override
    public UserEntity checkUser(String account, String dbId, String dbName) {
        log.error("用户信息获取失败");
        return null;
    }

    @Override
    public List<UserEntity> getListByManagerId(String userId) {
        return null;
    }

    @Override
    public ActionResult<UserInfoVO> getInfo(String id) {
        return null;
    }

    @Override
    public List<UserEntity> getListByPositionId(String userId) {
        return null;
    }

    @Override
    public List<UserEntity> getUserList() {
        return null;
    }

    @Override
    public List<UserAllModel> getDbUserAll() {
        return null;
    }

    @Override
    public UserEntity getInfoById(String id) {
        return null;
    }

    @Override
    public void updateById(UserEntity userEntity) {

    }

    @Override
    public UserEntity isExistUser(String account, String password, String tenantId, String dbName) throws LoginException {
        return null;
    }

}

package smart;

import smart.model.LoginForm;
import smart.model.currenuser.PCUserVO;
import smart.base.UserInfo;
import smart.exception.LoginException;
import smart.permission.entity.UserEntity;
/**
 * 登陆业务层
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
public interface LoginService {

    /**
     * 租戶登录验证
     * @param loginForm
     * @return
     * @throws LoginException
     */
    UserInfo checkTenant(LoginForm loginForm) throws LoginException;

    /**
     * 获取用户登陆信息
     * @return
     */
    PCUserVO getCurrentUser();

}

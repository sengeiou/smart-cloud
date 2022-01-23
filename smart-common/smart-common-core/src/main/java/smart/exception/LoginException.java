package smart.exception;

/**
 * 登录异常
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:53
 */
public class LoginException extends Exception {
    public LoginException(String message) {
        super(message);
    }
}

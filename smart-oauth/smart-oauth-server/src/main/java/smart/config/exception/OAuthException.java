package smart.config.exception;

import smart.base.ActionResult;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 自定义异常
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@ControllerAdvice
public class OAuthException {
    /**
     * 用户名和密码错误
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(OAuth2Exception.class)
    public ActionResult handleInvalidGrantException(OAuth2Exception e) {
        return ActionResult.fail("用户名或密码错误");
    }
}

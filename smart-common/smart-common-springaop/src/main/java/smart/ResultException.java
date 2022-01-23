package smart;

import com.alibaba.fastjson.JSON;
import smart.base.*;
//import smart.base.entity.LogEntity;
import smart.exception.DataException;
import smart.exception.LoginException;
import smart.exception.WorkFlowException;
import smart.exception.WxErrorException;
import smart.util.*;
import smart.util.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLSyntaxErrorException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:51
 */
@Slf4j
@RestController
@RestControllerAdvice
public class ResultException {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    //private LogApi logApi;

    @ResponseBody
    @ExceptionHandler(value = LoginException.class)
    public ActionResult loginException(LoginException e) {
        ActionResult result = ActionResult.fail(ActionResultCode.Fail.getCode(), e.getMessage());
            printLoginLog(e, "登陆异常");
        return result;
    }

    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    public ActionResult runtimeException(RuntimeException e) {
        ActionResult result = ActionResult.fail(ActionResultCode.Fail.getCode(), e.getMessage());
//        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
        printLoginLog(e, "系统异常");
//        }
        return result;
    }


    /**
     * 自定义异常内容返回
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = DataException.class)
    public ActionResult dataException(DataException e) {
        ActionResult result = ActionResult.fail(ActionResultCode.Fail.getCode(), e.getMessage());
        printLog(e, "系统异常");
        return result;
    }


    @ResponseBody
    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public ActionResult sqlException(SQLSyntaxErrorException e) {
        ActionResult result;
        log.error(e.getMessage());
        if(e.getMessage().contains("Unknown database")){
            result = ActionResult.fail(ActionResultCode.Fail.getCode(), "请求失败");
        }else{
            result = ActionResult.fail(ActionResultCode.Fail.getCode(), "数据库异常");
        }
        return result;
    }

//    @ResponseBody
//    @ExceptionHandler(value = SQLServerException.class)
//    public ActionResult sqlServerException(SQLServerException e) {
//        ActionResult result;
//        if (e.getMessage().contains("将截断字符串")) {
//            result = ActionResult.fail(ActionResultCode.Fail.getCode(), "某个字段字符长度超过限制，请检查。");
//        } else {
//            log.error(e.getMessage());
//            result = ActionResult.fail(ActionResultCode.Fail.getCode(), "数据库异常，请检查。");
//        }
//        return result;
//    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ActionResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> map = new HashMap<>(16);
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        for (int i = 0; i < allErrors.size(); i++) {
            String s = allErrors.get(i).getCodes()[0];
            //用分割的方法得到字段名
            String[] parts = s.split("\\.");
            String part1 = parts[parts.length - 1];
            map.put(part1, allErrors.get(i).getDefaultMessage());
        }
        String json = JSON.toJSONString(map);
        ActionResult result = ActionResult.fail(ActionResultCode.ValidateError.getCode(), json);
        printLog(e, "字段验证异常");
        return result;
    }

    @ResponseBody
    @ExceptionHandler(value = WorkFlowException.class)
    public ActionResult workFlowException(WorkFlowException e) {
        log.error("流程异常:" + e.getMessage(), e);
        printLog(e, "流程异常");
        return ActionResult.fail(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = WxErrorException.class)
    public ActionResult wxErrorException(WxErrorException e) {
        log.error("微信异常:" + e.getMessage(), e);
        printLog(e, "微信异常");
        return ActionResult.fail(e.getError().getErrorCode(), "操作过于频繁");
    }

    private void printLog(Exception e, String module) {
        try{
            String authorization = ServletUtil.getRequest().getHeader("Authorization");
            String token=JwtUtil.getRealToken(authorization);
            UserInfo info = JsonUtil.getJsonToBean(String.valueOf(redisUtil.getString(token)), UserInfo.class);
           /* LogEntity entity = new LogEntity();
            entity.setId(RandomUtil.uuId());
            entity.setCategory(LogSortEnum.Operate.getCode());
            entity.setUserId(info.getUserId());
            entity.setUserName(info.getUserName() + "/" + info.getUserAccount());
            if (!ServletUtil.getIsMobileDevice()) {
                String modelName = module;
                entity.setModuleName(modelName);
            }
            if (e.getMessage()==null){
                entity.setJson("未知异常");
            }else {
                entity.setJson(e.getMessage());
            }
            entity.setRequestURL(ServletUtil.getRequest().getServletPath());
            entity.setRequestMethod(ServletUtil.getRequest().getMethod());
            entity.setCategory(4);
            entity.setUserId(info.getUserId());
            entity.setIPAddress(IpUtil.getIpAddr());
            entity.setCreatorTime(new Date());
            entity.setPlatForm(ServletUtil.getUserAgent());
            logApi.writeLogRequest(entity);*/
        }catch (Exception g){
            log.error(g.getMessage());
        }

    }

    private void printLoginLog(Exception e, String module) {
        try{
            String authorization = ServletUtil.getRequest().getHeader("Authorization");
            String realToken = JwtUtil.getRealToken(authorization);
            if (realToken==null){
                return;
            }
            String token = String.valueOf(redisUtil.getString(realToken));
            UserInfo info = JsonUtil.getJsonToBean(token, UserInfo.class);
           /* LogEntity entity = new LogEntity();
            entity.setId(RandomUtil.uuId());
            entity.setCategory(LogSortEnum.Operate.getCode());
            entity.setUserId(info.getUserId());
            entity.setUserName(info.getUserName() + "/" + info.getUserAccount());
            if (!ServletUtil.getIsMobileDevice()) {
                String modelName = module;
                entity.setModuleName(modelName);
            }
            entity.setJson(e.getMessage());
            entity.setRequestURL(ServletUtil.getRequest().getServletPath());
            entity.setRequestMethod(ServletUtil.getRequest().getMethod());
            entity.setCategory(4);
            entity.setUserId(info.getUserId());
            entity.setIPAddress(IpUtil.getIpAddr());
            entity.setCreatorTime(new Date());
            entity.setPlatForm(ServletUtil.getUserAgent());
            logApi.writeLogRequest(entity);*/
        }catch (Exception g){
            log.error(e.getMessage());
        }

    }

}

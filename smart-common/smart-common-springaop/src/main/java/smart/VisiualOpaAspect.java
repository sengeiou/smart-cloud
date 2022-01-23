package smart;

import smart.util.RedisUtil;
import smart.util.ServletUtil;
//import smart.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-26
 */
@Slf4j
@Aspect
@Component
public class VisiualOpaAspect {

//    @Autowired
//    UserProvider userProvider;
    @Autowired
    private RedisUtil redisUtil;

    @Pointcut("(execution(* smart.*.controller.VisualdevModelDataController.*(..))) || execution(* smart.*.controller.VisualdevModelAppController.*(..)))" +
            "|| execution(* smart.*.controller.VisualdevGenController.*(..)))")
    public void visiualOpa() {

    }

    @After("visiualOpa()")
    public void doAroundService() {
        String method = ServletUtil.getRequest().getMethod().toLowerCase();
        if ("put".equals(method) || "delete".equals(method) || "post".equals(method)) {
            Set<String> allKey = new HashSet<>(16);
            allKey.addAll(redisUtil.getAllVisiualKeys());
            for (String key : allKey) {
                redisUtil.remove(key);
            }
        }
    }
}

package smart;

/**
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-26
 */

import smart.base.UserInfo;
//import smart.config.ConfigValueUtil;
import smart.util.data.DataSourceContextHolder;
import smart.util.StringUtil;
//import smart.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 17:12
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class DataSourceBindAspect {

    /*@Autowired
    UserProvider userProvider;
    @Autowired
    private ConfigValueUtil configValueUtil;*/

    @Pointcut("!execution(* smart.*.LoginController.Login(..)) && (execution(* smart.controller.*.*(..))  || execution(* smart.*.controller.*.*(..)))")
    public void bindDataSource() {

    }

    /**
     * NoDataSourceBind 不需要绑定数据库的注解
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("bindDataSource() && !@annotation(smart.util.NoDataSourceBind)")
    public Object doAroundService(ProceedingJoinPoint pjp) throws Throwable {
        //判断是否为多租户
       /* if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            UserInfo userInfo = userProvider.get();
            if (StringUtil.isNotEmpty(userInfo.getTenantDbConnectionString())) {
                DataSourceContextHolder.setDatasource(userInfo.getTenantId(), userInfo.getTenantDbConnectionString());
                Object obj = pjp.proceed();
                return obj;
            }
            log.error("租户" + userInfo.getTenantId() + "数据库不存在");
            return null;
        }*/
        Object obj = pjp.proceed();
        return obj;
    }
}


package com.bstek.ureport.console.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;

@Component
public class SessionUtil {

    @Autowired
    private ServletContext servletContext;

    public static final String USER_DATASOURCE_BIND = "com.jinshang.user.bind.datasource";

    public static String getCurrentDataSource() {
        String name = (String) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession().getAttribute(USER_DATASOURCE_BIND);
        return name;
    }

    public static final String USER_TENANTID_BIND = "yinmai.smart.tenantId";

    public String getCurrentTenantId() {
        String name= String.valueOf(servletContext.getAttribute(USER_TENANTID_BIND));
        return name;
    }

    public void setCurrentTenantId(String tenantId){
        servletContext.setAttribute(USER_TENANTID_BIND,tenantId);
    }
}

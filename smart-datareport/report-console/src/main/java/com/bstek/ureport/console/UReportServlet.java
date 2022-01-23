/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.console;

import com.bstek.ureport.console.util.ActionResult;
import com.bstek.ureport.console.util.UpdateData;
import smart.util.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @since 1月25日
 */
@Slf4j
public class UReportServlet extends HttpServlet {

    @Autowired
    private UpdateData updateData;

    private static final long serialVersionUID = 533049461276487971L;
    public static final String URL = "";
    private Map<String, ServletAction> actionMap = new HashMap<String, ServletAction>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WebApplicationContext applicationContext = getWebApplicationContext(config);
        Collection<ServletAction> handlers = applicationContext.getBeansOfType(ServletAction.class).values();
        for (ServletAction handler : handlers) {
            String url = handler.url();
            if (actionMap.containsKey(url)) {
                throw new RuntimeException("Handler [" + url + "] already exist.");
            }
            actionMap.put(url, handler);
        }
        //使@Autowired生效
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    protected WebApplicationContext getWebApplicationContext(ServletConfig config) {
        return WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "*");
        resp.setHeader("Access-Control-Max-Age", "3600");
        resp.setHeader("Access-Control-Allow-Headers", "Authorization,Origin,X-Requested-With,Content-Type,Accept,"
                + "content-Type,origin,x-requested-with,content-type,accept,authorization,token,id,X-Custom-Header,X-Cookie,Connection,User-Agent,Cookie,*");
        resp.setHeader("Access-Control-Request-Headers", "Authorization,Origin, X-Requested-With,content-Type,Accept");
        resp.setHeader("Access-Control-Expose-Headers", "*");
        String path = req.getContextPath() + URL;
        String uri = req.getRequestURI();
        String targetUrl = uri.substring(path.length());
        if (targetUrl.length() < 1) {
            outContent(resp, "Welcome to use ureport,please specify target url.");
            return;
        }
        int slashPos = targetUrl.indexOf("/", 1);
        if (slashPos > -1) {
            targetUrl = targetUrl.substring(0, slashPos);
        }
        ServletAction targetHandler = actionMap.get(targetUrl);
        if (targetHandler == null) {
            outContent(resp, "Handler [" + targetUrl + "] not exist.");
            return;
        }
        RequestHolder.setRequest(req);
        try {
            //判断是否是多租户，切换数据源
            boolean flag = updateData.getDbName(req);
            //验证token
            String token = "";
            try {
                token = JwtUtil.getRealToken(req.getHeader("Authorization"));
            }catch (Exception e){
                writeObjectToJson(resp,ActionResult.fail("Token验证失败"));
            }
            if (!flag) {
                targetHandler.execute(req, resp);
            } else {
                String account = updateData.getUserId(token);
                if (!StringUtils.isBlank(account) && !StringUtils.isEmpty(account)) {
                    targetHandler.execute(req, resp);
                } else {
                    token = updateData.getUserId(req.getParameter("token"));
                    if (!StringUtils.isBlank(token) && !StringUtils.isEmpty(token)) {
                        targetHandler.execute(req, resp);
                    } else {
                        writeObjectToJson(resp, ActionResult.fail("token验证失败"));
                    }
                }
            }
        } catch (Exception ex) {
//			resp.setCharacterEncoding("UTF-8");
//			PrintWriter pw=resp.getWriter();
//			Throwable e=buildRootException(ex);
//			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			String errorMsg = e.getMessage();
//			if(StringUtils.isBlank(errorMsg)){
//				errorMsg=e.getClass().getName();
//			}
//			pw.write(errorMsg);
//			pw.close();
//			throw new ServletException(ex);
            log.error(ex.getMessage());
//			ex.printStackTrace();
            writeObjectToJson(resp, ActionResult.fail("请检查接口路径、参数和数据库连接"));
        } finally {
            RequestHolder.clean();
        }
    }

    private Throwable buildRootException(Throwable throwable) {
        if (throwable.getCause() == null) {
            return throwable;
        }
        return buildRootException(throwable.getCause());
    }

    private void outContent(HttpServletResponse resp, String msg) throws IOException {
        resp.setContentType("text/html");
        PrintWriter pw = resp.getWriter();
        pw.write("<html>");
        pw.write("<header><title>UReport Console</title></header>");
        pw.write("<body>");
        pw.write(msg);
        pw.write("</body>");
        pw.write("</html>");
        pw.flush();
        pw.close();
    }

    protected void writeObjectToJson(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OutputStream out = resp.getOutputStream();
        try {
            mapper.writeValue(out, obj);
        } finally {
            out.flush();
            out.close();
        }
    }
}

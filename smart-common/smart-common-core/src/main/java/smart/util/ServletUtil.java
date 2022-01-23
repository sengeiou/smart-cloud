package smart.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 客户端工具类
 *
 * @author SmartCloud项目开发组
 */
public class ServletUtil
{

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest()
    {
        try
        {
            return getRequestAttributes().getRequest();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static String getHeader(String name) {
        if(getRequest()!=null){
            return getRequest().getHeader(name);
        }
        return null;
    }


    /**
     * 判断是否是手机端登陆
     */
    public static boolean getIsMobileDevice() {
        return isMobileDevice(ServletUtil.getUserAgent());
    }

    /**
     * 判断是否是手机端登陆
     */
    public static boolean getIsMobileDevice(String userAgent) {
        return isMobileDevice(userAgent);
    }


    /**
     * 获取User-Agent
     */
    public static String getUserAgent() {
        return ServletUtil.getHeader("User-Agent");
    }

    /**
     * 判断是否是移动设备
     * @param requestHeader
     * @return
     */
    public static boolean isMobileDevice(String requestHeader) {
        String[] deviceArray = new String[]{"android", "windows phone", "iphone", "ios", "ipad" ,"mqqbrowser"};
        if (requestHeader == null) {
            return false;
        }
        requestHeader = requestHeader.toLowerCase();
        for (int i = 0; i < deviceArray.length; i++) {
            if (requestHeader.indexOf(deviceArray[i]) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取ServletPath
     */
    public static String getServletPath() {
        return ServletUtil.getRequest().getServletPath();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse()
    {
        try
        {
            return getRequestAttributes().getResponse();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 获取session
     */
    public static HttpSession getSession()
    {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes()
    {
        try
        {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static Map<String, String> getHeaders(HttpServletRequest request)
    {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        if (enumeration != null)
        {
            while (enumeration.hasMoreElements())
            {
                String key = enumeration.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string 待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string)
    {
        try
        {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否是Ajax异步请求
     *
     * @param request
     */
    public static boolean isAjaxRequest(HttpServletRequest request)
    {
        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf("application/json") != -1)
        {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1)
        {
            return true;
        }

        String uri = request.getRequestURI();
        if (StringUtil.inStringIgnoreCase(uri, ".json", ".xml"))
        {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        if (StringUtil.inStringIgnoreCase(ajax, "json", "xml"))
        {
            return true;
        }
        return false;
    }
}

package com.bstek.ureport.console.util;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestUtil {

    //获取request内的参数
    public static String getPayload(HttpServletRequest request) throws IOException {
        ServletInputStream is = request.getInputStream();
        int nRead = 1;
        int nTotalRead = 0;
        byte[] bytes = new byte[10240 * 200];
        while (nRead > 0) {
            nRead = is.read(bytes, nTotalRead, bytes.length - nTotalRead);
            if (nRead > 0) {
                nTotalRead = nTotalRead + nRead;
            }
        }
        String str = new String(bytes, 0, nTotalRead, "UTF8");
        is.close();
        return str;
    }

    /**
     * body中form-data转map
     * @param request
     * @return
     */
    public static  Map<String, String> getParamsFromFormDataByNames(HttpServletRequest request){
        Map<String, String> map =new HashMap<>();
        Enumeration<String> er = request.getParameterNames();
        while (er.hasMoreElements()) {
            String name = (String) er.nextElement();
            String value = request.getParameter(name);
            map.put(name, value);
        }
        return map;
    }
}

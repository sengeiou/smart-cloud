package com.smart.pay.base.biz.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/12/2 16:41
 * @see com.smart.pay.biz.wx
 * @since 1.0
 **/
public class WriterUtil {

    /**
     * 写入字符串到response
     * @param response
     * @param contentType 数据类型
     * @param result 写入数据 默认UTF-8编码
     */
    public static void writer(HttpServletResponse response,String contentType, String result){
        writer(response,contentType,result,"UTF-8");
    }

    /**
     *
     * @param response
     * @param contentType 数据类型
     * @param result 写入数据
     * @param charset 编码
     */
    public static void writer(HttpServletResponse response,String contentType, String result,String charset){
        PrintWriter writer = null;
        try {
            response.setContentType(contentType);
            response.setCharacterEncoding(charset);
            writer = response.getWriter();
            writer.write(result);// 直接将完整的表单html输出到页面
            writer.flush();
            response.getWriter().close();
        } catch (IOException ignored) {
        } finally {
            if (writer !=null){
                writer.close();
            }
        }
    }

}

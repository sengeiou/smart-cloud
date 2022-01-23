package smart.util;

/**
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:56
 */
public class UploaderUtil {

    /**
     * 头像名称处理
     * @param fileName
     * @return
     */
    public static String uploaderImg(String fileName) {
        return uploaderImg(null, fileName);
    }

    /**
     * 头像名称处理
     * @param url
     * @param fileName
     * @return
     */
    public static String uploaderImg(String url, String fileName) {
        if (url == null) {
            url = "/api/file/Image/userAvatar/";
        }
        return url + fileName;
    }

    /**
     * 附件名称处理
     * @param url
     * @param fileName
     * @return
     */
    public static String uploaderFile(String url, String fileName) {
        if (url == null) {
            url = "/api/file/Download?encryption=";
        }
        String name = DesUtil.aesEncode(fileName);
        return url + name;
    }

    /**
     * 附件名称处理
     * @param fileName
     * @return
     */
    public static String uploaderFile(String fileName) {
        return uploaderFile(null, fileName);
    }

    /**
     * 代码生成器附件名称处理
     * @param fileName
     * @return
     */
    public static String uploaderVisualFile(String fileName) {
        String url = "/api/visualdev/Generater/DownloadVisCode?encryption=";
        String name = DesUtil.aesEncode(fileName);
        return url + name;
    }

}

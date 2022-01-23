package smart.util;


import java.util.Arrays;
/**
 * 文件操作工具类
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
public class OptimizeUtil {

    /**
     * 允许文件类型
     *
     * @param fileType      文件所有类型
     * @param fileExtension 当前文件类型
     * @return
     */
    public static boolean fileType(String fileType, String fileExtension) {
        String[] allowExtension = fileType.split(",");
        return Arrays.asList(allowExtension).contains(fileExtension.toLowerCase());
    }

    /**
     * 允许图片类型
     *
     * @param imageType     图片所有类型
     * @param fileExtension 当前图片类型
     * @return
     */
    public static boolean imageType(String imageType, String fileExtension) {
        String[] allowExtension = imageType.split(",");
        return Arrays.asList(allowExtension).contains(fileExtension.toLowerCase());
    }

    /**
     * 允许上传大小
     *
     * @param fileSize 文件大小
     * @param maxSize  最大的文件
     * @return
     */
    public static boolean fileSize(Long fileSize, int maxSize) {
        if (fileSize > maxSize) {
            return true;
        }
        return false;
    }

}

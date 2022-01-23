package smart.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信 API、基本接口
 */
public class QyApi {

    // token 接口
    public static final String TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";

    //--------------------------------------------部门--------------------------------------
    // 创建部门
    public static final String CREATE_DEPARTMENT = "https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token=%s";

    // 更新部门
    public static final String UPDATE_DEPARTMENT = "https://qyapi.weixin.qq.com/cgi-bin/department/update?access_token=%s";

    // 删除部门
    public static final String DELETE_DEPARTMENT = "https://qyapi.weixin.qq.com/cgi-bin/department/delete?access_token=%s&id=%s";

    // 获取部门列表
    public static final String GET_DEPARTMENT_LIST = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s&id=%s";

    //-------------------------------------------用户-----------------------------------------------------
    // 创建用户
    public static final String CREATE_USER = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=%s";

    // 更新用户
    public static final String UPDATE_USER = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=%s";

    // 删除用户
    public static final String DELETE_USER = "https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=%s&userid=%s";

    //-------------------------------------上传---------------------------------------------------
    // 上传素材
    public static final String MEDIA_UPLOAD = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=%s";

    //-------------------------------------消息--------------------------------------------------
    // 发送消息
    public static final String SEND_MESSAGE = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";


    //素材文件后缀
    public static Map<String, String> type_fix = new HashMap<>();
    public static Map<String, String> media_fix = new HashMap<>();
    //素材文件大小
    public static Map<String, Long> type_length = new HashMap<>();

    static {
        type_fix.put("image", "bmp|png|jpeg|jpg|gif");
        type_fix.put("voice", "mp3|wma|wav|amr");
        type_fix.put("video", "mp4");
        type_fix.put("thumb", "bmp|png|jpeg|jpg|gif");

        media_fix.put("image", "png|jpeg|jpg|gif");
        media_fix.put("voice", "mp3|amr");
        media_fix.put("video", "mp4");
        media_fix.put("thumb", "bmp|png|jpeg|jpg|gif");

        type_length.put("image", new Long(2 * 1024 * 1024));
        type_length.put("voice", new Long(2 * 1024 * 1024));
        type_length.put("video", new Long(10 * 1024 * 1024));
        type_length.put("thumb", new Long(64 * 1024 * 1024));

    }

    // 获取token接口
    public static String getTokenUrl(String corpId, String corpSecret) {
        return String.format(TOKEN, corpId, corpSecret);
    }

    //--------------------------------------------部门--------------------------------------
    //创建部门
    public static String createDepartment(String token) {
        return String.format(CREATE_DEPARTMENT, token);
    }

    //更新部门
    public static String updateDepartment(String token) {
        return String.format(UPDATE_DEPARTMENT, token);
    }

    //删除部门
    public static String deleteDepartment(String token, String id) {
        return String.format(DELETE_DEPARTMENT, token, id);
    }

    //部门列表
    public static String getDepartmentList(String token, String id) {
        return String.format(GET_DEPARTMENT_LIST, token, id);
    }

    //-------------------------------------------用户-----------------------------------------------------
    //创建用户
    public static String createUser(String token) {
        return String.format(CREATE_USER, token);
    }

    //更新用户
    public static String updateUser(String token) {
        return String.format(UPDATE_USER, token);
    }

    //删除用户
    public static String deleteUser(String token, String userId) {
        return String.format(DELETE_USER, token, userId);
    }

    //-------------------------------------上传---------------------------------------------------
    // 上传素材
    public static String mediaUpload(String token, String type) {
        return String.format(MEDIA_UPLOAD, token, type);
    }

    //-------------------------------------消息---------------------------------------------------
    // 发送消息
    public static String sendMessage(String token) {
        return String.format(SEND_MESSAGE, token);
    }

}

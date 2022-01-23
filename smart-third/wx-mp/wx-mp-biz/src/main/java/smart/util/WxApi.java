package smart.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号 API、微信基本接口
 */
public class WxApi {

    // token 接口
    public static final String TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    //--------------------------------------------素材--------------------------------------
    //素材文件后缀
    public static Map<String, String> type_fix = new HashMap<>();
    public static Map<String, String> media_fix = new HashMap<>();
    public static Map<String, Long> type_length = new HashMap<>();

    static {
        type_fix.put("image", "bmp|png|jpeg|jpg|gif");
        type_fix.put("voice", "mp3|wma|wav|amr");
        type_fix.put("video", "mp4");
        type_fix.put("thumb", "bmp|png|jpeg|jpg|gif");
        type_fix.put("file", "text|doc|docx|pdf|xls|xlsx");

        media_fix.put("image", "png|jpeg|jpg|gif");
        media_fix.put("voice", "mp3|amr");
        media_fix.put("video", "mp4");
        media_fix.put("thumb", "bmp|png|jpeg|jpg|gif");

        type_length.put("image", new Long(2 * 1024 * 1024));
        type_length.put("voice", new Long(2 * 1024 * 1024));
        type_length.put("video", new Long(10 * 1024 * 1024));
        type_length.put("thumb", new Long(64 * 1024 * 1024));
        type_length.put("file", new Long(20 * 1024 * 1024));

    }
    // 获取永久素材
    public static String getMaterial(String access_token) {
        return String.format(GET_MATERIAL, access_token);
    }

    // 删除永久图文素材
    public static String getDelMaterialURL(String access_token) {
        return String.format(DELETE_MATERIAL, access_token);
    }

    // 获取新增图文素材url
    public static String getNewsMaterialUrl(String access_token) {
        return String.format(ADD_NEWS_MATERIAL, access_token);
    }

    // 获取新增素材url
    public static String getMaterialUrl(String access_token, String type) {
        return String.format(ADD_MATERIAL, access_token, type);
    }

    // 获取素材列表接口
    public static String getBatchMaterialUrl(String access_token) {
        return String.format(GET_BATCH_MATERIAL, access_token);
    }

    //--------------------------------------------菜单--------------------------------------
    // 创建菜单
    public static final String MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";

    // 删除菜单
    public static final String MENU_DELETE = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=%s";

    // 获取菜单列表
    public static final String GET_MENU_LIST = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=%s";

    //--------------------------------------------素材--------------------------------------
    // 获取素材列表
    public static final String GET_BATCH_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=%s";

    // 新增其他类型永久素材
    public static final String ADD_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=%s&type=%s";

    // 新增永久图文素材
    public static final String ADD_NEWS_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=%s";

    // 根据media_id来获取永久素材
    public static final String GET_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=%s";

    // 根据media_id来删除永久图文素材
    public static final String DELETE_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=%s";

    //--------------------------------------------用户标签--------------------------------------
    //获取用户标签列表
    private static final String GET_USER_TAG = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=%s";

    //创建用户标签
    private static final String CREATE_USER_TAG = "https://api.weixin.qq.com/cgi-bin/tags/create?access_token=%s";

    //更新用户标签
    private static final String UPDATE_USER_TAG = "https://api.weixin.qq.com/cgi-bin/tags/update?access_token=%s";

    //删除用户标签
    private static final String DELETE_USER_TAG = "https://api.weixin.qq.com/cgi-bin/tags/delete?access_token=%s";

    //批量为用户打标签
    private static final String BATCHTAG_GING = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=%s";

    //批量为用户取消标签
    private static final String BATCHUNTAG_GING = "https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=%s";

    //--------------------------------------------用户--------------------------------------
    //黑名单列表
    private static final String GET_BLACK_LIST = "https://api.weixin.qq.com/cgi-bin/tags/members/getblacklist?access_token=%s";

    //修改关注者备注信息
    private static final String UPDATE_REMARK = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=%s";

    // 获取关注者列表
    public static final String GET_USER_LIST = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=%s";

    //加入黑名单
    private static final String BATCH_BLACK_LIST = "https://api.weixin.qq.com/cgi-bin/tags/members/batchblacklist?access_token=%s";

    //移除黑名单
    private static final String BATCHUN_BLACK_LIST = "https://api.weixin.qq.com/cgi-bin/tags/members/batchunblacklist?access_token=%s";

    //用户信息
    private static final String GET_USER_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

    //批量获取用户信息
    private static final String BATCHGET_LIST = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=%s";

    //获取标签下粉丝列表
    private static final String GET_TAG_USER_LIST="https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=%s";

    //--------------------------------------------群发--------------------------------------
    //群发预览
    public static final String MASS_PREVIEW = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=%s";

    // 根据标签进行群发
    public static final String MASS_SENDALL = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=%s";


    // 获取token接口
    public static String getTokenUrl(String appId, String appSecret) {
        return String.format(TOKEN, appId, appSecret);
    }

    //--------------------------------------------菜单--------------------------------------
    // 获取菜单创建接口
    public static String getMenuCreateUrl(String token) {
        return String.format(MENU_CREATE, token);
    }

    // 获取菜单删除接口
    public static String getMenuDeleteUrl(String token) {
        return String.format(MENU_DELETE, token);
    }

    // 获取菜单列表
    public static String getMenuListUrl(String token) {
        return String.format(GET_MENU_LIST, token);
    }


    //--------------------------------------------用户标签--------------------------------------
    //获取用户标签列表接口
    public static String getUserTagList(String token) {
        return String.format(GET_USER_TAG, token);
    }

    //获取创建用户标签接口
    public static String getCreateUserTag(String token) {
        return String.format(CREATE_USER_TAG, token);
    }

    //更新用户标签接口
    public static String getUpdateUserTag(String token) {
        return String.format(UPDATE_USER_TAG, token);
    }

    //删除用户标签接口
    public static String getDeleteUserTag(String token) {
        return String.format(DELETE_USER_TAG, token);
    }

    //批量为用户打标签
    public static String getBatchTagged(String token) {
        return String.format(BATCHTAG_GING, token);
    }

    //批量为用户取消标签
    public static String getBatchUnTagged(String token) {
        return String.format(BATCHUNTAG_GING, token);
    }

    //--------------------------------------------用户--------------------------------------
    //黑名单列表
    public static String getBlackList(String token){
        return String.format(GET_BLACK_LIST, token);
    }

    //修改关注者备注信息
    public static String getUpdateRemark(String token){
        return String.format(UPDATE_REMARK, token);
    }

    // 获取关注者列表
    public static String getUserList(String token, String nextOpenId){
        if (nextOpenId == null) {
            return String.format(GET_USER_LIST, token);
        } else {
            return String.format(GET_USER_LIST + "&next_openid=%s", token, nextOpenId);
        }
    }

    //加入黑名单
    public static String getBatchBlackList(String token){
        return String.format(BATCH_BLACK_LIST, token);
    }

    //移除黑名单
    public static String getBatchunBlackList(String token){
        return String.format(BATCHUN_BLACK_LIST, token);
    }

    //用户信息
    public static String getUserInfo(String token, String openId){
        return String.format(GET_USER_INFO, token,openId);
    }

    //批量获取用户信息
    public static String getBatchgetList(String token){
        return String.format(BATCHGET_LIST, token);
    }

    //获取标签下粉丝列表
    public static String getTagUserList(String token){
        return String.format(GET_TAG_USER_LIST, token);
    }

    //--------------------------------------------群发--------------------------------------
    // 群发预览url
    public static String getMassPreviewUrl(String access_token) {
        return String.format(MASS_PREVIEW, access_token);
    }

    // 根据标签进行群发url
    public static String getMassSendallUrl(String access_token) {
        return String.format(MASS_SENDALL, access_token);
    }

}

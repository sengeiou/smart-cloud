package smart.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import smart.exception.WxError;
import smart.exception.WxErrorException;
import smart.model.mpmaterial.MPMaterialModel;
import smart.util.wxutil.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信公众号 客户端，统一处理微信相关接口
 */
@Slf4j
public class WxApiClient {

    // 获取接口访问凭证
    public static JSONObject getAccessToken(String appId, String appSecret) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getTokenUrl(appId, appSecret), "GET", null);
        if (rstObj.getString("access_token") == null) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //-----------------------------标签------------------------------------
    //获取标签列表
    public static JSONObject userTagList(String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getUserTagList(accessToken), "POST", null);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //创建用户标签
    public static JSONObject createUserTag(String userTags, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getCreateUserTag(accessToken), "POST", userTags);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //更新用户标签
    public static JSONObject updateUserTag(String userTags, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getUpdateUserTag(accessToken), "POST", userTags);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //删除用户标签
    public static JSONObject deleteUserTag(String tagId, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getDeleteUserTag(accessToken), "POST", tagId);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //批量为用户打标签
    public static JSONObject batchTagged(String members, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getBatchTagged(accessToken), "POST", members);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //批量为用户取消标签
    public static JSONObject batchUnTagged(String members, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getBatchUnTagged(accessToken), "POST", members);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //------------------------------------菜单-------------------------------------
    // 发布菜单
    public static JSONObject publishMenus(String menus, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getMenuCreateUrl(accessToken), "POST", menus);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    // 删除菜单
    public static JSONObject deleteMenu(String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getMenuDeleteUrl(accessToken), "POST", null);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    // 菜单列表
    public static JSONObject menuList(String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getMenuListUrl(accessToken), "GET", null);
        if (HttpUtil.isWxError(rstObj)) {
            if (!"46003".equals(rstObj.getString("errcode"))) {
                throw new WxErrorException(WxError.fromJson(rstObj));
            }
        }
        return rstObj;
    }

    //-----------------------------------------素材--------------------------------------------
    //永久素材添加-图文
    public static JSONObject addNews(List<MPMaterialModel> materialModels, String accessToken) throws WxErrorException {
        JSONArray jsonArr = new JSONArray();
        for (MPMaterialModel model : materialModels) {
            JSONObject jsonObj = new JSONObject();
            // 上传图片素材
            jsonObj.put("thumb_media_id", model.getThumbMediaId());
            jsonObj.put("author", StringUtil.isNotEmpty(model.getAuthor()) ? model.getAuthor() : "");
            jsonObj.put("title", StringUtil.isNotEmpty(model.getTitle()) ? model.getTitle() : "");
            jsonObj.put("content_source_url", StringUtil.isNotEmpty(model.getContentSourceUrl()) ? model.getContentSourceUrl() : "");
            jsonObj.put("digest", StringUtil.isNotEmpty(model.getDigest()) ? model.getDigest() : "");
            jsonObj.put("show_cover_pic", StringUtil.isNotEmpty(model.getShowCoverPic()) ? model.getShowCoverPic() : 1);
            jsonObj.put("content", model.getContent());
            jsonArr.add(jsonObj);
        }
        JSONObject postObj = new JSONObject();
        postObj.put("articles", jsonArr);
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getNewsMaterialUrl(accessToken), "POST", postObj.toString());
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    /**
     * 永久素材添加-不包含图文
     *
     * @param accessToken 微信token
     * @param type        素材类型（image/voice/video/thumb）
     * @param fileUrl     文件的绝对路径
     * @param params      视频数据
     * @return
     * @throws WxErrorException
     */
    public static JSONObject addMateria(String accessToken, String type, String fileUrl, Map<String, String> params) throws WxErrorException {
        File file = new File(fileUrl);
        if (!file.exists()) {
            throw new WxErrorException(WxError.newBuilder().setErrorCode(-2).setErrorMsg("文件不存在").build());
        }
        String result = HttpUtil.sendHttpPost(WxApi.getMaterialUrl(accessToken, type), file, params);
        WxError wxError = WxError.fromJson(result);
        if (wxError.getErrorCode() != 0) {
            throw new WxErrorException(wxError);
        }
        return JSONObject.parseObject(result);
    }

    /**
     * 永久素材下载-包含图片、语音、缩略图
     *
     * @param accessToken 微信token
     * @param mediaId     多媒体素材ID
     * @param file        文件夹目录 例如D://down
     * @return
     * @throws WxErrorException
     */
    public static File downlodMateria(String accessToken, String mediaId, File file) throws WxErrorException {
        String url = String.format(WxApi.GET_MATERIAL, accessToken);
        Map map = new HashMap();
        map.put("media_id", mediaId);
        Object obj = HttpUtil.sendHttpPostFile(url, map, file);
        if (obj instanceof String) {
            WxError wxError = WxError.fromJson((String) obj);
            throw new WxErrorException(wxError);
        }
        if (null == obj) {
            throw new WxErrorException(WxError.newBuilder().setErrorCode(-3).setErrorMsg("下载出错").build());
        }
        return (File) obj;
    }

    /**
     * 根据media_id获取永久图文素材
     *
     * @param mediaId     多媒体素材ID
     * @param accessToken 微信token
     * @return
     */
    public static JSONObject getMaterial(String mediaId, String accessToken) throws WxErrorException {
        JSONObject postObj = new JSONObject();
        postObj.put("media_id", mediaId);
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getMaterial(accessToken), "POST", postObj.toString());
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    /**
     * 获取素材列表接口
     *
     * @param accessToken 微信token
     * @return
     */
    public static JSONObject getBatchMaterialUrl(String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getBatchMaterialUrl(accessToken), "GET", null);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    /**
     * 根据media_id删除永久图文素材
     *
     * @param media_id
     * @return
     */
    public static JSONObject deleteMaterial(String media_id, String accessToken) throws WxErrorException {
        JSONObject postObj = new JSONObject();
        postObj.put("media_id", media_id);
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getDelMaterialURL(accessToken), "POST", postObj.toString());
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //-----------------------------群发--------------------------------------------------

    /**
     * 预览
     *
     * @param accessToken
     * @param mass
     * @return
     * @throws WxErrorException
     */
    public static JSONObject massPreview(String accessToken, String mass) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getMassPreviewUrl(accessToken), "POST", mass);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    /**
     * 根据标签进行群发
     *
     * @param accessToken
     * @param mass
     * @return
     * @throws WxErrorException
     */
    public static JSONObject massSendallUrl(String accessToken, String mass) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getMassSendallUrl(accessToken), "POST", mass);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //-----------------------------用户--------------------------------------------------

    /**
     * 黑名单列表
     *
     * @param accessToken
     * @param black
     * @return
     * @throws WxErrorException
     */
    public static JSONObject blackList(String accessToken, String black) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getBlackList(accessToken), "POST", black);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    /**
     * 加入黑名单
     *
     * @param accessToken
     * @param black
     * @return
     * @throws WxErrorException
     */
    public static JSONObject batchBlackList(String accessToken, String black) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getBatchBlackList(accessToken), "POST", black);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    /**
     * 移除黑名单
     *
     * @param accessToken
     * @param black
     * @return
     * @throws WxErrorException
     */
    public static JSONObject batchunBlackList(String accessToken, String black) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getBatchunBlackList(accessToken), "POST", black);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    /**
     * 修改关注者备注信息
     *
     * @param accessToken
     * @return
     * @throws WxErrorException
     */
    public static JSONObject updateRemark(String accessToken, String remark) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getUpdateRemark(accessToken), "POST", remark);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    /**
     * 批量获取用户信息
     *
     * @param accessToken
     * @return
     * @throws WxErrorException
     */
    public static JSONObject batchgetList(String accessToken, String user) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getBatchgetList(accessToken), "POST", user);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }


    /**
     * 获取关注者列表
     *
     * @param accessToken
     * @return
     * @throws WxErrorException
     */
    public static JSONObject userList(String accessToken, String nextOpenId) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getUserList(accessToken, nextOpenId), "GET", null);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    /**
     * 用户个人信息
     *
     * @param accessToken
     * @return
     * @throws WxErrorException
     */
    public static JSONObject userInfo(String accessToken, String openId) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getUserInfo(accessToken, openId), "GET", null);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    /**
     * 获取标签下粉丝列表
     *
     * @param accessToken
     * @return
     * @throws WxErrorException
     */
    public static JSONObject tagUserList(String accessToken, String tag) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(WxApi.getTagUserList(accessToken), "GET", tag);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

}

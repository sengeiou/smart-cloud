package smart.util;

import com.alibaba.fastjson.JSONObject;
import smart.exception.WxError;
import smart.exception.WxErrorException;
import smart.util.wxutil.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 微信企业号 客户端，统一处理微信相关接口
 */
@Slf4j
public class QyApiClient {

    // 获取接口访问凭证
    public static JSONObject getAccessToken(String corpId, String corpSecret) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(QyApi.getTokenUrl(corpId, corpSecret), "GET", null);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //------------------------------------部门-------------------------------------
    // 创建部门
    public static JSONObject createDepartment(String department, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(QyApi.createDepartment(accessToken), "POST", department);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    // 更新部门
    public static boolean updateDepartment(String department, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(QyApi.updateDepartment(accessToken), "POST", department);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return true;
    }

    // 删除部门
    public static boolean deleteDepartment(String id, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(QyApi.deleteDepartment(accessToken, id), "GET", null);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return true;
    }

    // 部门列表
    public static JSONObject departmentList(String id, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(QyApi.getDepartmentList(accessToken, id), "GET", null);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //------------------------------------------用户--------------------------------------------
    // 创建用户
    public static boolean createUser(String user, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(QyApi.createUser(accessToken), "POST", user);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return true;
    }

    // 更新用户
    public static boolean updateUser(String user, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(QyApi.updateUser(accessToken), "POST", user);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return true;
    }

    // 删除用户
    public static boolean deleteUser(String id, String accessToken) throws WxErrorException {
        JSONObject rstObj = HttpUtil.httpsRequest(QyApi.deleteUser(accessToken, id), "GET", null);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return true;
    }

    //------------------------------------------上传--------------------------------------------
    /**
     * 上传临时素材文件
     *
     * @param accessToken 微信token
     * @param type        素材类型（image/voice/video/file）
     * @param fileUrl     文件的绝对路径
     * @return
     * @throws WxErrorException
     */
    public static JSONObject mediaUpload(String accessToken, String type, String fileUrl) throws WxErrorException {
        File file = new File(fileUrl);
        if (!file.exists()) {
            throw new WxErrorException(WxError.newBuilder().setErrorCode(-2).setErrorMsg("文件不存在").build());
        }
        String fileName = file.getName();
        //获取后缀名
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        long length = file.length();
        //此处做判断是为了尽可能的减少对微信API的调用次数
        if (WxApi.type_fix.get(type).indexOf(suffix) == -1) {
            throw new WxErrorException(WxError.newBuilder().setErrorCode(40005).setErrorMsg("不合法的文件类型").build());
        }
        if (length > WxApi.type_length.get(type)) {
            throw new WxErrorException(WxError.newBuilder().setErrorCode(40006).setErrorMsg("不合法的文件大小").build());
        }
        String result = HttpUtil.sendHttpPost(QyApi.mediaUpload(accessToken, type), file);
        JSONObject rstObj = JSONObject.parseObject(result);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return rstObj;
    }

    //------------------------------------------消息--------------------------------------------
    // 发送消息
    public static boolean sendMessage(String message, String accessToken) throws WxErrorException{
        JSONObject rstObj = HttpUtil.httpsRequest(QyApi.sendMessage(accessToken), "POST", message);
        if (HttpUtil.isWxError(rstObj)) {
            throw new WxErrorException(WxError.fromJson(rstObj));
        }
        return true;
    }
}

package smart.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import smart.model.BaseSystemInfo;
import smart.util.JsonUtil;
import smart.base.SysConfigApi;
import smart.base.Pagination;
import smart.exception.WxErrorException;
import smart.model.mpuser.MPUserModel;
import smart.service.MPUserService;
import smart.util.WxApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 公众号用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Slf4j
@Service
public class MPUserServiceImpl implements MPUserService {

    @Autowired
    private SysConfigApi sysConfigApi;

    @Override
    public List<MPUserModel> getList(Pagination pagination) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject resultJObject = WxApiClient.userList(tokenObject.getString("access_token"), null);
        JSONObject data = resultJObject.getJSONObject("data");
        List<MPUserModel> list = new ArrayList<>();
        if (null != data) {
            List<String> openIdList = JsonUtil.getJsonToList(data.getString("openid"), String.class);
            JSONArray userList = new JSONArray();
            for (String openId : openIdList) {
                JSONObject object = new JSONObject();
                object.put("openid", openId);
                object.put("lang", "zh_CN");
                userList.add(object);
            }
            JSONObject user = new JSONObject();
            user.put("user_list", userList);
            JSONObject usersJson = WxApiClient.batchgetList(tokenObject.getString("access_token"), user.toJSONString());
            list = JsonUtil.getJsonToList(usersJson.getString("user_info_list"), MPUserModel.class);
            return pagination.setData(list, resultJObject.getInteger("total"));
        }
        return pagination.setData(list,0);
    }

    @Override
    public List<MPUserModel> getList() throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject resultJObject = WxApiClient.userList(tokenObject.getString("access_token"), null);
        JSONObject data = resultJObject.getJSONObject("data");
        List<MPUserModel> list = new ArrayList<>();
        if (null != data) {
            List<String> openIdList = JsonUtil.getJsonToList(data.getString("openid"), String.class);
            JSONArray userList = new JSONArray();
            for (String openId : openIdList) {
                JSONObject object = new JSONObject();
                object.put("openid", openId);
                object.put("lang", "zh_CN");
                userList.add(object);
            }
            JSONObject user = new JSONObject();
            user.put("user_list", userList);
            JSONObject usersJson = WxApiClient.batchgetList(tokenObject.getString("access_token"), user.toJSONString());
            list = JsonUtil.getJsonToList(usersJson.getString("user_info_list"), MPUserModel.class);
        }
        return list;
    }

    @Override
    public List<MPUserModel> GetListByTagId(Pagination pagination, String tagId) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject tag = new JSONObject();
        tag.put("tagid", Integer.parseInt(tagId));
        tag.put("next_openid", "");
        JSONObject resultJObject = WxApiClient.tagUserList(tokenObject.getString("access_token"), tag.toJSONString());
        JSONObject data = resultJObject.getJSONObject("data");
        List<MPUserModel> list = new ArrayList<>();
        if (null != data) {
            List<String> openIdList = JsonUtil.getJsonToList(data.getString("openid"), String.class);
            JSONArray userList = new JSONArray();
            for (String openId : openIdList) {
                JSONObject object = new JSONObject();
                object.put("openid", openId);
                object.put("lang", "zh_CN");
                userList.add(object);
            }
            JSONObject user = new JSONObject();
            user.put("user_list", userList);
            JSONObject usersJson = WxApiClient.batchgetList(tokenObject.getString("access_token"), user.toJSONString());
            list = JsonUtil.getJsonToList(usersJson.getString("user_info_list"), MPUserModel.class);
            return pagination.setData(list, resultJObject.getInteger("count"));
        } else {
            return pagination.setData(list,0);
        }
    }

    @Override
    public List<MPUserModel> GetBlackList(Pagination pagination) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject beginOpenId = new JSONObject();
        beginOpenId.put("begin_openid", "");
        JSONObject resultJObject = WxApiClient.blackList(tokenObject.getString("access_token"), beginOpenId.toJSONString());
        JSONObject data = resultJObject.getJSONObject("data");
        List<MPUserModel> list = new ArrayList<>();
        if (null != data) {
            List<String> openIdList = JsonUtil.getJsonToList(data.getString("openid"), String.class);
            JSONArray userList = new JSONArray();
            for (String openId : openIdList) {
                JSONObject object = new JSONObject();
                object.put("openid", openId);
                object.put("lang", "zh_CN");
                userList.add(object);
            }
            JSONObject user = new JSONObject();
            user.put("user_list", userList);
            JSONObject usersJson = WxApiClient.batchgetList(tokenObject.getString("access_token"), user.toJSONString());
            list = JsonUtil.getJsonToList(usersJson.getString("user_info_list"), MPUserModel.class);
            return pagination.setData(list, resultJObject.getInteger("total"));
        } else {
            return pagination.setData(list,0);
        }
    }

    @Override
    public MPUserModel UserInfo(String openId) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject object = WxApiClient.userInfo(tokenObject.getString("access_token"), openId);
        MPUserModel model = JsonUtil.getJsonToBean(object.toJSONString(), MPUserModel.class);
        return model;
    }

    @Override
    public boolean UpdateRemark(MPUserModel userModel) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject object = new JSONObject();
        object.put("openid", userModel.getOpenid());
        object.put("remark", userModel.getRemark());
        JSONObject rstObj=WxApiClient.updateRemark(tokenObject.getString("access_token"), object.toJSONString());
        if(rstObj==null){
            return false;
        }
        return true;
    }

    @Override
    public boolean AddBatchBlack(String openId) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        String[] openIds = openId.split(",");
        List<String> openidList = Arrays.asList(openIds);
        JSONObject object = new JSONObject();
        object.put("openid_list", openidList);
        JSONObject rstObj= WxApiClient.batchBlackList(tokenObject.getString("access_token"), object.toJSONString());
        if(rstObj==null){
            return false;
        }
        return true;
    }

    @Override
    public boolean DeleteBatchUnBlack(String openId) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        String[] openIds = openId.split(",");
        List<String> openidList = Arrays.asList(openIds);
        JSONObject object = new JSONObject();
        object.put("openid_list", openidList);
        JSONObject rstObj= WxApiClient.batchunBlackList(tokenObject.getString("access_token"), object.toJSONString());
        if(rstObj==null){
            return false;
        }
        return true;
    }
}

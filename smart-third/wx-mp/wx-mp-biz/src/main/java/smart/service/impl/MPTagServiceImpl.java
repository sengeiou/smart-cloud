package smart.service.impl;

import com.alibaba.fastjson.JSONObject;
import smart.model.BaseSystemInfo;
import smart.util.JsonUtil;
import smart.base.SysConfigApi;
import smart.exception.WxErrorException;
import smart.model.mptag.MPTagsModel;
import smart.service.MPTagService;
import smart.util.WxApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class MPTagServiceImpl implements MPTagService {

    @Autowired
    private SysConfigApi sysConfigApi;

    @Override
    public List<MPTagsModel> GetTageList() throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject jsonObject = WxApiClient.userTagList(tokenObject.getString("access_token"));
        List<MPTagsModel> tagsList = JsonUtil.getJsonToList(jsonObject.getString("tags"), MPTagsModel.class);
        return tagsList;
    }

    @Override
    public void CreateTag(String tagName) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject name = new JSONObject();
        name.put("name", name);
        JSONObject tag = new JSONObject();
        tag.put("tag", name);
        WxApiClient.createUserTag(tag.toJSONString(), tokenObject.getString("access_token"));
    }

    @Override
    public void UpdateTag(MPTagsModel tagsModel) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject update = new JSONObject();
        update.put("name", tagsModel.getName());
        update.put("id", tagsModel.getId());
        JSONObject tag = new JSONObject();
        tag.put("tag", update);
        WxApiClient.updateUserTag(tag.toJSONString(), tokenObject.getString("access_token"));
    }

    @Override
    public boolean DeleteTag(int id) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject delete = new JSONObject();
        delete.put("id", id);
        JSONObject tag = new JSONObject();
        tag.put("tag", delete);
        JSONObject rstObj= WxApiClient.deleteUserTag(tag.toJSONString(), tokenObject.getString("access_token"));
         if(rstObj==null){
             return false;
         }
        return true;
    }

    @Override
    public void BatchTagged(String[] openid, String tagId) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject members = new JSONObject();
        members.put("openid_list", openid);
        members.put("tagid", Integer.parseInt(tagId));
        WxApiClient.batchTagged(members.toJSONString(), tokenObject.getString("access_token"));
    }

    @Override
    public void BatchUnTagged(String[] openid, String tagId) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        JSONObject members = new JSONObject();
        members.put("openid_list", openid);
        members.put("tagid", Integer.parseInt(tagId));
        WxApiClient.batchUnTagged(members.toJSONString(), tokenObject.getString("access_token"));
    }
}

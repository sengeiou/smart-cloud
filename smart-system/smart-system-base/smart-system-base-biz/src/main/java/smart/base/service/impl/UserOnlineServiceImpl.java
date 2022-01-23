package smart.base.service.impl;

import smart.base.Page;
import smart.base.UserInfo;
import smart.base.service.UserOnlineService;
import smart.util.JsonUtil;
import smart.util.StringUtil;
import smart.base.model.UserOnlineModel;
import smart.util.CacheKeyUtil;
import smart.util.RedisUtil;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 在线用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Service
public class UserOnlineServiceImpl implements UserOnlineService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;

    @Override
    public List<UserOnlineModel> getList(Page page) {
        List<UserOnlineModel> userOnlineList = new ArrayList<>();
        String onlineUserKey = cacheKeyUtil.getLoginOnline();
        Set<String> cacheKeys = redisUtil.getAllKeys();
        for (String cacheKey : cacheKeys) {
            if (cacheKey.length() > onlineUserKey.length() && cacheKey.substring(0, onlineUserKey.length()).equals(onlineUserKey)) {
               if(cacheKey.contains(onlineUserKey)) {
                   String onlineToken = String.valueOf(redisUtil.getString(cacheKey));
                   String online = String.valueOf(redisUtil.getString(onlineToken));
                   UserInfo userInfo = JsonUtil.getJsonToBean(online, UserInfo.class);
                   if (userInfo != null) {
                       UserOnlineModel userOnlineModel = new UserOnlineModel();
                       userOnlineModel.setUserId(userInfo.getUserId());
                       userOnlineModel.setUserName((userInfo.getUserName()) + "/" + userInfo.getUserAccount());
                       userOnlineModel.setLoginIPAddress(userInfo.getLoginIpAddress());
                       userOnlineModel.setLoginTime(userInfo.getLoginTime());
                       userOnlineModel.setLoginPlatForm(userInfo.getLoginPlatForm());
                       userOnlineModel.setTenantId(userInfo.getTenantId());
                       userOnlineModel.setToken(onlineToken);
                       userOnlineList.add(userOnlineModel);
                   }
               }
            }
        }
        String tenantId =userProvider.get().getTenantId();
        userOnlineList = userOnlineList.stream().filter(t -> String.valueOf(t.getTenantId()).equals(String.valueOf(tenantId))).collect(Collectors.toList());
        if(!StringUtil.isEmpty(page.getKeyword())){
            userOnlineList=userOnlineList.stream().filter(t->t.getUserName().contains(page.getKeyword())).collect(Collectors.toList());
        }
        userOnlineList.sort(Comparator.comparing(UserOnlineModel::getLoginTime).reversed());
        return userOnlineList;
    }

    @Override
    public void delete(String id) {
        userProvider.removeOnLine(id);
    }
}

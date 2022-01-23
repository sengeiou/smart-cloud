package smart.util;

import com.alibaba.fastjson.JSONObject;
import smart.base.OnlineUserModel;
import smart.base.OnlineUserProvider;
import smart.base.UserInfo;
import smart.util.jwt.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:57
 */
@Component
public class UserProvider {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;

    /**
     * 获取token
     */
    public static String getToken() {
        String toke = getAuthorize();
        return toke;
    }

    /**
     * 获取
     *
     * @param token
     * @return
     */
    public UserInfo get(String token) {
        UserInfo userInfo = new UserInfo();
        String tokens = null;
        if (token != null) {
            tokens = token;
        } else {
            tokens = UserProvider.getToken();
        }
        if (tokens != null) {
            tokens= JwtUtil.getRealToken(tokens);
            userInfo = JsonUtil.getJsonToBean(String.valueOf(redisUtil.getString(tokens)), UserInfo.class);
        }
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        return userInfo;
    }

    /**
     * 获取
     *
     * @return
     */
    public UserInfo get() {
        String tokens = UserProvider.getToken();
        return this.get(tokens);
    }

    /**
     * 获取Authorize
     */
    public static String getAuthorize() {
        String authorize = ServletUtil.getHeader(Constants.AUTHORIZATION);
        return authorize;
    }

    /**
     * 创建
     *
     * @param userInfo
     * @return
     */
    public void add(UserInfo userInfo) {
        String userId = userInfo.getUserId();
        String token = RandomUtil.uuId();
        long time= DateUtil.getTime(userInfo.getOverdueTime()) - DateUtil.getTime(new Date());

        String authorize = String.valueOf(redisUtil.getString(cacheKeyUtil.getUserAuthorize() + userId));
        String loginOnlineKey=cacheKeyUtil.getLoginOnline() + userId;
        redisUtil.remove(authorize);
        userInfo.setId(cacheKeyUtil.getLoginToken(userInfo.getTenantId()) + token);
        //记录Token
        redisUtil.insert(userInfo.getId(), userInfo,time);
        //记录在线
        if (ServletUtil.getIsMobileDevice()) {
            redisUtil.insert(cacheKeyUtil.getMobileLoginOnline() + userId, userInfo.getId(), time);
            //记录移动设备CID,用于消息推送
            if (ServletUtil.getHeader("clientId") != null) {
                String clientId = ServletUtil.getHeader("clientId");
                Map<String, String> map = new HashMap<>(16);
                map.put(userInfo.getUserId(), clientId);
                redisUtil.insert(cacheKeyUtil.getMobileDeviceList(), map);
            }
        } else {
            redisUtil.insert(loginOnlineKey, userInfo.getId(), time);
        }
    }

    /**
     * 移除在线
     */
    public void removeWebSocket(UserInfo userInfo) {
        //清除websocket登录状态
        String userId = String.valueOf(userInfo.getUserId());
        String tenandId = "null".equals(String.valueOf(userInfo.getTenantId())) ? "" : userInfo.getTenantId();
        OnlineUserModel user = OnlineUserProvider.getOnlineUserList().stream().filter(t -> userId.equals(t.getUserId()) && tenandId.equals(t.getTenantId())).findFirst().orElse(null);
        if (user != null) {
            JSONObject object = new JSONObject();
            object.put("method", "logout");
            user.getWebSocket().getAsyncRemote().sendText(object.toJSONString());
        }
    }

    /**
     * 移除
     */
    public void remove() {
        UserInfo userInfo = this.get();
        String userId = userInfo.getUserId();

        if (ServletUtil.getIsMobileDevice()) {
            redisUtil.removeHash(cacheKeyUtil.getMobileDeviceList(), userId);
        }
        if (userInfo.getId() != null) {
            redisUtil.remove(userInfo.getId());
        }
        redisUtil.remove(cacheKeyUtil.getUserAuthorize() + userId);
        redisUtil.remove(cacheKeyUtil.getLoginOnline() + userId);
        redisUtil.remove(cacheKeyUtil.getSystemInfo());
    }

    /**
     * 移除
     */
    public void removeCurrent() {
        UserInfo userInfo = this.get();
        String userId = userInfo.getUserId();
        if (ServletUtil.getIsMobileDevice()){
            String key = String.valueOf(redisUtil.getString(cacheKeyUtil.getMobileLoginOnline() + userInfo.getUserId()));
            redisUtil.remove(key);
            redisUtil.remove(cacheKeyUtil.getMobileLoginOnline() + userInfo.getUserId());
        }else {
            String key = String.valueOf(redisUtil.getString(cacheKeyUtil.getLoginOnline() + userInfo.getUserId()));
            redisUtil.remove(key);
            redisUtil.remove(cacheKeyUtil.getLoginOnline() + userInfo.getUserId());
        }
        redisUtil.remove(cacheKeyUtil.getUserAuthorize() + userId);
        redisUtil.remove(cacheKeyUtil.getSystemInfo());
    }

    /**
     * 移除在线
     */
    public void removeOnLine(String userId) {

        if (userId == null) {
            return;
        }
        String onlineToken = String.valueOf(redisUtil.getString(cacheKeyUtil.getLoginOnline() + userId));
        String mobileOnlineToken = String.valueOf(redisUtil.getString(cacheKeyUtil.getMobileLoginOnline() + userId));
        if (!StringUtils.isEmpty(onlineToken)) {
            redisUtil.remove(cacheKeyUtil.getLoginOnline() + userId);
            redisUtil.remove(onlineToken);
        }
        if (!StringUtils.isEmpty(mobileOnlineToken)) {
            redisUtil.remove(cacheKeyUtil.getMobileLoginOnline() + userId);
            redisUtil.removeHash(cacheKeyUtil.getMobileDeviceList(), userId);
        }
    }

    /**
     * 是否在线
     */
    public boolean isOnLine() {

        UserInfo userInfo = this.get();
        String userId = userInfo.getUserId();
        String onlineToken = String.valueOf(redisUtil.getString((ServletUtil.getIsMobileDevice() == true ? cacheKeyUtil.getMobileLoginOnline() : cacheKeyUtil.getLoginOnline()) + userId));
        return onlineToken.equals(userInfo.getId()) ? false : true;
    }

    /**
     * 是否过期
     */
    public boolean isOverdue() {
        UserInfo userInfo = this.get();
        return StringUtils.isEmpty(userInfo.getId()) ? false : true;
    }

    /**
     * 是否登陆
     */
    public boolean isLogined() {

        UserInfo userInfo = this.get();
        String userOnline = (ServletUtil.getIsMobileDevice() == true ? cacheKeyUtil.getMobileLoginOnline() : cacheKeyUtil.getLoginOnline()) + userInfo.getUserId();
        Object online = redisUtil.getString(userOnline);
        return online == null ? true : false;
    }

    /**
     * 通过上下文的租户查redis来获取token
     *
     * @return
     */
    public UserInfo get(String userId,String tenantId) {
        UserInfo userInfo;
        tenantId = "null".equals(String.valueOf(tenantId)) ? "" : tenantId;
        String token = tenantId + "login_online_" + userId;
        if (ServletUtil.getIsMobileDevice()) {
            token = tenantId + "login_online_mobile_" + userId;
        }
        String onlineInfo = String.valueOf(redisUtil.getString(token));
        userInfo = JsonUtil.getJsonToBean(String.valueOf(redisUtil.getString(onlineInfo)), UserInfo.class);
        return userInfo == null ? new UserInfo() : userInfo;
    }

}

package smart.permission.util;

import com.alibaba.fastjson.JSONObject;
import smart.base.OnlineUserModel;
import smart.base.OnlineUserProvider;

public class RemoveUtil {

    public static void removeOnLine(String userId) {
        String id = userId;
        OnlineUserModel user = OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getUserId().equals(id)).findFirst().isPresent() ? OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getUserId().equals(id)).findFirst().get() : null;
        if (user != null) {
            OnlineUserProvider.getOnlineUserList().remove(user);
        }
        if (OnlineUserProvider.getOnlineUserList().stream().filter(t -> String.valueOf(t.getUserId()).equals(id)).count() == 0) {
            for (OnlineUserModel item : OnlineUserProvider.getOnlineUserList()) {
                if (!item.getUserId().equals(id)) {
                    JSONObject map = new JSONObject();
                    map.put("method", "Offline");
                    map.put("userId", id);
                    item.getWebSocket().getAsyncRemote().sendText(map.toJSONString());
                }
            }
        }
    }
}

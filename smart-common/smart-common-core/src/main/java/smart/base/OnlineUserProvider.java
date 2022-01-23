package smart.base;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:51
 */
public class OnlineUserProvider {

    /**
     * 在线用户
     */
    private static List<OnlineUserModel> onlineUserList = new ArrayList<>();

    public static List<OnlineUserModel> getOnlineUserList() {
        return OnlineUserProvider.onlineUserList;
    }

    public static void addModel(OnlineUserModel model){
        OnlineUserProvider.onlineUserList.add(model);
    }
}

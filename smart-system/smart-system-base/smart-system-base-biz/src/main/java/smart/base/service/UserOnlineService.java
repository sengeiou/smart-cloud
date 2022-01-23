package smart.base.service;


import smart.base.Page;
import smart.base.model.UserOnlineModel;

import java.util.List;

/**
 * 在线用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface UserOnlineService {

    /**
     * 列表
     *
     * @return
     */
    List<UserOnlineModel> getList(Page page);

    /**
     * 删除
     *
     * @param id 主键值
     */
    void delete(String id);
}

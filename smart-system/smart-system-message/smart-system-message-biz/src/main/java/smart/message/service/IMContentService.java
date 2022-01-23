package smart.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.PageModel;
import smart.message.entity.IMContentEntity;
import smart.message.model.IMUnreadNumModel;

import java.util.List;

/**
 * 聊天内容
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface IMContentService extends IService<IMContentEntity> {

    /**
     * 获取消息列表
     *
     * @param sendUserId    发送者
     * @param receiveUserId 接收者
     * @param pageModel
     * @return
     */
    List<IMContentEntity> getMessageList(String sendUserId, String receiveUserId, PageModel pageModel);

    /**
     * 获取未读消息
     *
     * @param receiveUserId 接收者
     * @return
     */
    List<IMUnreadNumModel> getUnreadList(String receiveUserId);

    /**
     * 获取未读消息
     *
     * @param receiveUserId 接收者
     * @return
     */
    long getUnreadCount(String receiveUserId);

    /**
     * 发送消息
     *
     * @param sendUserId    发送者
     * @param receiveUserId 接收者
     * @param message       消息内容
     * @param messageType   消息类型
     * @return
     */
    void sendMessage(String sendUserId, String receiveUserId, String message, String messageType);

    /**
     * 已读消息
     *
     * @param sendUserId    发送者
     * @param receiveUserId 接收者
     * @return
     */
    void readMessage(String sendUserId, String receiveUserId);
}

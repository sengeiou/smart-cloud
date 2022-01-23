package smart.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.Pagination;
import smart.message.entity.MessageEntity;

import java.util.List;

/**
 * 消息实例
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface MessageService extends IService<MessageEntity> {

    /**
     * 列表（通知公告）
     *
     * @param pagination
     * @return
     */
    List<MessageEntity> getNoticeList(Pagination pagination);

    /**
     * 列表（通知公告）
     *
     * @return
     */
    List<MessageEntity> getNoticeList();

    /**
     * 列表（通知公告/系统消息/私信消息）
     *
     * @param pagination
     * @param type      类别
     * @return
     */
    List<MessageEntity> getMessageList(Pagination pagination, String type);

    /**
     * 列表（通知公告/系统消息/私信消息）
     *
     * @param pagination
     * @return
     */
    List<MessageEntity> getMessageList(Pagination pagination);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    MessageEntity getInfo(String id);

    /**
     * 默认消息
     * @param type 类别:1-通知公告/2-系统消息
     * @return
     */
    MessageEntity getInfoDefault(int type);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(MessageEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(MessageEntity entity);

    /**
     * 更新
     *
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, MessageEntity entity);

    /**
     * 消息已读（单条）
     *
     * @param messageId 消息主键
     */
    void messageRead(String messageId);

    /**
     * 消息已读（全部）
     */
    void messageRead();

    /**
     * 删除记录
     *
     * @param messageIds 消息Id
     */
    void deleteRecord(List<String> messageIds);

    /**
     * 获取未读数量（含 通知公告、系统消息）
     *
     * @param userId 用户主键
     * @return
     */
    long getUnreadCount(String userId);

    /**
     * 获取公告未读数量
     *
     * @param userId 用户主键
     * @return
     */
    int getUnreadNoticeCount(String userId);

    /**
     * 获取消息未读数量
     *
     * @param userId 用户主键
     * @return
     */
    int getUnreadMessageCount(String userId);

    /**
     * 发送公告
     *
     * @param toUserIds 发送用户
     * @param entity    消息信息
     */
    void sentNotice(List<String> toUserIds, MessageEntity entity);

    /**
     * 发送消息
     *
     * @param toUserIds 发送用户
     * @param title     标题
     */
    void sentMessage(List<String> toUserIds, String title);

    /**
     * 发送消息
     *
     * @param toUserIds 发送用户
     * @param title     标题
     * @param bodyText  内容
     */
    void sentMessage(List<String> toUserIds, String title, String bodyText);
}

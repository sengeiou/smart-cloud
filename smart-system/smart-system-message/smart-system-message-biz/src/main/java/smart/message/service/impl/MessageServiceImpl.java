package smart.message.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.PageUtil;
import smart.util.RandomUtil;
import smart.base.OnlineUserModel;
import smart.base.OnlineUserProvider;
import smart.base.Pagination;
import smart.base.UserInfo;
import smart.message.entity.MessageEntity;
import smart.message.entity.MessageReceiveEntity;
import smart.util.UserProvider;
import smart.message.mapper.MessageMapper;
import smart.message.service.MessageService;
import smart.message.service.MessagereceiveService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 消息实例
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageEntity> implements MessageService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private MessagereceiveService messagereceiveService;

    @Override
    public List<MessageEntity> getNoticeList(Pagination pagination) {
        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageEntity::getType, 1);
        //关键词（消息标题）
        if (!StringUtils.isEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().like(MessageEntity::getTitle, pagination.getKeyword());
        }
        //默认排序
        queryWrapper.lambda().orderByDesc(MessageEntity::getCreatorTime);
        Page<MessageEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<MessageEntity> userIPage = this.page(page, queryWrapper);
        return pagination.setData(userIPage.getRecords(), page.getTotal());
    }
    @Override
    public List<MessageEntity> getNoticeList() {
        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageEntity::getType, 1);

        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<MessageEntity> getMessageList(Pagination pagination, String type) {
        String userId = userProvider.get().getUserId();
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        StringBuilder sql = new StringBuilder();
        //关键词（消息标题）
        if (!StringUtils.isEmpty(pagination.getKeyword())) {
            sql.append(" AND m.F_Title like '%" + pagination.getKeyword() + "%' ");
        }
        //消息类别
        if (!StringUtils.isEmpty(type)) {
            sql.append(" AND m.F_Type = '" + type + "'");
        }
        sql.append(" ORDER BY  F_LastModifyTime desc");
        map.put("sql", sql.toString());
        List<MessageEntity> lists = this.baseMapper.getMessageList(map);
        return pagination.setData(PageUtil.getListPage((int) pagination.getCurrentPage(), (int) pagination.getPageSize(), lists), lists.size());
    }

    @Override
    public List<MessageEntity> getMessageList(Pagination pagination) {
        return this.getMessageList(pagination, null);
    }

    @Override
    public MessageEntity getInfo(String id) {
        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public MessageEntity getInfoDefault(int type) {
        List<MessageEntity> list = this.baseMapper.getInfoDefault(type);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            MessageEntity entity = new MessageEntity();
            return entity;
        }
    }

    @Override
    @Transactional
    public void delete(MessageEntity entity) {
        this.removeById(entity.getId());
        QueryWrapper<MessageReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageReceiveEntity::getMessageId, entity.getId());
        messagereceiveService.remove(queryWrapper);
    }

    @Override
    public void create(MessageEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setBodyText(entity.getBodyText());
        entity.setType(1);
        entity.setEnabledMark(0);
        entity.setCreatorUser(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, MessageEntity entity) {
        entity.setId(id);
        entity.setBodyText(entity.getBodyText());
        entity.setCreatorUser(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void messageRead(String messageId) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<MessageReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageReceiveEntity::getUserId, userId).eq(MessageReceiveEntity::getMessageId, messageId);
        MessageReceiveEntity entity = messagereceiveService.getOne(queryWrapper);
        if (entity != null) {
            entity.setIsRead(1);
            entity.setReadCount(entity.getReadCount() == null ? 1 : entity.getReadCount() + 1);
            entity.setReadTime(new Date());
            messagereceiveService.updateById(entity);
        }
    }

    @Override
    @Transactional
    public void messageRead() {
        String userId = userProvider.get().getUserId();
        QueryWrapper<MessageReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageReceiveEntity::getUserId, userId).eq(MessageReceiveEntity::getIsRead, 0);
        List<MessageReceiveEntity> entitys = messagereceiveService.list(queryWrapper);
        if (entitys.size() > 0) {
            for (MessageReceiveEntity entity : entitys) {
                entity.setIsRead(1);
                entity.setReadCount(entity.getReadCount() == null ? 1 : entity.getReadCount() + 1);
                entity.setReadTime(new Date());
                messagereceiveService.updateById(entity);
            }
        }
    }

    @Override
    @Transactional
    public void deleteRecord(List<String> messageIds) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<MessageReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageReceiveEntity::getUserId, userId).in(MessageReceiveEntity::getMessageId, messageIds);
        messagereceiveService.remove(queryWrapper);
    }

    @Override
    public long getUnreadCount(String userId) {
        QueryWrapper<MessageReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageReceiveEntity::getUserId, userId).eq(MessageReceiveEntity::getIsRead, 0);
        return messagereceiveService.count(queryWrapper);
    }

    @Override
    public int getUnreadNoticeCount(String userId) {
        int result = this.baseMapper.getUnreadNoticeCount(userId);
        return result;
    }

    @Override
    public int getUnreadMessageCount(String userId) {
        int result = this.baseMapper.getUnreadMessageCount(userId);
        return result;
    }

    @Override
    @Transactional
    public void sentNotice(List<String> toUserIds, MessageEntity entity) {
        UserInfo userInfo = userProvider.get();
        entity.setEnabledMark(1);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userInfo.getUserId());
        this.updateById(entity);
        List<MessageReceiveEntity> receiveEntityList = new ArrayList<>();
        for (String item : toUserIds) {
            MessageReceiveEntity messageReceiveEntity = new MessageReceiveEntity();
            messageReceiveEntity.setId(RandomUtil.uuId());
            messageReceiveEntity.setMessageId(entity.getId());
            messageReceiveEntity.setUserId(item);
            messageReceiveEntity.setIsRead(0);
            receiveEntityList.add(messageReceiveEntity);
            messagereceiveService.save(messageReceiveEntity);
        }
        //消息推送 - PC端
        for (int i = 0; i < toUserIds.size(); i++) {
            for (OnlineUserModel item : OnlineUserProvider.getOnlineUserList()) {
                if (toUserIds.get(i).equals(item.getUserId()) && userInfo.getTenantId().equals(item.getTenantId())) {
                    JSONObject map = new JSONObject();
                    map.put("method", "messagePush");
                    map.put("unreadNoticeCount", 1);
                    map.put("userId", userInfo.getTenantId());
                    map.put("toUserId", toUserIds);
                    map.put("title", entity.getTitle());
                    synchronized (map) {
                        item.getWebSocket().getAsyncRemote().sendText(map.toJSONString());
                    }
                }
            }
        }
//        //消息推送 - APP
//        GetuiAppPushUtil getuiAppPushUtil = new GetuiAppPushUtil(configValueUtil.getIgexinAppid(), configValueUtil.getIgexinAppkey(), configValueUtil.getIgexinMastersecret(), Boolean.parseBoolean(configValueUtil.getIgexinEnabled()));
//        getuiAppPushUtil.SendNotice(userInfo.getTenantId(), toUserIds, "系统公告", entity.getTitle(), "1");
    }

    @Override
    public void sentMessage(List<String> toUserIds, String title) {
        this.sentMessage(toUserIds, title, null);
    }

    @Override
    @Transactional
    public void sentMessage(List<String> toUserIds, String title, String bodyText) {
        UserInfo userInfo = userProvider.get();
        MessageEntity entity = new MessageEntity();
        entity.setTitle(title);
        entity.setBodyText(bodyText);
        entity.setId(RandomUtil.uuId());
        entity.setType(2);
        entity.setCreatorUser(userInfo.getUserId());
        entity.setLastModifyTime(entity.getCreatorTime());
        entity.setLastModifyUserId(entity.getCreatorUser());
        List<MessageReceiveEntity> receiveEntityList = new ArrayList<>();
        for (String item : toUserIds) {
            MessageReceiveEntity messageReceiveEntity = new MessageReceiveEntity();
            messageReceiveEntity.setId(RandomUtil.uuId());
            messageReceiveEntity.setMessageId(entity.getId());
            messageReceiveEntity.setUserId(item);
            messageReceiveEntity.setIsRead(0);
            receiveEntityList.add(messageReceiveEntity);
        }
        this.save(entity);
        for (MessageReceiveEntity messageReceiveEntity : receiveEntityList) {
            messagereceiveService.save(messageReceiveEntity);
        }
        //消息推送 - PC端
        for (int i = 0; i < toUserIds.size(); i++) {
            for (OnlineUserModel item : OnlineUserProvider.getOnlineUserList()) {
                if (toUserIds.get(i).equals(item.getUserId()) && userInfo.getTenantId().equals(item.getTenantId())) {
                    JSONObject map = new JSONObject();
                    map.put("method", "messagePush");
                    map.put("unreadNoticeCount", 2);
                    map.put("userId", userInfo.getTenantId());
                    map.put("toUserId", toUserIds);
                    map.put("title", entity.getTitle());
                    synchronized (map) {
                        item.getWebSocket().getAsyncRemote().sendText(map.toJSONString());
                    }
                }
            }
        }
//        //消息推送 - APP
//        GetuiAppPushUtil getuiAppPushUtil = new GetuiAppPushUtil(configValueUtil.getIgexinAppid(), configValueUtil.getIgexinAppkey(), configValueUtil.getIgexinMastersecret(), Boolean.parseBoolean(configValueUtil.getIgexinEnabled()));
//        getuiAppPushUtil.SendNotice(userInfo.getTenantId(), toUserIds, "系统消息", entity.getTitle(), "2");
    }
}

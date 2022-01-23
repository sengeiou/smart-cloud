package smart.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.RandomUtil;
import smart.base.PageModel;
import smart.message.entity.IMContentEntity;
import smart.message.model.IMUnreadNumModel;
import smart.message.service.IMContentService;
import smart.message.mapper.IMContentMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 聊天内容
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class IMContentServiceImpl extends ServiceImpl<IMContentMapper, IMContentEntity> implements IMContentService {

    @Override
    public List<IMContentEntity> getMessageList(String sendUserId, String receiveUserId, PageModel pageModel) {
        QueryWrapper<IMContentEntity> queryWrapper = new QueryWrapper<>();
        //发件人、收件人
        if (!StringUtils.isEmpty(sendUserId) && !StringUtils.isEmpty(receiveUserId)) {
            queryWrapper.lambda().and(wrapper -> {
                wrapper.eq(IMContentEntity::getSendUserId, sendUserId);
                wrapper.eq(IMContentEntity::getReceiveUserId, receiveUserId);
                wrapper.or().eq(IMContentEntity::getSendUserId, receiveUserId);
                wrapper.eq(IMContentEntity::getReceiveUserId, sendUserId);
            });
        }
        //关键字查询
        if (pageModel != null && pageModel.getKeyword() != null) {
            queryWrapper.lambda().like(IMContentEntity::getContent, pageModel.getKeyword());
        }
        //排序
        pageModel.setSidx("F_SendTime");
        if (StringUtils.isEmpty(pageModel.getSidx())) {
            queryWrapper.lambda().orderByDesc(IMContentEntity::getSendTime);
        } else {
            queryWrapper = "asc".equals(pageModel.getSord().toLowerCase()) ? queryWrapper.orderByAsc(pageModel.getSidx()) : queryWrapper.orderByDesc(pageModel.getSidx());
        }
        Page<IMContentEntity> page = new Page<>(pageModel.getPage(), pageModel.getRows());
        IPage<IMContentEntity> userIPage = this.page(page, queryWrapper);
        return pageModel.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public List<IMUnreadNumModel> getUnreadList(String receiveUserId) {
        List<IMUnreadNumModel> list = this.baseMapper.getUnreadList(receiveUserId);
        List<IMUnreadNumModel> list1 = this.baseMapper.getUnreadLists(receiveUserId);
        for (IMUnreadNumModel item : list) {
            IMUnreadNumModel defaultItem = list1.stream().filter(q -> q.getSendUserId().equals(item.getSendUserId())).findFirst().get();
            item.setDefaultMessage(defaultItem.getDefaultMessage());
            item.setDefaultMessageType(defaultItem.getDefaultMessageType());
            item.setDefaultMessageTime(defaultItem.getDefaultMessageTime());
        }
        return list;
    }

    @Override
    public long getUnreadCount(String receiveUserId) {
        QueryWrapper<IMContentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(IMContentEntity::getReceiveUserId, receiveUserId).eq(IMContentEntity::getState, 0);
        return this.count(queryWrapper);
    }

    @Override
    public void sendMessage(String sendUserId, String receiveUserId, String message, String messageType) {
        IMContentEntity entity = new IMContentEntity();
        entity.setId(RandomUtil.uuId());
        entity.setSendUserId(sendUserId);
        entity.setSendTime(new Date());
        entity.setReceiveUserId(receiveUserId);
        entity.setState(0);
        entity.setContent(message);
        entity.setContentType(messageType);
        this.save(entity);
    }

    @Override
    public void readMessage(String sendUserId, String receiveUserId) {
        QueryWrapper<IMContentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(IMContentEntity::getSendUserId,sendUserId);
        queryWrapper.lambda().eq(IMContentEntity::getReceiveUserId,receiveUserId);
        queryWrapper.lambda().eq(IMContentEntity::getState,0);
        List<IMContentEntity> list = this.list(queryWrapper);
        for (IMContentEntity entity : list){
            entity.setState(1);
            entity.setReceiveTime(new Date());
            this.updateById(entity);
        }
    }
}

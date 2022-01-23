package smart.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.model.BaseSystemInfo;
import smart.util.*;
import smart.base.SysConfigApi;
import smart.exception.WxErrorException;
import smart.model.mpmessage.PaginationMPMessage;
import smart.MPMaterialEntity;
import smart.MPMessageEntity;
import smart.mapper.MPMessageMapper;
import smart.service.MPMaterialService;
import smart.service.MPMessageService;
import smart.util.type.SortType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 公众号群发消息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Slf4j
@Service
public class MPMessageServiceImpl extends ServiceImpl<MPMessageMapper, MPMessageEntity> implements MPMessageService {

    @Autowired
    private SysConfigApi sysConfigApi;
    @Autowired
    private MPMaterialService mpMaterialService;
    @Autowired
    private UserProvider userProvider;

    @Override
    public List<MPMessageEntity> getList(PaginationMPMessage paginationMPMessage) {
        QueryWrapper<MPMessageEntity> queryWrapper = new QueryWrapper<>();
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationMPMessage.getStartTime() != null ? paginationMPMessage.getStartTime() : null;
        String endTime = paginationMPMessage.getEndTime() != null ? paginationMPMessage.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(MPMessageEntity::getSendDate, startTimes).le(MPMessageEntity::getSendDate, endTimes);
        }
        //关键字
//        String keyWord = paginationMPMessage.getKeyword() != null ? String.valueOf(paginationMPMessage.getKeyword()) : null;
//        if (!StringUtils.isEmpty(keyWord)) {
//            queryWrapper.lambda().and(
//                    t -> t.like(MPMessageEntity::getTitle, keyWord)
//                            .or().like(MPMessageEntity::getOpenId, keyWord)
//            );
//        }
        //排序
        if (SortType.ASC.equals(paginationMPMessage.getSidx().toLowerCase())) {
            queryWrapper.lambda().orderByAsc(MPMessageEntity::getSendDate);
        } else {
            queryWrapper.lambda().orderByDesc(MPMessageEntity::getSendDate);
        }
        Page<MPMessageEntity> page = new Page<>(paginationMPMessage.getCurrentPage(), paginationMPMessage.getPageSize());
        IPage<MPMessageEntity> userIPage = this.page(page, queryWrapper);
        return paginationMPMessage.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public void Preview(String openId, MPMessageEntity entity) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        JSONObject mass = new JSONObject();
        entity.setOpenId(openId);
        if (entity.getMsgType().compareTo(1) != 0) {//非文本
            mass = this.UploadFileByMediaId(entity);
        } else {//文本
            String mediaId = entity.getTxtContent();
            JSONObject content = new JSONObject();
            content.put("content", mediaId);
            mass.put("text", content);
            mass.put("msgtype", "text");
        }
        mass.put("touser", openId);
        //获取token
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        WxApiClient.massPreview(tokenObject.getString("access_token"), mass.toJSONString());
        entity.setId(RandomUtil.uuId());
        entity.setEnabledMark(0);
        entity.setSortCode(RandomUtil.parses());
        entity.setSendUser(userProvider.get().getUserId());
        entity.setSendDate(new Date());
        this.save(entity);
    }

    @Override
    public void SendGroupMessageByTagId(MPMessageEntity entity) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        boolean isToAll = ("1".equals(entity.getIsToAll()) ? true : false);
        JSONObject mass = new JSONObject();
        if (entity.getMsgType().compareTo(1) != 0) {//非文本
            mass = this.UploadFileByMediaId(entity);
        } else {//文本
            String mediaId = entity.getTxtContent();
            JSONObject content = new JSONObject();
            content.put("content", mediaId);
            mass.put("text", content);
            mass.put("msgtype", "text");
        }
        JSONObject filter = new JSONObject();
        filter.put("is_to_all", isToAll);
        filter.put("tag_id", entity.getTagId());
        mass.put("filter", filter);
        //获取token
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        WxApiClient.massSendallUrl(tokenObject.getString("access_token"), mass.toJSONString());
        entity.setId(RandomUtil.uuId());
        entity.setEnabledMark(0);
        entity.setSortCode(RandomUtil.parses());
        entity.setSendDate(new Date());
        entity.setSendUser(userProvider.get().getUserId());
        this.save(entity);
    }

    /**
     * 上传文件获取MediaId
     *
     * @param entity 实体对象
     * @return
     */
    private JSONObject UploadFileByMediaId(MPMessageEntity entity) throws WxErrorException {
        String mediaId = null;
        MPMaterialEntity materialEntity = new MPMaterialEntity();
        materialEntity.setTitle(entity.getTitle());
        materialEntity.setOpenId(entity.getOpenId());
        materialEntity.setFileJson(entity.getFileJson());
        materialEntity.setMaterialsType(entity.getMsgType());
        JSONObject media_id = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        switch (entity.getMsgType()) {
            case 4://视频
                materialEntity.setIntroduction(entity.getTxtContent());
                mediaId = mpMaterialService.UploadForeverVideo(materialEntity);
                media_id.put("media_id", mediaId);
                jsonObject.put("mpvideo", media_id);
                jsonObject.put("msgtype", "mpvideo");
                break;
            case 5://图文
                materialEntity.setAuthor(entity.getAuthor());
                materialEntity.setContent(entity.getContent());
                materialEntity.setContentSourceUrl(entity.getContentSourceUrl());
                materialEntity.setDigest(entity.getDigest());
                materialEntity.setShowCoverPic(entity.getShowCoverPic());
                materialEntity.setNeedOpenComment(entity.getNeedOpenComment());
                materialEntity.setOnlyFansCanComment(entity.getOnlyFansCanComment());
                //上传图文
                mediaId = mpMaterialService.UploadNews(materialEntity);
                media_id.put("media_id", mediaId);
                jsonObject.put("mpnews", media_id);
                jsonObject.put("msgtype", "mpnews");
                break;
            case 2://图片
                mediaId = mpMaterialService.UploadForeverMedia(materialEntity);
                media_id.put("media_id", mediaId);
                jsonObject.put("image", media_id);
                jsonObject.put("msgtype", "image");
                break;
            case 3://语音
                mediaId = mpMaterialService.UploadForeverMedia(materialEntity);
                media_id.put("media_id", mediaId);
                jsonObject.put("voice", media_id);
                jsonObject.put("msgtype", "voice");
                break;
            default:
                break;
        }
        return jsonObject;
    }
}

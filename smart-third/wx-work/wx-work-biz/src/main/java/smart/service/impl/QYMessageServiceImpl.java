package smart.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.emnus.FileTypeEnum;
import smart.file.FileApi;
import smart.model.BaseSystemInfo;
import smart.util.*;
import smart.base.SysConfigApi;
import smart.QYMessageEntity;
import smart.exception.WxErrorException;
import smart.mapper.QYMessageMapper;
import smart.model.qymessage.PaginationQYMessage;
import smart.service.QYMessageService;
import smart.util.wxutil.MediaFileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 消息发送
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Slf4j
@Service
public class QYMessageServiceImpl extends ServiceImpl<QYMessageMapper, QYMessageEntity> implements QYMessageService {


    @Autowired
    private SysConfigApi sysConfigApi;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FileApi fileApi;

    @Override
    public List<QYMessageEntity> getList(PaginationQYMessage paginationQyMessage) {
        QueryWrapper<QYMessageEntity> queryWrapper = new QueryWrapper<>();
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationQyMessage.getStartTime() != null ? paginationQyMessage.getStartTime() : null;
        String endTime = paginationQyMessage.getEndTime() != null ? String.valueOf(paginationQyMessage.getEndTime()) : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(QYMessageEntity::getSendDate, startTimes).le(QYMessageEntity::getSendDate, endTimes);
        }
        //关键字
//        String keyWord = paginationTime.getKeyword() != null ? String.valueOf(paginationTime.getKeyword()) : null;
//        if (!StringUtils.isEmpty(keyWord)) {
//            queryWrapper.lambda().and(
//                    t -> t.like(QYMessageEntity::getTitle, keyWord)
//            );
//        }
        //排序
        if (StringUtils.isEmpty(paginationQyMessage.getSidx())) {
            queryWrapper.lambda().orderByDesc(QYMessageEntity::getSendDate);
        } else {
            queryWrapper = "asc".equals(paginationQyMessage.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationQyMessage.getSidx()) : queryWrapper.orderByDesc(paginationQyMessage.getSidx());
        }
        Page<QYMessageEntity> page = new Page<>(paginationQyMessage.getCurrentPage(), paginationQyMessage.getPageSize());
        IPage<QYMessageEntity> userIpage = this.page(page, queryWrapper);
        return paginationQyMessage.setData(userIpage.getRecords(), page.getTotal());
    }

    @Override
    public void sent(QYMessageEntity entity) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        String filePath = fileApi.getPath(FileTypeEnum.TEMPORARY) + entity.getFileJson();
        String touser = entity.getFAll().compareTo(1) == 0 ? "@all" : entity.getToUserId().replace(",", "|");//成员
        boolean result = true;
        //上传
        JSONObject tokenObject = QyApiClient.getAccessToken(config.getQyhCorpId(), config.getQyhAgentSecret());
        String mediaId = "1".equals(String.valueOf(entity.getMsgType())) ? "" : this.Upload(entity.getMsgType(), tokenObject.getString("access_token"), filePath);
        JSONObject message = new JSONObject();
        message.put("touser", touser);
        message.put("agentid", Integer.parseInt(config.getQyhAgentId()));
        JSONObject mediaIds = new JSONObject();
        switch (entity.getMsgType()) {
            case 1://文本
                JSONObject content = new JSONObject();
                content.put("content", entity.getTxtContent());
                message.put("text", content);
                message.put("msgtype", "text");
                result = QyApiClient.sendMessage(message.toJSONString(), tokenObject.getString("access_token"));
                break;
            case 2://图片
                mediaIds.put("media_id", mediaId);
                message.put("image", mediaIds);
                message.put("msgtype", "image");
                result = QyApiClient.sendMessage(message.toJSONString(), tokenObject.getString("access_token"));
                break;
            case 3://语音
                mediaIds.put("media_id", mediaId);
                message.put("voice", mediaIds);
                message.put("msgtype", "voice");
                result = QyApiClient.sendMessage(message.toJSONString(), tokenObject.getString("access_token"));
                break;
            case 4://视频
                mediaIds.put("media_id", mediaId);
                message.put("video", mediaIds);
                message.put("msgtype", "video");
                result = QyApiClient.sendMessage(message.toJSONString(), tokenObject.getString("access_token"));
                break;
            case 5://文件
                mediaIds.put("media_id", mediaId);
                message.put("file", mediaIds);
                message.put("msgtype", "file");
                result = QyApiClient.sendMessage(message.toJSONString(), tokenObject.getString("access_token"));
                break;
            case 6://图文
                message.put("msgtype", "mpnews");
                JSONObject articleJson = new JSONObject();
                articleJson.put("author", entity.getAuthor());
                articleJson.put("title", entity.getTitle());
                articleJson.put("content_source_url", entity.getContentSourceUrl());
                articleJson.put("digest", entity.getFabstract());
                articleJson.put("content", entity.getContent());
                articleJson.put("thumb_media_id", mediaId);
                articleJson.put("show_cover_pic", 1);
                JSONArray articles = new JSONArray();
                articles.add(articleJson);
                JSONObject news= new JSONObject();
                news.put("articles",articles);
                message.put("mpnews", news);
                result = QyApiClient.sendMessage(message.toJSONString(), tokenObject.getString("access_token"));
                break;
            default:
                break;
        }
        entity.setId(RandomUtil.uuId());
        entity.setSortCode(RandomUtil.parses());
        entity.setSendDate(new Date());
        entity.setSendUserId(userProvider.get().getUserId());
        entity.setAgentId(config.getQyhAgentId());
        entity.setEnabledMark(result == true ? 1 : 0);
        this.save(entity);
    }

    //上传素材
    private String Upload(int type, String token, String filePath) throws WxErrorException {
        String fileType = "";
        switch (type) {
            case 2:
            case 6:
                fileType = MediaFileType.Image.getMessage();
                break;
            case 3:
                fileType = MediaFileType.Voice.getMessage();
                break;
            case 4:
                fileType = MediaFileType.Video.getMessage();
                break;
            case 5:
                fileType = MediaFileType.File.getMessage();
                break;
            default:
                break;
        }
        String mediaId = QyApiClient.mediaUpload(token, fileType, filePath).getString("media_id");
        return mediaId;
    }
}

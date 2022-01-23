package smart.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.model.BaseSystemInfo;
import smart.util.*;
import smart.base.Pagination;
import smart.base.SysConfigApi;
import smart.exception.WxErrorException;
import smart.config.ConfigValueUtil;
import smart.util.type.SortType;
import smart.util.wxutil.MediaFileType;
import smart.MPMaterialEntity;
import smart.mapper.MPMaterialMapper;
import smart.model.mpmaterial.MPMaterialModel;
import smart.service.MPMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 公众号素材
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Slf4j
@Service
public class MPMaterialServiceImpl extends ServiceImpl<MPMaterialMapper, MPMaterialEntity> implements MPMaterialService {

    @Autowired
    private SysConfigApi sysConfigApi;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private UserProvider userProvider;

    @Override
    public List<MPMaterialEntity> getList(int type, Pagination pagination) {
        QueryWrapper<MPMaterialEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MPMaterialEntity::getMaterialsType, type);
        //排序
        if (SortType.DESC.equals(pagination.getSidx().toLowerCase())) {
            queryWrapper.lambda().orderByDesc(MPMaterialEntity::getUploadDate);
        } else {
            queryWrapper.lambda().orderByAsc(MPMaterialEntity::getUploadDate);
        }
        Page page = new Page(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<MPMaterialEntity> userIPage = this.page(page, queryWrapper);
        return pagination.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public MPMaterialEntity getInfo(String mediaId) {
        QueryWrapper<MPMaterialEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MPMaterialEntity::getMediaId, mediaId);
        return this.getOne(queryWrapper);
    }

    @Override
    public String UploadForeverMedia(MPMaterialEntity entity) throws WxErrorException {
        String filePath = configValueUtil.getTemporaryFilePath();
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        //获取token
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        //上传素材
        String type = "";
        if (entity.getMaterialsType() == 2) {
            type = MediaFileType.Image.getMessage();
        } else if (entity.getMaterialsType() == 3) {
            type = MediaFileType.Voice.getMessage();
        }
        JSONObject result = WxApiClient.addMateria(tokenObject.getString("access_token"), type, filePath + entity.getFileJson(), null);
        entity.setMediaId(result.getString("media_id"));
        entity.setReturnUrl(result.getString("url"));
        entity.setEnabledMark(0);
        entity.setId(RandomUtil.uuId());
        entity.setSortCode(RandomUtil.parses());
        entity.setUploadUser(userProvider.get().getUserId());
        entity.setUploadDate(new Date());
        this.save(entity);
        String materialPath = configValueUtil.getMpMaterialFilePath() + entity.getFileJson();
        FileUtil.copyFile(filePath + entity.getFileJson(), materialPath);
        return entity.getMediaId();
    }

    @Override
    public String UploadForeverVideo(MPMaterialEntity entity) throws WxErrorException {
        String filePath = configValueUtil.getTemporaryFilePath();
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        //获取token
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        //上传素材
        Map<String, String> params = new HashMap<>();
        JSONObject json = new JSONObject();
        json.put("title", entity.getTitle());
        json.put("introduction", entity.getIntroduction());
        params.put("description", json.toJSONString());
        JSONObject result = WxApiClient.addMateria(tokenObject.getString("access_token"), MediaFileType.Video.getMessage(), filePath + entity.getFileJson(), params);
        entity.setMediaId(result.getString("media_id"));
        entity.setReturnUrl(result.getString("url"));
        entity.setEnabledMark(0);
        entity.setId(RandomUtil.uuId());
        entity.setSortCode(RandomUtil.parses());
        entity.setUploadUser(userProvider.get().getUserId());
        entity.setUploadDate(new Date());
        this.save(entity);
        String materialPath = configValueUtil.getMpMaterialFilePath() + entity.getFileJson();
        FileUtil.copyFile(filePath + entity.getFileJson(), materialPath);
        return entity.getMediaId();
    }

    @Override
    public String UploadNews(MPMaterialEntity entity) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        MPMaterialEntity upload = entity;
        //上传图片
        String thumbMediaId = null;
        if (StringUtils.isNotEmpty(upload.getFileJson())) {
            thumbMediaId = this.UploadForeverMedia(upload);
        }
        List<MPMaterialModel> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(thumbMediaId)) {
            MPMaterialModel materialModel = new MPMaterialModel();
            materialModel.setTitle(entity.getTitle());
            materialModel.setAuthor(entity.getAuthor());
            materialModel.setContent(entity.getContent());
            materialModel.setContentSourceUrl(entity.getContentSourceUrl());
            materialModel.setDigest(entity.getDigest());
            materialModel.setNeedOpenComment(1);
            materialModel.setOnlyFansCanComment(0);
            materialModel.setShowCoverPic("1");
            materialModel.setThumbMediaId(thumbMediaId);
            materialModel.setOpenId(entity.getOpenId());
            list.add(materialModel);
            //获取token
            JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
            //上传图文
            JSONObject postObj = WxApiClient.addNews(list, tokenObject.getString("access_token"));
            if (postObj.getString("media_id") != null) {
                upload.setThumbMediaId(thumbMediaId);
                upload.setShowCoverPic(materialModel.getShowCoverPic());
                upload.setNeedOpenComment(materialModel.getNeedOpenComment());
                upload.setOnlyFansCanComment(materialModel.getOnlyFansCanComment());
                this.updateById(upload);
            }
            return entity.getMediaId();
        }
        return null;
    }

    @Override
    public boolean DeleteForeverMedia(MPMaterialEntity entity) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        //获取token
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        WxApiClient.deleteMaterial(entity.getMediaId(), tokenObject.getString("access_token"));
        String path = configValueUtil.getMpMaterialFilePath() + entity.getFileJson();
        FileUtil.deleteFile(path);
         return  this.removeById(entity.getId());
    }
}

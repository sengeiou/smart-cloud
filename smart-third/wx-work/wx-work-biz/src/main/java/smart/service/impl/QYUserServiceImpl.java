package smart.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.emnus.FileTypeEnum;
import smart.file.FileApi;
import smart.model.BaseSystemInfo;
import smart.util.*;
import smart.base.SysConfigApi;
import smart.base.Page;
import smart.QYDepartmentEntity;
import smart.QYUserEntity;
import smart.exception.WxErrorException;
import smart.mapper.QYUserMapper;
import smart.permission.OrganizeApi;
import smart.permission.PositionApi;
import smart.permission.UsersApi;
import smart.permission.entity.OrganizeEntity;
import smart.permission.entity.PositionEntity;
import smart.permission.entity.UserEntity;
import smart.permission.model.user.UserAllModel;
import smart.service.QYUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业号用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Slf4j
@Service
public class QYUserServiceImpl extends ServiceImpl<QYUserMapper, QYUserEntity> implements QYUserService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UsersApi usersApi;
    @Autowired
    private PositionApi positionApi;
    @Autowired
    private OrganizeApi organizeApi;
    @Autowired
    private SysConfigApi sysConfigApi;
    @Autowired
    private FileApi fileApi;

    @Override
    public List<QYUserEntity> getList(Page page) {
        String orderBy = "F_CreateTime desc";
        //排序
//        if (StringUtils.isEmpty(pagination.getSidx())) {
//            orderBy = "F_CreateTime desc";
//        } else {
//            orderBy = "F_" + pagination.getSidx() + " " + pagination.getSort();
//        }
        List<QYUserEntity> data = this.baseMapper.getList(orderBy);
//        int total = data.size();
        if (data.size() > 0) {
            List<OrganizeEntity> organizeList = organizeApi.getList();
            List<PositionEntity> positionList = positionApi.getListAll().getData();
            for (QYUserEntity entity : data) {
                OrganizeEntity organize = organizeList.stream().filter(t -> t.getId().equals(entity.getOrganizeId())).findFirst().orElse(null);
                entity.setOrganizeId("");
                if (organize != null) {
                    entity.setOrganizeId(organize.getFullName());
                }
                PositionEntity position = positionList.stream().filter(t -> t.getId().equals(entity.getPositionId())).findFirst().orElse(null);
                entity.setPositionId("");
                if (position != null) {
                    entity.setPositionId(position.getFullName());
                }
            }
        }
        if (StringUtil.isNotEmpty(page.getKeyword())) {
            data = data.stream().filter(t -> t.getRealName().contains(page.getKeyword()) || t.getAccount().contains(page.getKeyword())).collect(Collectors.toList());
        }
        return data;
    }

    @Override
    public List<QYUserEntity> getListAll() {
        QueryWrapper<QYUserEntity> queryWrapper = new QueryWrapper<>();
        return this.list(queryWrapper);
    }

    @Override
    public void synchronization(List<UserEntity> userList, List<QYDepartmentEntity> departList) throws WxErrorException {
        String userId = userProvider.get().getUserId();
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        List<QYUserEntity> syncList = this.getListAll();
        //删除
        if (syncList.size() > 0) {
            for (QYUserEntity syncUsersEntity : syncList) {
                if (userList.stream().filter(u -> u.getId().equals(syncUsersEntity.getUserId())).count() == 0) {
                    JSONObject tokenObject = QyApiClient.getAccessToken(config.getQyhCorpId(), config.getQyhCorpSecret());
                    boolean resultBool = QyApiClient.deleteUser(syncUsersEntity.getId(), tokenObject.getString("access_token"));
                    if (resultBool == true) {
                        this.removeById(syncUsersEntity.getId());
                    }
                }
            }
        }
        //同步可以使用的用户
        userList = userList.stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        //创建-编辑
        if (userList.size() > 0) {
            List<UserAllModel> userAll = usersApi.getAll().getData();
            for (UserEntity userEntity : userList) {
                JSONObject tokenObject = QyApiClient.getAccessToken(config.getQyhCorpId(), config.getQyhCorpSecret());
                String filePath = fileApi.getPath(FileTypeEnum.USERAVATAR) + userEntity.getHeadIcon();
                File file = new File(filePath);
                String mediaId = null;
                if (file.exists()) {
                    mediaId = QyApiClient.mediaUpload(tokenObject.getString("access_token"), "image", filePath).getString("media_id");
                }
                List<QYUserEntity> users = syncList.stream().filter(s -> s.getUserId().equals(userEntity.getId())).collect(Collectors.toList());
                if (users.size() == 0) {
                    JSONObject object = this.qyMemberModel(userEntity, mediaId, userAll, departList);
                    boolean resultBool = QyApiClient.createUser(object.toJSONString(), tokenObject.getString("access_token"));
                    if (resultBool == true) {
                        QYUserEntity userMergeEntity = JsonUtil.getJsonToBean(userEntity, QYUserEntity.class);
                        userMergeEntity.setId(RandomUtil.uuId());
                        userMergeEntity.setUserId(userEntity.getId());
                        userMergeEntity.setCreateTime(new Date());
                        userMergeEntity.setCreatorUserId(userId);
                        userMergeEntity.setSyncState(1);
                        this.save(userMergeEntity);
                    }
                } else {
                    JSONObject object = this.qyMemberModel(userEntity, mediaId, userAll, departList);
                    boolean resultBool = QyApiClient.updateUser(object.toJSONString(), tokenObject.getString("access_token"));
                    if (resultBool) {
                        QYUserEntity qyUserEntity = users.get(0);
                        qyUserEntity.setLastModifyUserId(userId);
                        qyUserEntity.setLastModifyTime(userEntity.getLastModifyTime());
                        this.updateById(qyUserEntity);
                    }
                }
            }
        }
    }

    /**
     * 封装企业用户实体
     *
     * @param userEntity 同步的用户
     * @param media_id   头像
     * @param userAll    所有用户
     * @param departList 同步成功的部门
     * @return
     */
    private JSONObject qyMemberModel(UserEntity userEntity, String media_id, List<UserAllModel> userAll, List<QYDepartmentEntity> departList) {
        JSONObject object = new JSONObject();
        if (media_id != null) {
            object.put("avatar_mediaid", media_id);
        }
        object.put("email", userEntity.getEmail());
        object.put("enable", userEntity.getEnabledMark());
        object.put("gender", userEntity.getGender());
        object.put("mobile", userEntity.getMobilePhone());
        object.put("userid", userEntity.getId());
        object.put("telephone", userEntity.getLandline());
        object.put("name", userEntity.getRealName());
        UserAllModel userAllModel = userAll.stream().filter(t -> t.getId().equals(userEntity.getId())).findFirst().orElse(new UserAllModel());
        object.put("position", userAllModel.getPositionName());
        if (userAllModel.getOrganizeId() != null) {
            String[] organizeId = userAllModel.getOrganizeId().split(",");
            List<Integer> department = new ArrayList<>();
            for (String id : organizeId) {
                Integer weChatDeptId = departList.stream().filter(t -> t.getOrganizeId().equals(id)).findFirst().get().getWeChatDeptId();
                department.add(weChatDeptId);
            }
            object.put("department", department);
        }
        return object;
    }

}

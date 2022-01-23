package smart.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.RandomUtil;
import smart.permission.entity.UserRelationEntity;
import smart.permission.mapper.UserRelationMapper;
import smart.permission.service.UserRelationService;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户关系
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Service
@Transactional(isolation= Isolation.DEFAULT,propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
public class UserRelationServiceImpl extends ServiceImpl<UserRelationMapper, UserRelationEntity> implements UserRelationService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<UserRelationEntity> getListByUserId(String userId) {
        QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRelationEntity::getUserId, userId).orderByDesc(UserRelationEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<UserRelationEntity> getListByObjectId(String objectId) {
        QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRelationEntity::getObjectId, objectId).orderByDesc(UserRelationEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<UserRelationEntity> getListByObjectIdAll(List<String> objectId) {
        List<UserRelationEntity> list = new ArrayList<>();
        if (objectId.size() > 0) {
            QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(UserRelationEntity::getObjectId, objectId).orderByDesc(UserRelationEntity::getCreatorTime);
            list = this.list(queryWrapper);
        }
        return list;
    }

    @Override
    public void deleteListByObjectId(String objectId) {
        QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRelationEntity::getObjectId, objectId);
        this.remove(queryWrapper);
    }
    @Override
    public void deleteListByObjTypeAndUserId(String objectType,String userId) {
        QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRelationEntity::getObjectType, objectType).eq(UserRelationEntity::getUserId, userId);
        this.remove(queryWrapper);
    }



    @Override
    public UserRelationEntity getInfo(String id) {
        QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRelationEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public void save(String objectId, List<UserRelationEntity> entitys) {
        List<UserRelationEntity> existList = this.getListByObjectId(objectId);
        List<UserRelationEntity> relationList = new ArrayList<>();
        for (int i = 0; i < entitys.size(); i++) {
            UserRelationEntity entity = entitys.get(i);
            entity.setId(RandomUtil.uuId());
            entity.setSortCode(Long.parseLong(i + ""));
            entity.setCreatorUserId(userProvider.get().getUserId());
            if (existList.stream().filter(t -> t.getUserId().equals(entity.getUserId())).count() == 0) {
                relationList.add(entity);
            }
        }
        for (UserRelationEntity entity : relationList) {
            this.save(entity);
        }
    }

    @Override
    @Transactional
    public void delete(String[] ids) {
        for (String item : ids) {
            QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserRelationEntity::getId, item);
            this.remove(queryWrapper);
        }
    }
}

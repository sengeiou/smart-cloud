package smart.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.*;
import smart.permission.entity.AuthorizeEntity;
import smart.permission.entity.PositionEntity;
import smart.permission.entity.UserRelationEntity;
import smart.permission.mapper.PositionMapper;
import smart.permission.model.position.PaginationPosition;
import smart.permission.service.AuthorizeService;
import smart.permission.service.PositionService;
import smart.permission.service.UserRelationService;
import smart.util.type.SortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 岗位信息
 *
 * @copyright 智慧停车公司
 * @author 开发平台组
 * @version V3.0.0
 * @date 2019年9月26日 上午9:18
 */
@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, PositionEntity> implements PositionService {

    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;

    @Override
    public List<PositionEntity> getList() {
        QueryWrapper<PositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(PositionEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<PositionEntity> getPosRedisList() {
        if(redisUtil.exists(cacheKeyUtil.getPositionList())){
            return JsonUtil.getJsonToList(redisUtil.getString(cacheKeyUtil.getPositionList()).toString(),PositionEntity.class);
        }
        QueryWrapper<PositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PositionEntity::getEnabledMark,1);

        List<PositionEntity> list=this.list(queryWrapper);
        if(list.size()>0){
            redisUtil.insert(cacheKeyUtil.getPositionList(), JsonUtil.getObjectToString(list),300);
        }
        return list;
    }

    @Override
    public List<PositionEntity> getList(PaginationPosition paginationPosition) {
        QueryWrapper<PositionEntity> queryWrapper = new QueryWrapper<>();
        //组织机构
        if (!StringUtil.isEmpty(paginationPosition.getOrganizeId())) {
            String[] organizeIds = paginationPosition.getOrganizeId().split(",");
            if(organizeIds.length>0){
                queryWrapper.lambda().in(PositionEntity::getOrganizeId,organizeIds);
            }
        }
        //关键字（名称、编码）
        if (!StringUtil.isEmpty(paginationPosition.getKeyword())) {
            queryWrapper.lambda().and(
                    t->t.like(PositionEntity::getFullName,paginationPosition.getKeyword())
                            .or().like(PositionEntity::getEnCode,paginationPosition.getKeyword())
            );
        }
        //排序
        if (SortType.ASC.equals(paginationPosition.getSort().toLowerCase())) {
            queryWrapper.lambda().orderByAsc(PositionEntity::getSortCode).orderByAsc(PositionEntity::getCreatorTime);
        }else {
            queryWrapper.lambda().orderByDesc(PositionEntity::getSortCode).orderByDesc(PositionEntity::getCreatorTime);
        }
        Page<PositionEntity> page = new Page<>(paginationPosition.getCurrentPage(), paginationPosition.getPageSize());
        IPage<PositionEntity> userIPage = this.page(page, queryWrapper);
        return paginationPosition.setData(userIPage.getRecords(),page.getTotal());
    }

    @Override
    public List<PositionEntity> getListByUserId(String userId) {
        return this.baseMapper.getListByUserId(userId);
    }

    @Override
    public PositionEntity getInfo(String id) {
        QueryWrapper<PositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PositionEntity::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<PositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PositionEntity::getFullName,fullName);
        if(!StringUtil.isEmpty(id)){
            queryWrapper.lambda().ne(PositionEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<PositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PositionEntity::getEnCode,enCode);
        if(!StringUtil.isEmpty(id)){
            queryWrapper.lambda().ne(PositionEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void create(PositionEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, PositionEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(PositionEntity entity) {
        this.removeById(entity.getId());
        QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRelationEntity::getObjectId,entity.getId());
        userRelationService.remove(queryWrapper);
        QueryWrapper<AuthorizeEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AuthorizeEntity::getObjectId,entity.getId());
        authorizeService.remove(wrapper);
    }

    @Override
    @Transactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        PositionEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<PositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(PositionEntity::getSortCode, upSortCode)
                .eq(PositionEntity::getOrganizeId,upEntity.getOrganizeId())
                .orderByDesc(PositionEntity::getSortCode);
        List<PositionEntity> downEntity = this.list(queryWrapper);
        if(downEntity.size()>0){
            //交换两条记录的sort值
            Long temp = upEntity.getSortCode();
            upEntity.setSortCode(downEntity.get(0).getSortCode());
            downEntity.get(0).setSortCode(temp);
            this.updateById(downEntity.get(0));
            this.updateById(upEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    @Transactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        PositionEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<PositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(PositionEntity::getSortCode, upSortCode)
                .eq(PositionEntity::getOrganizeId,downEntity.getOrganizeId())
                .orderByAsc(PositionEntity::getSortCode);
        List<PositionEntity> upEntity = this.list(queryWrapper);
        if(upEntity.size()>0){
            //交换两条记录的sort值
            Long temp = downEntity.getSortCode();
            downEntity.setSortCode(upEntity.get(0).getSortCode());
            upEntity.get(0).setSortCode(temp);
            this.updateById(upEntity.get(0));
            this.updateById(downEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    public List<PositionEntity> getPositionName(List<String> id) {
        List<PositionEntity> roleList = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<PositionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(PositionEntity::getId, id);
            roleList = this.list(queryWrapper);
        }
        return roleList;
    }
}

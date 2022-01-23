package smart.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import smart.util.StringUtil;
import smart.permission.entity.OrganizeEntity;
import smart.permission.mapper.OrganizeMapper;
import smart.permission.service.OrganizeService;
import smart.permission.service.PositionService;
import smart.permission.service.UserService;
import smart.util.CacheKeyUtil;
import smart.util.DateUtil;
import smart.util.RedisUtil;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织机构
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Service
public class OrganizeServiceImpl extends ServiceImpl<OrganizeMapper, OrganizeEntity> implements OrganizeService {

    @Autowired
    private PositionService positionService;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserProvider userProvider;

    @Override
    public List<OrganizeEntity> getList() {
        QueryWrapper<OrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(OrganizeEntity::getSortCode);
        List<OrganizeEntity> list=this.list(queryWrapper);
        return list;
    }

    @Override
    public List<OrganizeEntity> getOrgRedisList() {
        if(redisUtil.exists(cacheKeyUtil.getOrganizeList())){
            return JsonUtil.getJsonToList(redisUtil.getString(cacheKeyUtil.getOrganizeList()).toString(),OrganizeEntity.class);
        }
        QueryWrapper<OrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrganizeEntity::getEnabledMark,1);

        List<OrganizeEntity> list=this.list(queryWrapper);
        if(list.size()>0){
            redisUtil.insert(cacheKeyUtil.getOrganizeList(), JsonUtil.getObjectToString(list),300);
        }
        return list;
    }

    @Override
    public OrganizeEntity getInfo(String id) {
        QueryWrapper<OrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrganizeEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<OrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrganizeEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)){
            queryWrapper.lambda().ne(OrganizeEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<OrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrganizeEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)){
            queryWrapper.lambda().ne(OrganizeEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void create(OrganizeEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, OrganizeEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(DateUtil.getNowDate());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(OrganizeEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    @Transactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        OrganizeEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<OrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(OrganizeEntity::getSortCode, upSortCode)
                .eq(OrganizeEntity::getParentId, upEntity.getParentId())
                .orderByDesc(OrganizeEntity::getSortCode);
        List<OrganizeEntity> downEntity = this.list(queryWrapper);
        if (downEntity.size() > 0) {
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
        OrganizeEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<OrganizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(OrganizeEntity::getSortCode, upSortCode)
                .eq(OrganizeEntity::getParentId, downEntity.getParentId())
                .orderByAsc(OrganizeEntity::getSortCode);
        List<OrganizeEntity> upEntity = this.list(queryWrapper);
        if (upEntity.size() > 0) {
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
    public boolean allowdelete(String id) {
        Long subdataCount = this.getList().stream().filter(m -> m.getParentId().equals(id)).count();
        Long positionCount = positionService.getList().stream().filter(m -> m.getOrganizeId().equals(id)).count();
        Long userCount = userService.getList().stream().filter(m -> m.getOrganizeId().equals(id)).count();
        return (subdataCount + positionCount + userCount == 0);
    }

    @Override
    public List<OrganizeEntity> getOrganizeName(List<String> id){
        List<OrganizeEntity> list = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<OrganizeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(OrganizeEntity::getId, id);
            list = this.list(queryWrapper);
        }
        return list;
    }
}

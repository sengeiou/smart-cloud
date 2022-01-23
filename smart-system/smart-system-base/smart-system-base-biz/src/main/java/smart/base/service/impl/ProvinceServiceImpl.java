package smart.base.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.mapper.ProvinceMapper;
import smart.base.service.ProvinceService;
import smart.util.RandomUtil;
import smart.base.entity.ProvinceEntity;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 行政区划
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class ProvinceServiceImpl extends ServiceImpl<ProvinceMapper, ProvinceEntity> implements ProvinceService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceEntity::getFullName, fullName);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(ProvinceEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceEntity::getEnCode, enCode);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(ProvinceEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public List<ProvinceEntity> getList(String parentId) {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceEntity::getParentId, parentId).orderByAsc(ProvinceEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<ProvinceEntity> getAllList() {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(ProvinceEntity::getSortCode).orderByAsc(ProvinceEntity::getCreatorTime);

        return this.list(queryWrapper);
    }


    @Override
    public ProvinceEntity getInfo(String id) {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(ProvinceEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public void create(ProvinceEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, ProvinceEntity entity) {

        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    @Transactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        ProvinceEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(ProvinceEntity::getSortCode, upSortCode)
                .eq(ProvinceEntity::getParentId, upEntity.getParentId())
                .orderByDesc(ProvinceEntity::getSortCode);
        List<ProvinceEntity> downEntity = this.list(queryWrapper);
        if (downEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = upEntity.getSortCode();
            upEntity.setSortCode(downEntity.get(0).getSortCode());
            downEntity.get(0).setSortCode(temp);
            updateById(downEntity.get(0));
            updateById(upEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    @Transactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        ProvinceEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(ProvinceEntity::getSortCode, upSortCode)
                .eq(ProvinceEntity::getParentId, downEntity.getParentId())
                .orderByAsc(ProvinceEntity::getSortCode);
        List<ProvinceEntity> upEntity = this.list(queryWrapper);
        if (upEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = downEntity.getSortCode();
            downEntity.setSortCode(upEntity.get(0).getSortCode());
            upEntity.get(0).setSortCode(temp);
            updateById(upEntity.get(0));
            updateById(downEntity);
            isOk = true;
        }
        return isOk;
    }
}

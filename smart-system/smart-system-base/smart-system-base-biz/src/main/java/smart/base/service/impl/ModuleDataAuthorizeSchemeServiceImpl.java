package smart.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.mapper.ModuleDataAuthorizeSchemeMapper;
import smart.base.service.ModuleDataAuthorizeSchemeService;
import smart.util.DateUtil;
import smart.util.RandomUtil;
import smart.base.entity.ModuleDataAuthorizeSchemeEntity;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据权限方案
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class ModuleDataAuthorizeSchemeServiceImpl extends ServiceImpl<ModuleDataAuthorizeSchemeMapper, ModuleDataAuthorizeSchemeEntity> implements ModuleDataAuthorizeSchemeService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ModuleDataAuthorizeSchemeEntity> getList() {
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(ModuleDataAuthorizeSchemeEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleDataAuthorizeSchemeEntity> getList(String moduleId) {
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getModuleId, moduleId).orderByAsc(ModuleDataAuthorizeSchemeEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public ModuleDataAuthorizeSchemeEntity getInfo(String id) {
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleDataAuthorizeSchemeEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(ModuleDataAuthorizeSchemeEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setSortCode(RandomUtil.parses());
        entity.setEnabledMark(1);
        this.save(entity);
    }

    @Override
    public boolean update(String id, ModuleDataAuthorizeSchemeEntity entity) {
        entity.setId(id);
        entity.setEnabledMark(1);
        entity.setLastModifyTime(DateUtil.getNowDate());
        return  this.updateById(entity);
    }

    @Override
    public void delete(ModuleDataAuthorizeSchemeEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    @Transactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        ModuleDataAuthorizeSchemeEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(ModuleDataAuthorizeSchemeEntity::getSortCode, upSortCode)
                .orderByDesc(ModuleDataAuthorizeSchemeEntity::getSortCode);
        List<ModuleDataAuthorizeSchemeEntity> downEntity = this.list(queryWrapper);
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
        ModuleDataAuthorizeSchemeEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<ModuleDataAuthorizeSchemeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(ModuleDataAuthorizeSchemeEntity::getSortCode, upSortCode)
                .orderByAsc(ModuleDataAuthorizeSchemeEntity::getSortCode);
        List<ModuleDataAuthorizeSchemeEntity> upEntity = this.list(queryWrapper);
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
}

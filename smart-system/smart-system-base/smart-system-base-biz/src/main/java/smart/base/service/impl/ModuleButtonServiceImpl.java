package smart.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.Pagination;
import smart.base.service.ModuleButtonService;
import smart.util.*;
import smart.base.entity.ModuleButtonEntity;
import smart.base.mapper.ModuleButtonMapper;
import smart.util.type.SortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 按钮权限
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class ModuleButtonServiceImpl extends ServiceImpl<ModuleButtonMapper, ModuleButtonEntity> implements ModuleButtonService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ModuleButtonEntity> getList() {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(ModuleButtonEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleButtonEntity> getList(String moduleId) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleButtonEntity::getModuleId, moduleId).orderByAsc(ModuleButtonEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleButtonEntity> getList(String moduleId, Pagination pagination) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        if (SortType.ASC.equals(pagination.getSort().toLowerCase())){
            queryWrapper.lambda().eq(ModuleButtonEntity::getModuleId, moduleId).orderByAsc(ModuleButtonEntity::getSortCode).orderByAsc(ModuleButtonEntity::getCreatorTime);
        }else {
            queryWrapper.lambda().eq(ModuleButtonEntity::getModuleId, moduleId).orderByDesc(ModuleButtonEntity::getSortCode).orderByDesc(ModuleButtonEntity::getCreatorTime);
        }
        //关键字查询
        if(!StringUtil.isEmpty(pagination.getKeyword())){
            queryWrapper.lambda().and(
                    t->t.like(ModuleButtonEntity::getFullName,pagination.getKeyword())
                            .or().like(ModuleButtonEntity::getEnCode,pagination.getKeyword())
            );
        }
        return this.list(queryWrapper);
    }

    @Override
    public ModuleButtonEntity getInfo(String id) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleButtonEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String moduleId, String fullName, String id) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleButtonEntity::getFullName, fullName).eq(ModuleButtonEntity::getModuleId, moduleId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ModuleButtonEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String moduleId, String enCode, String id) {
        QueryWrapper<ModuleButtonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleButtonEntity::getEnCode, enCode).eq(ModuleButtonEntity::getModuleId, moduleId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ModuleButtonEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void create(ModuleButtonEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setSortCode(RandomUtil.parses());
        this.save(entity);
    }

    @Override
    public boolean update(String id, ModuleButtonEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(DateUtil.getNowDate());
       return this.updateById(entity);
    }

    @Override
    public void delete(ModuleButtonEntity entity) {
        this.removeById(entity.getId());
    }


}

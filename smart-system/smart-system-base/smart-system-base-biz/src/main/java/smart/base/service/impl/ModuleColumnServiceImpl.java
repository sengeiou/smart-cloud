package smart.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.Pagination;
import smart.base.mapper.ModuleColumnMapper;
import smart.base.service.ModuleColumnService;
import smart.util.DateUtil;
import smart.util.RandomUtil;
import smart.util.StringUtil;
import smart.base.entity.ModuleColumnEntity;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 列表权限
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class ModuleColumnServiceImpl extends ServiceImpl<ModuleColumnMapper, ModuleColumnEntity> implements ModuleColumnService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ModuleColumnEntity> getList() {
        QueryWrapper<ModuleColumnEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(ModuleColumnEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<ModuleColumnEntity> getList(String moduleId, Pagination pagination) {
        QueryWrapper<ModuleColumnEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleColumnEntity::getModuleId, moduleId).orderByAsc(ModuleColumnEntity::getSortCode);
        if(!StringUtil.isEmpty(pagination.getKeyword())){
            queryWrapper.lambda().and(
                    t-> t.like(ModuleColumnEntity::getEnCode,pagination.getKeyword()).or().like(ModuleColumnEntity::getFullName,pagination.getKeyword())
            );
        }

        return this.list(queryWrapper);
    }


    @Override
    public List<ModuleColumnEntity> getListByBindTable(String bindTable) {
        QueryWrapper<ModuleColumnEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleColumnEntity::getBindTable, bindTable).orderByAsc(ModuleColumnEntity::getSortCode);
        List<ModuleColumnEntity> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public ModuleColumnEntity getInfo(String id) {
        QueryWrapper<ModuleColumnEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleColumnEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String moduleId, String fullName, String id) {
        QueryWrapper<ModuleColumnEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleColumnEntity::getFullName, fullName).eq(ModuleColumnEntity::getModuleId, moduleId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ModuleColumnEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String moduleId, String enCode, String id) {
        QueryWrapper<ModuleColumnEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleColumnEntity::getEnCode, enCode).eq(ModuleColumnEntity::getModuleId, moduleId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ModuleColumnEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void create(ModuleColumnEntity entity) {
        List<ModuleColumnEntity> bindTableList = this.getListByBindTable(entity.getBindTable());
        if (bindTableList.size() > 0) {
            List<ModuleColumnEntity> sortCodes = bindTableList.stream().filter(t -> t.getBindTable().equals(entity.getBindTable())).collect(Collectors.toList());
            Long sortCode = sortCodes.get(sortCodes.size() - 1).getSortCode() + 1;
            entity.setSortCode(sortCode);
        } else {
            entity.setSortCode(RandomUtil.parses());
        }
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public void create(List<ModuleColumnEntity> entitys) {
        Long sortCode = RandomUtil.parses();
        String userId = userProvider.get().getUserId();
        for (ModuleColumnEntity entity : entitys) {
            entity.setId(RandomUtil.uuId());
            entity.setSortCode(sortCode++);
            entity.setEnabledMark(entity.getEnabledMark()==1?0:1);
            entity.setCreatorUserId(userId);
            this.save(entity);
        }
    }

    @Override
    public boolean update(String id, ModuleColumnEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

    @Override
    public void delete(ModuleColumnEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        ModuleColumnEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<ModuleColumnEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ModuleColumnEntity::getModuleId, upEntity.getModuleId())
                .eq(ModuleColumnEntity::getBindTable, upEntity.getBindTable())
                .lt(ModuleColumnEntity::getSortCode, upSortCode)
                .orderByDesc(ModuleColumnEntity::getSortCode);
        List<ModuleColumnEntity> downEntity = this.list(queryWrapper);
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
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        ModuleColumnEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<ModuleColumnEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ModuleColumnEntity::getModuleId, downEntity.getModuleId())
                .eq(ModuleColumnEntity::getBindTable, downEntity.getBindTable())
                .gt(ModuleColumnEntity::getSortCode, upSortCode)
                .orderByAsc(ModuleColumnEntity::getSortCode);
        List<ModuleColumnEntity> upEntity = this.list(queryWrapper);
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

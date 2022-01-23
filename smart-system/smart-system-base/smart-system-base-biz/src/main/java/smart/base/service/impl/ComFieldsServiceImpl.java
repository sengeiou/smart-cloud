package smart.base.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.mapper.BaseComFieldsMapper;
import smart.base.service.ComFieldsService;
import smart.util.RandomUtil;
import smart.base.entity.ComFieldsEntity;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 *
 * 常用字段表
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Service
public class ComFieldsServiceImpl extends ServiceImpl<BaseComFieldsMapper, ComFieldsEntity> implements ComFieldsService {

	@Autowired
    private UserProvider userProvider;


    @Override
    public List<ComFieldsEntity> getList() {
        QueryWrapper<ComFieldsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(ComFieldsEntity::getSortcode);
        return this.list(queryWrapper);
    }

    @Override
    public ComFieldsEntity getInfo(String id) {
        QueryWrapper<ComFieldsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ComFieldsEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<ComFieldsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ComFieldsEntity::getFieldName, fullName);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(ComFieldsEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }



    @Override
    public void create(ComFieldsEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatortime(new Date());
        entity.setCreatoruserid(userProvider.get().getUserId());
        entity.setEnabledmark(1);
        this.save(entity);
    }

    @Override
    public boolean update(String id, ComFieldsEntity entity) {
        entity.setId(id);
        entity.setLastmodifytime(new Date());
        entity.setLastmodifyuserid(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(ComFieldsEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

}

package smart.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.RandomUtil;
import smart.util.StringUtil;
import smart.base.mapper.VisualdevMapper;
import smart.base.service.VisualdevService;
import smart.base.VisualdevEntity;
import smart.base.model.PaginationVisualdev;
import smart.util.CacheKeyUtil;
import smart.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 可视化开发功能表
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-04-02
 */
@Service
public class VisualdevServiceImpl extends ServiceImpl<VisualdevMapper, VisualdevEntity> implements VisualdevService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;

    @Override
    public List<VisualdevEntity> getList(PaginationVisualdev paginationVisualdev) {
        QueryWrapper<VisualdevEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtil.isEmpty(paginationVisualdev.getKeyword())) {
            queryWrapper.lambda().like(VisualdevEntity::getFullName, paginationVisualdev.getKeyword());
        }
        queryWrapper.lambda().eq(VisualdevEntity::getType, paginationVisualdev.getType());
        queryWrapper.lambda().orderByDesc(VisualdevEntity::getCreatorTime);
        return list(queryWrapper);
    }


    @Override
    public List<VisualdevEntity> getList() {
        QueryWrapper<VisualdevEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(VisualdevEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public VisualdevEntity getInfo(String id) {
        QueryWrapper<VisualdevEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualdevEntity::getId, id);
        return this.getOne(queryWrapper);
    }


    @Override
    public void create(VisualdevEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setSortCode(RandomUtil.parses());
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualdevEntity entity) {
        entity.setId(id);
        String redisKey = cacheKeyUtil.getVisiualData() + id;
        if (redisUtil.exists(redisKey)) {
            redisUtil.remove(redisKey);
        }
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualdevEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
}

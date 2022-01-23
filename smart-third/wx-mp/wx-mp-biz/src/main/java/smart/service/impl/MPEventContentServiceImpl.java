package smart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.MPEventContentEntity;
import smart.mapper.MPEventContentMapper;
import smart.service.MPEventContentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 事件内容
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class MPEventContentServiceImpl extends ServiceImpl<MPEventContentMapper, MPEventContentEntity> implements MPEventContentService {

    @Override
    public MPEventContentEntity getInfo(String id) {
        QueryWrapper<MPEventContentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MPEventContentEntity::getEventKey, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean delete(MPEventContentEntity entity) {
       return this.removeById(entity.getEventKey());
    }

    @Override
    public void create(MPEventContentEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(MPEventContentEntity entity) {
        return  this.updateById(entity);
    }
}

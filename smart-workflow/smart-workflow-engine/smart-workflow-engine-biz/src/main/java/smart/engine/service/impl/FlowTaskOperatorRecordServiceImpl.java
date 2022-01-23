package smart.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.engine.entity.FlowTaskOperatorRecordEntity;
import smart.engine.mapper.FlowTaskOperatorRecordMapper;
import smart.engine.service.FlowTaskOperatorRecordService;
import smart.util.RandomUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程经办
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class FlowTaskOperatorRecordServiceImpl extends ServiceImpl<FlowTaskOperatorRecordMapper, FlowTaskOperatorRecordEntity> implements FlowTaskOperatorRecordService {


    @Override
    public List<FlowTaskOperatorRecordEntity> getList(String taskId) {
        QueryWrapper<FlowTaskOperatorRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getTaskId, taskId).orderByAsc(FlowTaskOperatorRecordEntity::getHandleTime);
        return this.list(queryWrapper);
    }

    @Override
    public FlowTaskOperatorRecordEntity getInfo(String id) {
        QueryWrapper<FlowTaskOperatorRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(FlowTaskOperatorRecordEntity entity) {
        QueryWrapper<FlowTaskOperatorRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getId, entity.getId());
        this.remove(queryWrapper);
    }

    @Override
    public void create(FlowTaskOperatorRecordEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public void update(String id, FlowTaskOperatorRecordEntity entity) {
        entity.setId(id);
        this.updateById(entity);
    }
}

package smart.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.engine.entity.FlowTaskCirculateEntity;
import smart.engine.mapper.FlowTaskCirculateMapper;
import smart.engine.service.FlowTaskCirculateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程传阅
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class FlowTaskCirculateServiceImpl extends ServiceImpl<FlowTaskCirculateMapper, FlowTaskCirculateEntity> implements FlowTaskCirculateService {

    @Override
    public void deleteByTaskId(String taskId) {
        QueryWrapper<FlowTaskCirculateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskCirculateEntity::getTaskId, taskId);
        this.remove(queryWrapper);
    }

    @Override
    public void deleteByNodeId(String nodeId) {
        QueryWrapper<FlowTaskCirculateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskCirculateEntity::getTaskNodeId, nodeId);
        this.remove(queryWrapper);
    }

    @Override
    public void create(List<FlowTaskCirculateEntity> entitys) {
        for (FlowTaskCirculateEntity entity : entitys) {
            this.save(entity);
        }
    }
}

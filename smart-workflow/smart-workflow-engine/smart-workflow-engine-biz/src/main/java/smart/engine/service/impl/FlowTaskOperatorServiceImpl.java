package smart.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.UserInfo;
import smart.engine.entity.FlowTaskOperatorEntity;
import smart.engine.mapper.FlowTaskOperatorMapper;
import smart.engine.service.FlowTaskOperatorService;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程经办记录
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class FlowTaskOperatorServiceImpl extends ServiceImpl<FlowTaskOperatorMapper, FlowTaskOperatorEntity> implements FlowTaskOperatorService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<FlowTaskOperatorEntity> getList(String taskId) {
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getTaskId, taskId).orderByDesc(FlowTaskOperatorEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public FlowTaskOperatorEntity getInfo(String id) {
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public FlowTaskOperatorEntity getInfo(String taskId, String nodeNo) {
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        UserInfo userInfo = userProvider.get();
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getTaskId, taskId)
                .eq(FlowTaskOperatorEntity::getNodeCode, nodeNo)
                .eq(FlowTaskOperatorEntity::getHandleId, userInfo.getUserId())
                .eq(FlowTaskOperatorEntity::getCompletion, 0);
        return this.getOne(queryWrapper);
    }

    @Override
    public void deleteByTaskId(String taskId) {
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getTaskId, taskId);
        this.remove(queryWrapper);
    }

    @Override
    public void deleteByNodeId(String nodeId) {
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorEntity::getTaskNodeId, nodeId).eq(FlowTaskOperatorEntity::getCompletion, 0);
        this.remove(queryWrapper);
    }

    @Override
    public void create(List<FlowTaskOperatorEntity> entitys) {
        for (FlowTaskOperatorEntity entity : entitys) {
            this.save(entity);
        }
    }

    @Override
    public void update(FlowTaskOperatorEntity entity) {
        this.updateById(entity);
    }

    @Override
    public void update(String taskNodeId, List<String> userId) {
        if (userId.size() > 0) {
            this.baseMapper.updateDelegateUser(taskNodeId, "'" + String.join("','", userId) + "'");
        }
    }

    @Override
    public void update(String taskNodeId, String type) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("taskNodeId", taskNodeId);
        map.put("type", type);
        this.baseMapper.updateFixedapprover(map);
    }

    @Override
    public void update(String taskId) {
        this.baseMapper.updateState(taskId);
    }

    @Override
    public List<FlowTaskOperatorEntity> press(String[] nodeCode, String taskId) {
        QueryWrapper<FlowTaskOperatorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(FlowTaskOperatorEntity::getNodeCode, nodeCode)
                .eq(FlowTaskOperatorEntity::getCompletion, 0)
                .eq(FlowTaskOperatorEntity::getTaskId, taskId);
        return this.list(queryWrapper);
    }

    @Override
    public void updateReject(String taskId, Set<String> nodeId) {
        if (nodeId.size() > 0) {
            this.baseMapper.updateReject(taskId, "'" + String.join("','", nodeId) + "'");
        }
    }

}

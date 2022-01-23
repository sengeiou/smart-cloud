package smart.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.BillRuleApi;
import smart.engine.service.FlowTaskService;
import smart.exception.WorkFlowException;
import smart.form.entity.RewardPunishmentEntity;
import smart.form.mapper.RewardPunishmentMapper;
import smart.form.model.rewardpunishment.RewardPunishmentForm;
import smart.form.service.RewardPunishmentService;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 行政赏罚单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Service
public class RewardPunishmentServiceImpl extends ServiceImpl<RewardPunishmentMapper, RewardPunishmentEntity> implements RewardPunishmentService {

    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private FlowTaskService flowTaskService;

    @Override
    public RewardPunishmentEntity getInfo(String id) {
        QueryWrapper<RewardPunishmentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RewardPunishmentEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void save(String id, RewardPunishmentEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_RewardPunishmentNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.save(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo());
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void submit(String id, RewardPunishmentEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_RewardPunishmentNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.submit(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(), entity);
    }

    @Override
    public void data(String id, String data) {
        RewardPunishmentForm rewardPunishmentForm = JsonUtil.getJsonToBean(data, RewardPunishmentForm.class);
        RewardPunishmentEntity entity = JsonUtil.getJsonToBean(rewardPunishmentForm, RewardPunishmentEntity.class);
        entity.setId(id);
        this.updateById(entity);
    }
}

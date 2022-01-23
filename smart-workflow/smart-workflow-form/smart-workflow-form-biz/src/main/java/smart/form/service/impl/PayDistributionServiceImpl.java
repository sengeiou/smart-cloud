package smart.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.BillRuleApi;
import smart.engine.service.FlowTaskService;
import smart.exception.WorkFlowException;
import smart.form.entity.PayDistributionEntity;
import smart.form.mapper.PayDistributionMapper;
import smart.form.model.paydistribution.PayDistributionForm;
import smart.form.service.PayDistributionService;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 薪酬发放
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Service
public class PayDistributionServiceImpl extends ServiceImpl<PayDistributionMapper, PayDistributionEntity> implements PayDistributionService {

    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private FlowTaskService flowTaskService;

    @Override
    public PayDistributionEntity getInfo(String id) {
        QueryWrapper<PayDistributionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PayDistributionEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void save(String id, PayDistributionEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_PayDistributionNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.save(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo());
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void submit(String id, PayDistributionEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_PayDistributionNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.submit(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(), entity);
    }

    @Override
    public void data(String id, String data) {
        PayDistributionForm payDistributionForm = JsonUtil.getJsonToBean(data, PayDistributionForm.class);
        PayDistributionEntity entity = JsonUtil.getJsonToBean(payDistributionForm, PayDistributionEntity.class);
        entity.setId(id);
        this.updateById(entity);
    }
}

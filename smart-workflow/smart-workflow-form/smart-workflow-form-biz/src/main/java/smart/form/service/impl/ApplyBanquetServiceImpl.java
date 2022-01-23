package smart.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.BillRuleApi;
import smart.engine.service.FlowTaskService;
import smart.exception.WorkFlowException;
import smart.form.entity.ApplyBanquetEntity;
import smart.form.mapper.ApplyBanquetMapper;
import smart.form.model.applybanquet.ApplyBanquetForm;
import smart.form.service.ApplyBanquetService;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 宴请申请
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Service
public class ApplyBanquetServiceImpl extends ServiceImpl<ApplyBanquetMapper, ApplyBanquetEntity> implements ApplyBanquetService {

    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private FlowTaskService flowTaskService;

    @Override
    public ApplyBanquetEntity getInfo(String id) {
        QueryWrapper<ApplyBanquetEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApplyBanquetEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void save(String id, ApplyBanquetEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_ApplyBanquetNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.save(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo());
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void submit(String id, ApplyBanquetEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_ApplyBanquetNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.submit(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(), entity);
    }

    @Override
    public void data(String id, String data) {
        ApplyBanquetForm applyBanquetForm = JsonUtil.getJsonToBean(data, ApplyBanquetForm.class);
        ApplyBanquetEntity entity = JsonUtil.getJsonToBean(applyBanquetForm, ApplyBanquetEntity.class);
        entity.setId(id);
        this.updateById(entity);
    }
}

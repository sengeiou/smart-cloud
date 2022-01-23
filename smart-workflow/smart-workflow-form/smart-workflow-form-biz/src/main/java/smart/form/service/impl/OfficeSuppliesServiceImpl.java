package smart.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.BillRuleApi;
import smart.engine.service.FlowTaskService;
import smart.exception.WorkFlowException;
import smart.form.entity.OfficeSuppliesEntity;
import smart.form.mapper.OfficeSuppliesMapper;
import smart.form.model.officesupplies.OfficeSuppliesForm;
import smart.form.service.OfficeSuppliesService;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 领用办公用品申请表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Service
public class OfficeSuppliesServiceImpl extends ServiceImpl<OfficeSuppliesMapper, OfficeSuppliesEntity> implements OfficeSuppliesService {

    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private FlowTaskService flowTaskService;

    @Override
    public OfficeSuppliesEntity getInfo(String id) {
        QueryWrapper<OfficeSuppliesEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OfficeSuppliesEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void save(String id, OfficeSuppliesEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_OfficeSuppliesNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.save(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo());
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void submit(String id, OfficeSuppliesEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_OfficeSuppliesNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.submit(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(), entity);
    }

    @Override
    public void data(String id, String data) {
        OfficeSuppliesForm officeSuppliesForm = JsonUtil.getJsonToBean(data, OfficeSuppliesForm.class);
        OfficeSuppliesEntity entity = JsonUtil.getJsonToBean(officeSuppliesForm, OfficeSuppliesEntity.class);
        entity.setId(id);
        this.updateById(entity);
    }
}

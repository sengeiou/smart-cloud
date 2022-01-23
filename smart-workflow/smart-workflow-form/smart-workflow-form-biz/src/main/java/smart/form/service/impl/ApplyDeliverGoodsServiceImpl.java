package smart.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.BillRuleApi;
import smart.engine.service.FlowTaskService;
import smart.exception.WorkFlowException;
import smart.form.entity.ApplyDeliverGoodsEntity;
import smart.form.entity.ApplyDeliverGoodsEntryEntity;
import smart.form.mapper.ApplyDeliverGoodsMapper;
import smart.form.model.applydelivergoods.ApplyDeliverGoodsForm;
import smart.form.service.ApplyDeliverGoodsEntryService;
import smart.form.service.ApplyDeliverGoodsService;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 发货申请单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Service
public class ApplyDeliverGoodsServiceImpl extends ServiceImpl<ApplyDeliverGoodsMapper, ApplyDeliverGoodsEntity> implements ApplyDeliverGoodsService {

    @Autowired
    private ApplyDeliverGoodsEntryService applyDeliverGoodsEntryService;
    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private FlowTaskService flowTaskService;

    @Override
    public List<ApplyDeliverGoodsEntryEntity> getDeliverEntryList(String id) {
        QueryWrapper<ApplyDeliverGoodsEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApplyDeliverGoodsEntryEntity::getInvoiceId, id).orderByDesc(ApplyDeliverGoodsEntryEntity::getSortCode);
        return applyDeliverGoodsEntryService.list(queryWrapper);
    }

    @Override
    public ApplyDeliverGoodsEntity getInfo(String id) {
        QueryWrapper<ApplyDeliverGoodsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApplyDeliverGoodsEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void save(String id, ApplyDeliverGoodsEntity entity, List<ApplyDeliverGoodsEntryEntity> applyDeliverGoodsEntryEntityList) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            for (int i = 0; i < applyDeliverGoodsEntryEntityList.size(); i++) {
                applyDeliverGoodsEntryEntityList.get(i).setId(RandomUtil.uuId());
                applyDeliverGoodsEntryEntityList.get(i).setInvoiceId(entity.getId());
                applyDeliverGoodsEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                applyDeliverGoodsEntryService.save(applyDeliverGoodsEntryEntityList.get(i));
            }
            this.save(entity);
            billRuleApi.useBillNumber("WF_ApplyDeliverGoodsNo");
        } else {
            entity.setId(id);
            QueryWrapper<ApplyDeliverGoodsEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ApplyDeliverGoodsEntryEntity::getInvoiceId, entity.getId());
            applyDeliverGoodsEntryService.remove(queryWrapper);
            for (int i = 0; i < applyDeliverGoodsEntryEntityList.size(); i++) {
                applyDeliverGoodsEntryEntityList.get(i).setId(RandomUtil.uuId());
                applyDeliverGoodsEntryEntityList.get(i).setInvoiceId(entity.getId());
                applyDeliverGoodsEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                applyDeliverGoodsEntryService.save(applyDeliverGoodsEntryEntityList.get(i));
            }
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.save(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo());
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void submit(String id, ApplyDeliverGoodsEntity entity, List<ApplyDeliverGoodsEntryEntity> applyDeliverGoodsEntryEntityList) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            for (int i = 0; i < applyDeliverGoodsEntryEntityList.size(); i++) {
                applyDeliverGoodsEntryEntityList.get(i).setId(RandomUtil.uuId());
                applyDeliverGoodsEntryEntityList.get(i).setInvoiceId(entity.getId());
                applyDeliverGoodsEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                applyDeliverGoodsEntryService.save(applyDeliverGoodsEntryEntityList.get(i));
            }
            this.save(entity);
            billRuleApi.useBillNumber("WF_ApplyDeliverGoodsNo");
        } else {
            entity.setId(id);
            QueryWrapper<ApplyDeliverGoodsEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ApplyDeliverGoodsEntryEntity::getInvoiceId, entity.getId());
            applyDeliverGoodsEntryService.remove(queryWrapper);
            for (int i = 0; i < applyDeliverGoodsEntryEntityList.size(); i++) {
                applyDeliverGoodsEntryEntityList.get(i).setId(RandomUtil.uuId());
                applyDeliverGoodsEntryEntityList.get(i).setInvoiceId(entity.getId());
                applyDeliverGoodsEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                applyDeliverGoodsEntryService.save(applyDeliverGoodsEntryEntityList.get(i));
            }
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.submit(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(), entity);
    }

    @Override
    public void data(String id, String data) {
        ApplyDeliverGoodsForm applyDeliverGoodsForm = JsonUtil.getJsonToBean(data, ApplyDeliverGoodsForm.class);
        ApplyDeliverGoodsEntity entity = JsonUtil.getJsonToBean(applyDeliverGoodsForm, ApplyDeliverGoodsEntity.class);
        List<ApplyDeliverGoodsEntryEntity> applyDeliverGoodsEntryEntityList = JsonUtil.getJsonToList(applyDeliverGoodsForm.getEntryList(), ApplyDeliverGoodsEntryEntity.class);
        entity.setId(id);
        QueryWrapper<ApplyDeliverGoodsEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApplyDeliverGoodsEntryEntity::getInvoiceId, entity.getId());
        applyDeliverGoodsEntryService.remove(queryWrapper);
        for (int i = 0; i < applyDeliverGoodsEntryEntityList.size(); i++) {
            applyDeliverGoodsEntryEntityList.get(i).setId(RandomUtil.uuId());
            applyDeliverGoodsEntryEntityList.get(i).setInvoiceId(entity.getId());
            applyDeliverGoodsEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
            applyDeliverGoodsEntryService.save(applyDeliverGoodsEntryEntityList.get(i));
        }
        this.updateById(entity);
    }
}

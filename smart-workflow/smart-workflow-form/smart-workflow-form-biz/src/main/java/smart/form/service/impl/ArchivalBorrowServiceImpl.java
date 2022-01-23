package smart.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.BillRuleApi;
import smart.engine.service.FlowTaskService;
import smart.exception.WorkFlowException;
import smart.form.entity.ArchivalBorrowEntity;
import smart.form.mapper.ArchivalBorrowMapper;
import smart.form.model.archivalborrow.ArchivalBorrowForm;
import smart.form.service.ArchivalBorrowService;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 档案借阅申请
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Service
public class ArchivalBorrowServiceImpl extends ServiceImpl<ArchivalBorrowMapper, ArchivalBorrowEntity> implements ArchivalBorrowService {

    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private FlowTaskService flowTaskService;

    @Override
    public ArchivalBorrowEntity getInfo(String id) {
        QueryWrapper<ArchivalBorrowEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArchivalBorrowEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void save(String id, ArchivalBorrowEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            save(entity);
            billRuleApi.useBillNumber("WF_ArchivalBorrowNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.save(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo());
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void submit(String id, ArchivalBorrowEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            save(entity);
            billRuleApi.useBillNumber("WF_ArchivalBorrowNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.submit(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(), entity);
    }

    @Override
    public void data(String id, String data) {
        ArchivalBorrowForm archivalBorrowForm = JsonUtil.getJsonToBean(data, ArchivalBorrowForm.class);
        ArchivalBorrowEntity entity = JsonUtil.getJsonToBean(archivalBorrowForm, ArchivalBorrowEntity.class);
        entity.setId(id);
        this.updateById(entity);
    }
}

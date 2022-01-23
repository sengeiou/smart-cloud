package smart.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.BillRuleApi;
import smart.engine.service.FlowTaskService;
import smart.exception.WorkFlowException;
import smart.form.entity.ArticlesWarehousEntity;
import smart.form.mapper.ArticlesWarehousMapper;
import smart.form.model.articleswarehous.ArticlesWarehousForm;
import smart.form.service.ArticlesWarehousService;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 用品入库申请表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Service
public class ArticlesWarehousServiceImpl extends ServiceImpl<ArticlesWarehousMapper, ArticlesWarehousEntity> implements ArticlesWarehousService {

    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private FlowTaskService flowTaskService;

    @Override
    public ArticlesWarehousEntity getInfo(String id) {
        QueryWrapper<ArticlesWarehousEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticlesWarehousEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void save(String id, ArticlesWarehousEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_ArticlesWarehousNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.save(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo());
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void submit(String id, ArticlesWarehousEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_ArticlesWarehousNo");
        } else {
            entity.setId(id);
            this.updateById(entity);
        }
        //流程信息
        flowTaskService.submit(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(), entity);
    }

    @Override
    public void data(String id, String data) {
        ArticlesWarehousForm articlesWarehousForm = JsonUtil.getJsonToBean(data, ArticlesWarehousForm.class);
        ArticlesWarehousEntity entity = JsonUtil.getJsonToBean(articlesWarehousForm, ArticlesWarehousEntity.class);
        entity.setId(id);
        this.updateById(entity);
    }
}

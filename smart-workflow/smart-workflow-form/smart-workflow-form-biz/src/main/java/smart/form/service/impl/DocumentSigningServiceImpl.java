package smart.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.BillRuleApi;
import smart.engine.service.FlowTaskService;
import smart.exception.WorkFlowException;
import smart.form.entity.DocumentSigningEntity;
import smart.form.mapper.DocumentSigningMapper;
import smart.form.model.documentsigning.DocumentSigningForm;
import smart.form.service.DocumentSigningService;
import smart.form.util.FileManageUtil;
import smart.form.util.FileModel;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件签阅表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Service
public class DocumentSigningServiceImpl extends ServiceImpl<DocumentSigningMapper, DocumentSigningEntity> implements DocumentSigningService {

    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FileManageUtil fileManageUtil;

    @Override
    public DocumentSigningEntity getInfo(String id) {
        QueryWrapper<DocumentSigningEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DocumentSigningEntity::getId, id);
        DocumentSigningEntity entity = this.getOne(queryWrapper);
        if (entity != null) {
            entity.setDocumentContent(entity.getDocumentContent());
        }
        return entity;
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void save(String id, DocumentSigningEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_DocumentSigningNo");
            //添加附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.createFile(data);
        } else {
            entity.setId(id);
            this.updateById(entity);
            //更新附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.updateFile(data);
        }
        //流程信息
        flowTaskService.save(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo());
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void submit(String id, DocumentSigningEntity entity) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
            billRuleApi.useBillNumber("WF_DocumentSigningNo");
            //添加附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.createFile(data);
        } else {
            entity.setId(id);
            this.updateById(entity);
            //更新附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.updateFile(data);
        }
        //流程信息
        flowTaskService.submit(id, entity.getFlowId(), entity.getId(), entity.getFlowTitle(), entity.getFlowUrgent(), entity.getBillNo(), entity);
    }

    @Override
    public void data(String id, String data) {
        DocumentSigningForm documentSigningForm = JsonUtil.getJsonToBean(data, DocumentSigningForm.class);
        DocumentSigningEntity entity = JsonUtil.getJsonToBean(documentSigningForm, DocumentSigningEntity.class);
        entity.setId(id);
        this.updateById(entity);
    }
}

package smart.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.BillRuleApi;
import smart.engine.service.FlowTaskService;
import smart.exception.WorkFlowException;
import smart.form.entity.ProcurementEntryEntity;
import smart.form.entity.ProcurementMaterialEntity;
import smart.form.mapper.ProcurementMaterialMapper;
import smart.form.model.procurementmaterial.ProcurementMaterialForm;
import smart.form.service.ProcurementEntryService;
import smart.form.service.ProcurementMaterialService;
import smart.form.util.FileManageUtil;
import smart.form.util.FileModel;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 采购原材料
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Service
public class ProcurementMaterialServiceImpl extends ServiceImpl<ProcurementMaterialMapper, ProcurementMaterialEntity> implements ProcurementMaterialService {

    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private ProcurementEntryService procurementEntryEntityService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FileManageUtil fileManageUtil;

    @Override
    public List<ProcurementEntryEntity> getProcurementEntryList(String id) {
        QueryWrapper<ProcurementEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProcurementEntryEntity::getProcurementId, id).orderByDesc(ProcurementEntryEntity::getSortCode);
        return procurementEntryEntityService.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public ProcurementMaterialEntity getInfo(String id) {
        QueryWrapper<ProcurementMaterialEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProcurementMaterialEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void save(String id, ProcurementMaterialEntity entity, List<ProcurementEntryEntity> procurementEntryEntityList) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            for (int i = 0; i < procurementEntryEntityList.size(); i++) {
                procurementEntryEntityList.get(i).setId(RandomUtil.uuId());
                procurementEntryEntityList.get(i).setProcurementId(entity.getId());
                procurementEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                procurementEntryEntityService.save(procurementEntryEntityList.get(i));
            }
            //创建
            this.save(entity);
            billRuleApi.useBillNumber("WF_ProcurementMaterialNo");
            //添加附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.createFile(data);
        } else {
            entity.setId(id);
            QueryWrapper<ProcurementEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ProcurementEntryEntity::getProcurementId, entity.getId());
            procurementEntryEntityService.remove(queryWrapper);
            for (int i = 0; i < procurementEntryEntityList.size(); i++) {
                procurementEntryEntityList.get(i).setId(RandomUtil.uuId());
                procurementEntryEntityList.get(i).setProcurementId(entity.getId());
                procurementEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                procurementEntryEntityService.save(procurementEntryEntityList.get(i));
            }
            //编辑
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
    public void submit(String id, ProcurementMaterialEntity entity, List<ProcurementEntryEntity> procurementEntryEntityList) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            for (int i = 0; i < procurementEntryEntityList.size(); i++) {
                procurementEntryEntityList.get(i).setId(RandomUtil.uuId());
                procurementEntryEntityList.get(i).setProcurementId(entity.getId());
                procurementEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                procurementEntryEntityService.save(procurementEntryEntityList.get(i));
            }
            //创建
            this.save(entity);
            billRuleApi.useBillNumber("WF_ProcurementMaterialNo");
            //添加附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.createFile(data);
        } else {
            entity.setId(id);
            QueryWrapper<ProcurementEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ProcurementEntryEntity::getProcurementId, entity.getId());
            procurementEntryEntityService.remove(queryWrapper);
            for (int i = 0; i < procurementEntryEntityList.size(); i++) {
                procurementEntryEntityList.get(i).setId(RandomUtil.uuId());
                procurementEntryEntityList.get(i).setProcurementId(entity.getId());
                procurementEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                procurementEntryEntityService.save(procurementEntryEntityList.get(i));
            }
            //编辑
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
        ProcurementMaterialForm procurementMaterialForm = JsonUtil.getJsonToBean(data, ProcurementMaterialForm.class);
        ProcurementMaterialEntity entity = JsonUtil.getJsonToBean(procurementMaterialForm, ProcurementMaterialEntity.class);
        List<ProcurementEntryEntity> procurementEntryEntityList = JsonUtil.getJsonToList(procurementMaterialForm.getEntryList(), ProcurementEntryEntity.class);
        entity.setId(id);
        QueryWrapper<ProcurementEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProcurementEntryEntity::getProcurementId, entity.getId());
        procurementEntryEntityService.remove(queryWrapper);
        for (int i = 0; i < procurementEntryEntityList.size(); i++) {
            procurementEntryEntityList.get(i).setId(RandomUtil.uuId());
            procurementEntryEntityList.get(i).setProcurementId(entity.getId());
            procurementEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
            procurementEntryEntityService.save(procurementEntryEntityList.get(i));
        }
        //编辑
        this.updateById(entity);
    }
}

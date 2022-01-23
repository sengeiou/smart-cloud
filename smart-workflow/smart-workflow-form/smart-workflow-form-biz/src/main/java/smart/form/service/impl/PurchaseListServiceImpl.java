package smart.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.BillRuleApi;
import smart.engine.service.FlowTaskService;
import smart.exception.WorkFlowException;
import smart.form.entity.PurchaseListEntity;
import smart.form.entity.PurchaseListEntryEntity;
import smart.form.mapper.PurchaseListMapper;
import smart.form.model.purchaselist.PurchaseListForm;
import smart.form.service.PurchaseListEntryService;
import smart.form.service.PurchaseListService;
import smart.form.util.FileManageUtil;
import smart.form.util.FileModel;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 日常物品采购清单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Service
public class PurchaseListServiceImpl extends ServiceImpl<PurchaseListMapper, PurchaseListEntity> implements PurchaseListService {

    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private PurchaseListEntryService purchaseListEntryService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FileManageUtil fileManageUtil;

    @Override
    public List<PurchaseListEntryEntity> getPurchaseEntryList(String id) {
        QueryWrapper<PurchaseListEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PurchaseListEntryEntity::getPurchaseId, id).orderByDesc(PurchaseListEntryEntity::getSortCode);
        return purchaseListEntryService.list(queryWrapper);
    }

    @Override
    public PurchaseListEntity getInfo(String id) {
        QueryWrapper<PurchaseListEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PurchaseListEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = WorkFlowException.class)
    public void save(String id, PurchaseListEntity entity, List<PurchaseListEntryEntity> purchaseListEntryEntityList) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            for (int i = 0; i < purchaseListEntryEntityList.size(); i++) {
                purchaseListEntryEntityList.get(i).setId(RandomUtil.uuId());
                purchaseListEntryEntityList.get(i).setPurchaseId(entity.getId());
                purchaseListEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                purchaseListEntryService.save(purchaseListEntryEntityList.get(i));
            }
            //创建
            this.save(entity);
            billRuleApi.useBillNumber("WF_PurchaseListNo");
            //添加附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.createFile(data);
        } else {
            entity.setId(id);
            QueryWrapper<PurchaseListEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(PurchaseListEntryEntity::getPurchaseId, entity.getId());
            purchaseListEntryService.remove(queryWrapper);
            for (int i = 0; i < purchaseListEntryEntityList.size(); i++) {
                purchaseListEntryEntityList.get(i).setId(RandomUtil.uuId());
                purchaseListEntryEntityList.get(i).setPurchaseId(entity.getId());
                purchaseListEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                purchaseListEntryService.save(purchaseListEntryEntityList.get(i));
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
    public void submit(String id, PurchaseListEntity entity, List<PurchaseListEntryEntity> purchaseListEntryEntityList) throws WorkFlowException {
        //表单信息
        if (id == null) {
            entity.setId(RandomUtil.uuId());
            for (int i = 0; i < purchaseListEntryEntityList.size(); i++) {
                purchaseListEntryEntityList.get(i).setId(RandomUtil.uuId());
                purchaseListEntryEntityList.get(i).setPurchaseId(entity.getId());
                purchaseListEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                purchaseListEntryService.save(purchaseListEntryEntityList.get(i));
            }
            //创建
            this.save(entity);
            billRuleApi.useBillNumber("WF_PurchaseListNo");
            //添加附件
            List<FileModel> data = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
            fileManageUtil.createFile(data);
        } else {
            entity.setId(id);
            QueryWrapper<PurchaseListEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(PurchaseListEntryEntity::getPurchaseId, entity.getId());
            purchaseListEntryService.remove(queryWrapper);
            for (int i = 0; i < purchaseListEntryEntityList.size(); i++) {
                purchaseListEntryEntityList.get(i).setId(RandomUtil.uuId());
                purchaseListEntryEntityList.get(i).setPurchaseId(entity.getId());
                purchaseListEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                purchaseListEntryService.save(purchaseListEntryEntityList.get(i));
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
        PurchaseListForm purchaseListForm = JsonUtil.getJsonToBean(data, PurchaseListForm.class);
        PurchaseListEntity entity = JsonUtil.getJsonToBean(purchaseListForm, PurchaseListEntity.class);
        List<PurchaseListEntryEntity> purchaseListEntryEntityList = JsonUtil.getJsonToList(purchaseListForm.getEntryList(), PurchaseListEntryEntity.class);
        entity.setId(id);
        QueryWrapper<PurchaseListEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PurchaseListEntryEntity::getPurchaseId, entity.getId());
        purchaseListEntryService.remove(queryWrapper);
        for (int i = 0; i < purchaseListEntryEntityList.size(); i++) {
            purchaseListEntryEntityList.get(i).setId(RandomUtil.uuId());
            purchaseListEntryEntityList.get(i).setPurchaseId(entity.getId());
            purchaseListEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
            purchaseListEntryService.save(purchaseListEntryEntityList.get(i));
        }
        //编辑
        this.updateById(entity);
    }
}

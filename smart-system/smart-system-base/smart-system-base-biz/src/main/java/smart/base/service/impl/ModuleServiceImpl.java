package smart.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.VisualDevelopmentApi;
import smart.base.VisualdevEntity;
import smart.base.mapper.ModuleMapper;
import smart.base.model.Template6.BtnData;
import smart.base.model.Template6.IndexGridField6Model;
import smart.base.service.ModuleButtonService;
import smart.base.service.ModuleColumnService;
import smart.base.service.ModuleDataAuthorizeService;
import smart.base.service.ModuleService;
import smart.base.util.JSONUtil;
import smart.base.util.VisualUtil;
import smart.util.DateUtil;
import smart.util.RandomUtil;
import smart.util.StringUtil;
import smart.base.entity.ModuleButtonEntity;
import smart.base.entity.ModuleColumnEntity;
import smart.base.entity.ModuleDataAuthorizeEntity;
import smart.base.entity.ModuleEntity;
import smart.util.type.StringNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 系统功能
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, ModuleEntity> implements ModuleService {

    @Autowired
    private ModuleButtonService moduleButtonService;
    @Autowired
    private ModuleColumnService moduleColumnService;
    @Autowired
    private ModuleDataAuthorizeService moduleDataAuthorizeService;
    @Autowired
    private VisualDevelopmentApi visualDevelopmentApi;


    @Override
    public List<ModuleEntity> getList() {
        QueryWrapper<ModuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(ModuleEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public ModuleEntity getInfo(String id) {
        QueryWrapper<ModuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<ModuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ModuleEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<ModuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ModuleEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(ModuleEntity entity) {
        this.removeById(entity.getId());
        QueryWrapper<ModuleButtonEntity> buttonWrapper = new QueryWrapper<>();
        buttonWrapper.lambda().eq(ModuleButtonEntity::getModuleId, entity.getId());
        moduleButtonService.remove(buttonWrapper);
        QueryWrapper<ModuleColumnEntity> columnWrapper = new QueryWrapper<>();
        columnWrapper.lambda().eq(ModuleColumnEntity::getModuleId, entity.getId());
        moduleColumnService.remove(columnWrapper);
        QueryWrapper<ModuleDataAuthorizeEntity> dataWrapper = new QueryWrapper<>();
        dataWrapper.lambda().eq(ModuleDataAuthorizeEntity::getModuleId, entity.getId());
        moduleDataAuthorizeService.remove(dataWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(ModuleEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
        //添加默认按钮
        if(StringNumber.THREE.equals(String.valueOf(entity.getType()))){
            Map<String,Object> propJsonMap= JSONUtil.StringToMap(entity.getPropertyJson());
            if(propJsonMap!=null){
                VisualdevEntity visualdevEntity =visualDevelopmentApi.getInfo(propJsonMap.get("moduleId").toString());
                //去除模板中的F_
                visualdevEntity= VisualUtil.delfKey(visualdevEntity);
                if(visualdevEntity!=null){
                    List<BtnData> btnData =new ArrayList<>();
                    Map<String,Object> column=JSONUtil.StringToMap(visualdevEntity.getColumnData());
                    if(column.get("columnBtnsList")!=null){
                        btnData.addAll(JSONUtil.getJsonToList(JSONUtil.getJsonToListMap(column.get("columnBtnsList").toString()),BtnData.class));
                    }
                    if(column.get("btnsList")!=null){
                        btnData.addAll(JSONUtil.getJsonToList(JSONUtil.getJsonToListMap(column.get("btnsList").toString()),BtnData.class));
                    }
                    if(btnData.size()>0){
                        for(BtnData btn:btnData){
                            ModuleButtonEntity moduleButtonEntity=new ModuleButtonEntity();
                            moduleButtonEntity.setId(RandomUtil.uuId());
                            moduleButtonEntity.setParentId("-1");
                            moduleButtonEntity.setFullName(btn.getLabel());
                            moduleButtonEntity.setEnCode("btn_"+btn.getValue());
                            moduleButtonEntity.setSortCode(0L);
                            moduleButtonEntity.setModuleId(entity.getId());
                            moduleButtonEntity.setEnabledMark(1);
                            moduleButtonEntity.setIcon(btn.getIcon());
                            moduleButtonService.save(moduleButtonEntity);
                        }
                    }
                    List<IndexGridField6Model> indexGridField6Models =new ArrayList<>();
                    if(column.get("columnList")!=null){
                        indexGridField6Models.addAll(JSONUtil.getJsonToList(JSONUtil.getJsonToListMap(column.get("columnList").toString()),IndexGridField6Model.class));
                        if(indexGridField6Models.size()>0){
                            for(IndexGridField6Model field6Model:indexGridField6Models){
                                ModuleColumnEntity moduleColumnEntity=new ModuleColumnEntity();
                                moduleColumnEntity.setId(RandomUtil.uuId());
                                moduleColumnEntity.setParentId("-1");
                                moduleColumnEntity.setFullName(field6Model.getLabel());
                                moduleColumnEntity.setEnCode(field6Model.getProp());
                                moduleColumnEntity.setSortCode(0L);
                                moduleColumnEntity.setModuleId(entity.getId());
                                moduleColumnEntity.setEnabledMark(1);
                                moduleColumnService.save(moduleColumnEntity);
                            }
                        }
                    }
                }
            }
        }else if (StringNumber.FOUR.equals(String.valueOf(entity.getType()))) {
            for (int i = 0; i < 3; i++) {
                String fullName = "新增";
                String value = "add";
                String icon = "el-icon-plus";
                if(i==1) {
                    fullName = "编辑";
                    value = "edit";
                    icon = "el-icon-edit";
                }
                if(i==2) {
                    fullName = "删除";
                    value = "remove";
                    icon = "el-icon-delete";
                }
                ModuleButtonEntity moduleButtonEntity = new ModuleButtonEntity();
                moduleButtonEntity.setId(RandomUtil.uuId());
                moduleButtonEntity.setParentId("-1");
                moduleButtonEntity.setFullName(fullName);
                moduleButtonEntity.setEnCode("btn_" + value);
                moduleButtonEntity.setSortCode(0L);
                moduleButtonEntity.setModuleId(entity.getId());
                moduleButtonEntity.setEnabledMark(1);
                moduleButtonEntity.setIcon(icon);
                moduleButtonService.save(moduleButtonEntity);
            }

        }
    }

    @Override
    public boolean update(String id, ModuleEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(DateUtil.getNowDate());
       return this.updateById(entity);
    }
}

package smart.base.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.service.DictionaryDataService;
import smart.permission.service.UserService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import smart.util.StringUtil;
import smart.base.ActionResult;
import smart.base.DictionaryDataApi;
import smart.base.vo.ListVO;
import smart.base.VisualdevEntity;
import smart.base.entity.DictionaryDataEntity;
import smart.base.model.*;
import smart.base.model.Template6.BtnData;
import smart.base.service.VisualdevService;
import smart.base.util.VisualUtil;
import smart.exception.DataException;
import smart.onlinedev.model.PaginationModel;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.onlinedev.service.VisualdevModelDataService;
import smart.permission.UsersApi;
import smart.permission.model.user.UserAllModel;
import smart.util.visiual.SmartKeyConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 可视化基础模块
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "可视化基础模块", description = "Base")
@RestController
@RequestMapping("/Base")
public class VisualdevController {

    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private UserService userService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private VisualdevModelDataService visualdevModelDataService;


    @ApiOperation("获取功能列表")
    @GetMapping
    public ActionResult list(PaginationVisualdev paginationVisualdev) {
        List<VisualdevEntity> list = visualdevService.getList(paginationVisualdev);
        List<VisualDevListVO> data = JsonUtil.getJsonToList(list, VisualDevListVO.class);
        List<UserAllModel> userAllVos = userService.getAll();
        for (VisualDevListVO vo : data) {
            for (UserAllModel userVo : userAllVos) {
                if (userVo.getId().equals(vo.getCreatorUser())) {
                    vo.setCreatorUser(userVo.getRealName() + "/" + userVo.getAccount());
                }
                if (userVo.getId().equals(vo.getLastmodifyuser())) {
                    vo.setLastmodifyuser(userVo.getRealName() + "/" + userVo.getAccount());
                }
            }
        }
        ListVO listVO = new ListVO();
        listVO.setList(data);
        return ActionResult.success(listVO);
    }

    @ApiOperation("获取功能列表下拉框")
    @GetMapping("/Selector")
    public ActionResult selectorList(Integer type) {
        List<VisualdevEntity> list = visualdevService.getList().stream().filter(t -> t.getState() == 1).collect(Collectors.toList());
        if (type != null) {
            list = list.stream().filter(t -> type.equals(t.getType())).collect(Collectors.toList());
        }
        List<VisualdevTreeVO> voList = new ArrayList<>();
        HashSet<String> cate = new HashSet<>(16);
        for (VisualdevEntity entity : list) {
            if (!StringUtil.isEmpty(entity.getCategory())) {
                DictionaryDataEntity dataEntity = dictionaryDataService.getInfo(entity.getCategory());
                if (dataEntity != null) {
                    int i = cate.size();
                    cate.add(dataEntity.getId());
                    if (cate.size() == i + 1) {
                        VisualdevTreeVO visualdevTreeVO = new VisualdevTreeVO();
                        visualdevTreeVO.setId(entity.getCategory());
                        visualdevTreeVO.setFullName(dataEntity.getFullName());
                        visualdevTreeVO.setHasChildren(true);
                        voList.add(visualdevTreeVO);
                    }
                }
            }
        }

        for (VisualdevTreeVO vo : voList) {
            List<VisualdevTreeChildModel> visualdevTreeChildModelList = new ArrayList<>();
            for (VisualdevEntity entity : list) {
                if (vo.getId().equals(entity.getCategory())) {
                    VisualdevTreeChildModel model = JsonUtil.getJsonToBean(entity, VisualdevTreeChildModel.class);
                    visualdevTreeChildModelList.add(model);
                }
            }
            vo.setChildren(visualdevTreeChildModelList);
        }
        ListVO listVO = new ListVO();
        listVO.setList(voList);
        return ActionResult.success(listVO);
    }

    @ApiOperation("获取功能信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        VisualdevEntity entity = visualdevService.getInfo(id);
        VisualDevInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, VisualDevInfoVO.class);
        return ActionResult.success(vo);
    }
    @ApiOperation("获取功能信息")
    @GetMapping("/getInfo/{id}")
    public VisualdevEntity getInfo(@PathVariable("id") String id) throws DataException {
        return visualdevService.getInfo(id);
    }

    @ApiOperation("获取表单主表属性下拉框")
    @GetMapping("/{id}/FormDataFields")
    public ActionResult getFormData(@PathVariable("id") String id) {
        VisualdevEntity entity = visualdevService.getInfo(id);
        Map<String, Object> formData = JsonUtil.stringToMap(entity.getFormData());
        List<FieLdsModel> fieLdsModelList = JsonUtil.getJsonToList(formData.get("fields").toString(), FieLdsModel.class);
        List<FormDataField> formDataFieldList = new ArrayList<>();
        for (FieLdsModel fieLdsModel : fieLdsModelList) {
            if (!"".equals(fieLdsModel.getVModel()) && !SmartKeyConsts.CHILD_TABLE.equals(fieLdsModel.getConfig().getJnpfKey()) && !"relationForm".equals(fieLdsModel.getConfig().getJnpfKey())) {
                FormDataField formDataField = new FormDataField();
                formDataField.setLabel(fieLdsModel.getConfig().getLabel());
                formDataField.setVModel(fieLdsModel.getVModel());
                formDataFieldList.add(formDataField);
            }
        }
        ListVO<FormDataField> listVO = new ListVO();
        listVO.setList(formDataFieldList);
        return ActionResult.success(listVO);
    }

    @ApiOperation("获取表单主表属性列表")
    @GetMapping("/{id}/FieldDataSelect")
    public ActionResult getFormData(@PathVariable("id") String id, String field) throws ParseException, DataException, IOException, SQLException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(id);
        PaginationModel paginationModel = new PaginationModel();
        Map<String, Object> formData = JsonUtil.stringToMap(visualdevEntity.getFormData());
        List<FieLdsModel> fieLdsModelList = JsonUtil.getJsonToList(formData.get("fields").toString(), FieLdsModel.class);
        List<FieLdsModel> newFieLdsModelList = new ArrayList<>();
        for (FieLdsModel fieLdsModel : fieLdsModelList) {
            if (field.equals(fieLdsModel.getVModel())) {
                newFieLdsModelList.add(fieLdsModel);
            }
        }
        formData.put("fields", JsonUtilEx.getObjectToString(newFieLdsModelList));
        visualdevEntity.setFormData(JsonUtil.getJsonToBean(formData, String.class));
        List<Map<String, Object>> realList = visualdevModelDataService.getListResult(visualdevEntity, paginationModel);
        List<FieldDataSelectVO> voList = new ArrayList<>();
        for (Map<String, Object> realMap : realList) {
            if (realMap.containsKey(field)) {
                FieldDataSelectVO fieldDataSelectVO = new FieldDataSelectVO();
                fieldDataSelectVO.setId(realMap.get("id").toString());
                fieldDataSelectVO.setFullName(realMap.get(field).toString());
                voList.add(fieldDataSelectVO);
            }
        }
        ListVO listVO = new ListVO();
        listVO.setList(voList);
        return ActionResult.success(listVO);
    }


    /**
     * 复制功能
     *
     * @param id
     * @return
     */
    @ApiOperation("复制功能")
    @PostMapping("/{id}/Actions/Copy")
    public ActionResult copyInfo(@PathVariable("id") String id) {
        VisualdevEntity entity = visualdevService.getInfo(id);
        entity.setState(0);
        entity.setFullName(entity.getFullName() + "_副本");
        entity.setLastModifyTime(null);
        entity.setLastModifyUser(null);
        entity.setCreatorTime(null);
        VisualdevEntity entity1 = JsonUtil.getJsonToBean(entity, VisualdevEntity.class);
        visualdevService.create(entity1);
        return ActionResult.success("新建成功");
    }


    /**
     * 更新功能状态
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新功能状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult update(@PathVariable("id") String id) {
        VisualdevEntity entity = visualdevService.getInfo(id);
        if (entity != null) {
            if (entity.getState() == 1) {
                entity.setState(0);
            } else {
                entity.setState(1);
            }
            boolean flag = visualdevService.update(entity.getId(), entity);
            if (flag == false) {
                return ActionResult.fail("更新失败，任务不存在");
            }
        }
        return ActionResult.success("更新成功");
    }


    @ApiOperation("新建功能")
    @PostMapping
    public ActionResult create(@RequestBody VisualDevCrForm visualDevCrForm) {
        VisualdevEntity entity = JsonUtil.getJsonToBean(JsonUtilEx.getObjectToString(visualDevCrForm), VisualdevEntity.class);
        visualdevService.create(entity);
        return ActionResult.success("新建成功");
    }


    @ApiOperation("修改功能")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody VisualDevUpForm visualDevUpForm) {
        VisualdevEntity entity = JsonUtil.getJsonToBean(JsonUtilEx.getObjectToString(visualDevUpForm), VisualdevEntity.class);
        boolean flag = visualdevService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }


    @ApiOperation("删除功能")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        VisualdevEntity entity = visualdevService.getInfo(id);
        if (entity != null) {
            visualdevService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    @ApiOperation("获取模板按钮和列表字段")
    @GetMapping("/ModuleBtn")
    public ActionResult getModuleBtn(String moduleId) {
        VisualdevEntity visualdevEntity =visualdevService.getInfo(moduleId);
        //去除模板中的F_
        VisualUtil.delfKey(visualdevEntity);
        List<BtnData> btnData =new ArrayList<>();
        Map<String,Object> column=JsonUtil.stringToMap(visualdevEntity.getColumnData());
        if(column.get("columnBtnsList")!=null){
            btnData.addAll(JsonUtil.getJsonToList(JsonUtil.getJsonToListMap(column.get("columnBtnsList").toString()),BtnData.class));
        }
        if(column.get("btnsList")!=null){
            btnData.addAll(JsonUtil.getJsonToList(JsonUtil.getJsonToListMap(column.get("btnsList").toString()),BtnData.class));
        }
        return ActionResult.success(btnData);
    }

}


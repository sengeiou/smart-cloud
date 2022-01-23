package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.*;
import smart.base.entity.DictionaryDataEntity;
import smart.base.entity.ProvinceEntity;
import smart.base.vo.ListVO;
import smart.base.vo.PaginationVO;
import smart.exception.DataException;
import smart.model.tableexample.*;
import smart.permission.UsersApi;
import smart.permission.model.user.UserAllModel;
import smart.service.TableExampleService;
import smart.entity.TableExampleEntity;
import smart.model.tableexample.postil.PostilInfoVO;
import smart.model.tableexample.postil.PostilModel;
import smart.model.tableexample.postil.PostilSendForm;
import smart.util.DateUtil;
import smart.util.JsonUtil;
import smart.util.StringUtil;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.util.UserProvider;
import smart.util.type.StringNumber;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表格示例数据
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "表格示例数据", value = "TableExample")
@RestController
@RequestMapping("/TableExample")
public class TableExampleController {

    @Autowired
    private TableExampleService tableExampleService;
    @Autowired
    private AreaApi areaApi;
    @Autowired
    private DictionaryDataApi dictionaryDataApi;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UsersApi usersApi;

    /**
     * 列表
     *
     * @param paginationTableExample
     * @return
     */
    @ApiOperation("获取表格数据列表")
    @GetMapping
    public ActionResult list(PaginationTableExample paginationTableExample) {
        List<TableExampleEntity> data = tableExampleService.getList(paginationTableExample);
        List<TableExampleListVO> list = JsonUtil.getJsonToList(data, TableExampleListVO.class);
        List<UserAllModel> userAllModels=usersApi.getAll().getData();
        for(TableExampleListVO tableExampleListVO:list){
            for(UserAllModel user:userAllModels){
                if(user.getId().equals(tableExampleListVO.getRegistrant())){
                    tableExampleListVO.setRegistrant(user.getRealName()+"/"+user.getAccount());
                }
            }
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationTableExample, PaginationVO.class);
        return ActionResult.page(list,paginationVO);
    }

    /**
     * 列表（树形表格）
     *
     * @param typeId 主键值
     * @return
     */
    @ApiOperation("（树形表格）")
    @GetMapping("/ControlSample/{typeId}")
    public ActionResult list(@PathVariable("typeId") String typeId, PaginationTableExample paginationTableExample) {
        List<TableExampleEntity> data = tableExampleService.getList(typeId, paginationTableExample);
        List<TableExampleListVO> list = JsonUtil.getJsonToList(data, TableExampleListVO.class);
        List<UserAllModel> userAllModels=usersApi.getAll().getData();
        for(TableExampleListVO tableExampleListVO:list){
            for(UserAllModel user:userAllModels){
                if(user.getId().equals(tableExampleListVO.getRegistrant())){
                    tableExampleListVO.setRegistrant(user.getRealName()+"/"+user.getAccount());
                }
            }
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationTableExample, PaginationVO.class);
        return ActionResult.page(list,paginationVO);
    }

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("获取表格分组列表")
    @GetMapping("/All")
    public ActionResult listAll(String keyword) {
        List<TableExampleEntity> data = tableExampleService.getList();
        List<TableExampleListAllVO> list = JsonUtil.getJsonToList(data, TableExampleListAllVO.class);
        List<UserAllModel> userAllModels=usersApi.getAll().getData();
        for(TableExampleListAllVO tableExampleListAllVO:list){
            for(UserAllModel user:userAllModels){
                if(user.getId().equals(tableExampleListAllVO.getRegistrant())){
                    tableExampleListAllVO.setRegistrant(user.getRealName()+"/"+user.getAccount());
                }
            }
        }
        ListVO<TableExampleListAllVO> vo = new ListVO<>();
        vo.setList(list);
        return ActionResult.success(vo);
    }

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("获取延伸扩展列表(行政区划)")
    @GetMapping("/IndustryList")
    public ActionResult industryList(String keyword) {
        List<ProvinceEntity> data = areaApi.getList("-1").getData();
        if (!StringUtils.isEmpty(keyword)){
            data = data.stream().filter(t->t.getFullName().contains(keyword)).collect(Collectors.toList());
        }
        List<TableExampleIndustryListVO> listVos = JsonUtil.getJsonToList(data, TableExampleIndustryListVO.class);
        ListVO<TableExampleIndustryListVO> vo = new ListVO<>();
        vo.setList(listVos);
        return ActionResult.success(vo);
    }

    /**
     * 列表
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取城市信息列表(获取延伸扩展列表(行政区划))")
    @GetMapping("/CityList/{id}")
    public ActionResult cityList(@PathVariable("id") String id) {
        List<ProvinceEntity> data = areaApi.getList(id).getData();
        List<TableExampleCityListVO> listVos = JsonUtil.getJsonToList(data, TableExampleCityListVO.class);
        ListVO<TableExampleCityListVO> vo = new ListVO<>();
        vo.setList(listVos);
        return ActionResult.success(vo);
    }

    /**
     * 列表（表格树形）
     *
     * @return
     */
    @ApiOperation("表格树形")
    @GetMapping("/ControlSample/TreeList")
    public ActionResult treeList(String isTree) {
        List<DictionaryDataEntity> data = dictionaryDataApi.getList("d59a3cf65f9847dbb08be449e3feae16").getData();
        List<TableExampleTreeModel> treeList = new ArrayList<>();
        for (DictionaryDataEntity entity : data) {
            TableExampleTreeModel treeModel = new TableExampleTreeModel();
            treeModel.setId(entity.getId());
            treeModel.setText(entity.getFullName());
            treeModel.setParentId(entity.getParentId());
            treeModel.setLoaded(true);
            treeModel.setExpanded(true);
            treeModel.setHt(JsonUtil.entityToMap(entity));
            treeList.add(treeModel);
        }
        if (isTree != null && StringNumber.ONE.equals(isTree)) {
            List<SumTree<TableExampleTreeModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
            List<TableExampleTreeModel> listVO = JsonUtil.getJsonToList(trees, TableExampleTreeModel.class);
            ListVO vo = new ListVO();
            vo.setList(listVO);
            return ActionResult.success(vo);
        }
        ListVO vo = new ListVO();
        vo.setList(treeList);
        return ActionResult.success(vo);
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取普通表格示例信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        TableExampleEntity entity = tableExampleService.getInfo(id);
        TableExampleInfoVO vo = JsonUtil.getJsonToBeanEx(entity, TableExampleInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除项目")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        TableExampleEntity entity = tableExampleService.getInfo(id);
        if (entity != null) {
            tableExampleService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 创建
     *
     * @param tableExampleCrForm 实体对象
     * @return
     */
    @ApiOperation("新建项目")
    @PostMapping
    public ActionResult create(@RequestBody @Valid TableExampleCrForm tableExampleCrForm) {
        TableExampleEntity entity = JsonUtil.getJsonToBean(tableExampleCrForm, TableExampleEntity.class);
        entity.setCostAmount(entity.getCostAmount() == null ? new BigDecimal("0") : entity.getCostAmount());
        entity.setTunesAmount(entity.getTunesAmount() == null ? new BigDecimal("0") : entity.getTunesAmount());
        entity.setProjectedIncome(entity.getProjectedIncome() == null ? new BigDecimal("0") : entity.getProjectedIncome());
        entity.setSign("0000000");
        tableExampleService.create(entity);
        return ActionResult.success("创建成功");
    }

    /**
     * 更新
     *
     * @param id                 主键值
     * @param tableExampleUpForm 实体对象
     * @return
     */
    @ApiOperation("更新项目")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid TableExampleUpForm tableExampleUpForm) {
        TableExampleEntity entity = JsonUtil.getJsonToBean(tableExampleUpForm, TableExampleEntity.class);
        entity.setCostAmount(entity.getCostAmount() == null ? new BigDecimal("0") : entity.getCostAmount());
        entity.setTunesAmount(entity.getTunesAmount() == null ? new BigDecimal("0") : entity.getTunesAmount());
        entity.setProjectedIncome(entity.getProjectedIncome() == null ? new BigDecimal("0") : entity.getProjectedIncome());
        boolean flag = tableExampleService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 更新标签
     *
     * @param id                     主键值
     * @param tableExampleSignUpForm 实体对象
     * @return
     */
    @ApiOperation("更新标记")
    @PutMapping("/UpdateSign/{id}")
    public ActionResult updateSign(@PathVariable("id") String id, @RequestBody @Valid TableExampleSignUpForm tableExampleSignUpForm) {
        TableExampleEntity entity = JsonUtil.getJsonToBean(tableExampleSignUpForm, TableExampleEntity.class);
        TableExampleEntity tableExampleEntity = tableExampleService.getInfo(id);
        if (tableExampleEntity == null) {
            return ActionResult.success("更新失败，数据不存在");
        }
        tableExampleEntity.setSign(entity.getSign());
        tableExampleService.update(id, entity);
        return ActionResult.success("更新成功");
    }

    /**
     * 行编辑
     *
     * @param tableExampleRowUpForm 实体对象
     * @return
     */
    @ApiOperation("行编辑")
    @PutMapping("/{id}/Actions/RowsEdit")
    public ActionResult rowEditing(@PathVariable("id") String id, @RequestBody @Valid TableExampleRowUpForm tableExampleRowUpForm) {
        TableExampleEntity entity = JsonUtil.getJsonToBean(tableExampleRowUpForm, TableExampleEntity.class);
        entity.setCostAmount(entity.getCostAmount() == null ? new BigDecimal("0") : entity.getCostAmount());
        entity.setTunesAmount(entity.getTunesAmount() == null ? new BigDecimal("0") : entity.getTunesAmount());
        entity.setProjectedIncome(entity.getProjectedIncome() == null ? new BigDecimal("0") : entity.getProjectedIncome());
        entity.setId(id);
        boolean falg = tableExampleService.rowEditing(entity);
        if (falg == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 发送
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("发送批注")
    @PostMapping("/{id}/Postil")
    public ActionResult sendPostil(@PathVariable("id") String id, @RequestBody PostilSendForm postilSendForm) {
        TableExampleEntity tableExampleEntity = tableExampleService.getInfo(id);
        if (tableExampleEntity == null) {
            return ActionResult.success("发送失败，数据不存在");
        }
        UserInfo userInfo = userProvider.get();
        PostilModel model = new PostilModel();
        model.setCreatorTime(DateUtil.getNow("+8"));
        model.setText(postilSendForm.getText());
        model.setUserId(userInfo != null ? userInfo.getUserName() + "/" + userInfo.getUserAccount() : "");
        List<PostilModel> list = new ArrayList<>();
        list.add(model);
        if (!StringUtil.isEmpty(tableExampleEntity.getPostilJson())) {
            list.addAll(JsonUtil.getJsonToList(tableExampleEntity.getPostilJson(), PostilModel.class));
        }

        String postilJson = JsonUtil.getObjectToString(list);
        tableExampleEntity.setPostilJson(postilJson);
        tableExampleEntity.setPostilCount(list.size());
        tableExampleService.update(id, tableExampleEntity);
        return ActionResult.success("发送成功");
    }


    /**
     * 发送
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取批注")
    @GetMapping("/{id}/Actions/Postil")
    public ActionResult getPostil(@PathVariable("id") String id) {
        TableExampleEntity tableExampleEntity = tableExampleService.getInfo(id);
        if (tableExampleEntity == null) {
            return ActionResult.success("获取失败，数据不存在");
        }
        PostilInfoVO vo = new PostilInfoVO();
        vo.setPostilJson(tableExampleEntity.getPostilJson());
        return ActionResult.success(vo);
    }

    /**
     * 删除批注
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除批注")
    @DeleteMapping("/{id}/Postil/{index}")
    public ActionResult deletePostil(@PathVariable("id") String id, @PathVariable("index") int index) {
        TableExampleEntity tableExampleEntity = tableExampleService.getInfo(id);
        if (tableExampleEntity == null) {
            return ActionResult.success("删除失败，数据不存在");
        }
        List<PostilModel> list = JsonUtil.getJsonToList(tableExampleEntity.getPostilJson(), PostilModel.class);
        list.remove(index);
        String postilJson = JsonUtil.getObjectToString(list);
        tableExampleEntity.setPostilJson(postilJson);
        tableExampleEntity.setPostilCount((list.size()));
        tableExampleService.update(id, tableExampleEntity);
        return ActionResult.success("删除成功");
    }
}

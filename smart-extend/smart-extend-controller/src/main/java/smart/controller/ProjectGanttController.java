package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.model.projectgantt.*;
import smart.permission.model.user.UserAllModel;
import smart.service.ProjectGanttService;
import smart.entity.ProjectGanttEntity;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.Page;
import smart.exception.DataException;
import smart.util.treeutil.ListToTreeUtil;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.permission.UsersApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目计划
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "项目计划", value = "ProjectGantt")
@RestController
@RequestMapping("/ProjectGantt")
public class ProjectGanttController {

    @Autowired
    private ProjectGanttService projectGanttService;
    @Autowired
    private UsersApi usersApi;


    /**
     * 项目列表
     *
     * @param page
     * @return
     */
    @ApiOperation("获取项目管理列表")
    @GetMapping
    public ActionResult list(Page page) {
        List<ProjectGanttEntity> data = projectGanttService.getList(page);
        List<ProjectGanttListVO> list = JsonUtil.getJsonToList(data, ProjectGanttListVO.class);
        //获取用户给项目参与人员列表赋值
        List<UserAllModel> allUser=usersApi.getAll().getData();
        for(ProjectGanttListVO vo:list){
            List<ProjectGanttManagerIModel> list1=new ArrayList<>();
            String[] ids=vo.getManagerIds().split(",");
            for(String id:ids){
                for(UserAllModel user:allUser){
                    if(user.getId().equals(id)){
                        ProjectGanttManagerIModel model1=new ProjectGanttManagerIModel();
                        model1.setAccount(user.getRealName()+"/"+user.getAccount());
                        model1.setHeadIcon(user.getHeadIcon());
                        list1.add(model1);
                    }
                }
            }
            vo.setManagersInfo(list1);
        }
        ListVO listVO = new ListVO<>();
        listVO.setList(list);
        return ActionResult.success(listVO);
    }

    /**
     * 任务列表
     *
     * @param page
     * @param projectId     项目Id
     * @return
     */
    @ApiOperation("获取项目任务列表")
    @GetMapping("/{projectId}/Task")
    public ActionResult taskList(Page page, @PathVariable("projectId") String projectId) {
        List<ProjectGanttEntity> data = projectGanttService.getTaskList(projectId);
        List<ProjectGanttEntity> dataAll = data;
        if (!StringUtils.isEmpty(page.getKeyword())) {
            data = data.stream().filter(t -> String.valueOf(t.getFullName()).contains(page.getKeyword()) || String.valueOf(t.getEnCode()).contains(page.getKeyword())).collect(Collectors.toList());
        }
        List<ProjectGanttEntity> list = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(data, dataAll), ProjectGanttEntity.class);
        List<ProjectGanttTreeModel> treeList = JsonUtil.getJsonToList(list,ProjectGanttTreeModel.class);
        List<SumTree<ProjectGanttTreeModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        List<ProjectGanttTaskTreeVO> listVO = JsonUtil.getJsonToList(trees, ProjectGanttTaskTreeVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ActionResult.success(vo);
    }

    /**
     * 任务树形
     *
     * @param projectId 项目Id
     * @return
     */
    @ApiOperation("获取项目计划任务树形（新建任务）")
    @GetMapping("/{projectId}/Task/Selector")
    public ActionResult taskTreeView(@PathVariable("projectId") String projectId) {
        List<ProjectGanttTaskTreeModel> treeList = new ArrayList<>();
        List<ProjectGanttEntity> data = projectGanttService.getTaskList(projectId);
        for (ProjectGanttEntity entity : data) {
            ProjectGanttTaskTreeModel treeModel = new ProjectGanttTaskTreeModel();
            treeModel.setId(entity.getId());
            treeModel.setFullName(entity.getFullName());
            treeModel.setParentId(entity.getParentId());
            treeList.add(treeModel);
        }
        List<SumTree<ProjectGanttTaskTreeModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        List<ProjectGanttTaskTreeVO> listVO = JsonUtil.getJsonToList(trees, ProjectGanttTaskTreeVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ActionResult.success(vo);
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取项目计划信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ProjectGanttEntity entity = projectGanttService.getInfo(id);
        ProjectGanttInfoVO vo = JsonUtil.getJsonToBeanEx(entity, ProjectGanttInfoVO.class);
        return ActionResult.success(vo);
    }
    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取项目计划信息")
    @GetMapping("Task/{id}")
    public ActionResult taskInfo(@PathVariable("id") String id) throws DataException {
        ProjectGanttEntity entity = projectGanttService.getInfo(id);
        ProjectGanttTaskInfoVO vo = JsonUtil.getJsonToBeanEx(entity, ProjectGanttTaskInfoVO.class);
        return ActionResult.success(vo);
    }


    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除项目计划/任务")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        if (projectGanttService.allowDelete(id)) {
            ProjectGanttEntity entity = projectGanttService.getInfo(id);
            if (entity != null) {
                projectGanttService.delete(entity);
                return ActionResult.success("删除成功");
            }
            return ActionResult.fail("删除失败，此任务不存在");
        } else {
            return ActionResult.fail("此记录被关联引用,不允许被删除");
        }
    }

    /**
     * 创建
     *
     * @param projectGanttCrForm 实体对象
     * @return
     */
    @ApiOperation("添加项目计划")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ProjectGanttCrForm projectGanttCrForm) {
        ProjectGanttEntity entity = JsonUtil.getJsonToBean(projectGanttCrForm, ProjectGanttEntity.class);
        entity.setType(1);
        entity.setParentId("0");
        if (projectGanttService.isExistByFullName(projectGanttCrForm.getFullName(),entity.getId())){
            return ActionResult.fail("项目名称不能重复");
        }
        if (projectGanttService.isExistByEnCode(projectGanttCrForm.getEnCode(),entity.getId())){
            return ActionResult.fail("项目编码不能重复");
        }
        projectGanttService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 编辑
     *
     * @param id                 主键值
     * @param projectGanttUpForm 实体对象
     * @return
     */
    @ApiOperation("修改项目计划")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id,@RequestBody @Valid ProjectGanttUpForm projectGanttUpForm) {
        ProjectGanttEntity entity = JsonUtil.getJsonToBean(projectGanttUpForm, ProjectGanttEntity.class);
        if (projectGanttService.isExistByFullName(projectGanttUpForm.getFullName(),id)){
            return ActionResult.fail("项目名称不能重复");
        }
        if (projectGanttService.isExistByEnCode(projectGanttUpForm.getEnCode(),id)){
            return ActionResult.fail("项目编码不能重复");
        }
        boolean flag=  projectGanttService.update(id, entity);
        if(flag==false){
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }



    /**
     * 创建
     *
     * @param projectGanttTsakCrForm 实体对象
     * @return
     */
    @ApiOperation("添加项目任务")
    @PostMapping("/Task")
    public ActionResult createTask(@RequestBody @Valid ProjectGanttTsakCrForm projectGanttTsakCrForm) {
        ProjectGanttEntity entity = JsonUtil.getJsonToBean(projectGanttTsakCrForm, ProjectGanttEntity.class);
        entity.setType(2);
        if (projectGanttService.isExistByFullName(projectGanttTsakCrForm.getFullName(),entity.getId())){
            return ActionResult.fail("任务名称不能重复");
        }
        projectGanttService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 编辑
     *
     * @param id                 主键值
     * @param projectGanttTsakCrForm 实体对象
     * @return
     */
    @ApiOperation("修改项目任务")
    @PutMapping("/Task/{id}")
    public ActionResult updateTask(@PathVariable("id") String id, @RequestBody @Valid ProjectGanttTsakUpForm projectGanttTsakCrForm) {
        ProjectGanttEntity entity = JsonUtil.getJsonToBean(projectGanttTsakCrForm, ProjectGanttEntity.class);
        if (projectGanttService.isExistByFullName(projectGanttTsakCrForm.getFullName(),id)){
            return ActionResult.fail("任务名称不能重复");
        }
        boolean flag=  projectGanttService.update(id, entity);
        if(flag==false){
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

}

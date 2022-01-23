package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.permission.model.user.UserAllModel;
import smart.service.WorkLogService;
import smart.entity.WorkLogEntity;
import smart.model.worklog.WorkLogCrForm;
import smart.model.worklog.WorkLogInfoVO;
import smart.model.worklog.WorkLogListVO;
import smart.model.worklog.WorkLogUpForm;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.Pagination;
import smart.base.vo.PaginationVO;
import smart.exception.DataException;
import smart.permission.UsersApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作日志
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "app工作日志", value = "WorkLog")
@RestController
@RequestMapping("/WorkLog")
public class WorkLogController {

    @Autowired
    private WorkLogService workLogService;
    @Autowired
    private UsersApi usersApi;

    /**
     * 列表(我发出的)
     *
     * @param pageModel 请求参数
     * @return
     */
    @GetMapping("/Send")
    public ActionResult getSendList(Pagination pageModel) {
        List<WorkLogEntity> data = workLogService.getSendList(pageModel);
        List<WorkLogListVO> list = JsonUtil.getJsonToList(data, WorkLogListVO.class);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pageModel, PaginationVO.class);
        return ActionResult.page(list, paginationVO);
    }

    /**
     * 列表(我收到的)
     *
     * @param pageModel 请求参数
     * @return
     */
    @GetMapping("/Receive")
    public ActionResult getReceiveList(Pagination pageModel) {
        List<WorkLogEntity> data = workLogService.getReceiveList(pageModel);
        List<WorkLogListVO> list = JsonUtil.getJsonToList(data, WorkLogListVO.class);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pageModel, PaginationVO.class);
        return ActionResult.page(list, paginationVO);
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        WorkLogEntity entity = workLogService.getInfo(id);
        String[] userIds = entity.getToUserId().split(",");
        List<UserAllModel> modelList = usersApi.getAll().getData();
        List<String> userName = new ArrayList<>();
        for (String userId : userIds) {
            UserAllModel model = modelList.stream().filter(t -> t.getId().equals(userId)).findFirst().orElse(null);
            if (model != null) {
                userName.add(model.getRealName() + "/" + model.getAccount());
            }
        }
        entity.setToUserId(String.join(",", userName));
        WorkLogInfoVO vo = JsonUtil.getJsonToBeanEx(entity, WorkLogInfoVO.class);
        vo.setUserIds(String.join(",",userIds));
        return ActionResult.success(vo);
    }

    /**
     * 新建
     *
     * @param workLogCrForm 实体对象
     * @return
     */
    @ApiOperation("新建")
    @PostMapping
    public ActionResult create(@RequestBody @Valid WorkLogCrForm workLogCrForm) {
        WorkLogEntity entity = JsonUtil.getJsonToBean(workLogCrForm, WorkLogEntity.class);
        workLogService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新
     *
     * @param id            主键值
     * @param workLogUpForm 实体对象
     * @return
     */
    @ApiOperation("更新")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid WorkLogUpForm workLogUpForm) {
        WorkLogEntity entity = JsonUtil.getJsonToBean(workLogUpForm, WorkLogEntity.class);
        boolean flag = workLogService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        WorkLogEntity entity = workLogService.getInfo(id);
        if (entity != null) {
            workLogService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }
}


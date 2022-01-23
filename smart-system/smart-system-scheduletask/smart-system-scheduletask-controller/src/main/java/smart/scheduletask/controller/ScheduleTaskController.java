package smart.scheduletask.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.Pagination;
import smart.base.vo.PaginationVO;
import smart.scheduletask.entity.TimeTaskEntity;
import smart.exception.DataException;
import smart.scheduletask.entity.TimeTaskLogEntity;
import smart.scheduletask.model.*;
import smart.scheduletask.service.TimetaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 任务调度
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "任务调度", value = "TimeTask")
@RestController
@RequestMapping("/ScheduleTask")
public class ScheduleTaskController {

    @Autowired
    private TimetaskService timetaskService;

    /**
     * 获取任务调度列表
     *
     * @param pagination
     * @return
     */
    @ApiOperation("获取任务调度列表")
    @GetMapping
    public ActionResult list(Pagination pagination) {
        List<TimeTaskEntity> data = timetaskService.getList(pagination);
        List<TaskVO> list = JsonUtil.getJsonToList(data, TaskVO.class);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(list, paginationVO);
    }

    /**
     * 获取任务调度日志列表
     *
     * @param pagination
     * @param taskId     任务Id
     * @return
     */
    @ApiOperation("获取任务调度日志列表")
    @GetMapping("/{id}/TaskLog")
    public ActionResult list(@PathVariable("id") String taskId, Pagination pagination) {
        List<TimeTaskLogEntity> data = timetaskService.getTaskLogList(pagination, taskId);
        List<TaskLogVO> list = JsonUtil.getJsonToList(data, TaskLogVO.class);
        PaginationVO pageModel = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(list, pageModel);
    }

    /**
     * 获取任务调度信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取任务调度信息")
    @GetMapping("/Info/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        TimeTaskEntity entity = timetaskService.getInfo(id);
        TaskInfoVO vo = JsonUtil.getJsonToBeanEx(entity, TaskInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建任务调度
     *
     * @param taskCrForm
     * @return
     */
    @ApiOperation("新建任务调度")
    @PostMapping
    public ActionResult create(@RequestBody @Valid TaskCrForm taskCrForm) {
        TimeTaskEntity entity = JsonUtil.getJsonToBean(taskCrForm, TimeTaskEntity.class);
        if (timetaskService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ActionResult.fail("任务名称不能重复");
        }
        timetaskService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 修改任务调度
     *
     * @param id         主键值
     * @param taskUpForm
     * @return
     */
    @ApiOperation("修改任务调度")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid TaskUpForm taskUpForm) {
        TimeTaskEntity entity = JsonUtil.getJsonToBean(taskUpForm, TimeTaskEntity.class);
        if (timetaskService.isExistByFullName(entity.getFullName(), id)) {
            return ActionResult.fail("任务名称不能重复");
        }
        TimeTaskEntity taskEntity = timetaskService.getInfo(id);
        if (taskEntity == null) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        entity.setEnabledMark(taskEntity.getEnabledMark());
        entity.setRunCount(taskEntity.getRunCount());
        try{
            boolean flag = timetaskService.update(id, entity);
            System.out.println(flag);
            if (!flag) {
                    return ActionResult.fail("更新失败，数据不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除任务
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除任务")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        TimeTaskEntity entity = timetaskService.getInfo(id);
        if (entity != null) {
            timetaskService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 停止任务调度
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("停止任务调度")
    @PutMapping("/{id}/Actions/Stop")
    public ActionResult stop(@PathVariable("id") String id) {
        TimeTaskEntity entity = timetaskService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark(0);
            timetaskService.update(id, entity);
            return ActionResult.success("操作成功");
        }
        return ActionResult.fail("操作失败，任务不存在");
    }

    /**
     * 启动任务调度
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("启动任务调度")
    @PutMapping("/{id}/Actions/Enable")
    public ActionResult enable(@PathVariable("id") String id) {
        TimeTaskEntity entity = timetaskService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark(1);
            timetaskService.update(id, entity);
            return ActionResult.success("操作成功");
        }
        return ActionResult.fail("操作失败，任务不存在");
    }
}

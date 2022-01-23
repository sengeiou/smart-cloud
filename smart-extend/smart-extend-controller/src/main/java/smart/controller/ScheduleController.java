package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.model.schedule.*;
import smart.service.ScheduleService;
import smart.entity.ScheduleEntity;
import smart.util.DateUtil;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.exception.DataException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日程安排
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "日程安排", value = "Schedule")
@RestController
@RequestMapping("/Schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 列表
     *
     * @param scheduleTime 时间对象
     * @return
     */
    @ApiOperation("获取日程安排列表")
    @GetMapping
    public ActionResult list(ScheduleTime scheduleTime) {
        List<ScheduleEntity> data = scheduleService.getList(scheduleTime.getStartTime(), scheduleTime.getEndTime());
        List<ScheduleListVO> list = JsonUtil.getJsonToList(data, ScheduleListVO.class);
        ListVO<ScheduleListVO> listVO = new ListVO();
        listVO.setList(list);
        return ActionResult.success(listVO);
    }

    /**
     * 列表（app使用）
     *
     * @param scheduleTime 时间对象
     * @return
     */
    @ApiOperation("（待定）列表")
    @GetMapping("/AppList")
    public ActionResult appList(ScheduleTimes scheduleTime) {
        Map<String, Object> days = new LinkedHashMap<>();
        List<ScheduleEntity> scheduleList = scheduleService.getList(scheduleTime.getStartTime(), scheduleTime.getEndTime());
        Date start = DateUtil.stringToDates(scheduleTime.getStartTime());
        Date end = DateUtil.stringToDates(scheduleTime.getEndTime());
        List<Date> item = DateUtil.getAllDays(start, end);
        if(StringUtils.isEmpty(scheduleTime.getDateTime())){
            scheduleTime.setDateTime(DateUtil.dateNow("yyyyMMdd"));
        }else{
            scheduleTime.setDateTime(scheduleTime.getDateTime().replaceAll("-",""));
        }
        Map<String,List<ScheduleEntity>> dataList = new HashMap<>();
        for (int i = 0; i < item.size(); i++) {
            String startTime = DateUtil.daFormat(item.get(i)) + " 00:00";
            String endTime = DateUtil.daFormat(item.get(i)) + " 23:59";
            List<ScheduleEntity> count = scheduleList.stream().filter(m -> DateUtil.dateFormat(m.getStartTime()).compareTo(endTime) <= 0 && DateUtil.dateFormat(m.getEndTime()).compareTo(startTime) >= 0).collect(Collectors.toList());
            String time = DateUtil.daFormat(item.get(i)).replaceAll("-", "");
            days.put(time, count.size());
            dataList.put(time,count);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("signList", days);
        List<ScheduleEntity> todayList = dataList.get(scheduleTime.getDateTime());
        data.put("todayList", JsonUtil.listToJsonfield(todayList));
        return ActionResult.success(data);
    }

    /**
     * 列表(app使用）
     *
     * @param scheduleTimes 时间对象
     * @return
     */
    @ApiOperation("（待定）列表")
    @GetMapping("/AppDayList")
    public ActionResult list(ScheduleTimes scheduleTimes) {
        List<ScheduleEntity> data = scheduleService.getList(scheduleTimes.getStartTime(), scheduleTimes.getEndTime());
        List<ScheduleEntity> datas = new ArrayList<>();
        Date dateTimes = DateUtil.stringToDates(scheduleTimes.getDateTime());
        if (!StringUtils.isEmpty(scheduleTimes.getDateTime())) {
            for (ScheduleEntity entity : data) {
                Date startTimes = entity.getStartTime();
                Date endTimes = entity.getEndTime();
                if (DateUtil.isEffectiveDate(dateTimes, startTimes, endTimes)) {
                    datas.add(entity);
                }
            }
        }
        return ActionResult.success(JsonUtil.listToJsonfield(datas));
    }


    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取日程安排信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ScheduleEntity entity = scheduleService.getInfo(id);
        ScheduleInfoVO vo = JsonUtil.getJsonToBeanEx(entity, ScheduleInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建
     *
     * @param scheduleCrForm 实体对象
     * @return
     */
    @ApiOperation("新建日程安排")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ScheduleCrForm scheduleCrForm) {
        ScheduleEntity entity = JsonUtil.getJsonToBean(scheduleCrForm, ScheduleEntity.class);
        scheduleService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新
     *
     * @param id             主键值
     * @param scheduleUpForm 实体对象
     * @return
     */
    @ApiOperation("更新日程安排")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid ScheduleUpForm scheduleUpForm) {
        ScheduleEntity entity = JsonUtil.getJsonToBean(scheduleUpForm, ScheduleEntity.class);
        boolean flag = scheduleService.update(id, entity);
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
    @ApiOperation("删除日程安排")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        ScheduleEntity entity = scheduleService.getInfo(id);
        if (entity != null) {
            scheduleService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

}

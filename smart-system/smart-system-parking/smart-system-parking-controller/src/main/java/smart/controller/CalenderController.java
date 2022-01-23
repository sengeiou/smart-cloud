package smart.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.base.UserInfo;
import smart.entity.CalenderEntity;
import smart.model.calender.CalenderCrFrom;
import smart.model.calender.CalenderJspVO;
import smart.service.CalenderService;
import smart.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日期设置
 */
@Slf4j
@RestController
@Api(tags = "日期设置" , description = "Calender")
@RequestMapping("/Calender")
public class CalenderController {

    @Autowired
    private UserProvider userProvider;

    @Autowired
    CalenderService calenderService;

    @GetMapping
    @ApiOperation("获取日历名称下拉框数据")
    public ActionResult getCalenderName(){
        List<String> list =calenderService.getAllName();
       return ActionResult.success(list);
    }

    /**
     * 创建日期名称
     *
     * @param calenderCrFrom
     * @return
     */
    @PostMapping
    @Transactional
    @ApiOperation("添加日历名称,传参name")
    public ActionResult createCalenderName(@RequestBody CalenderCrFrom calenderCrFrom) {
        if (StringUtil.isBlank(calenderCrFrom.getName())){
            return ActionResult.fail("参数名称为空！");
        }
        UserInfo userInfo = userProvider.get();
        calenderCrFrom.setCreatortime(DateUtil.getNow());
        calenderCrFrom.setCreatoruserid(userInfo.getUserId());
        CalenderEntity entity = JsonUtil.getJsonToBean(calenderCrFrom, CalenderEntity.class);
        return calenderService.createName(entity);
    }

    /**
     * 定时创建日历下一年的休息日配置
     * @return
     */
    @GetMapping("/scheduleCreateCalender")
    @Transactional
    @ApiOperation("定时创建日历下一年的休息日配置")
    public ActionResult scheduleCreateCalender() {
        return calenderService.scheduleCreateCalender();
    }

    /**
     * 删除日期名称
     *
     * @param name
     * @return
     */
    @DeleteMapping("/{name}")
    @Transactional
    @ApiOperation("删除日历名称,传参name")
    public ActionResult deleteCalenderName(@PathVariable("name") String name){
        if (StringUtil.isBlank(name)){
            return ActionResult.fail("删除对象不能为空！");
        }else{
            return calenderService.deleteCalenderName(name);
        }
    }

    @GetMapping("/getCalender")
    @ApiOperation("获取某年某个日历配置下的日期设置,传参name,year")
    public ActionResult getCalender(CalenderJspVO jspVO){
        List<JSONObject> list = calenderService.getCalender(jspVO);
        return ActionResult.success(list);
    }

    @PostMapping("/saveCalender")
    @Transactional
    @ApiOperation("添加日期设置,传参name,type,list")
    public ActionResult saveCalender(@RequestBody CalenderJspVO jspVO){
        UserInfo userInfo = userProvider.get();
        jspVO.setCreatortime(DateUtil.getNowDate());
        jspVO.setCreatoruserid(userInfo.getUserId());
        return calenderService.saveCalender(jspVO);
    }

}

package smart.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.ActionResult;
import smart.entity.CalenderEntity;
import smart.model.calender.CalenderJspVO;

import java.util.List;

public interface CalenderService extends IService<CalenderEntity> {
    /**
     * 获取所有日历管理名称
     * @return
     */
    List<String> getAllName();

    /**
     * 添加日历名称 添加时默认添加休息日
     * @param entity
     */
    ActionResult createName(CalenderEntity entity);

    /**
     * 删除该名称的日历
     * @param name
     */
    ActionResult deleteCalenderName(String name);

    /**
     * 获取一年中的日历设置
     * @param jspVO
     * @return
     */
    List<JSONObject> getCalender(CalenderJspVO jspVO);

    /**
     * 更新某个名称下的日历配置
     * @param jspVO
     * @return
     */
    ActionResult saveCalender(CalenderJspVO jspVO);

    /**
     * 定时创建明年的休息日设置
     * @return
     */
    ActionResult scheduleCreateCalender();
}

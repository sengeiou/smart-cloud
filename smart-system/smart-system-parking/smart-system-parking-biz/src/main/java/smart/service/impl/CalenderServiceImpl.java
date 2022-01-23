package smart.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.ActionResult;
import smart.entity.CalenderEntity;
import smart.mapper.CalenderMapper;
import smart.model.calender.CalenderJspVO;
import smart.service.CalenderService;
import smart.util.DateUtil;
import smart.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CalenderServiceImpl extends ServiceImpl<CalenderMapper, CalenderEntity> implements CalenderService {

    @Autowired
    CalenderMapper calenderMapper;

    /**
     * 获取所有日历名称
     *
     * @return
     */
    @Override
    public List<String> getAllName() {
        return calenderMapper.getAllName();
    }

    /**
     * 添加日历名称 默认添加当前年份的所有节假日
     *
     * @param entity
     */
    @Override
    public ActionResult createName(CalenderEntity entity) {
        //日历名称不能重复
        Integer existingNumber = calenderMapper.getNumberByName(entity.getName());
        if (existingNumber != null && existingNumber > 0) {
            return ActionResult.fail("该名称已存在!");
        }

        //获取所有的节假日
        entity.setId(RandomUtil.uuId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        List<String> holiday = DateUtil.getHolidaysByYear(calendar.get(Calendar.YEAR));

        //处理数据并添加
        List<CalenderEntity> list = new ArrayList<>();
        if (holiday.size() > 0) {
            holiday.forEach(s -> {
                CalenderEntity listEntity = CalenderEntity.builder()
                        .id(RandomUtil.uuId()).date(s).name(entity.getName()).mark(1).creatortime(entity.getCreatortime())
                        .creatoruserid(entity.getCreatoruserid()).build();
                list.add(listEntity);
            });
        }
        this.saveBatch(list, 200);
        return ActionResult.success("新建成功！");
    }

    /**
     * 定时生成明年休息日
     *
     * @return
     */
    @Override
    public ActionResult scheduleCreateCalender() {
        log.info("定时调用calender");
        //获取所有的日历名称
        List<String> names = calenderMapper.getAllName();
        if (names == null || names.size() < 1) {
            return ActionResult.success("没有日历配置(success)");
        }
        //获取所有的节假日
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        List<String> holiday = DateUtil.getHolidaysByYear(calendar.get(Calendar.YEAR) + 1);

        //处理数据并添加
        List<CalenderEntity> list = new ArrayList<>();
        if (holiday.size() > 0) {
            for (int i = 0; i < names.size(); i++) {
                String name = names.get(i);
                //数据库已设置的明年数据
                CalenderJspVO jspVO = new CalenderJspVO();
                jspVO.setName(name);
                jspVO.setYear(calendar.get(Calendar.YEAR) + 1);
                List<CalenderEntity> old = getCalenderByYearAndName(jspVO);
                Map<String, CalenderEntity> map = old != null && old.size() > 0 ? old.stream().collect(Collectors.toMap(CalenderEntity::getDate, a -> a)) : null;

                holiday.forEach(s -> {
                    //数据库没有设置的添加到数据库
                    if (map == null || !map.containsKey(s)) {
                        CalenderEntity listEntity = CalenderEntity.builder()
                                .id(RandomUtil.uuId()).date(s).name(name)
                                .mark(1).creatortime(DateUtil.getNowDate())
                                .creatoruserid("系统定时创建").build();
                        list.add(listEntity);
                    }
                });
            }
            this.saveBatch(list, 200);
        }
        return ActionResult.success("定时生成日历配置成功！");
    }

    @Override
    public ActionResult deleteCalenderName(String name) {
        //查询是否有绑定，如有绑定，不能删除
        Integer existingConfiguration = calenderMapper.existingConfiguration(name);
        if (existingConfiguration != null && existingConfiguration > 0) {
            return ActionResult.fail("该日期配置有收费标准绑定，请先解绑!");
        }
//        calenderMapper.deleteCalenderName(name);
        Map<String, Object> map = new HashMap<>();
        map.put("F_name", name);
        this.removeByMap(map);
        return ActionResult.success("删除成功！");
    }

    private List<CalenderEntity> getCalenderByYearAndName(CalenderJspVO jspVO) {
        QueryWrapper<CalenderEntity> queryWrapper = new QueryWrapper<>();
        if (jspVO.getYear() != null && jspVO.getYear() > 0) {
            queryWrapper.lambda().between(CalenderEntity::getDate, jspVO.getYear() + "-01-01", jspVO.getYear() + "-12-31");
        }
        if (StringUtils.isNotBlank(jspVO.getName())) {
            queryWrapper.lambda().eq(CalenderEntity::getName, jspVO.getName());
        }
        return this.list(queryWrapper);
    }

    /**
     * 获取某年某个日历名称下的日历设置
     *
     * @param jspVO
     * @return
     */
    @Override
    public List<JSONObject> getCalender(CalenderJspVO jspVO) {
        List<CalenderEntity> list = getCalenderByYearAndName(jspVO);
        List<JSONObject> result = new ArrayList<>();
        list.forEach(s -> {
            JSONObject json = new JSONObject();
            json.put("date", s.getDate());
            json.put("className", s.getMark());
            result.add(json);
        });
        return result;
    }

    /**
     * 设置某个名称下的日历
     *
     * @param jspVO
     * @return
     */
    @Override
    public ActionResult saveCalender(CalenderJspVO jspVO) {
        //前端要修改的数据
        List<JSONObject> list = jspVO.getList();
        Integer type = jspVO.getType();

        //查询原来的数据
        List<CalenderEntity> entities = getCalenderByYearAndName(jspVO);
        Map<String, CalenderEntity> map = entities.stream().collect(Collectors.toMap(CalenderEntity::getDate, a -> a));

        //处理数据
        List<CalenderEntity> update = new ArrayList<>();
        List<CalenderEntity> add = new ArrayList<>();
        List<String> delete = new ArrayList<>();
        list.forEach(s -> {
            String date = s.getString("date");
            int markNew = s.getInteger("className");
            if (map.containsKey(date)) {
                int mark = map.get(date).getMark();
                if (markNew == type && mark != type && type != 0) {
                    //更新
                    CalenderEntity old = map.get(date);
                    CalenderEntity entity = CalenderEntity.builder()
                            .id(old.getId()).date(old.getDate()).name(old.getName()).mark(3)
                            .build();
                    update.add(entity);
                }
            } else {
                //添加
                CalenderEntity entity = CalenderEntity.builder()
                        .id(RandomUtil.uuId()).date(date).name(jspVO.getName()).mark(type)
                        .creatortime(jspVO.getCreatortime())
                        .creatoruserid(jspVO.getCreatoruserid()).build();
                add.add(entity);
            }
        });
        //过滤出要删除的
        Map<String, JSONObject> mapList = list.stream().collect(Collectors.toMap(json -> json.getString("date"), a -> a));
        entities.forEach(s -> {
            if (!mapList.containsKey(s.getDate())) {
                delete.add(s.getId());
            }
        });
//        this.saveBatch(add);
//        this.updateBatchById(update);
        add.addAll(update);
        this.saveOrUpdateBatch(add);
        this.removeByIds(delete);

        return ActionResult.success("保存成功!");
    }

}

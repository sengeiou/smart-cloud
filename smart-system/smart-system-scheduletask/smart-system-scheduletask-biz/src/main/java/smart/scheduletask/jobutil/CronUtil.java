package smart.scheduletask.jobutil;

import smart.base.UserInfo;
import smart.base.entity.DbLinkEntity;
import smart.util.data.DataSourceContextHolder;
import smart.emnus.TimetaskTypes;
import smart.scheduletask.entity.TimeTaskEntity;
import smart.scheduletask.entity.TimeTaskLogEntity;
import smart.scheduletask.model.ContentModel;
import smart.scheduletask.model.FrequencyModel;
import smart.scheduletask.service.TimeTaskLogService;
import smart.scheduletask.service.TimetaskService;
import smart.util.*;
import smart.util.context.SpringContext;
import smart.util.type.MethodType;
import smart.util.type.RequestType;
import smart.util.type.StringNumber;
import smart.util.wxutil.HttpUtil;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.*;

/**
 * cron工具类
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/12 15:31
 */
@Slf4j
public class CronUtil {

    /**
     * 设置cron表达式
     *
     * @param model
     * @param cycle
     * @return
     */
    public static String getOneCron(ContentModel model, String cycle) {
        String cron = null;
        if (StringNumber.TWO.equals(model.getFrequency())) {
            StringBuilder builder = new StringBuilder();
            StringBuilder second = new StringBuilder();
            StringBuilder minute = new StringBuilder();
            StringBuilder hours = new StringBuilder();
            StringBuilder day = new StringBuilder();
            StringBuilder mounth = new StringBuilder();
            StringBuilder week = new StringBuilder();
            second.append(StringUtil.isNotEmpty(model.getSeconds()) ? model.getSeconds() : "*");
            hours.append(StringUtil.isNotEmpty(model.getHours()) ? model.getHours() : "*");
            minute.append(StringUtil.isNotEmpty(model.getMinute()) ? model.getMinute() : "*");
            mounth.append(StringUtil.isNotEmpty(model.getMonth()) ? model.getMonth() : "*");
            if (StringNumber.ONE.equals(model.getType())) {
                day.append(StringUtil.isNotEmpty(model.getDay()) ? model.getDay() : "*");
                week.append("?");
            } else if (StringNumber.TWO.equals(model.getType())) {
                day.append("?");
                week.append(StringUtil.isNotEmpty(model.getWeek()) ? model.getWeek() : "*");
            }
            builder.append(second + " " + minute + " " + hours + " " + day + " " + mounth + " " + week);
            cron = builder.toString();
        } else if (StringNumber.FOUR.equals(model.getFrequency())) {
            cron = cycle;
        }
        return cron;
    }

    /**
     * 获取多个表达式
     *
     * @param model
     * @return
     */
    public static List<String> getMoreCron(ContentModel model) {
        List<String> cron = new ArrayList<>();
        if (StringNumber.THREE.equals(model.getFrequency())) {
            for (FrequencyModel frequency : model.getFrequencyList()) {
                StringBuilder builder = new StringBuilder();
                StringBuilder second = new StringBuilder("*");
                StringBuilder hours = new StringBuilder();
                StringBuilder minute = new StringBuilder();
                StringBuilder week = new StringBuilder();
                StringBuilder day = new StringBuilder();
                StringBuilder mounth = new StringBuilder();
                if (StringNumber.ONE.equals(frequency.getType())) {
                    day.append("*");
                    week.append("?");
                } else if (StringNumber.TWO.equals(frequency.getType())) {
                    day.append("?");
                    week.append(frequency.getWeek());
                } else if ("3".equals(frequency.getType())) {
                    day.append(frequency.getDay());
                    week.append("?");
                }
                hours.append(frequency.getHours());
                minute.append(frequency.getMinute());
                mounth.append(frequency.getMonth());
                builder.append(second + " " + minute + " " + hours + " " + day + " " + mounth + " " + week);
                cron.add(builder.toString());
            }
        }
        return cron;
    }

    /**
     * 存储过程
     *
     * @param model
     * @param entity
     * @param dataSourceUtil
     * @param dbName
     */
    public static void storage(ContentModel model, TimeTaskEntity entity, DataSourceUtil dataSourceUtil, String dbName) {
        TimeTaskLogEntity baskLog = new TimeTaskLogEntity();
        baskLog.setId(RandomUtil.uuId());
        baskLog.setTaskId(entity.getId());
        baskLog.setRunTime(DateUtil.getNowDate());
        try {
            System.out.println("存储过程");
            StringBuilder sql = new StringBuilder();
            sql.append("{call " + model.getStored() + "(");
            int parameter = model.getStoredParameter().size();
            for (int i = 0; i < parameter; i++) {
                sql.append("?,");
            }
            if (parameter > 0) {
                sql.deleteCharAt(sql.length() - 1);
            }
            sql.append(")}");
            @Cleanup Connection con = null;
            if (StringNumber.ZERO.equals(model.getDatabase())) {
                con = JdbcUtil.getConn(model.getUserName(), model.getPassword(), model.getUrl());
            } else {
                DbLinkEntity link = model.getLink();
                con = JdbcUtil.getConn(link.getDbType().toLowerCase(), link.getUserName(), link.getPassword(), link.getHost(), link.getPort(), link.getServiceName());
            }
            CallableStatement callStmt = null;
            boolean restul = false;
            if (con != null) {
                callStmt = con.prepareCall(sql.toString());
                if (callStmt != null) {
                    for (int i = 0; i < model.getStoredParameter().size(); i++) {
                        Map<String, Object> paramter = model.getStoredParameter().get(i);
                        String value = String.valueOf(paramter.get("value"));
                        callStmt.setString(i + 1, value);
                    }
                    restul = callStmt.execute();
                }
            }
            if (restul) {
                baskLog.setRunResult(0);
                baskLog.setDescription("存储过程调用成功");
            } else {
                baskLog.setRunResult(1);
                baskLog.setDescription("存储过程调用失败");
            }
        } catch (Exception e) {
            baskLog.setRunResult(1);
            baskLog.setDescription("存储过程调用失败");
        }
        //切换数据源
        if (StringUtil.isNotEmpty(dbName)) {
            UserInfo userInfo = SpringContext.getBean(UserInfo.class);
            DataSourceContextHolder.setDatasource(userInfo.getTenantId(), userInfo.getTenantDbConnectionString());
        }
        //插入日志
        TimeTaskLogService taskLogService = SpringContext.getBean(TimeTaskLogService.class);
        List<TimeTaskLogEntity> taskList = taskLogService.getTaskList(entity.getId());
        taskLogService.save(baskLog);
        //更新次数
        TimetaskService timetaskService = SpringContext.getBean(TimetaskService.class);
        TimeTaskEntity info = timetaskService.getInfo(entity.getId());
        info.setRunCount(taskList.size() + 1);
        info.setLastModifyTime(new Date());
        if (!StringNumber.ONE.equals(model.getFrequency())) {
            if (StringNumber.ONE.equals(model.getEnd())) {
                info.setNextRunTime(DateUtil.getNextCronDate(entity.getExecuteCycleJson(), null));
            } else {
                long endTime = DateUtil.stringToDate(model.getEndTime()).getTime();
                long nowTime = System.currentTimeMillis();
                if (endTime > nowTime) {
                    info.setNextRunTime(DateUtil.getNextCronDate(entity.getExecuteCycleJson(), null));
                }
            }
        }
        timetaskService.updateById(info);
    }

    /**
     * 接口
     *
     * @param model
     * @param entity
     * @param dataSourceUtil
     * @param dbName
     */
    public static void connector(ContentModel model, TimeTaskEntity entity, DataSourceUtil dataSourceUtil, String dbName) {
        TimeTaskLogEntity baskLog = new TimeTaskLogEntity();
        baskLog.setId(RandomUtil.uuId());
        baskLog.setTaskId(entity.getId());
        baskLog.setRunTime(DateUtil.getNowDate());
        try {
            String json = null;
            if (model.getParameter() != null) {
                if (model.getParameter().size() != 0) {
                    json = JsonUtil.getObjectToString(model.getParameter());
                }
            }
            String faceurl = model.getInterfaceUrl();
            if (MethodType.GET.equals(model.getInterfaceType().toUpperCase())) {
                faceurl = getUrl(model.getInterfaceUrl(), model.getParameter());
                json = null;
            }
            boolean falg = false;
            if (model.getInterfaceUrl().contains(RequestType.HTTP)) {
                falg = HttpUtil.httpCronRequest(faceurl, model.getInterfaceType(), json);
            } else if (model.getInterfaceUrl().contains(RequestType.HTTPS)) {
                falg = HttpUtil.httpsCronRequest(faceurl, model.getInterfaceType(), json);
            }
            if (falg) {
                baskLog.setRunResult(0);
            } else {
                baskLog.setRunResult(1);
                baskLog.setDescription("无接口");
            }
        } catch (Exception e) {
            log.error("定时任务的接口错误:{}", e.getMessage());
            baskLog.setRunResult(1);
            baskLog.setDescription("无接口");
        }
        //切换数据源
        if (StringUtil.isNotEmpty(dbName)) {
            UserInfo userInfo = SpringContext.getBean(UserInfo.class);
            DataSourceContextHolder.setDatasource(userInfo.getTenantId(), userInfo.getTenantDbConnectionString());
        }
        //插入日志
        TimeTaskLogService taskLogService = SpringContext.getBean(TimeTaskLogService.class);
        List<TimeTaskLogEntity> taskList = taskLogService.getTaskList(entity.getId());
        taskLogService.save(baskLog);
        //更新次数
        TimetaskService timetaskService = SpringContext.getBean(TimetaskService.class);
        TimeTaskEntity info = timetaskService.getInfo(entity.getId());
        info.setLastModifyTime(new Date());
        info.setRunCount(taskList.size() + 1);
        if (!StringNumber.ONE.equals(model.getFrequency())) {
            if (StringNumber.ONE.equals(model.getEnd())) {
                info.setNextRunTime(DateUtil.getNextCronDate(entity.getExecuteCycleJson(), null));
            }else{
                long endTime = DateUtil.stringToDate(model.getEndTime()).getTime();
                long nowTime = System.currentTimeMillis();
                if (endTime > nowTime) {
                    info.setNextRunTime(DateUtil.getNextCronDate(entity.getExecuteCycleJson(), null));
                }
            }
        }
        timetaskService.updateById(info);
    }

    /**
     * 定时任务赋值
     *
     * @param model
     * @param entity
     * @param type
     */
    public static void task(ContentModel model, TimeTaskEntity entity, int type) {
        //执行效率
        if (StringNumber.ONE.equals(model.getFrequency()) || StringNumber.TWO.equals(model.getFrequency())) {
            String cycle = CronUtil.getOneCron(model, entity.getExecuteCycleJson());
            if (cycle != null) {
                entity.setExecuteCycleJson(cycle);
            } else {
                entity.setExecuteCycleJson(DateUtil.getDateToCron(null));
            }
        } else if (StringNumber.THREE.equals(model.getFrequency())) {
            List<String> listCron = CronUtil.getMoreCron(model);
            StringBuilder builder = new StringBuilder();
            for (String cron : listCron) {
                builder.append(cron + ";");
            }
            entity.setExecuteCycleJson(builder.toString());
        } else if (StringNumber.FOUR.equals(model.getFrequency())) {
            entity.setExecuteCycleJson(model.getCron());
        }
        //赋值次数0.新增 1.修改
        if (type == 0) {
            if (StringNumber.ONE.equals(model.getStart()) && StringNumber.ONE.equals(model.getFrequency())) {
                entity.setRunCount(1);
                entity.setLastRunTime(DateUtil.getNowDate());
            } else {
                entity.setRunCount(0);
            }
        } else {
            if (StringNumber.ONE.equals(String.valueOf(entity.getEnabledMark()))) {
                if (StringNumber.ONE.equals(model.getStart()) && StringNumber.ONE.equals(model.getFrequency())) {
                    entity.setRunCount(entity.getRunCount() + 1);
                    entity.setLastRunTime(DateUtil.getNowDate());
                }
            }
        }
    }

    /**
     * 启动定时
     *
     * @param entity
     * @param model
     * @param dbName
     * @param tenant
     * @param dataSourceUtil
     * @param jobManager
     */
    public static void startJob(TimeTaskEntity entity, ContentModel model, String dbName, boolean tenant, DataSourceUtil dataSourceUtil, JobManager jobManager) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("data", entity);
        map.put("content", model);
        if (tenant) {
            map.put("dbName", dbName);
        }
        if (StringNumber.ONE.equals(String.valueOf(entity.getEnabledMark()))) {
            if (StringNumber.ONE.equals(model.getStart())) {
                map.put("type", 1);
                JobDataMap dataMap = new JobDataMap(map);
                //1.判断执行次数
                if (String.valueOf(TimetaskTypes.One.getCode()).equals(model.getFrequency())) {
                    if (StringNumber.TWO.equals(entity.getExecuteType())) {
                        String name = tenant ? dbName : null;
                        CronUtil.storage(model, entity, dataSourceUtil, name);
                    } else if (StringNumber.ONE.equals(entity.getExecuteType())) {
                        String name = tenant ? dbName : null;
                        CronUtil.connector(model, entity, dataSourceUtil, name);
                    }
                } else if (String.valueOf(TimetaskTypes.Two.getCode()).equals(model.getFrequency())) {
                    jobManager.addJob(entity.getFullName(), dbName, entity.getExecuteCycleJson(), dataMap);
                } else if (String.valueOf(TimetaskTypes.Three.getCode()).equals(model.getFrequency())) {
                    String[] cycle = entity.getExecuteCycleJson().split(";");
                    for (int i = 0; i < cycle.length; i++) {
                        String cron = cycle[i];
                        jobManager.addJob(entity.getFullName(), dbName + "_" + i, cron, dataMap);
                    }
                } else if (String.valueOf(TimetaskTypes.Four.getCode()).equals(model.getFrequency())) {
                    jobManager.addJob(entity.getFullName(), dbName, entity.getExecuteCycleJson(), dataMap);
                }
            } else if (StringNumber.TWO.equals(model.getStart())) {
                map.put("type", 2);
                JobDataMap dataMap = new JobDataMap(map);
                String startDate = DateUtil.getDateToCron(DateUtil.stringToDate(model.getStartTime()));
                jobManager.addJob(entity.getFullName(), dbName + "_start", startDate, dataMap);
            }
            if (StringNumber.TWO.equals(model.getEnd())) {
                map.put("type", 3);
                JobDataMap dataMap = new JobDataMap(map);
                String endDate = DateUtil.getDateToCron(DateUtil.stringToDate(model.getEndTime()));
                jobManager.addJob(entity.getFullName(), dbName + "_end", endDate, dataMap);
            }
        }
    }

    /**
     * 数据库连接赋值
     *
     * @param entity
     * @param model
     * @param dbName
     * @param link
     * @param dataSourceUtil
     */
    public static void database(TimeTaskEntity entity, ContentModel model, String dbName, DbLinkEntity link, DataSourceUtil dataSourceUtil) {
        if (StringNumber.TWO.equals(entity.getExecuteType())) {
            if (!StringNumber.ZERO.equals(model.getDatabase())) {
                if (link != null) {
                    model.setLink(link);
                }
            } else {
                if (StringUtil.isEmpty(dbName)) {
                    dbName = dataSourceUtil.getDbName();
                }
                String url = dataSourceUtil.getUrl().replace("{dbName}", dbName);
                model.setUrl(url);
                model.setUserName(dataSourceUtil.getUserName());
                model.setPassword(dataSourceUtil.getPassword());
            }
        }
    }

    /**
     * get的参数拼接到url
     *
     * @param url
     * @param params
     * @return
     */
    private static String getUrl(String url, List<Map<String, Object>> params) {
        StringBuilder urlStringBuilder = new StringBuilder(url);
        try {
            if (params.size() > 0) {
                urlStringBuilder.append("?");
                for (Map<String, Object> param : params) {
                    urlStringBuilder.append(param.get("key")).append("=").append(param.get("value")).append("&");
                }
                String substring = urlStringBuilder.substring(0, url.length() - 1);
                return substring;
            }
        } catch (Exception e) {
            log.error("url错误{}", e.getMessage());
        }
        return url;
    }

    /**
     * 删除定时任务，删除下次时间
     *
     * @param dataSourceUtil
     * @param dbName
     * @param id
     */
    public static void removeNextTime(DataSourceUtil dataSourceUtil, String dbName, String id) {
        //切换数据源
        if (StringUtil.isNotEmpty(dbName)) {
            UserInfo userInfo = SpringContext.getBean(UserInfo.class);
            DataSourceContextHolder.setDatasource(userInfo.getTenantId(), userInfo.getTenantDbConnectionString());
        }
        TimetaskService time = SpringContext.getBean(TimetaskService.class);
        TimeTaskEntity info = time.getInfo(id);
        info.setNextRunTime(null);
        time.updateById(info);
    }

}

package smart.scheduletask.jobutil;

import smart.scheduletask.entity.TimeTaskEntity;
import smart.scheduletask.model.ContentModel;
import smart.util.DataSourceUtil;
import smart.util.context.SpringContext;
import smart.util.type.IntegerNumber;
import smart.util.type.StringNumber;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

/**
 * 执行任务
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/12 15:31
 */
@Slf4j
public class MyJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        TimeTaskEntity entity = dataMap.get("data") != null ? (TimeTaskEntity) dataMap.get("data") : null;
        ContentModel model = dataMap.get("content") != null ? (ContentModel) dataMap.get("content") : null;
        //1.执行一次 2.开始时间执行任务 3.结束任务
        Integer type = dataMap.getIntValue("type");
        //数据库
        String dbName = dataMap.getString("dbName");
        DataSourceUtil dataSourceUtil = SpringContext.getBean(DataSourceUtil.class);
        JobManager jobManager = new JobManager();
        if (type == 1) {
            if (entity != null) {
                if (StringNumber.TWO.equals(entity.getExecuteType())) {
                    CronUtil.storage(model, entity, dataSourceUtil, dbName);
                } else if (StringNumber.ONE.equals(entity.getExecuteType())) {
                    CronUtil.connector(model, entity, dataSourceUtil, dbName);
                }
            }
        } else if (type == IntegerNumber.TWO) {
            if (StringNumber.ONE.equals(model.getFrequency())) {
                jobManager.removeJob(entity.getFullName(), dbName);
                if (StringNumber.TWO.equals(entity.getExecuteType())) {
                    CronUtil.storage(model, entity, dataSourceUtil, dbName);
                } else if (StringNumber.ONE.equals(entity.getExecuteType())) {
                    CronUtil.connector(model, entity, dataSourceUtil, dbName);
                }
            } else {
                dataMap.put("type", 1);
                if (!StringNumber.THREE.equals(model.getFrequency())) {
                    jobManager.addJob(entity.getFullName(), dbName, CronUtil.getOneCron(model, entity.getExecuteCycleJson()), dataMap);
                } else if (StringNumber.THREE.equals(model.getFrequency())) {
                    String[] cycle = entity.getExecuteCycleJson().split(";");
                    for (int i = 0; i < cycle.length; i++) {
                        String cron = cycle[i];
                        jobManager.addJob(entity.getFullName(), dbName + "_" + i, cron, dataMap);
                    }
                }
            }
        } else if (type == IntegerNumber.THREE) {
            if (!StringNumber.THREE.equals(model.getFrequency())) {
                jobManager.removeJob(entity.getFullName(), dbName);
            } else if (StringNumber.THREE.equals(model.getFrequency())) {
                String[] cycle = entity.getExecuteCycleJson().split(";");
                for (int i = 0; i < cycle.length; i++) {
                    jobManager.removeJob(entity.getFullName(), dbName + "_" + i);
                }
            }
            CronUtil.removeNextTime(dataSourceUtil, dbName, entity.getId());
        }
    }
}

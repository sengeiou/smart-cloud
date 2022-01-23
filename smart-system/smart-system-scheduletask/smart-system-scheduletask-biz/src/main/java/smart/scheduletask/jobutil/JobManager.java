package smart.scheduletask.jobutil;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

/**
 * 任务管理
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/12 15:31
 */
@Slf4j
@Component
public class JobManager {

    private SchedulerFactory schedulerFactory = new StdSchedulerFactory();

    /**
     * 添加任务
     *
     * @param jobName      计划名称
     * @param jobGroupName 租户名称
     * @param cron         时间
     * @param jobDataMap   其他参数
     */
    public void addJob(String jobName, String jobGroupName, String cron, JobDataMap jobDataMap) {
        this.addJob(jobName, jobGroupName, "tri_" + jobName, "tri_" + jobGroupName, MyJob.class, cron, jobDataMap);
    }

    /**
     * 移除一个任务
     *
     * @param jobName
     * @param jobGroupName 租户名称
     */
    public void removeJob(String jobName, String jobGroupName) {
        this.removeJob(jobName, jobGroupName, "tri_" + jobName, "tri_" + jobGroupName);
    }

    /**
     * 修改一个任务的触发时间
     *
     * @param jobName
     * @param jobGroupName 租户名称
     * @param cron         时间设置，参考quartz说明文档
     */
    public void updateJob(String jobName, String jobGroupName, String cron, JobDataMap jobDataMap) {
        this.updateJob(jobName, jobGroupName, "tri_" + jobName, "tri_" + jobGroupName, cron, jobDataMap);
    }

    /**
     * 添加一个定时任务
     *
     * @param jobName          任务名
     * @param jobGroupName     任务组名
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param jobClass         任务
     * @param cron             时间设置，参考quartz说明文档
     */
    private void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class jobClass, String cron, JobDataMap jobDataMap) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            // 任务名，任务组，任务执行类
            JobDetail jobDetail = null;
            if (jobDataMap != null) {
                jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).setJobData(jobDataMap).build();
            } else {
                jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
            }
            // 触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组
            triggerBuilder.withIdentity(triggerName, triggerGroupName);
            triggerBuilder.startNow();
            // 触发器时间设定
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            // 调度容器设置JobDetail和Trigger
            sched.scheduleJob(jobDetail, trigger);
            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            removeJob(jobName,jobGroupName);
            addJob(jobName,jobGroupName,cron,jobDataMap);
            log.error("添加定时任务失败:{}", e.getMessage());
        }
    }

    /**
     * 移除一个任务
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     */
    private void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            // 停止触发器
            sched.pauseTrigger(triggerKey);
            // 移除触发器
            sched.unscheduleJob(triggerKey);
            // 删除任务
            sched.deleteJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (Exception e) {
            log.error("移除定时任务失败:{}", e.getMessage());
        }
    }

    /**
     * 修改一个任务的触发时间
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param cron             时间设置，参考quartz说明文档
     */
    private void updateJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, String cron, JobDataMap jobDataMap) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                /** 方式一 ：调用 rescheduleJob 开始 */
                // 触发器
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                // 触发器名,触发器组
                if(jobDataMap !=null) {
                    triggerBuilder.withIdentity(triggerName, triggerGroupName).usingJobData(jobDataMap);
                }else {
                    triggerBuilder.withIdentity(triggerName, triggerGroupName);
                }
                triggerBuilder.startNow();
                // 触发器时间设定
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
                // 创建Trigger对象
                trigger = (CronTrigger) triggerBuilder.build();
                // 方式一 ：修改一个任务的触发时间
                sched.rescheduleJob(triggerKey, trigger);
                /** 方式一 ：调用 rescheduleJob 结束 */

                /** 方式二：先删除，然后在创建一个新的Job  */
                //JobDetail jobDetail = sched.getJobDetail(JobKey.jobKey(jobName, jobGroupName));
                //Class<? extends Job> jobClass = jobDetail.getJobClass();
                //removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
                //addJob(jobName, jobGroupName, triggerName, triggerGroupName, jobClass, cron);
                /** 方式二 ：先删除，然后在创建一个新的Job */
            }
        } catch (Exception e) {
            addJob(jobName,jobGroupName,cron,jobDataMap);
            log.error("修改定时任务失败:{}", e.getMessage());
        }
    }

    /**
     * 启动所有定时任务
     */
    public void startJobs() {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭所有定时任务
     */
    public void shutdownJobs() {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

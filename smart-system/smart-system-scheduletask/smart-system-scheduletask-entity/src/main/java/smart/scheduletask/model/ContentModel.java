package smart.scheduletask.model;

import smart.base.entity.DbLinkEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ContentModel {
    //任务名称（旧的）
    private String name;
    //开始时间 1.立即执行 2.设置开始时间
    private String start;
    //执行频率 1.执行一次 2.重复执行 3.调度明细 4.调度任务
    private String frequency;
    //结束 1.无限制 2.设定结束时间
    private String end;
    //开始时间
    private String startTime;
    //秒
    private String seconds;
    //分
    private String minute;
    //小时
    private String hours;
    //天
    private String day;
    //选择 1.月 2.周
    private String type;
    //月
    private String month;
    //周
    private String week;
    //结束时间
    private String endTime;
    //任务重启
    private boolean restart;
    //间隔时间
    private String restartFrequency;
    //重启次数
    private String restartTime;
    //频率明细
    private List<FrequencyModel> frequencyList;
    //表达式设置
    private String cron;

    //请求类型
    private String interfaceType;
    //请求路径
    private String interfaceUrl;
    //请求参数
    private List<Map<String, Object>> parameter;
    //数据库
    private String database;
    //数据库连接
    private DbLinkEntity link;
    //数据库密码
    private String password;
    //数据库账号
    private String userName;
    //数据库url
    private String url;

    //存储名称
    private String stored;
    //存储参数
    private List<Map<String, Object>> storedParameter;

}

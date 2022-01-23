package smart.scheduletask.model;

import lombok.Data;

@Data
public class FrequencyModel {
    //执行日 1.每日 2.每周 3.每月
    private String type;
    //小时 1,2,3
    private String hours;
    //分 1,2,3
    private String minute;
    //周 2
    private String week;
    //天 3
    private String day;
    //执行月
    private String month;
}

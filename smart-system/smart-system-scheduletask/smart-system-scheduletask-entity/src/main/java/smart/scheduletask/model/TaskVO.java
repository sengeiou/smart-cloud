package smart.scheduletask.model;

import lombok.Data;

@Data
public class TaskVO {
    private String fullName;
    private String enCode;
    private String runCount;
    private long lastRunTime;
    private long nextRunTime;
    private String description;
    private String id;
    private Integer enabledMark;
    private long sortCode;
}

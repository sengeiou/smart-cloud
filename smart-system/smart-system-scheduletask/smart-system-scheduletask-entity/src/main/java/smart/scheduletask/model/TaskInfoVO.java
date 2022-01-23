package smart.scheduletask.model;

import lombok.Data;

@Data
public class TaskInfoVO {
    private String id;
    private String fullName;
    private String executeType;
    private String description;
    private String executeContent;
    private long sortCode;
    private String enCode;
}

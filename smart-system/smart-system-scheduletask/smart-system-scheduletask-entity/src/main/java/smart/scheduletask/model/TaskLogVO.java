package smart.scheduletask.model;

import lombok.Data;

@Data
public class TaskLogVO {
   private String description;

  private String id;

  private Integer runResult;

   private long runTime;
}

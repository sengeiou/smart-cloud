package smart.portal.model;

import lombok.Data;

@Data
public class MyFlowTodoVO {
    private String id;
    private Integer enabledMark;
    private Long startTime;
    private Long endTime;
    private String content;
}

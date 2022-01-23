package smart.permission.model.authorize;

import lombok.Data;

@Data
public class SaveBatchForm {
    private String[] roleIds;
    private String[] positionIds;
    private String[] userIds;
    private String[] module;
    private String[] button;
    private String[] column;
    private String[] resource;
}

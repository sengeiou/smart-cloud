package smart.base.model;

import lombok.Data;

@Data
public class VisualDevListVO {
    private String id;
    private String fullName;
    private String enCode;
    private Integer state;

    private String type;
    private String tables;
    private String description;
    private long creatorTime;
    private String creatorUser;
    private String category;
    private long lastmodifytime;
    private String lastmodifyuser;
}

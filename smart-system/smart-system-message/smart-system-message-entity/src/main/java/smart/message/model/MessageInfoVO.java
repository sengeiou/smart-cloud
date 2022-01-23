package smart.message.model;

import lombok.Data;

@Data
public class MessageInfoVO {
    private String id;
    private String title;
    private Integer type;
    private long lastModifyTime;
    private String creatorUser;
    private Integer isRead;
}

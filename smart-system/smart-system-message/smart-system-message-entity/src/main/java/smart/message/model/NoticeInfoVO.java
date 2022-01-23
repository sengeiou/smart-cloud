package smart.message.model;

import lombok.Data;

@Data
public class NoticeInfoVO {
    private String id;
    private String title;
    private String bodyText;
    private String creatorUser;
    private long creatorTime;
}

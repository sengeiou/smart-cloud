package smart.model.worklog;

import lombok.Data;

@Data
public class WorkLogInfoVO {
    private String id;
    private String title;
    private String question;
    private long creatorTime;
    private String todayContent;
    private String tomorrowContent;
    private String toUserId;
    private String userIds;
}

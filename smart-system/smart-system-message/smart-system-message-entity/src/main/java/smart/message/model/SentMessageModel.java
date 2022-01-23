package smart.message.model;

import lombok.Data;

import java.util.List;

@Data
public class SentMessageModel {
    private List<String> toUserIds;
    private String title;
    private String bodyText;
}

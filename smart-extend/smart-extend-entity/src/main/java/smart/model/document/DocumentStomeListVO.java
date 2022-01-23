package smart.model.document;

import lombok.Data;

@Data
public class DocumentStomeListVO {
    private long shareTime;
    private String fileExtension;
    private String fileSize;
    private String fullName;
    private String id;
    private String creatorUserId;
}

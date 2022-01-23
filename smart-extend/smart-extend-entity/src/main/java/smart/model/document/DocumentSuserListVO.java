package smart.model.document;

import lombok.Data;

import java.util.Date;

@Data
public class DocumentSuserListVO {
    private String id;
    private String documentId;
    private String shareUserId;
    private Date shareTime;
}

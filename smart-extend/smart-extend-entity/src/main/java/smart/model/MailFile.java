package smart.model;

import lombok.Data;

@Data
public class MailFile {
    private String fileId;
    private String fileName;
    private String fileSize;
    private String fileTime;
    private String fileState;
}

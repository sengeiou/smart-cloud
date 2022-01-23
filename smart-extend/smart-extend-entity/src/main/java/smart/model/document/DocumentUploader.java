package smart.model.document;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentUploader {
    private String parentId;
    private MultipartFile file;
}

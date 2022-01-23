package smart.base.model;

import lombok.Data;

@Data
public class VisualDevCrForm {
    private String fullName;
    private String enCode;
    private String type;
    private String description;
    private String formData;
    private String columnData;
    private String tables;
    private String category;
    private Integer state=0;
}

package smart.model.mpmenu;

import lombok.Data;

@Data
public class MPMenuForm {
    private String type;
    private String parentId;
    private String fullName;
    private String url;
    private String content;
}

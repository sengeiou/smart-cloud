package smart.base.model;

import lombok.Data;

import java.util.List;

@Data
public class VisualdevTreeVO {
    private String id;
    private String fullName;
    private Boolean hasChildren;
    private List<VisualdevTreeChildModel> children;
}

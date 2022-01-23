package smart.base.model.button;

import lombok.Data;

import java.util.List;

@Data
public class ButtonTreeListSelectVO {
    private String id;
    private String parentId;
    private String fullName;
    private String icon;
    private List<ButtonTreeListModel> children;
}

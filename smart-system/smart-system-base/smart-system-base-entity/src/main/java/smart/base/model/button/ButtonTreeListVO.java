package smart.base.model.button;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ButtonTreeListVO {
    private Long sortCode;
    private String id;
    private String parentId;
    private String fullName;
    private String icon;
    private String enCode;
    private Integer enabledMark;
    private Boolean hasChildren;
    private List<ButtonTreeListVO> children;
}

package smart.base.model.dictionarytype;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DictionaryTypeSelectVO {
    private String id;
    private String parentId;
    private Boolean hasChildren;
    private List<DictionaryTypeSelectVO> children = new ArrayList<>();
    private String fullName;
    private String enCode;
}

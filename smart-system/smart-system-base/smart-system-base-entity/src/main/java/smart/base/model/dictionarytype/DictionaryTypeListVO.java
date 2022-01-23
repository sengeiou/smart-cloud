package smart.base.model.dictionarytype;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DictionaryTypeListVO {
    private String id;
    private String parentId;
    private Boolean hasChildren;
    private Integer isTree;
    private List<DictionaryTypeListVO> children = new ArrayList<>();
    private String fullName;
    private String enCode;
    private long sortCode;
}

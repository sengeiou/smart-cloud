package smart.base.model.dictionarytype;

import lombok.Data;

@Data
public class DictionaryTypeInfoVO {
    private String id;
    private String parentId;
    private String fullName;
    private String enCode;
    private Integer isTree;
    private String description;
    private long sortCode;
}

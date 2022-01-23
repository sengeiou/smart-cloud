package smart.base.model.dictionarydata;

import lombok.Data;

@Data
public class DictionaryDataInfoVO {
    private String id;
    private String parentId;
    private String description;
    private String fullName;
    private String enCode;
    private Integer enabledMark;
    private String dictionaryTypeId;
    private long sortCode;
}

package smart.base.model.dictionarydata;

import lombok.Data;

import java.util.List;

@Data
public class DictionaryDataListTreeVO {
    private String id;
    private String parentId;
    private Boolean hasChildren;
    private List<DictionaryDataListTreeVO> children;
    private String fullName;
    private String enCode;
    private Integer enabledMark;
    private long sortCode;
}

package smart.base.model.dictionarydata;

import lombok.Data;

import java.util.List;


@Data
public class DictionaryDataListVO {
    private String id;
    private String fullName;
    private String enCode;
    private Integer enabledMark;
    private Boolean hasChildren;
    private String parentId;
    private List<DictionaryDataListVO> children;
    private long sortCode;

}

package smart.base.model.province;

import lombok.Data;

@Data
public class ProvinceListVO {
    private String id;

    private String fullName;

    private String enCode;

    private Integer enabledMark;

    private Boolean isLeaf;
    private Boolean hasChildren;
    private long sortCode;
}

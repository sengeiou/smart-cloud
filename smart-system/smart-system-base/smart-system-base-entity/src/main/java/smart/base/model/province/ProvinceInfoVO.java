package smart.base.model.province;

import lombok.Data;

@Data
public class ProvinceInfoVO {

    private String id;

    private String fullName;

    private String enCode;

    private Integer enabledMark;

    private String description;

    private String parentId;
    private String parentName;
    private long sortCode;
}

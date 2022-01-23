package smart.base.model.province;

import lombok.Data;

@Data
public class ProvinceSelectListVO {
    private String id;

    private String fullName;

//    private String icon;

    private Boolean isLeaf;
}

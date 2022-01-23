package smart.permission.model.position;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class PositionListVO {
    private String id;
    private String fullName;
    private String enCode;
    private String type;
    private Long creatorTime;
    private String description;
    @JSONField(name = "organizeId")
    private String department;
    private Integer enabledMark;
    @ApiModelProperty(value = "排序")
    private Long sortCode;
}

package smart.base.model.dblink;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DbLinkListVO {
    @ApiModelProperty(value = "连接名称")
    private String fullName;
    @ApiModelProperty(value = "连接驱动")
    private String dbType;
    @ApiModelProperty(value = "主机名称")
    private String host;
    @ApiModelProperty(value = "端口")
    private String port;
    @ApiModelProperty(value = "创建时间",example = "1")
    private long creatorTime;
    @ApiModelProperty(value = "创建人")
    @JSONField(name = "creatorUserId")
    private String creatorUser;
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "修改时间")
    private long lastModifyTime;
    @ApiModelProperty(value = "修改用户")
    @JSONField(name = "lastModifyUserId")
    private String lastModifyUser;
    @ApiModelProperty(value = "有效标志")
    private Integer enabledMark;
    @ApiModelProperty(value = "排序码")
    private long sortCode;


}

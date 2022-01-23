package smart.base.model.dbbackup;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DbBackupListVO {

    @ApiModelProperty(value = "备份主键")
    private String id;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    @ApiModelProperty(value = "创建时间",example = "1")
    private long creatorTime;

    @ApiModelProperty(value = "文件访问地址")
    private String fileUrl;

}

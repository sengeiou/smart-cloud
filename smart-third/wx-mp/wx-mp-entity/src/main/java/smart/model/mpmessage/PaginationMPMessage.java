package smart.model.mpmessage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PaginationMPMessage {
    @ApiModelProperty(value = "每页条数",example = "20")
    private long pageSize=20;
    @ApiModelProperty(value = "排序类型")
    private String sort="desc";
    @ApiModelProperty(value = "排序列")
    private String sidx="";
    @ApiModelProperty(value = "当前页数",example = "1")
    private long currentPage=1;
    private String startTime;
    private String endTime;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private long total;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private long records;

    public <T> List<T> setData(List<T> data, long records) {
        this.total = records;
        return data;
    }
}

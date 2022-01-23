package smart.model;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class ReportManageModel {
    @JSONField(name = "F_Id")
    private String id;
    @JSONField(name = "F_FullName")
    private String fullName;
    @JSONField(name = "F_Category")
    private String category;
    @JSONField(name = "F_UrlAddress")
    private String urlAddress;
}

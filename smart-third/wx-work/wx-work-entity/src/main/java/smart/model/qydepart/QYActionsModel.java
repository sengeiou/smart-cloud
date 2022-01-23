package smart.model.qydepart;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class QYActionsModel {
    private String id;
    private String parentId;
    private String fullName;
    private String managerId;
    private String propertyJson;
    private Integer enabledMark;
    private Date lastModifyTime;
    private String description;
    private String category;
    private String enCode;
    private List<QYActionsModel> children =new ArrayList<>();
}

package smart.permission.model.user;

import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class UserAllModel extends SumTree {
    private String id;
    private String account;
    private String gender;
    private String realName;
    private String headIcon;
    private String department;
    private String departmentId;
    private String organizeId;
    private String organize;
    private String roleId;
    private String roleName;
    private String positionId;
    private String positionName;
    private String managerId;
    private String managerName;
    private String quickQuery;
    private String portalId;
    private Integer isAdministrator;
}

package smart.model.currenuser;

import lombok.Data;

import java.util.List;

@Data
public class PCUserVO {
    private List<MenuTreeVO> menuList;
    private List<PermissionModel> permissionList;
    private UserCommonInfoVO userInfo;

    public PCUserVO() {
    }

    public PCUserVO(List<MenuTreeVO> menuList, List<PermissionModel> permissionList, UserCommonInfoVO userInfo) {
        this.menuList = menuList;
        this.permissionList = permissionList;
        this.userInfo = userInfo;
    }
}

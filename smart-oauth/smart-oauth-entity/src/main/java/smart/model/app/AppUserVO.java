package smart.model.app;

import lombok.Data;

import java.util.List;

@Data
public class AppUserVO {
    private AppInfoModel userInfo;
    private List<AppMenuModel> menuList;
    private List<AppFlowFormModel> flowFormList;
}

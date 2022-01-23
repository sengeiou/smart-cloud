package smart.permission.model.authorize;

import lombok.Data;

import java.util.List;

@Data
public class AuthorizeDataReturnVO {
    List<AuthorizeDataReturnModel> list;
    List<String> ids;
    //all字段里面不包括菜单id
    List<String> all;
}

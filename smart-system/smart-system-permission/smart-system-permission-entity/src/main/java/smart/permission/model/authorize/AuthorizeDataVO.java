package smart.permission.model.authorize;

import lombok.Data;

@Data
public class AuthorizeDataVO {
//    private AuthorizeDataReturnVO menu;
    private AuthorizeDataReturnVO module;
    private AuthorizeDataReturnVO button;
    private AuthorizeDataReturnVO column;
    private AuthorizeDataReturnVO resource;

}

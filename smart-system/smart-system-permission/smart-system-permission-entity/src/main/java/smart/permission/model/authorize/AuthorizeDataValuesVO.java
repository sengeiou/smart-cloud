package smart.permission.model.authorize;

import lombok.Data;

import java.util.List;

@Data
public class AuthorizeDataValuesVO {
    List<AuthorizeDataReturnModel> list;
    List<String> ids;
    List<String> all;
}

package smart.permission.model.authorize;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizeDataReturnModel {
    private String id;
    private String fullName;
    private String icon;
    private String type;
    private Long sortCode;
    private List<AuthorizeDataReturnModel> children;
}

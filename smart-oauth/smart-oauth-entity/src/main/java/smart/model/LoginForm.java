package smart.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginForm {
    public LoginForm(String account, String password, String grant_type, String client_id, String client_secret, String scope) {
        this.account = account;
        this.password = password;
        this.grant_type = grant_type;
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.scope = scope;
    }

    @JSONField(name = "username")
    private String account;
    private String password;
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String scope;
}

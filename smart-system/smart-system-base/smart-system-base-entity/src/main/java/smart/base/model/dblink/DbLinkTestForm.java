package smart.base.model.dblink;


import lombok.Data;

@Data
public class DbLinkTestForm {
    private String dbType;

    private String userName;

    private String serviceName;

    private String password;

    private String port;

    private String host;

}

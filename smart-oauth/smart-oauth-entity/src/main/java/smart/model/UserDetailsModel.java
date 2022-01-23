package smart.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class UserDetailsModel implements UserDetails {

    /**
     * 租户手机号
     */
    private String tenantId;
    /**
     * 租户数据库
     */
    private String dbName;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户名
     */
    private String username;

    public UserDetailsModel(){

    }

    public UserDetailsModel(String tenantId, String dbName, String password, String username) {
        this.tenantId = tenantId;
        this.dbName = dbName;
        this.password = password;
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.tenantId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

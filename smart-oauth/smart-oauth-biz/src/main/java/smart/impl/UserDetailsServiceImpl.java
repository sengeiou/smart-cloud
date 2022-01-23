package smart.impl;

import smart.config.ConfigValueUtil;
import smart.util.data.DataSourceContextHolder;
import smart.model.password.PassContextHolder;
import smart.permission.UsersApi;
import smart.permission.entity.UserEntity;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 验证账号密码
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UsersApi usersApi;
    @Autowired
    private ConfigValueUtil configValueUtil;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = null;
        UserEntity userEntity = null;
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())){
           userEntity = usersApi.checkUser(username, DataSourceContextHolder.getDatasourceId(), DataSourceContextHolder.getDatasourceName());
        }else {
            userEntity = usersApi.checkUser(username, "1", "1");
        }
        Collection<SimpleGrantedAuthority> list = new ArrayList();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("aa");
        list.add(simpleGrantedAuthority);
        user = new User(username, userEntity.getPassword(), list);
        PassContextHolder.setUserName(username);
        return user;
    }

}

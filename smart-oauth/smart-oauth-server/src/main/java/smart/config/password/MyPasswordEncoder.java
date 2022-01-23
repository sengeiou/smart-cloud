package smart.config.password;

import smart.config.ConfigValueUtil;
import smart.util.data.DataSourceContextHolder;
import smart.model.password.PassContextHolder;
import smart.util.Md5Util;
import smart.permission.UsersApi;
import smart.permission.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * MD5加密加盐
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Component
public class MyPasswordEncoder implements PasswordEncoder {
    @Autowired
    private UsersApi usersApi;
    @Autowired
    private ConfigValueUtil configValueUtil;

    @Override
    public String encode(CharSequence charSequence) {
        return Md5Util.getStringMd5((String)charSequence);
    }

    @Override
    public boolean matches(CharSequence rawEncoder, String encoder) {
        //验证账号密码
        if (PassContextHolder.getUserName()!=null) {
            UserEntity infoVo = null;
            //判断是否为多租户
            if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
               infoVo = usersApi.checkUser(PassContextHolder.getUserName(), DataSourceContextHolder.getDatasourceId(),DataSourceContextHolder.getDatasourceName());
            }else {
                infoVo = usersApi.checkUser(PassContextHolder.getUserName(),"1","1");
            }
            PassContextHolder.removeUserName();
            return encoder.equals(Md5Util.getStringMd5(rawEncoder + infoVo.getSecretkey().toLowerCase()));
        }else {
            //验证客户端，客户端采用MD5方式加密
            return encoder.equals(Md5Util.getStringMd5((String)rawEncoder));
        }
    }
}

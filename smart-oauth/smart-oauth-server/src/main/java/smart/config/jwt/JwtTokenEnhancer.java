package smart.config.jwt;

import smart.config.ConfigValueUtil;
import smart.util.data.DataSourceContextHolder;
import smart.base.LogApi;
import smart.base.UserInfo;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 根据需要配置Jwt内容增强器
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {
    @Autowired
    private LogApi logApi;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ConfigValueUtil configValueUtil;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        LinkedHashMap linkedHashMap = (LinkedHashMap) oAuth2Authentication.getUserAuthentication().getDetails();
        UserInfo userInfo = userProvider.get(String.valueOf(linkedHashMap.get("userId")), DataSourceContextHolder.getDatasourceId());
        //创建map，将需要增加的内容放置到map中
        Map<String, Object> map = new HashMap<>();
        //移除在线
        userProvider.removeWebSocket(userInfo);
        map.put("token", userInfo.getId());
        userInfo.setId(userInfo.getId());
        //写入日志
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())){
            logApi.writeLogAsync(DataSourceContextHolder.getDatasourceId(),DataSourceContextHolder.getDatasourceName(),userInfo.getUserId(), userInfo.getUserName(), userInfo.getUserAccount(), "登录成功");
        }else {
            logApi.writeLogAsync("1","1",userInfo.getUserId(), userInfo.getUserName(), userInfo.getUserAccount(), "登录成功");
        }
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(map);
        return oAuth2AccessToken;
    }
}

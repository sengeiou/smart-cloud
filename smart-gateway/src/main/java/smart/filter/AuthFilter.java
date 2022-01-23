package smart.filter;

import smart.base.ActionResultCode;
import smart.util.*;
import smart.base.ActionResult;
import smart.base.UserInfo;
import smart.util.jwt.JwtUtil;
import smart.white.GatewayWhite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关验证token
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //进行token验证
        String url = exchange.getRequest().getURI().getPath();
        String token = exchange.getRequest().getHeaders().getFirst(Constants.AUTHORIZATION);
        GatewayWhite white = new GatewayWhite();
        if (StringUtil.matches(url, white.getWhiteUrl())) {
            return chain.filter(exchange);
        }
        String realToken = JwtUtil.getRealToken(token);
        if (StringUtil.isBlank(realToken)) {
            ActionResult result = ActionResult.fail(ActionResultCode.SessionOverdue.getCode(), ActionResultCode.SessionOverdue.getMessage());
            return setUnauthorizedResponse(exchange, result);
        } else {
            if (!redisUtil.exists(realToken)) {
                ActionResult result = ActionResult.fail(ActionResultCode.SessionError.getCode(), ActionResultCode.SessionError.getMessage());
                return setUnauthorizedResponse(exchange, result);
            }
            UserInfo userInfo = JsonUtil.getJsonToBean(String.valueOf(redisUtil.getString(realToken)),UserInfo.class);
            //是否在线
            String userAgent =  exchange.getRequest().getHeaders().getFirst(Constants.USER_AGENT);
            if(!isOnLine(userInfo,userAgent)){
                ActionResult result = ActionResult.fail(ActionResultCode.SessionOffLine.getCode(), ActionResultCode.SessionOffLine.getMessage());
                redisUtil.remove(realToken);
                return setUnauthorizedResponse(exchange, result);
            }
            //重新给redis中的token设置有效时间
            tokenTimeout(userInfo);
        }
        return chain.filter(exchange);
    }


    private Mono<Void> setUnauthorizedResponse(ServerWebExchange exchange, ActionResult result) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.OK);
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            return bufferFactory.wrap(JsonUtil.getObjectToString(result).getBytes());
        }));
    }

    @Override
    public int getOrder() {
        return -200;
    }

    /**
     * 重新给redis中的token设置有效时间
     * @param userInfo
     */
    private void tokenTimeout(UserInfo userInfo){
        String tenantId = StringUtil.isNotEmpty(userInfo.getTenantId())?userInfo.getTenantId():"";
        String userId = userInfo.getUserId();
        String onlineInfo=tenantId+"login_online_"+userId;
        if (ServletUtil.getIsMobileDevice()){
            onlineInfo = tenantId+"login_online_mobile_"+userId;
        }
        redisUtil.expire(onlineInfo,userInfo.getTokenTimeout()*60);
        redisUtil.expire(userInfo.getId(),userInfo.getTokenTimeout()*60);
    }

    /**
     * 是否在线
     */
    private boolean isOnLine(UserInfo userInfo, String userAgent) {
        String online ;
        if (ServletUtil.getIsMobileDevice(userAgent)) {
            online = userInfo.getTenantId()+"login_online_mobile_"+userInfo.getUserId();
        }else{
            online = userInfo.getTenantId()+"login_online_"+userInfo.getUserId();
        }
        //判断是否在线
        if(redisUtil.exists(online)){
            //判断在线的token是否正确
            if(userInfo.getId().equals(redisUtil.getString(online).toString())){
                return true;
            }
        }
        return false ;
    }

}

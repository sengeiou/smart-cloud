package com.bstek.ureport.console.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bstek.ureport.console.config.DataSourceConfig;
import smart.util.data.DataSourceContextHolder;
import smart.util.jwt.JwtUtil;
import smart.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@Slf4j
public class UpdateData {
    @Autowired
    private DataSourceConfig dataSourceConfig;
    @Autowired
    private RedisUtil redisUtil;

    public boolean getDbName(HttpServletRequest req) {
        try {
            if (Boolean.valueOf(getMultiTenancy(dataSourceConfig.getMultiTenancy()))) {
                String token = JwtUtil.getRealToken(req.getHeader("Authorization"));
                try {
                    String dbNames = String.valueOf(redisUtil.getString(token));
                    Map<String, Object> map = JSONUtil.StringToMap(dbNames);
                    String dbName = String.valueOf(map.get("tenantDbConnectionString"));
                    String tenantId = String.valueOf(map.get("tenantId"));
                    DataSourceContextHolder.setDatasource(tenantId,dbName);
                }catch (Exception e){
                    Object obj = redisUtil.getString(token);
                    JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);
                    String dbName = String.valueOf(jsonObject.get("TenantDbConnectionString"));
                    String tenantId = String.valueOf(jsonObject.get("TenantId"));
                    DataSourceContextHolder.setDatasource(tenantId,dbName);
                }
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return false;
    }

    //判断是否开启多租户
    public boolean getMultiTenancy(String multiTenancy){
        if (Boolean.valueOf(multiTenancy)) {
            return true;
        }
        return false;
    }

    //获取用户名
    public String getUserName(String token){
        try {
            Object dbNames = redisUtil.getString(token);
            Map<String, Object> map = JSONUtil.StringToMap(String.valueOf(dbNames));
            return String.valueOf(map.get("userName"));
        }catch (Exception e){
            Object obj = redisUtil.getString(token);
            JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);
            return String.valueOf(jsonObject.get("UserName"));
        }
    }

    //获取Account
    public String getUserId(String token){
        try {
            Object dbNames = redisUtil.getString(token);
            Map<String, Object> map = JSONUtil.StringToMap(String.valueOf(dbNames));
            return String.valueOf(map.get("userAccount"));
        }catch (Exception e){
            Object obj = redisUtil.getString(token);
            JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);
            return String.valueOf(jsonObject.get("UserAccount"));
        }
    }

}

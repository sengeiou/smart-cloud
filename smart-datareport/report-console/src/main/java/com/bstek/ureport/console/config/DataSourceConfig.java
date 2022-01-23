package com.bstek.ureport.console.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("dataSourceConfig")
@Data
public class DataSourceConfig {
    //判断是否为多租户
    @Value("${config.MultiTenancy}")
    private String multiTenancy;

}

package com.bstek.ureport.console;

import com.bstek.ureport.console.config.DataReportListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = {"smart","com.bstek.ureport.console"})
@ImportResource("classpath:ureport.xml")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "smart")
@MapperScan(basePackages = {"com.bstek.ureport.console.ureport.mapper"})
public class SmartDataReportApplication {

    public static void main(String[] args) {
        try{
            SpringApplication springApplication = new SpringApplication(SmartDataReportApplication.class);
            //添加监听器
            springApplication.addListeners(new DataReportListener());
            springApplication.run(args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Bean
    public ServletRegistrationBean buildUreportServlet(){
        return new ServletRegistrationBean(new UReportServlet(), "/*");
    }

}

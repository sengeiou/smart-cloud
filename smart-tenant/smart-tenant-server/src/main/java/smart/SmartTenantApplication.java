package smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 多租户服务启动类
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@SpringBootApplication
@EnableFeignClients
public class SmartTenantApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTenantApplication.class, args);
        System.out.println("tenant启动成功");
    }

}

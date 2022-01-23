package smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 鉴权启动程序
 *
 * @author SmartCloud项目开发组
 */
@SpringBootApplication
@EnableFeignClients
public class SmartOauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartOauthApplication.class, args);
        System.out.println("鉴权启动成功");
    }

}

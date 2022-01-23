package smart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SmartExtendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartExtendApplication.class, args);
        System.out.println("Extend启动成功");
    }
}

package smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SmartSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartSystemApplication.class, args);
        System.out.println("System启动成功");
    }

}

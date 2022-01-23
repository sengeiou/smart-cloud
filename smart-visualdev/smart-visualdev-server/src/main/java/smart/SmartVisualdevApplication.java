package smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "smart")
@EnableFeignClients(basePackages = "smart")
public class SmartVisualdevApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartVisualdevApplication.class, args);
        System.out.println("visualdev启动成功");
    }

}

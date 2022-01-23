package smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "smart")
@EnableFeignClients(basePackages = "smart")
public class SmartParkingInsideApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartParkingInsideApplication.class, args);
        System.out.println("路内停车服务启动成功");
    }

}

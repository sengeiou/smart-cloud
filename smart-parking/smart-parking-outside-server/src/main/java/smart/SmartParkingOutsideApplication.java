package smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "smart")
@EnableFeignClients(basePackages = "smart")
public class SmartParkingOutsideApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartParkingOutsideApplication.class, args);
        System.out.println("路外停车场服务启动成功");
    }

}

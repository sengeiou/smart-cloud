package smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "smart")
@EnableFeignClients
public class SmartThirdApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartThirdApplication.class, args);
        System.out.println("Third启动成功");
    }

}

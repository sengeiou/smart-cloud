package smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"smart","com.smart.pay"})
@EnableFeignClients(basePackages = {"smart","com.smart.pay.api"})
public class SmartPayCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartPayCoreApplication.class, args);
        System.out.println("PayCore启动成功");
    }

}

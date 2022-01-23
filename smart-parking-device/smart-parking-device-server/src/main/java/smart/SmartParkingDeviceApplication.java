package smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "smart")
@EnableFeignClients(basePackages = "smart")
public class SmartParkingDeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartParkingDeviceApplication.class, args);
        System.out.println("停车设备接入服务启动成功");
    }

}

package smart;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SmartFileAplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartFileAplication.class,args);
        System.out.println("SmartFile启动完成");
    }
}

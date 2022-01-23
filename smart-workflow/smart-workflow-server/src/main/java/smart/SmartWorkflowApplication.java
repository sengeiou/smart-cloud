package smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 工作流启动类
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 11:32
 */
@SpringBootApplication
@EnableFeignClients
public class SmartWorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartWorkflowApplication.class, args);
        System.out.println("work启动成功");
    }

}

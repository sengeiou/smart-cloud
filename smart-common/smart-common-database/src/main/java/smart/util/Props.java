package smart.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:51
 */
@Data
@Component
public class Props {
    @Value("${config.MultiTenancyUrl}")
    private String portUrl;

    @Value("${spring.profiles.active}")
    private String active;

    @Value("${spring.redis.database}")
    private String dnName;
}

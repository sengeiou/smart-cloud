package smart.base.fallback;

import smart.base.LogApi;
import smart.base.entity.LogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
/**
 * 调用系统日志Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
@Slf4j
public class LogApiFallback implements LogApi {

    @Override
    public void writeLogAsync(String dbId, String dbName, String userId, String userName, String account, String abstracts) {
        log.error("写入登陆日志失败");
    }

    @Override
    public void writeLogRequest(LogEntity logEntity) {

    }
}

package smart.base;

import smart.base.entity.LogEntity;
import smart.base.fallback.LogApiFallback;
import smart.utils.FeignName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
/**
 * 调用系统日志Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME , fallback = LogApiFallback.class, path = "/Base/Log")
public interface LogApi {
    /**
     * 写入日志
     *
     * @param userId
     * @param userName
     * @param abstracts
     */
    @PostMapping("/writeLogAsync/{dbId}/{dbName}/{userId}/{userName}/{account}/{abstracts}")
    void writeLogAsync(@PathVariable("dbId") String dbId, @PathVariable("dbName") String dbName, @PathVariable("userId") String userId, @PathVariable("userName") String userName, @PathVariable("account") String account, @PathVariable("abstracts") String abstracts);

    /**
     * 写入请求日志
     */
    @PostMapping("/writeLogRequest")
    void writeLogRequest(@RequestBody LogEntity logEntity);

}

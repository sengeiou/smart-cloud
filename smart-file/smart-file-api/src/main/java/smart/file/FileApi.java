package smart.file;

import smart.file.fallback.FileApiFallback;
import smart.utils.FeignName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 通过api调用文件路径
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.FILE_SERVER_NAME, fallback = FileApiFallback.class)
public interface FileApi {
    /**
     * 通过type获取路径
     *
     * @param type 类型
     * @return
     */
    @GetMapping("/getPath/{type}")
    String getPath(@PathVariable("type") String type);
}

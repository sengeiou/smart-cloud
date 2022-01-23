package smart.file.fallback;

import smart.file.FileApi;
import org.springframework.stereotype.Component;
/**
 * 降级处理
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class FileApiFallback implements FileApi {
    @Override
    public String getPath(String type) {
        return null;
    }
}

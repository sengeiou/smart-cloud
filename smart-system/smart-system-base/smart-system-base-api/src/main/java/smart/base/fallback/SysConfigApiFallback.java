package smart.base.fallback;

import smart.base.SysConfigApi;
import smart.model.BaseSystemInfo;
import smart.base.entity.SysConfigEntity;
import smart.base.model.mp.MPSavaModel;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * 调用系统配置Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class SysConfigApiFallback implements SysConfigApi {

    @Override
    public BaseSystemInfo getSysInfo(String tenantId, String dbName) {
        return null;
    }

    @Override
    public List<SysConfigEntity> getSysInfo(String type) {
        return null;
    }

    @Override
    public BaseSystemInfo getWeChatInfo() {
        return null;
    }

    @Override
    public boolean saveMp(MPSavaModel mpSavaModel) {
        return false;
    }
}

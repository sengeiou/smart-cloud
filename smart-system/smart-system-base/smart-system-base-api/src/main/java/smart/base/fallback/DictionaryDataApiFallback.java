package smart.base.fallback;

import smart.base.DictionaryDataApi;
import smart.base.ActionResult;
import smart.base.entity.DictionaryDataEntity;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * 调用数据字典Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class DictionaryDataApiFallback implements DictionaryDataApi {
    @Override
    public ActionResult<List<DictionaryDataEntity>> getList(String dictionary) {
        return null;
    }

    @Override
    public ActionResult<List<DictionaryDataEntity>> getListAll() {
        return null;
    }

    @Override
    public ActionResult<DictionaryDataEntity> getInfo(String id) {
        return null;
    }
}

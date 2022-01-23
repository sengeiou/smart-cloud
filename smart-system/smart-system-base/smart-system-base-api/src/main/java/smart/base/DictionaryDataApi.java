package smart.base;

import smart.base.entity.DictionaryDataEntity;
import smart.utils.FeignName;
import smart.base.fallback.DictionaryDataApiFallback;
import smart.base.ActionResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
/**
 * 调用数据字典Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME , fallback = DictionaryDataApiFallback.class, path = "/Base/DictionaryData")
public interface DictionaryDataApi {
    /**
     * 获取字典数据信息列表
     * @param dictionary
     * @return
     */
    @GetMapping("/getList/{dictionary}")
    ActionResult<List<DictionaryDataEntity>> getList(@PathVariable("dictionary") String dictionary);

    /**
     * 获取字典数据信息列表
     * @return
     */
    @GetMapping("/getListAll")
    ActionResult<List<DictionaryDataEntity>> getListAll();

    /**
     * 获取字典数据信息
     * @param id
     * @return
     */
    @GetMapping("/{id}/info")
    ActionResult<DictionaryDataEntity> getInfo(@PathVariable("id") String id);
}

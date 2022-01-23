package smart.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.Pagination;
import smart.base.entity.LanguageMapEntity;
import smart.base.model.language.LanguageListDTO;

import java.util.List;

/**
 * 翻译数据
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface LanguageMapService extends IService<LanguageMapEntity> {

    /**
     * 列表
     *
     * @param pagination      分页
     * @param languageTypeId 分类主键
     * @return
     */
    List<LanguageListDTO> getList(Pagination pagination, String languageTypeId);

    /**
     * 列表
     *
     * @param fnCode 编码
     * @return
     */
    List<LanguageMapEntity> getList(String fnCode);

    /**
     * 重复验证（翻译标记）
     *
     * @param fnCode  编码
     * @param signKey 翻译标记
     * @return
     */
    boolean isExistBySignKey(String fnCode, String signKey);

    /**
     * 创建
     *
     * @param entitys 实体对象
     */
    void create(List<LanguageMapEntity> entitys);

    /**
     * 更新
     *
     * @param fnCode  编码
     * @param entitys 实体对象
     * @return
     */
    boolean update(String fnCode, List<LanguageMapEntity> entitys);

    /**
     * 删除
     *
     * @param fnCode 编码
     * @return
     */
    boolean delete(String fnCode);

    /**
     * 上移
     *
     * @param fnCode 编码
     * @return
     */
    boolean first(String fnCode);

    /**
     * 下移
     *
     * @param fnCode 编码
     * @return
     */
    boolean next(String fnCode);
}

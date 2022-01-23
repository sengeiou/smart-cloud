package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.Pagination;
import smart.base.vo.PaginationVO;
import smart.base.model.language.*;
import smart.base.entity.LanguageMapEntity;
import smart.exception.DataException;
import smart.base.service.LanguageMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 翻译数据
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "翻译数据", value = "Language")
@RestController
@RequestMapping("/Base/Language")
public class LanguageController {

    @Autowired
    private LanguageMapService languageMapService;

    /**
     * 列表
     *
     * @param pagination 分页
     * @param typeId     分类主键
     * @return
     */
    @ApiOperation("获取翻译列表(带分页)")
    @GetMapping("/List/{typeId}")
    public ActionResult getList(Pagination pagination, @PathVariable("typeId") String typeId) {
        List<LanguageListDTO> data = languageMapService.getList(pagination, typeId);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(data, paginationVO);
    }

    /**
     * 信息
     *
     * @param encode 主键
     * @return
     */
    @ApiOperation("获取翻译")
    @GetMapping("/{encode}")
    public ActionResult get(@PathVariable("encode") String encode) throws DataException {
        List<LanguageMapEntity> data = languageMapService.getList(encode);
        List<LanguageCrModel> list = JsonUtil.getJsonToList(data, LanguageCrModel.class);
        LanguageInfoVO vo = JsonUtil.getJsonToBeanEx(data.get(0), LanguageInfoVO.class);
        vo.setTranslateList(JsonUtil.getJsonToList(list, LanguageCrModel.class));
        return ActionResult.success(vo);
    }

    /**
     * 新建
     *
     * @param languageCrForm dto实体
     * @return
     */
    @ApiOperation("添加翻译")
    @PostMapping
    public ActionResult create(@RequestBody @Valid LanguageCrForm languageCrForm) {
        List<LanguageMapEntity> languageMapList = JsonUtil.getJsonToList(languageCrForm.getTranslateList(), LanguageMapEntity.class);
        for (LanguageMapEntity entity : languageMapList) {
            entity.setLanguageTypeId(languageCrForm.getLanguageTypeId());
            entity.setSignKey(languageCrForm.getSignKey());
        }
        if (languageMapService.isExistBySignKey(null, languageCrForm.getSignKey())) {
            return ActionResult.fail("翻译标记不能重复");
        }
        languageMapService.create(languageMapList);
        return ActionResult.success("创建成功");
    }

    /**
     * 更新
     *
     * @param languageUpForm dto实体
     * @param encode         编码
     * @return
     */
    @ApiOperation("修改翻译")
    @PutMapping("/{encode}")
    public ActionResult update(@RequestBody @Valid LanguageUpForm languageUpForm, @PathVariable("encode") String encode) {
        List<LanguageMapEntity> languageMapList = JsonUtil.getJsonToList(languageUpForm.getTranslateList(), LanguageMapEntity.class);
        for (LanguageMapEntity entity : languageMapList) {
            entity.setLanguageTypeId(languageUpForm.getLanguageTypeId());
            entity.setSignKey(languageUpForm.getSignKey());
        }
        if (languageMapService.isExistBySignKey(encode, languageUpForm.getSignKey())) {
            return ActionResult.fail("翻译标记不能重复");
        }
        boolean flag = languageMapService.update(encode, languageMapList);
        if (flag == false) {
            ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    @ApiOperation("删除翻译")
    @DeleteMapping("/{enCode}")
    public ActionResult delete(@PathVariable("enCode") String enCode) {
        boolean flag = languageMapService.delete(enCode);
        if (flag == false) {
            return ActionResult.fail("删除失败，数据不存在");
        }
        return ActionResult.success("删除成功");
    }

}

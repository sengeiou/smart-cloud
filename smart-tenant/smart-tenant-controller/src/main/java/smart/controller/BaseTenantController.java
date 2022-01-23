package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.BaseTenantEntity;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import smart.base.ActionResult;
import smart.base.vo.PageListVO;
import smart.base.vo.PaginationVO;
import smart.model.*;
import smart.service.BaseTenantLogService;
import smart.service.BaseTenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * baseTenant
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Slf4j
@RestController
@Api(tags = "baseTenant", value = "tenant")
@RequestMapping
public class BaseTenantController {

    @Autowired
    private BaseTenantService baseTenantService;
    @Autowired
    private BaseTenantLogService baseTenantLogService;

    /**
     * 创建租户数据库
     *
     * @param dbName
     * @return
     */
    @ApiOperation("创建租户数据库")
    @PostMapping("/DbName/{dbName}")
    public ActionResult createTable(@PathVariable("dbName") String dbName) {
        baseTenantService.createDb(dbName);
        return ActionResult.success("数据库创建完成");
    }

    /**
     * 获取租户
     *
     * @return
     */
    @ApiOperation("获取租户")
    @GetMapping
    public ActionResult list(BaseTenantPage baseTenantPage) {
        List<BaseTenantEntity> list = baseTenantService.getList(baseTenantPage);
        List<BaseTenantListVO> listVos = JsonUtil.getJsonToList(list, BaseTenantListVO.class);
        PageListVO vo = new PageListVO();
        PaginationVO pageModel = JsonUtil.getJsonToBean(baseTenantPage, PaginationVO.class);
        vo.setList(listVos);
        vo.setPagination(pageModel);
        return ActionResult.success(vo);
    }

    /**
     * 获取租户数据库
     *
     * @param encode
     * @return
     */
    @ApiOperation("获取租户数据库")
    @GetMapping("/DbName/{encode}")
    public ActionResult dbContent(@PathVariable("encode") String encode) {
        BaseTenantEntity entity = baseTenantService.getEnCode(encode);
        if (entity == null) {
            return ActionResult.fail("租户不存在");
        }
        Date nowData = new Date();
        Date expires = entity.getExpiresTime();
        if (nowData.getTime() > expires.getTime()) {
            return ActionResult.fail("租户已到期");
        }
        if (entity != null) {
            baseTenantLogService.logAsync(entity.getId(),entity.getEnCode());
        }
        BaseTenantVO vo = new BaseTenantVO();
        vo.setDbName(entity.getDbserviceName());
        return ActionResult.success(vo);
    }

    /**
     * 信息
     *
     * @param id
     * @return
     */
    @ApiOperation("信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) {
        BaseTenantEntity entity = baseTenantService.getInfo(id);
        BaseTenantInfoVO vo = JsonUtil.getJsonToBean(entity, BaseTenantInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id, @RequestBody @Valid BaseTenantDeForm deForm) {
        BaseTenantEntity entity = baseTenantService.getInfo(id);
        if (entity != null) {
            baseTenantService.delete(entity,deForm);
        }
        return ActionResult.success("删除成功");
    }

    /**
     * 新增
     *
     * @param baseTenantCrForm
     * @return
     */
    @ApiOperation("新增")
    @PostMapping
    public ActionResult create(@RequestBody @Valid BaseTenantCrForm baseTenantCrForm) {
        BaseTenantEntity enCode = baseTenantService.getEnCode(baseTenantCrForm.getEnCode());
        if (enCode != null) {
            return ActionResult.fail("租户已存在");
        }
        BaseTenantEntity entity = JsonUtil.getJsonToBean(baseTenantCrForm, BaseTenantEntity.class);
        entity.setId(RandomUtil.uuId());
        String dbName = "tenant_"+baseTenantCrForm.getEnCode().toLowerCase();
        entity.setDbserviceName(dbName);
        baseTenantService.create(entity);
        baseTenantService.createDb(dbName);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新
     *
     * @param id
     * @return
     */
    @ApiOperation("更新")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid BaseTenantUpForm baseTenantUpForm) {
        BaseTenantEntity entity = baseTenantService.getInfo(id);
        if (entity != null) {
            entity = JsonUtil.getJsonToBean(baseTenantUpForm, BaseTenantEntity.class);
            baseTenantService.update(id, entity);
            return ActionResult.success("更新成功");
        } else {
            return ActionResult.fail("更新失败，数据不存在");
        }
    }

}

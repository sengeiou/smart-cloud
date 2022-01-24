package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.config.ConfigValueUtil;
import smart.util.data.DataSourceContextHolder;
import smart.model.BaseSystemInfo;
import smart.util.JsonUtil;
import smart.util.NoDataSourceBind;
import smart.util.RandomUtil;
import smart.base.ActionResult;
import smart.base.model.mp.MPSavaModel;
import smart.base.model.systemconfig.EmailTestForm;
import smart.base.model.systemconfig.SysConfigModel;
import smart.base.entity.EmailConfigEntity;
import smart.base.entity.SysConfigEntity;
import smart.base.service.CheckLoginService;
import smart.base.service.SysconfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "系统配置",value = "SysConfig")
@RestController
@RequestMapping("/Base/SysConfig")
public class SysConfigController {

    @Autowired
    private SysconfigService sysconfigService;
    @Autowired
    private CheckLoginService checkLoginService;
    @Autowired
    private ConfigValueUtil configValueUtil;

    /**
     * 列表
     * @return
     */
    @ApiOperation("列表")
    @GetMapping
    public ActionResult list() {
        List<SysConfigEntity> list = sysconfigService.getList("SysConfig");
        HashMap<String, String> map = new HashMap<>();
        for (SysConfigEntity sys : list) {
            map.put(sys.getFkey(), sys.getValue());
        }
        SysConfigModel sysConfigModel= JsonUtil.getJsonToBean(map,SysConfigModel.class);
        return ActionResult.success(sysConfigModel);
    }

    /**
     * 保存设置
     *
     * @return
     */
    @ApiOperation("更新系统配置")
    @PutMapping
    public ActionResult save(@RequestBody SysConfigModel sysConfigModel) {
        List<SysConfigEntity> entitys = new ArrayList<>();
        Map<String, Object> map = JsonUtil.entityToMap(sysConfigModel);
        map.put("isLog","1");
        map.put("sysTheme","1");
        map.put("pageSize","30");
        map.put("lastLoginTime",1);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            SysConfigEntity entity = new SysConfigEntity();
            entity.setId(RandomUtil.uuId());
            entity.setFkey(entry.getKey());
            entity.setValue(String.valueOf(entry.getValue()));
            entitys.add(entity);
        }
        sysconfigService.save(entitys);
        return ActionResult.success("操作成功");
    }

    /**
     * 获取BaseSystemInfo
     * @return
     */
    @NoDataSourceBind
    @GetMapping("/getInfo/{tenantId}/{dbName}")
    public BaseSystemInfo getSysInfo(@PathVariable("tenantId") String tenantId, @PathVariable("dbName") String dbName) {
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            DataSourceContextHolder.setDatasource(tenantId, dbName);
        }
        BaseSystemInfo sysInfo = sysconfigService.getSysInfo();
        return sysInfo;
    }

    /**
     * 获取微信配置列表
     * @param type
     * @return
     */
    @GetMapping("/getSysConfigInfo/{type}")
    public List<SysConfigEntity> getSysInfo(@PathVariable("type") String type){
        List<SysConfigEntity> list = sysconfigService.getList(type);
        return list;
    }

    /**
     * 获取微信配置列表
     * @return
     */
    @GetMapping("/getWeChatInfo")
    public BaseSystemInfo getWeChatInfo(){
        BaseSystemInfo weChatInfo = sysconfigService.getWeChatInfo();
        return weChatInfo;
    }


    /**
     * 保存公众号配置
     * @param mpSavaModel
     */
    @PostMapping("/SaveMp")
    public boolean saveMp(@RequestBody MPSavaModel mpSavaModel){
        List<SysConfigEntity> entitys = mpSavaModel.getEntitys();
        return sysconfigService.saveMp(entitys);
    }

    /**
     * 邮箱账户密码验证
     *
     * @return
     */
    @ApiOperation("邮箱连接测试")
    @PostMapping("/Email/Test")
    public ActionResult checkLogin(@RequestBody EmailTestForm emailTestForm) {
        EmailConfigEntity entity = JsonUtil.getJsonToBean(emailTestForm, EmailConfigEntity.class);
        String result = checkLoginService.checkLogin(entity);
        if ("true".equals(result)) {
            return ActionResult.success("验证成功");
        } else {
            return ActionResult.fail(result);
        }
    }

}

package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import smart.base.SysConfigApi;
import smart.base.ActionResult;
import smart.base.entity.SysConfigEntity;
import smart.base.model.mp.MPSavaModel;
import smart.model.MPConfigModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公众号配置
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "微信配置",description = "WeChatConfig")
@RestController
@RequestMapping("/WeChat")
public class MPConfigController {

    @Autowired
    private SysConfigApi sysConfigApi;

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("获取公众号配置信息")
    @GetMapping("/MPConfig")
    public ActionResult MPConfigList() {
        List<SysConfigEntity> list = sysConfigApi.getSysInfo("WeChat").stream().filter(t->"MPConfig".equals(t.getCategory())).collect(Collectors.toList());
        HashMap<String, String> map = new HashMap<>();
        for (SysConfigEntity sys : list) {
            map.put(sys.getFkey(), sys.getValue());
        }
        MPConfigModel mpConfigModel= JsonUtil.getJsonToBean(map, MPConfigModel.class);
        return ActionResult.success(mpConfigModel);
    }

    /**
     * 保存设置
     *
     * @return
     */
    @ApiOperation("更新公众号配置信息")
    @PutMapping("/MPConfig")
    public ActionResult MPConfigSave(@RequestBody MPConfigModel mpConfigModel) {
        List<SysConfigEntity> entitys = new ArrayList<>();
        Map<String, Object> map = JsonUtil.entityToMap(mpConfigModel);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            SysConfigEntity entity = new SysConfigEntity();
            entity.setId(RandomUtil.uuId());
            entity.setFkey(entry.getKey());
            entity.setValue(String.valueOf(entry.getValue()));
            entitys.add(entity);
        }
        MPSavaModel mpSavaModel = new MPSavaModel(entitys);
        boolean flag=sysConfigApi.saveMp(mpSavaModel);
        if (flag ==false){
            return ActionResult.fail("操作失败，数据不存在");
        }
        return ActionResult.success("操作成功");
    }
}

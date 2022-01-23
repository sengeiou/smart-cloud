package smart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.ActionResult;
import smart.entity.DeviceEntity;
import smart.mapper.DeviceMapper;
import smart.service.DeviceService;
import org.springframework.stereotype.Service;


/**
 *
 * 设备管理
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-11-28 12:22:22
 */
@Service

public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DeviceEntity> implements DeviceService {
    @Override
    public DeviceEntity getDeviceBySN(String sn){
        QueryWrapper<DeviceEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(DeviceEntity::getSn,sn);
        queryWrapper.lambda().eq(DeviceEntity::getEnabledmark,"1");
        return this.getOne(queryWrapper);
    }

    /**
     * 设备统一校验
     * @param sn
     * @return
     */
    @Override
    public ActionResult deviceVerification(String sn){
        if(sn == null){
            return ActionResult.fail("设备SN参数为空！");
        }
        DeviceEntity device = getDeviceBySN(sn);
        if(device == null){
            return ActionResult.fail("设备:"+sn+"未注册或无效状态，取消处理！");
        }
        this.updateById(device);
        return ActionResult.success();
    }
}

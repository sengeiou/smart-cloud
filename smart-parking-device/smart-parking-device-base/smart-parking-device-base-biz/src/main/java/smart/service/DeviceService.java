package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.ActionResult;
import smart.entity.DeviceEntity;
import smart.model.device.DevicePagination;

import java.util.List;

/**
 *
 * 设备管理
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-11-28 12:22:22
 */
public interface DeviceService extends IService<DeviceEntity> {
    DeviceEntity getDeviceBySN(String sn);

    /**
     * 设备数据接入统一校验
     * @param sn
     * @return
     */
    ActionResult deviceVerification(String sn);
}

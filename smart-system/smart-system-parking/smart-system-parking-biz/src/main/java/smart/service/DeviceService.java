package smart.service;

import smart.entity.DeviceEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import smart.model.device.DevicePagination;
import java.util.*;
/**
 *
 * 设备管理
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-11-28 12:22:22
 */
public interface DeviceService extends IService<DeviceEntity> {

    List<DeviceEntity> getList(DevicePagination devicePagination);

    List<DeviceEntity> getTypeList(DevicePagination devicePagination,String dataType);



    DeviceEntity getInfo(String id);

    void delete(DeviceEntity entity);

    void create(DeviceEntity entity);

    boolean update( String id, DeviceEntity entity);

//  子表方法
}

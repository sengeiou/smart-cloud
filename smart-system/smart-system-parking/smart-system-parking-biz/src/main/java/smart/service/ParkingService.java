package smart.service;

import smart.entity.ParkingEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import smart.model.parking.ParkingPagination;
import java.util.*;
/**
 *
 * 车场管理
 * 版本： V3.1.0
 * 版权： SmartCloud项目开发组
 * 作者： SmartCloud
 * 日期： 2021-11-15 14:30:27
 */
public interface ParkingService extends IService<ParkingEntity> {

    List<ParkingEntity> getList(ParkingPagination parkingPagination);

    List<ParkingEntity> getTypeList(ParkingPagination parkingPagination,String dataType);



    ParkingEntity getInfo(String id);

    void delete(ParkingEntity entity);

    void create(ParkingEntity entity);

    boolean update( String id, ParkingEntity entity);

//  子表方法
}

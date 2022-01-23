package smart.service;

import smart.entity.ParkingAreaEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import smart.model.parkingarea.ParkingAreaPagination;
import java.util.*;
/**
 *
 * 片区管理
 * 版本： V3.1.0
 * 版权： SmartCloud项目开发组
 * 作者： SmartCloud
 * 日期： 2021-11-15 15:56:54
 */
public interface ParkingAreaService extends IService<ParkingAreaEntity> {

    List<ParkingAreaEntity> getList(ParkingAreaPagination parkingAreaPagination);

    List<ParkingAreaEntity> getTypeList(ParkingAreaPagination parkingAreaPagination,String dataType);



    ParkingAreaEntity getInfo(String id);

    void delete(ParkingAreaEntity entity);

    void create(ParkingAreaEntity entity);

    boolean update( String id, ParkingAreaEntity entity);

//  子表方法
}

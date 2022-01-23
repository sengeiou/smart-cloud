package smart.service;

import smart.entity.ParkingSpaceEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import smart.model.parkingspace.ParkingSpacePagination;
import java.util.*;
/**
 *
 * 泊位管理
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-11-12 13:48:03
 */
public interface ParkingSpaceService extends IService<ParkingSpaceEntity> {

    List<ParkingSpaceEntity> getList(ParkingSpacePagination parkingSpacePagination);

    List<ParkingSpaceEntity> getTypeList(ParkingSpacePagination parkingSpacePagination,String dataType);



    ParkingSpaceEntity getInfo(String id);

    void delete(ParkingSpaceEntity entity);

    void create(ParkingSpaceEntity entity);

    boolean update( String id, ParkingSpaceEntity entity);

//  子表方法
}

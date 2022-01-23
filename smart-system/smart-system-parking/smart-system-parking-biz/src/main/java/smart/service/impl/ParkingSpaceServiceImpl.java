package smart.service.impl;

import smart.entity.ParkingSpaceEntity;
import smart.mapper.ParkingSpaceMapper;
import smart.service.ParkingSpaceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.RandomUtil;
import smart.model.parkingspace.ParkingSpacePagination;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import smart.util.*;
import java.util.*;

/**
 *
 * 泊位管理
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-11-12 13:48:03
 */
@Service

public class ParkingSpaceServiceImpl extends ServiceImpl<ParkingSpaceMapper, ParkingSpaceEntity> implements ParkingSpaceService {

    @Autowired
    private UserProvider userProvider;


    @Override
    public List<ParkingSpaceEntity> getList(ParkingSpacePagination parkingSpacePagination){
        String userId=userProvider.get().getUserId();
        QueryWrapper<ParkingSpaceEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(parkingSpacePagination.getPid()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getPid,parkingSpacePagination.getPid()));
        }

        if(!"null".equals(String.valueOf(parkingSpacePagination.getDevice()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getDevice,parkingSpacePagination.getDevice()));
        }

        if(!"null".equals(String.valueOf(parkingSpacePagination.getName()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getName,parkingSpacePagination.getName()));
        }

        if(!"null".equals(String.valueOf(parkingSpacePagination.getType()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getType,parkingSpacePagination.getType()));
        }

        if(!"null".equals(String.valueOf(parkingSpacePagination.getEnabledmark()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getEnabledmark,parkingSpacePagination.getEnabledmark()));
        }

        if(!"null".equals(String.valueOf(parkingSpacePagination.getCreatortime()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getCreatortime,parkingSpacePagination.getCreatortime()));
        }

        //排序
        if(StringUtil.isEmpty(parkingSpacePagination.getSidx())){
            queryWrapper.lambda().orderByDesc(ParkingSpaceEntity::getCreatortime);
        }else{
            queryWrapper="asc".equals(parkingSpacePagination.getSort().toLowerCase())?queryWrapper.orderByAsc(parkingSpacePagination.getSidx()):queryWrapper.orderByDesc(parkingSpacePagination.getSidx());
        }
        Page<ParkingSpaceEntity> page=new Page<>(parkingSpacePagination.getCurrentPage(), parkingSpacePagination.getPageSize());
        IPage<ParkingSpaceEntity> userIPage=this.page(page,queryWrapper);
        return parkingSpacePagination.setData(userIPage.getRecords(),userIPage.getTotal());
    }
    @Override
    public List<ParkingSpaceEntity> getTypeList(ParkingSpacePagination parkingSpacePagination,String dataType){
        QueryWrapper<ParkingSpaceEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(parkingSpacePagination.getPid()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getPid,parkingSpacePagination.getPid()));
        }

        if(!"null".equals(String.valueOf(parkingSpacePagination.getDevice()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getDevice,parkingSpacePagination.getDevice()));
        }

        if(!"null".equals(String.valueOf(parkingSpacePagination.getName()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getName,parkingSpacePagination.getName()));
        }

        if(!"null".equals(String.valueOf(parkingSpacePagination.getType()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getType,parkingSpacePagination.getType()));
        }

        if(!"null".equals(String.valueOf(parkingSpacePagination.getEnabledmark()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getEnabledmark,parkingSpacePagination.getEnabledmark()));
        }

        if(!"null".equals(String.valueOf(parkingSpacePagination.getCreatortime()))){
            queryWrapper.lambda().and(t->t.like(ParkingSpaceEntity::getCreatortime,parkingSpacePagination.getCreatortime()));
        }

        //排序
        if(StringUtil.isEmpty(parkingSpacePagination.getSidx())){
            queryWrapper.lambda().orderByDesc(ParkingSpaceEntity::getCreatortime);
        }else{
            queryWrapper="asc".equals(parkingSpacePagination.getSort().toLowerCase())?queryWrapper.orderByAsc(parkingSpacePagination.getSidx()):queryWrapper.orderByDesc(parkingSpacePagination.getSidx());
        }
        if("0".equals(dataType)){
            Page<ParkingSpaceEntity> page=new Page<>(parkingSpacePagination.getCurrentPage(), parkingSpacePagination.getPageSize());
            IPage<ParkingSpaceEntity> userIPage=this.page(page,queryWrapper);
            return parkingSpacePagination.setData(userIPage.getRecords(),userIPage.getTotal());
        }else{
            return this.list(queryWrapper);
        }
    }

    @Override
    public ParkingSpaceEntity getInfo(String id){
        QueryWrapper<ParkingSpaceEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(ParkingSpaceEntity::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(ParkingSpaceEntity entity){
        this.save(entity);
    }

    @Override
    public boolean update(String id, ParkingSpaceEntity entity){
        entity.setId(id);
        return this.updateById(entity);
    }
    @Override
    public void delete(ParkingSpaceEntity entity){
        if(entity!=null){
            this.removeById(entity.getId());
        }
    }
    //子表方法
}

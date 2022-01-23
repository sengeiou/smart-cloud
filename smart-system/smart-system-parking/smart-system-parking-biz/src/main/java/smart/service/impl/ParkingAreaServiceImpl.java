package smart.service.impl;

import smart.entity.ParkingAreaEntity;
import smart.mapper.ParkingAreaMapper;
import smart.service.ParkingAreaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.RandomUtil;
import smart.model.parkingarea.ParkingAreaPagination;
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
 * 片区管理
 * 版本： V3.1.0
 * 版权： SmartCloud项目开发组
 * 作者： SmartCloud
 * 日期： 2021-11-15 15:56:54
 */
@Service

public class ParkingAreaServiceImpl extends ServiceImpl<ParkingAreaMapper, ParkingAreaEntity> implements ParkingAreaService {

    @Autowired
    private UserProvider userProvider;


    @Override
    public List<ParkingAreaEntity> getList(ParkingAreaPagination parkingAreaPagination){
        String userId=userProvider.get().getUserId();
        QueryWrapper<ParkingAreaEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(parkingAreaPagination.getName()))){
            queryWrapper.lambda().and(t->t.like(ParkingAreaEntity::getName,parkingAreaPagination.getName()));
        }

        if(!"null".equals(String.valueOf(parkingAreaPagination.getContactuserid()))){
            queryWrapper.lambda().and(t->t.like(ParkingAreaEntity::getContactuserid,parkingAreaPagination.getContactuserid()));
        }

        if(!"null".equals(String.valueOf(parkingAreaPagination.getEnabledmark()))){
            queryWrapper.lambda().and(t->t.like(ParkingAreaEntity::getEnabledmark,parkingAreaPagination.getEnabledmark()));
        }

        //排序
        if(StringUtil.isEmpty(parkingAreaPagination.getSidx())){
            queryWrapper.lambda().orderByDesc(ParkingAreaEntity::getId);
        }else{
            queryWrapper="asc".equals(parkingAreaPagination.getSort().toLowerCase())?queryWrapper.orderByAsc(parkingAreaPagination.getSidx()):queryWrapper.orderByDesc(parkingAreaPagination.getSidx());
        }
        Page<ParkingAreaEntity> page=new Page<>(parkingAreaPagination.getCurrentPage(), parkingAreaPagination.getPageSize());
        IPage<ParkingAreaEntity> userIPage=this.page(page,queryWrapper);
        return parkingAreaPagination.setData(userIPage.getRecords(),userIPage.getTotal());
    }
    @Override
    public List<ParkingAreaEntity> getTypeList(ParkingAreaPagination parkingAreaPagination,String dataType){
        QueryWrapper<ParkingAreaEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(parkingAreaPagination.getName()))){
            queryWrapper.lambda().and(t->t.like(ParkingAreaEntity::getName,parkingAreaPagination.getName()));
        }

        if(!"null".equals(String.valueOf(parkingAreaPagination.getContactuserid()))){
            queryWrapper.lambda().and(t->t.like(ParkingAreaEntity::getContactuserid,parkingAreaPagination.getContactuserid()));
        }

        if(!"null".equals(String.valueOf(parkingAreaPagination.getEnabledmark()))){
            queryWrapper.lambda().and(t->t.like(ParkingAreaEntity::getEnabledmark,parkingAreaPagination.getEnabledmark()));
        }

        //排序
        if(StringUtil.isEmpty(parkingAreaPagination.getSidx())){
            queryWrapper.lambda().orderByDesc(ParkingAreaEntity::getId);
        }else{
            queryWrapper="asc".equals(parkingAreaPagination.getSort().toLowerCase())?queryWrapper.orderByAsc(parkingAreaPagination.getSidx()):queryWrapper.orderByDesc(parkingAreaPagination.getSidx());
        }
        if("0".equals(dataType)){
            Page<ParkingAreaEntity> page=new Page<>(parkingAreaPagination.getCurrentPage(), parkingAreaPagination.getPageSize());
            IPage<ParkingAreaEntity> userIPage=this.page(page,queryWrapper);
            return parkingAreaPagination.setData(userIPage.getRecords(),userIPage.getTotal());
        }else{
            return this.list(queryWrapper);
        }
    }

    @Override
    public ParkingAreaEntity getInfo(String id){
        QueryWrapper<ParkingAreaEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(ParkingAreaEntity::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(ParkingAreaEntity entity){
        this.save(entity);
    }

    @Override
    public boolean update(String id, ParkingAreaEntity entity){
        entity.setId(id);
        return this.updateById(entity);
    }
    @Override
    public void delete(ParkingAreaEntity entity){
        if(entity!=null){
            this.removeById(entity.getId());
        }
    }
    //子表方法
}

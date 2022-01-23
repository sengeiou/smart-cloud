package smart.service.impl;

import smart.entity.ParkingEntity;
import smart.mapper.ParkingMapper;
import smart.service.ParkingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.RandomUtil;
import smart.model.parking.ParkingPagination;
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
 * 车场管理
 * 版本： V3.1.0
 * 版权： SmartCloud项目开发组
 * 作者： SmartCloud
 * 日期： 2021-11-15 14:30:27
 */
@Service

public class ParkingServiceImpl extends ServiceImpl<ParkingMapper, ParkingEntity> implements ParkingService {

    @Autowired
    private UserProvider userProvider;


    @Override
    public List<ParkingEntity> getList(ParkingPagination parkingPagination){
        String userId=userProvider.get().getUserId();
        QueryWrapper<ParkingEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(parkingPagination.getPaid()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getId,parkingPagination.getPaid()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getName()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getName,parkingPagination.getName()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getType()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getType,parkingPagination.getType()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getContactuserid()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getContactuserid,parkingPagination.getContactuserid()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getLeveladdress()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getLeveladdress,parkingPagination.getLeveladdress()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getEnabledmark()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getEnabledmark,parkingPagination.getEnabledmark()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getIssupportadvancepayment()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getIssupportadvancepayment,parkingPagination.getIssupportadvancepayment()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getIsselfsupport()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getIsselfsupport,parkingPagination.getIsselfsupport()));
        }

        //排序
        if(StringUtil.isEmpty(parkingPagination.getSidx())){
            queryWrapper.lambda().orderByDesc(ParkingEntity::getCreatortime);
        }else{
            queryWrapper="asc".equals(parkingPagination.getSort().toLowerCase())?queryWrapper.orderByAsc(parkingPagination.getSidx()):queryWrapper.orderByDesc(parkingPagination.getSidx());
        }
        Page<ParkingEntity> page=new Page<>(parkingPagination.getCurrentPage(), parkingPagination.getPageSize());
        IPage<ParkingEntity> userIPage=this.page(page,queryWrapper);
        return parkingPagination.setData(userIPage.getRecords(),userIPage.getTotal());
    }
    @Override
    public List<ParkingEntity> getTypeList(ParkingPagination parkingPagination,String dataType){
        QueryWrapper<ParkingEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(parkingPagination.getPaid()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getId,parkingPagination.getPaid()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getName()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getName,parkingPagination.getName()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getType()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getType,parkingPagination.getType()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getContactuserid()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getContactuserid,parkingPagination.getContactuserid()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getLeveladdress()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getLeveladdress,parkingPagination.getLeveladdress()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getEnabledmark()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getEnabledmark,parkingPagination.getEnabledmark()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getIssupportadvancepayment()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getIssupportadvancepayment,parkingPagination.getIssupportadvancepayment()));
        }

        if(!"null".equals(String.valueOf(parkingPagination.getIsselfsupport()))){
            queryWrapper.lambda().and(t->t.like(ParkingEntity::getIsselfsupport,parkingPagination.getIsselfsupport()));
        }

        //排序
        if(StringUtil.isEmpty(parkingPagination.getSidx())){
            queryWrapper.lambda().orderByDesc(ParkingEntity::getCreatortime);
        }else{
            queryWrapper="asc".equals(parkingPagination.getSort().toLowerCase())?queryWrapper.orderByAsc(parkingPagination.getSidx()):queryWrapper.orderByDesc(parkingPagination.getSidx());
        }
        if("0".equals(dataType)){
            Page<ParkingEntity> page=new Page<>(parkingPagination.getCurrentPage(), parkingPagination.getPageSize());
            IPage<ParkingEntity> userIPage=this.page(page,queryWrapper);
            return parkingPagination.setData(userIPage.getRecords(),userIPage.getTotal());
        }else{
            return this.list(queryWrapper);
        }
    }

    @Override
    public ParkingEntity getInfo(String id){
        QueryWrapper<ParkingEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(ParkingEntity::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(ParkingEntity entity){
        this.save(entity);
    }

    @Override
    public boolean update(String id, ParkingEntity entity){
        entity.setId(id);
        return this.updateById(entity);
    }
    @Override
    public void delete(ParkingEntity entity){
        if(entity!=null){
            this.removeById(entity.getId());
        }
    }
    //子表方法
}

package smart.service.impl;

import smart.entity.DeviceEntity;
import smart.mapper.DeviceMapper;
import smart.service.DeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.RandomUtil;
import smart.model.device.DevicePagination;
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
 * 设备管理
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-11-28 12:22:22
 */
@Service

public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DeviceEntity> implements DeviceService {

    @Autowired
    private UserProvider userProvider;


    @Override
    public List<DeviceEntity> getList(DevicePagination devicePagination){
        String userId=userProvider.get().getUserId();
        QueryWrapper<DeviceEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(devicePagination.getName()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getName,devicePagination.getName()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getCode()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getCode,devicePagination.getCode()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getSn()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getSn,devicePagination.getSn()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getNetworktype()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getNetworktype,devicePagination.getNetworktype()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getOnlinestatus()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getOnlinestatus,devicePagination.getOnlinestatus()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getAlarmstatus()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getAlarmstatus,devicePagination.getAlarmstatus()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getDevicestatus()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getDevicestatus,devicePagination.getDevicestatus()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getEnabledmark()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getEnabledmark,devicePagination.getEnabledmark()));
        }

        //排序
        if(StringUtil.isEmpty(devicePagination.getSidx())){
            queryWrapper.lambda().orderByDesc(DeviceEntity::getCreatortime);
        }else{
            queryWrapper="asc".equals(devicePagination.getSort().toLowerCase())?queryWrapper.orderByAsc(devicePagination.getSidx()):queryWrapper.orderByDesc(devicePagination.getSidx());
        }
        Page<DeviceEntity> page=new Page<>(devicePagination.getCurrentPage(), devicePagination.getPageSize());
        IPage<DeviceEntity> userIPage=this.page(page,queryWrapper);
        return devicePagination.setData(userIPage.getRecords(),userIPage.getTotal());
    }
    @Override
    public List<DeviceEntity> getTypeList(DevicePagination devicePagination,String dataType){
        QueryWrapper<DeviceEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(devicePagination.getName()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getName,devicePagination.getName()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getCode()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getCode,devicePagination.getCode()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getSn()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getSn,devicePagination.getSn()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getNetworktype()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getNetworktype,devicePagination.getNetworktype()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getOnlinestatus()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getOnlinestatus,devicePagination.getOnlinestatus()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getAlarmstatus()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getAlarmstatus,devicePagination.getAlarmstatus()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getDevicestatus()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getDevicestatus,devicePagination.getDevicestatus()));
        }

        if(!"null".equals(String.valueOf(devicePagination.getEnabledmark()))){
            queryWrapper.lambda().and(t->t.like(DeviceEntity::getEnabledmark,devicePagination.getEnabledmark()));
        }

        //排序
        if(StringUtil.isEmpty(devicePagination.getSidx())){
            queryWrapper.lambda().orderByDesc(DeviceEntity::getCreatortime);
        }else{
            queryWrapper="asc".equals(devicePagination.getSort().toLowerCase())?queryWrapper.orderByAsc(devicePagination.getSidx()):queryWrapper.orderByDesc(devicePagination.getSidx());
        }
        if("0".equals(dataType)){
            Page<DeviceEntity> page=new Page<>(devicePagination.getCurrentPage(), devicePagination.getPageSize());
            IPage<DeviceEntity> userIPage=this.page(page,queryWrapper);
            return devicePagination.setData(userIPage.getRecords(),userIPage.getTotal());
        }else{
            return this.list(queryWrapper);
        }
    }

    @Override
    public DeviceEntity getInfo(String id){
        QueryWrapper<DeviceEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(DeviceEntity::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(DeviceEntity entity){
        this.save(entity);
    }

    @Override
    public boolean update(String id, DeviceEntity entity){
        entity.setId(id);
        return this.updateById(entity);
    }
    @Override
    public void delete(DeviceEntity entity){
        if(entity!=null){
            this.removeById(entity.getId());
        }
    }
    //子表方法
}

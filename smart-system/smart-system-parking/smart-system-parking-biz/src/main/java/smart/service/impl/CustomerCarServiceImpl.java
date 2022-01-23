package smart.service.impl;

import smart.entity.CustomerCarEntity;
import smart.mapper.CustomerCarMapper;
import smart.service.CustomerCarService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.RandomUtil;
import smart.model.customercar.CustomerCarPagination;
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
 * 车辆信息
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-11-17 11:44:41
 */
@Service

public class CustomerCarServiceImpl extends ServiceImpl<CustomerCarMapper, CustomerCarEntity> implements CustomerCarService {

    @Autowired
    private UserProvider userProvider;


    @Override
    public List<CustomerCarEntity> getList(CustomerCarPagination customerCarPagination){
        String userId=userProvider.get().getUserId();
        QueryWrapper<CustomerCarEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(customerCarPagination.getCuid()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getCuid,customerCarPagination.getCuid()));
        }

        if(!"null".equals(String.valueOf(customerCarPagination.getPlatenumber()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getPlatenumber,customerCarPagination.getPlatenumber()));
        }

        if(!"null".equals(String.valueOf(customerCarPagination.getPlatetype()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getPlatetype,customerCarPagination.getPlatetype()));
        }

        if(!"null".equals(String.valueOf(customerCarPagination.getCartype()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getCartype,customerCarPagination.getCartype()));
        }

        if(!"null".equals(String.valueOf(customerCarPagination.getVin()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getVin,customerCarPagination.getVin()));
        }

        if(!"null".equals(String.valueOf(customerCarPagination.getEnabledmark()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getEnabledmark,customerCarPagination.getEnabledmark()));
        }

        //排序
        if(StringUtil.isEmpty(customerCarPagination.getSidx())){
            queryWrapper.lambda().orderByDesc(CustomerCarEntity::getCuid);
        }else{
            queryWrapper="asc".equals(customerCarPagination.getSort().toLowerCase())?queryWrapper.orderByAsc(customerCarPagination.getSidx()):queryWrapper.orderByDesc(customerCarPagination.getSidx());
        }
        Page<CustomerCarEntity> page=new Page<>(customerCarPagination.getCurrentPage(), customerCarPagination.getPageSize());
        IPage<CustomerCarEntity> userIPage=this.page(page,queryWrapper);
        return customerCarPagination.setData(userIPage.getRecords(),userIPage.getTotal());
    }
    @Override
    public List<CustomerCarEntity> getTypeList(CustomerCarPagination customerCarPagination,String dataType){
        QueryWrapper<CustomerCarEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(customerCarPagination.getCuid()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getCuid,customerCarPagination.getCuid()));
        }

        if(!"null".equals(String.valueOf(customerCarPagination.getPlatenumber()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getPlatenumber,customerCarPagination.getPlatenumber()));
        }

        if(!"null".equals(String.valueOf(customerCarPagination.getPlatetype()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getPlatetype,customerCarPagination.getPlatetype()));
        }

        if(!"null".equals(String.valueOf(customerCarPagination.getCartype()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getCartype,customerCarPagination.getCartype()));
        }

        if(!"null".equals(String.valueOf(customerCarPagination.getVin()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getVin,customerCarPagination.getVin()));
        }

        if(!"null".equals(String.valueOf(customerCarPagination.getEnabledmark()))){
            queryWrapper.lambda().and(t->t.like(CustomerCarEntity::getEnabledmark,customerCarPagination.getEnabledmark()));
        }

        //排序
        if(StringUtil.isEmpty(customerCarPagination.getSidx())){
            queryWrapper.lambda().orderByDesc(CustomerCarEntity::getCuid);
        }else{
            queryWrapper="asc".equals(customerCarPagination.getSort().toLowerCase())?queryWrapper.orderByAsc(customerCarPagination.getSidx()):queryWrapper.orderByDesc(customerCarPagination.getSidx());
        }
        if("0".equals(dataType)){
            Page<CustomerCarEntity> page=new Page<>(customerCarPagination.getCurrentPage(), customerCarPagination.getPageSize());
            IPage<CustomerCarEntity> userIPage=this.page(page,queryWrapper);
            return customerCarPagination.setData(userIPage.getRecords(),userIPage.getTotal());
        }else{
            return this.list(queryWrapper);
        }
    }

    @Override
    public CustomerCarEntity getInfo(String id){
        QueryWrapper<CustomerCarEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(CustomerCarEntity::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(CustomerCarEntity entity){
        this.save(entity);
    }

    @Override
    public boolean update(String id, CustomerCarEntity entity){
        entity.setId(id);
        return this.updateById(entity);
    }
    @Override
    public void delete(CustomerCarEntity entity){
        if(entity!=null){
            this.removeById(entity.getId());
        }
    }
    //子表方法
}

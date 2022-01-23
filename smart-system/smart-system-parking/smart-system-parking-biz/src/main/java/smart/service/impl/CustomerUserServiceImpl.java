package smart.service.impl;

import smart.entity.CustomerUserEntity;
import smart.mapper.CustomerUserMapper;
import smart.service.CustomerUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.RandomUtil;
import smart.model.customeruser.CustomerUserPagination;
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
 * 车主用户
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-11-17 10:02:02
 */
@Service

public class CustomerUserServiceImpl extends ServiceImpl<CustomerUserMapper, CustomerUserEntity> implements CustomerUserService {

    @Autowired
    private UserProvider userProvider;


    @Override
    public List<CustomerUserEntity> getList(CustomerUserPagination customerUserPagination){
        String userId=userProvider.get().getUserId();
        QueryWrapper<CustomerUserEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(customerUserPagination.getUsername()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getUsername,customerUserPagination.getUsername()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getNickname()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getNickname,customerUserPagination.getNickname()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getMobile()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getMobile,customerUserPagination.getMobile()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getGender()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getGender,customerUserPagination.getGender()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getIsfollow()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getIsfollow,customerUserPagination.getIsfollow()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getUsertype()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getUsertype,customerUserPagination.getUsertype()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getRegistsource()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getRegistsource,customerUserPagination.getRegistsource()));
        }

        //排序
        if(StringUtil.isEmpty(customerUserPagination.getSidx())){
            queryWrapper.lambda().orderByDesc(CustomerUserEntity::getCreatortime);
        }else{
            queryWrapper="asc".equals(customerUserPagination.getSort().toLowerCase())?queryWrapper.orderByAsc(customerUserPagination.getSidx()):queryWrapper.orderByDesc(customerUserPagination.getSidx());
        }
        Page<CustomerUserEntity> page=new Page<>(customerUserPagination.getCurrentPage(), customerUserPagination.getPageSize());
        IPage<CustomerUserEntity> userIPage=this.page(page,queryWrapper);
        return customerUserPagination.setData(userIPage.getRecords(),userIPage.getTotal());
    }
    @Override
    public List<CustomerUserEntity> getTypeList(CustomerUserPagination customerUserPagination,String dataType){
        QueryWrapper<CustomerUserEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(customerUserPagination.getUsername()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getUsername,customerUserPagination.getUsername()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getNickname()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getNickname,customerUserPagination.getNickname()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getMobile()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getMobile,customerUserPagination.getMobile()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getGender()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getGender,customerUserPagination.getGender()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getIsfollow()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getIsfollow,customerUserPagination.getIsfollow()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getUsertype()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getUsertype,customerUserPagination.getUsertype()));
        }

        if(!"null".equals(String.valueOf(customerUserPagination.getRegistsource()))){
            queryWrapper.lambda().and(t->t.like(CustomerUserEntity::getRegistsource,customerUserPagination.getRegistsource()));
        }

        //排序
        if(StringUtil.isEmpty(customerUserPagination.getSidx())){
            queryWrapper.lambda().orderByDesc(CustomerUserEntity::getCreatortime);
        }else{
            queryWrapper="asc".equals(customerUserPagination.getSort().toLowerCase())?queryWrapper.orderByAsc(customerUserPagination.getSidx()):queryWrapper.orderByDesc(customerUserPagination.getSidx());
        }
        if("0".equals(dataType)){
            Page<CustomerUserEntity> page=new Page<>(customerUserPagination.getCurrentPage(), customerUserPagination.getPageSize());
            IPage<CustomerUserEntity> userIPage=this.page(page,queryWrapper);
            return customerUserPagination.setData(userIPage.getRecords(),userIPage.getTotal());
        }else{
            return this.list(queryWrapper);
        }
    }

    @Override
    public CustomerUserEntity getInfo(String id){
        QueryWrapper<CustomerUserEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(CustomerUserEntity::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(CustomerUserEntity entity){
        this.save(entity);
    }

    @Override
    public boolean update(String id, CustomerUserEntity entity){
        entity.setId(id);
        return this.updateById(entity);
    }
    @Override
    public void delete(CustomerUserEntity entity){
        if(entity!=null){
            this.removeById(entity.getId());
        }
    }
    //子表方法
}

package smart.service.impl;

import smart.entity.ChargesEntity;
import smart.mapper.ChargesMapper;
import smart.service.ChargesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.RandomUtil;
import smart.model.charges.ChargesPagination;
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
 * 收费标准
 * 版本： V3.1.0
 * 版权： SmartCloud项目开发组
 * 作者： SmartCloud
 * 日期： 2021-12-10 15:18:55
 */
@Service

public class ChargesServiceImpl extends ServiceImpl<ChargesMapper, ChargesEntity> implements ChargesService {

    @Autowired
    private UserProvider userProvider;


    @Override
    public List<ChargesEntity> getList(ChargesPagination chargesPagination){
        String userId=userProvider.get().getUserId();
        QueryWrapper<ChargesEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(chargesPagination.getStandard_name()))){
            queryWrapper.lambda().and(t->t.like(ChargesEntity::getStandard_name,chargesPagination.getStandard_name()));
        }

        if(!"null".equals(String.valueOf(chargesPagination.getStandard_type()))){
            queryWrapper.lambda().and(t->t.like(ChargesEntity::getStandard_type,chargesPagination.getStandard_type()));
        }

        if(!"null".equals(String.valueOf(chargesPagination.getStart_time()))){
            queryWrapper.lambda().eq(ChargesEntity::getStart_time, DateUtil.stringToDates(DateUtil.daFormat(chargesPagination.getStart_time())));
        }

        if(!"null".equals(String.valueOf(chargesPagination.getEnd_time()))){
            queryWrapper.lambda().eq(ChargesEntity::getEnd_time, DateUtil.stringToDates(DateUtil.daFormat(chargesPagination.getEnd_time())));
        }

        //排序
        if(StringUtil.isEmpty(chargesPagination.getSidx())){
            queryWrapper.lambda().orderByDesc(ChargesEntity::getStart_time);
        }else{
            queryWrapper="asc".equals(chargesPagination.getSort().toLowerCase())?queryWrapper.orderByAsc(chargesPagination.getSidx()):queryWrapper.orderByDesc(chargesPagination.getSidx());
        }
        Page<ChargesEntity> page=new Page<>(chargesPagination.getCurrentPage(), chargesPagination.getPageSize());
        IPage<ChargesEntity> userIPage=this.page(page,queryWrapper);
        return chargesPagination.setData(userIPage.getRecords(),userIPage.getTotal());
    }
    @Override
    public List<ChargesEntity> getTypeList(ChargesPagination chargesPagination,String dataType){
        QueryWrapper<ChargesEntity> queryWrapper=new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if(!"null".equals(String.valueOf(chargesPagination.getStandard_name()))){
            queryWrapper.lambda().and(t->t.like(ChargesEntity::getStandard_name,chargesPagination.getStandard_name()));
        }

        if(!"null".equals(String.valueOf(chargesPagination.getStandard_type()))){
            queryWrapper.lambda().and(t->t.like(ChargesEntity::getStandard_type,chargesPagination.getStandard_type()));
        }

        if(!"null".equals(String.valueOf(chargesPagination.getStart_time()))){
            queryWrapper.lambda().eq(ChargesEntity::getStart_time, DateUtil.stringToDates(DateUtil.daFormat(chargesPagination.getStart_time())));
        }

        if(!"null".equals(String.valueOf(chargesPagination.getEnd_time()))){
            queryWrapper.lambda().eq(ChargesEntity::getEnd_time, DateUtil.stringToDates(DateUtil.daFormat(chargesPagination.getEnd_time())));
        }

        //排序
        if(StringUtil.isEmpty(chargesPagination.getSidx())){
            queryWrapper.lambda().orderByDesc(ChargesEntity::getStart_time);
        }else{
            queryWrapper="asc".equals(chargesPagination.getSort().toLowerCase())?queryWrapper.orderByAsc(chargesPagination.getSidx()):queryWrapper.orderByDesc(chargesPagination.getSidx());
        }
        if("0".equals(dataType)){
            Page<ChargesEntity> page=new Page<>(chargesPagination.getCurrentPage(), chargesPagination.getPageSize());
            IPage<ChargesEntity> userIPage=this.page(page,queryWrapper);
            return chargesPagination.setData(userIPage.getRecords(),userIPage.getTotal());
        }else{
            return this.list(queryWrapper);
        }
    }

    @Override
    public ChargesEntity getInfo(String id){
        QueryWrapper<ChargesEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(ChargesEntity::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(ChargesEntity entity){
        this.save(entity);
    }

    @Override
    public boolean update(String id, ChargesEntity entity){
        entity.setId(id);
        return this.updateById(entity);
    }
    @Override
    public void delete(ChargesEntity entity){
        if(entity!=null){
            this.removeById(entity.getId());
        }
    }
    //子表方法
}

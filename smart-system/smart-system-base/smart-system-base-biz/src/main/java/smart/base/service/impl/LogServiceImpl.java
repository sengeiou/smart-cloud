package smart.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.LogSortEnum;
import smart.base.PaginationTime;
import smart.base.UserInfo;
import smart.base.mapper.LogMapper;
import smart.base.service.LogService;
import smart.base.entity.LogEntity;
import smart.permission.model.user.UserLogForm;
import smart.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 系统日志
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, LogEntity> implements LogService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<LogEntity> getList(int category, PaginationTime paginationTime) {
        UserInfo userInfo = userProvider.get();
        QueryWrapper<LogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LogEntity::getCategory, category);
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationTime.getStartTime() != null ? paginationTime.getStartTime() : null;
        String endTime = paginationTime.getEndTime() != null ? paginationTime.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(LogEntity::getCreatorTime, startTimes).le(LogEntity::getCreatorTime, endTimes);
        }
        //关键字（用户、IP地址、功能名称）
        String keyWord = paginationTime.getKeyword() != null ? paginationTime.getKeyword() : null;
        if (!StringUtils.isEmpty(keyWord)) {
            queryWrapper.lambda().and(
                    t -> t.like(LogEntity::getUserName, keyWord)
                            .or().like(LogEntity::getIPAddress, keyWord)
                            .or().like(LogEntity::getModuleName, keyWord)
            );
        }
        //用户Id
        String userId = userInfo.getUserId() != null ? userInfo.getUserId() : null;
        String userAccount = userInfo.getUserAccount() != null ? userInfo.getUserAccount() : null;
        if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(userAccount)) {
            queryWrapper.lambda().and(
                    t -> t.eq(LogEntity::getUserId, userId)
                            .or().eq(LogEntity::getUserId, userAccount)
            );
        }
        //排序
        if (StringUtils.isEmpty(paginationTime.getSidx())) {
            queryWrapper.lambda().orderByDesc(LogEntity::getCreatorTime);
        } else {
            queryWrapper = "asc".equals(paginationTime.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationTime.getSidx()) : queryWrapper.orderByDesc(paginationTime.getSidx());
        }
        Page<LogEntity> page = new Page<>(paginationTime.getCurrentPage(), paginationTime.getPageSize());
        IPage<LogEntity> userIPage = this.page(page, queryWrapper);
        return paginationTime.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public List<LogEntity> getList(UserLogForm userLogForm) {
        UserInfo userInfo = userProvider.get();
        QueryWrapper<LogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LogEntity::getCategory, userLogForm.getCategory());
        //日期范围（近7天、近1月、近3月、自定义）
        if (!StringUtil.isEmpty(userLogForm.getStartTime()) && !StringUtil.isEmpty(userLogForm.getEndTime())) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(userLogForm.getStartTime())) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(userLogForm.getEndTime())) + " 23:59:59");
            queryWrapper.lambda().ge(LogEntity::getCreatorTime, startTimes).le(LogEntity::getCreatorTime, endTimes);
        }
        //关键字（用户、IP地址、功能名称）
        String keyWord = userLogForm.getKeyword();
        if (!StringUtil.isEmpty(keyWord)) {
            queryWrapper.lambda().and(
                    t -> t.like(LogEntity::getUserName, keyWord)
                            .or().like(LogEntity::getIPAddress, keyWord)
                            .or().like(LogEntity::getModuleName, keyWord)
            );
        }
        //用户Id
        String userId = userInfo.getUserId() != null ? userInfo.getUserId() : null;
        String userAccount = userInfo.getUserAccount() != null ? userInfo.getUserAccount() : null;
        if (!StringUtil.isEmpty(userId) && !StringUtil.isEmpty(userAccount)) {
            queryWrapper.lambda().and(
                    t -> t.eq(LogEntity::getUserId, userId)
                            .or().eq(LogEntity::getUserId, userAccount)
            );
        }
        //排序
        if (StringUtil.isEmpty(userLogForm.getSidx())) {
            queryWrapper.lambda().orderByDesc(LogEntity::getCreatorTime);
        } else {
            queryWrapper = "asc".equals(userLogForm.getSort().toLowerCase()) ? queryWrapper.orderByAsc(userLogForm.getSidx()) : queryWrapper.orderByDesc(userLogForm.getSidx());
        }
        Page<LogEntity> page = new Page<>(userLogForm.getCurrentPage(), userLogForm.getPageSize());
        IPage<LogEntity> userIPage = this.page(page, queryWrapper);
        return userLogForm.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public LogEntity getInfo(String id) {
        QueryWrapper<LogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LogEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(LogEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setIPAddress(entity.getIPAddress() == null ? IpUtil.getIpAddr() : entity.getIPAddress());
        entity.setPlatForm(entity.getPlatForm() == null ? ServletUtil.getUserAgent() : entity.getPlatForm());
        this.save(entity);
    }

    @Override
    @Transactional
    public boolean delete(String[] ids) {
        if (ids.length > 0) {
            QueryWrapper<LogEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(LogEntity::getId, ids);
            return this.remove(queryWrapper);
        }
        return false;
    }

    @Override
    public void WriteLogAsync(String userId, String userName, String abstracts) {
        LogEntity entity = new LogEntity();
        entity.setId(RandomUtil.uuId());
        entity.setUserId(userId);
        entity.setUserName(userName);
        entity.setAbstracts(abstracts);
        entity.setRequestURL(ServletUtil.getServletPath());
        entity.setRequestMethod(ServletUtil.getRequest().getMethod());
        entity.setIPAddress(IpUtil.getIpAddr());
        entity.setPlatForm(ServletUtil.getUserAgent());
        entity.setCategory(LogSortEnum.Login.getCode());
        this.save(entity);
    }

    @Override
    public void WriteLogAsync(LogEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }
}

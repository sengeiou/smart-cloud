package smart.base.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.config.ConfigValueUtil;
import smart.util.JsonUtil;
import smart.util.NoDataSourceBind;
import smart.util.StringUtil;
import smart.base.ActionResult;
import smart.base.PaginationTime;
import smart.base.vo.PaginationVO;
import smart.base.model.logmodel.*;
import smart.base.entity.LogEntity;
import smart.base.service.LogService;
import smart.util.data.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统日志
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "系统日志", value = "Log")
@RestController
@RequestMapping("/Base/Log")
@Slf4j
public class LogController {

    @Autowired
    private LogService logService;
    @Autowired
    private ConfigValueUtil configValueUtil;


    /**
     * 获取系统日志信息
     *
     * @param category 主键值分类 1：登录日志，2.访问日志，3.操作日志，4.异常日志，5.请求日志
     * @return
     */
    @ApiOperation("获取系统日志列表")
    @GetMapping("/{category}")
    public ActionResult getInfoList(@PathVariable("category") String category, PaginationTime paginationTime) {
        if(StringUtil.isEmpty(category)||!StringUtil.isNumeric(category)){
            return ActionResult.fail("获取失败");
        }
        List<LogEntity> list = logService.getList(Integer.parseInt(category),paginationTime);
        PaginationVO paginationVO= JsonUtil.getJsonToBean(paginationTime,PaginationVO.class);
        int i=Integer.parseInt(category);
        switch (i){
            case 1:
                List<LoginLogVO> loginLogVOList= JsonUtil.getJsonToList(list,LoginLogVO.class);
                return ActionResult.page(loginLogVOList,paginationVO);
            case 4:
                List<ErrorLogVO> errorLogVOList= JsonUtil.getJsonToList(list,ErrorLogVO.class);
                return ActionResult.page(errorLogVOList,paginationVO);
            case 5:
                List<RequestLogVO> requestLogVOList= JsonUtil.getJsonToList(list,RequestLogVO.class);
                return ActionResult.page(requestLogVOList,paginationVO);
            default:
                return ActionResult.fail("获取失败");
        }
    }

    /**
     * 保存日志
     * @param logCrFrom
     * @return
     */
    @ApiOperation("保存系统日志")
    @PostMapping
    public void create(@RequestBody LogCrFrom logCrFrom){
        LogEntity entity = JsonUtil.getJsonToBean(logCrFrom, LogEntity.class);
        logService.create(entity);
    }

    /**
     * 批量删除系统日志
     *
     * @return
     */
    @ApiOperation("批量删除系统日志")
    @DeleteMapping
    public ActionResult delete(@RequestBody LogDelForm logDelForm) {
        boolean flag = logService.delete(logDelForm.getIds());
        if (flag == false) {
            return ActionResult.fail("删除失败，数据不存在");
        }
        return ActionResult.success("删除成功");
    }

    /**
     * 写入日志
     * @param userId
     * @param userName
     * @param abstracts
     */
    @NoDataSourceBind
    @PostMapping("/writeLogAsync/{dbId}/{dbName}/{userId}/{userName}/{account}/{abstracts}")
    public void writeLogAsync(@PathVariable("dbId") String dbId, @PathVariable("dbName") String dbName,@PathVariable("userId") String userId, @PathVariable("userName") String userName,@PathVariable("account") String account, @PathVariable("abstracts") String abstracts) {
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            DataSourceContextHolder.setDatasource(dbId, dbName);
        }
        try{
            logService.WriteLogAsync(userId,userName+"/"+account,abstracts);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    /**
     * 写入请求日志
     */
    @PostMapping("/writeLogRequest")
    public void writeLogRequest(@RequestBody LogEntity logEntity) {
        try{
            logService.save(logEntity);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

}

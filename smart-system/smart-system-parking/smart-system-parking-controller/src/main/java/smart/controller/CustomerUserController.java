package smart.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import smart.base.service.BillRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.base.vo.PageListVO;
import smart.base.vo.PaginationVO;
import smart.base.UserInfo;
import smart.base.vo.DownloadVO;
import smart.config.ConfigValueUtil;
import smart.exception.DataException;
import org.springframework.transaction.annotation.Transactional;
import smart.base.entity.ProvinceEntity;
import smart.model.customeruser.CustomerUserPaginationExportModel;
import smart.model.customeruser.CustomerUserPagination;
import smart.permission.entity.UserEntity;
import smart.permission.service.UserService;
import smart.model.customeruser.CustomerUserCrForm;
import smart.model.customeruser.CustomerUserInfoVO;
import smart.model.customeruser.CustomerUserListVO;
import smart.model.customeruser.CustomerUserUpForm;
import smart.entity.*;
import smart.base.service.ProvinceService;
import smart.util.*;
import smart.base.util.*;
import smart.base.vo.ListVO;
import smart.util.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smart.entity.CustomerUserEntity;
import smart.service.CustomerUserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


/**
 * 车主用户
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-17 10:02:02
 */
@Slf4j
@RestController
@Api(tags = "车主用户", description = "customer")
@RequestMapping("/CustomerUser")
public class CustomerUserController {
    @Autowired
    private GeneraterSwapUtil generaterSwapUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private UserProvider userProvider;

    @Autowired
    private CustomerUserService customerUserService;


    /**
     * 列表
     *
     * @param customerUserPagination
     * @return
     */
    @GetMapping
    public ActionResult list(CustomerUserPagination customerUserPagination) throws IOException {
        List<CustomerUserEntity> list = customerUserService.getList(customerUserPagination);
        //处理id字段转名称，若无需转或者为空可删除

        for (CustomerUserEntity entity : list) {
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<CustomerUserListVO> listVO = JsonUtil.getJsonToList(list, CustomerUserListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(customerUserPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }


    /**
     * 创建
     *
     * @param customerUserCrForm
     * @return
     */
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid CustomerUserCrForm customerUserCrForm) throws DataException {
        UserInfo userInfo = userProvider.get();
        customerUserCrForm.setFollowtime(DateUtil.getNow());
        customerUserCrForm.setUnfollowtime(DateUtil.getNow());
        customerUserCrForm.setCreatortime(DateUtil.getNow());
        CustomerUserEntity entity = JsonUtil.getJsonToBean(customerUserCrForm, CustomerUserEntity.class);
        entity.setId(RandomUtil.uuId());
        customerUserService.create(entity);
        return ActionResult.success("新建成功");
    }


    /**
     * 模板下载
     *
     * @return
     */
    @ApiOperation("模板下载")
    @GetMapping("/templateDownload")
    public ActionResult<DownloadVO> TemplateDownload() {
        UserInfo userInfo = userProvider.get();
        DownloadVO vo = DownloadVO.builder().build();
        try {
            vo.setName("职员信息.xlsx");
            vo.setUrl(UploaderUtil.uploaderFile("/api/Common/DownloadModel?encryption=", userInfo.getId() + "#" + "职员信息.xlsx" + "#" + "Temporary"));
        } catch (Exception e) {
            log.error("信息导出Excel错误:{}", e.getMessage());
        }
        return ActionResult.success(vo);
    }

    /**
     * 导出Excel
     *
     * @return
     */
    @ApiOperation("导出Excel")
    @GetMapping("/Actions/Export")
    public ActionResult Export(CustomerUserPaginationExportModel customerUserPaginationExportModel) throws IOException {
        CustomerUserPagination customerUserPagination = JsonUtil.getJsonToBean(customerUserPaginationExportModel, CustomerUserPagination.class);
        List<CustomerUserEntity> list = customerUserService.getTypeList(customerUserPagination, customerUserPaginationExportModel.getDataType());
        //处理id字段转名称，若无需转或者为空可删除

        for (CustomerUserEntity entity : list) {
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<CustomerUserListVO> listVO = JsonUtil.getJsonToList(list, CustomerUserListVO.class);
        //转换为map输出
        List<Map<String, Object>> mapList = JsonUtil.getJsonToListMap(JsonUtil.getObjectToStringDateFormat(listVO, "yyyy-MM-dd HH:mm:ss"));
        String[] keys = !StringUtil.isEmpty(customerUserPaginationExportModel.getSelectKey()) ? customerUserPaginationExportModel.getSelectKey().split(",") : new String[0];
        UserInfo userInfo = userProvider.get();
        DownloadVO vo = creatModelExcel(configValueUtil.getTemporaryFilePath(), mapList, keys, userInfo);
        return ActionResult.success(vo);
    }

    //导出表格
    public static DownloadVO creatModelExcel(String path, List<Map<String, Object>> list, String[] keys, UserInfo userInfo) {
        DownloadVO vo = DownloadVO.builder().build();
        try {
            List<ExcelExportEntity> entitys = new ArrayList<>();
            if (keys.length > 0) {
                for (String key : keys) {
                    switch (key) {
                        case "username":
                            entitys.add(new ExcelExportEntity("用户", "username"));
                            break;
                        case "nickname":
                            entitys.add(new ExcelExportEntity("昵称", "nickname"));
                            break;
                        case "mobile":
                            entitys.add(new ExcelExportEntity("手机号", "mobile"));
                            break;
                        case "walletbalance":
                            entitys.add(new ExcelExportEntity("钱包余额", "walletbalance"));
                            break;
                        case "gender":
                            entitys.add(new ExcelExportEntity("性别", "gender"));
                            break;
                        case "isfollow":
                            entitys.add(new ExcelExportEntity("是否关注公众号", "isfollow"));
                            break;
                        case "followtime":
                            entitys.add(new ExcelExportEntity("关注时间", "followtime"));
                            break;
                        case "unfollowtime":
                            entitys.add(new ExcelExportEntity("取关时间", "unfollowtime"));
                            break;
                        case "openidsmall":
                            entitys.add(new ExcelExportEntity("小程序openid", "openidsmall"));
                            break;
                        case "openidpublic":
                            entitys.add(new ExcelExportEntity("公众号openid", "openidpublic"));
                            break;
                        case "country":
                            entitys.add(new ExcelExportEntity("所在国家", "country"));
                            break;
                        case "province":
                            entitys.add(new ExcelExportEntity("省份", "province"));
                            break;
                        case "city":
                            entitys.add(new ExcelExportEntity("城市", "city"));
                            break;
                        case "usertype":
                            entitys.add(new ExcelExportEntity("用户类型", "usertype"));
                            break;
                        case "registsource":
                            entitys.add(new ExcelExportEntity("注册来源", "registsource"));
                            break;
                        case "creatortime":
                            entitys.add(new ExcelExportEntity("创建时间", "creatortime"));
                            break;
                        case "lastmodifytime":
                            entitys.add(new ExcelExportEntity("修改时间", "lastmodifytime"));
                            break;
                        case "lastmodifyuserid":
                            entitys.add(new ExcelExportEntity("修改用户", "lastmodifyuserid"));
                            break;
                        default:
                            break;
                    }
                }
            }
            ExportParams exportParams = new ExportParams(null, "表单信息");
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entitys, list);
            String fileName = "表单信息" + DateUtil.dateNow("yyyyMMddHHmmss") + ".xls";
            vo.setName(fileName);
            vo.setUrl(UploaderUtil.uploaderFile(userInfo.getId() + "#" + fileName + "#" + "Temporary"));
            path = path + fileName;
            FileOutputStream fos = new FileOutputStream(path);
            workbook.write(fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vo;
    }


    /**
     * 信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ActionResult<CustomerUserInfoVO> info(@PathVariable("id") String id) {
        CustomerUserEntity entity = customerUserService.getInfo(id);
        CustomerUserInfoVO vo = JsonUtil.getJsonToBean(entity, CustomerUserInfoVO.class);
        if (vo.getFollowtime() != null) {
            vo.setFollowtime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getFollowtime())));
        }
        if (vo.getUnfollowtime() != null) {
            vo.setUnfollowtime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getUnfollowtime())));
        }
        if (vo.getCreatortime() != null) {
            vo.setCreatortime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getCreatortime())));
        }
        if (vo.getLastmodifytime() != null) {
            vo.setLastmodifytime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getLastmodifytime())));
        }
        UserEntity userEntity = userService.getInfo(entity.getLastmodifyuserid());
        if (userEntity != null) {
            vo.setLastmodifyuserid(userEntity.getRealName() + "/" + userEntity.getAccount());
        }
        return ActionResult.success(vo);
    }


    /**
     * 更新
     *
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    @Transactional
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid CustomerUserUpForm customerUserUpForm) throws DataException {
        CustomerUserEntity entity = customerUserService.getInfo(id);
        if (entity != null) {
            customerUserService.delete(entity);
            UserInfo userInfo = userProvider.get();
            customerUserUpForm.setFollowtime(DateUtil.dateFormat(entity.getFollowtime()));
            customerUserUpForm.setUnfollowtime(DateUtil.dateFormat(entity.getUnfollowtime()));
            customerUserUpForm.setCreatortime(DateUtil.dateFormat(entity.getCreatortime()));
            customerUserUpForm.setLastmodifytime(DateUtil.dateFormat(new Date()));
            customerUserUpForm.setLastmodifyuserid(userInfo.getUserId());
            entity = JsonUtil.getJsonToBean(customerUserUpForm, CustomerUserEntity.class);
            entity.setId(id);
            customerUserService.create(entity);
            return ActionResult.success("更新成功");
        } else {
            return ActionResult.fail("更新失败，数据不存在");
        }
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ActionResult delete(@PathVariable("id") String id) {
        CustomerUserEntity entity = customerUserService.getInfo(id);
        if (entity != null) {
            customerUserService.delete(entity);
        }
        return ActionResult.success("删除成功");
    }

}

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
import smart.model.parkingarea.ParkingAreaPaginationExportModel;
import smart.model.parkingarea.ParkingAreaPagination;
import smart.permission.entity.UserEntity;
import smart.permission.service.UserService;
import smart.model.parkingarea.ParkingAreaCrForm;
import smart.model.parkingarea.ParkingAreaInfoVO;
import smart.model.parkingarea.ParkingAreaListVO;
import smart.model.parkingarea.ParkingAreaUpForm;
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
import smart.entity.ParkingAreaEntity;
import smart.service.ParkingAreaService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


/**
 * 片区管理
 *
 * @版本： V3.1.0
 * @版权： SmartCloud项目开发组
 * @作者： SmartCloud
 * @日期： 2021-11-15 15:56:54
 */
@Slf4j
@RestController
@Api(tags = "片区管理", description = "system")
@RequestMapping("/ParkingArea")
public class ParkingAreaController {
    @Autowired
    private GeneraterSwapUtil generaterSwapUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;

    @Autowired
    private ParkingAreaService parkingAreaService;


    /**
     * 列表
     *
     * @param parkingAreaPagination
     * @return
     */
    @GetMapping
    public ActionResult list(ParkingAreaPagination parkingAreaPagination) throws IOException {
        List<ParkingAreaEntity> list = parkingAreaService.getList(parkingAreaPagination);
        //处理id字段转名称，若无需转或者为空可删除

        for (ParkingAreaEntity entity : list) {
            entity.setContactuserid(generaterSwapUtil.userSelectValues(entity.getContactuserid()));
            entity.setCreatoruserid(generaterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<String> arrayFieldList = new ArrayList<>();
        arrayFieldList.add("contactuserid");
        List<Map<String, Object>> mapListVO = JsonUtil.getJsonToListMap(JsonUtil.getObjectToString(list));
        List<Map<String, Object>> newMapListVO = new ArrayList<>();
        for (Map<String, Object> dataMap : mapListVO) {
            for (String field : arrayFieldList) {
                if (dataMap.get(field) != null) {
                    dataMap.put(field, dataMap.get(field).toString().split(","));
                }
            }
            newMapListVO.add(dataMap);
        }
        List<ParkingAreaListVO> listVO = JsonUtil.getJsonToList(JsonUtil.getObjectToString(newMapListVO), ParkingAreaListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(parkingAreaPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }


    /**
     * 创建
     *
     * @param parkingAreaCrForm
     * @return
     */
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid ParkingAreaCrForm parkingAreaCrForm) throws DataException {
        UserInfo userInfo = userProvider.get();
        parkingAreaCrForm.setCreatortime(DateUtil.getNow());
        parkingAreaCrForm.setCreatoruserid(userInfo.getUserId());
        ParkingAreaEntity entity = JsonUtil.getJsonToBean(parkingAreaCrForm, ParkingAreaEntity.class);
        entity.setId(RandomUtil.uuId());
        parkingAreaService.create(entity);
        return ActionResult.success("新建成功");
    }


    /**
     * 信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ActionResult<ParkingAreaInfoVO> info(@PathVariable("id") String id) {
        ParkingAreaEntity entity = parkingAreaService.getInfo(id);
        ParkingAreaInfoVO vo = JsonUtil.getJsonToBean(entity, ParkingAreaInfoVO.class);
        if (vo.getCreatortime() != null) {
            vo.setCreatortime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getCreatortime())));
        }
        UserEntity userEntity = userService.getInfo(entity.getCreatoruserid());
        if (userEntity != null) {
            vo.setCreatoruserid(userEntity.getRealName() + "/" + userEntity.getAccount());
        }
        if (vo.getLastmodifytime() != null) {
            vo.setLastmodifytime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getLastmodifytime())));
        }
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
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid ParkingAreaUpForm parkingAreaUpForm) throws DataException {
        ParkingAreaEntity entity = parkingAreaService.getInfo(id);
        if (entity != null) {
            parkingAreaService.delete(entity);
            UserInfo userInfo = userProvider.get();
            parkingAreaUpForm.setCreatortime(DateUtil.dateFormat(entity.getCreatortime()));
            parkingAreaUpForm.setCreatoruserid(entity.getCreatoruserid());
            parkingAreaUpForm.setLastmodifytime(DateUtil.dateFormat(new Date()));
            parkingAreaUpForm.setLastmodifyuserid(userInfo.getUserId());
            entity = JsonUtil.getJsonToBean(parkingAreaUpForm, ParkingAreaEntity.class);
            entity.setId(id);
            parkingAreaService.create(entity);
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
        ParkingAreaEntity entity = parkingAreaService.getInfo(id);
        if (entity != null) {
            parkingAreaService.delete(entity);
        }
        return ActionResult.success("删除成功");
    }

}

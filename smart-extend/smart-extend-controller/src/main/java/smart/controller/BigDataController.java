package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.service.BigDataService;
import smart.entity.BigDataEntity;
import smart.model.bidata.BigBigDataListVO;
import smart.base.ActionResult;
import smart.base.Pagination;
import smart.base.vo.PaginationVO;
import smart.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 大数据测试
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "大数据测试", value = "BigData")
@RestController
@RequestMapping("/BigData")
public class BigDataController {

    @Autowired
    private BigDataService bigDataService;

    /**
     * 列表
     *
     * @param pagination
     * @return
     */
    @ApiOperation("获取大数据测试列表分页")
    @GetMapping
    public ActionResult list(Pagination pagination) {
        List<BigDataEntity> data = bigDataService.getList(pagination);
        List<BigBigDataListVO> list= JsonUtil.getJsonToList(data,BigBigDataListVO.class);
        PaginationVO paginationVO  = JsonUtil.getJsonToBean(pagination,PaginationVO.class);
        return ActionResult.page(list,paginationVO);
    }

    /**
     * 新建
     *
     * @return
     */
    @ApiOperation("添加大数据测试")
    @PostMapping
    public ActionResult create() throws WorkFlowException {
        bigDataService.create(10000);
        return ActionResult.success("新建成功10000条数据");
    }
}

package smart.form.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.vo.ListVO;
import smart.base.vo.PaginationVO;
import smart.emnus.FileTypeEnum;
import smart.engine.enums.FlowStatusEnum;
import smart.file.FileApi;
import smart.util.*;
import smart.base.*;
import smart.base.model.util.DownloadVO;
import smart.form.entity.OrderEntity;
import smart.form.entity.OrderEntryEntity;
import smart.form.entity.OrderReceivableEntity;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.model.order.*;
import smart.permission.UsersApi;
import smart.permission.model.user.UserAllModel;
import smart.form.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */

@Slf4j
@Api(tags = "订单信息", value = "Order")
@RestController
@RequestMapping("//Form/CrmOrder")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UsersApi userService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FileApi fileApi;

    /**
     * 获取订单信息列表（带分页）
     *
     * @param paginationOrder
     * @return
     */
    @ApiOperation("获取订单信息列表")
    @GetMapping
    public ActionResult list(PaginationOrder paginationOrder) {
        List<OrderEntity> data = orderService.getList(paginationOrder);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationOrder, PaginationVO.class);
        List<OrderListVO> listVO = JsonUtil.getJsonToList(data, OrderListVO.class);
        if (listVO.size() > 0) {
            List<UserAllModel> userList = userService.getAll().getData();
            for (OrderListVO order : listVO) {
                UserAllModel user = userList.stream().filter(t -> t.getId().equals(order.getCreatorUserId())).findFirst().orElse(new UserAllModel());
                order.setCreatorUser(user.getRealName() + "/" + user.getAccount());
            }
        }
        return ActionResult.page(listVO, paginationVO);
    }

    /**
     * 获取订单信息子列表（订单明细）
     *
     * @param id 主表Id
     * @return
     */
    @ApiOperation("获取订单信息子列表（订单明细）")
    @GetMapping("/OrderEntry/{id}/Items")
    public ActionResult orderEntryList(@PathVariable("id") String id) {
        List<OrderEntryEntity> data = orderService.getOrderEntryList(id);
        List<OrderEntryListVO> result = JsonUtil.getJsonToList(data, OrderEntryListVO.class);
        ListVO vo = new ListVO();
        vo.setList(result);
        return ActionResult.success(vo);
    }

    /**
     * 获取订单信息子列表（订单收款）
     *
     * @param id 主表Id
     * @return
     */
    @ApiOperation("获取订单列表-收款计划")
    @GetMapping("/{id}/CollectionPlan")
    public ActionResult orderReceivableList(@PathVariable("id") String id) {
        List<OrderReceivableEntity> data = orderService.getOrderReceivableList(id);
        List<OrderReceivableListVO> result = JsonUtil.getJsonToList(data, OrderReceivableListVO.class);
        ListVO vo = new ListVO();
        vo.setList(result);
        return ActionResult.success(vo);
    }

    /**
     * 获取订单信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取订单信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        OrderEntity orderEntity = orderService.getInfo(id);
        List<OrderEntryEntity> orderEntryList = orderService.getOrderEntryList(id);
        List<OrderReceivableEntity> orderReceivableList = orderService.getOrderReceivableList(id);
        List<UserAllModel> userList = userService.getAll().getData();
        OrderInfoVO infoVo = JsonUtilEx.getJsonToBeanEx(orderEntity, OrderInfoVO.class);
        if (infoVo.getCreatorUserId() != null) {
            UserAllModel user = userList.stream().filter(t -> t.getId().equals(infoVo.getCreatorUserId())).findFirst().orElse(new UserAllModel());
            infoVo.setCreatorUserId(user.getRealName() + "/" + user.getAccount());
        }
        if (infoVo.getLastModifyUserId() != null) {
            UserAllModel user = userList.stream().filter(t -> t.getId().equals(infoVo.getLastModifyUserId())).findFirst().orElse(new UserAllModel());
            infoVo.setLastModifyUserId(user.getRealName() + "/" + user.getAccount());
        }
        List<OrderInfoOrderEntryModel> orderEntryModels = JsonUtil.getJsonToList(orderEntryList, OrderInfoOrderEntryModel.class);
        infoVo.setGoodsList(orderEntryModels);
        List<OrderInfoOrderReceivableModel> orderReceivableModels = JsonUtil.getJsonToList(orderReceivableList, OrderInfoOrderReceivableModel.class);
        infoVo.setCollectionPlanList(orderReceivableModels);
        return ActionResult.success(infoVo);
    }

    /**
     * 获取订单信息（前单）
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取订单信息（前单）")
    @GetMapping("/{id}/Actions/Prev")
    public ActionResult prevInfo(@PathVariable("id") String id) throws DataException {
        OrderInfoVO vo = orderService.getInfoVo(id, "prev");
        return ActionResult.success(vo);
    }

    /**
     * 获取订单信息（后单）
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取订单信息（后单）")
    @GetMapping("/{id}/Actions/Next")
    public ActionResult nextInfo(@PathVariable("id") String id) throws DataException {
        OrderInfoVO vo = orderService.getInfoVo(id, "next");
        return ActionResult.success(vo);
    }

    /**
     * 信息导出Excel
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("信息导出Excel")
    @GetMapping("/{id}/Export")
    public ActionResult exportExcel(@PathVariable("id") String id) {
        UserInfo userInfo = userProvider.get();
        List<UserAllModel> userList = userService.getAll().getData();
        OrderEntity orderEntity = orderService.getInfo(id);
        if (orderEntity != null) {
            UserAllModel creatorUserId = userList.stream().filter(t -> t.getId().equals(orderEntity.getCreatorUserId())).findFirst().orElse(new UserAllModel());
            if (creatorUserId.getAccount() != null && creatorUserId.getRealName() != null) {
                orderEntity.setCreatorUserId(creatorUserId.getRealName() + "/" + creatorUserId.getAccount());
            }
            UserAllModel lastModifyUserId = userList.stream().filter(t -> t.getId().equals(String.valueOf(orderEntity.getLastModifyUserId()))).findFirst().orElse(new UserAllModel());
            if (lastModifyUserId.getAccount() != null && lastModifyUserId.getRealName() != null) {
                orderEntity.setLastModifyUserId(creatorUserId.getRealName() + "/" + creatorUserId.getAccount());
            }
            OrderExportModel exportModel = JsonUtil.getJsonToBean(orderEntity, OrderExportModel.class);
            //保存数据
            Map<String, Object> map = new HashMap<>(16);
            map.put("order", JSON.parse(JSONObject.toJSONStringWithDateFormat(exportModel, "yyyy-MM-dd HH:mm:ss")));
            List<OrderEntryEntity> orderEntryList = orderService.getOrderEntryList(id);
            String num = "0";
            BigDecimal qtyNum = new BigDecimal(num);
            BigDecimal amountNum = new BigDecimal(num);
            BigDecimal actualAmountNum = new BigDecimal(num);
            for (OrderEntryEntity entity : orderEntryList) {
                BigDecimal qty = null;
                if (entity.getQty() == null) {
                    qty = new BigDecimal(num);
                } else {
                    qty = entity.getQty();
                }
                qtyNum = qtyNum.add(qty);
                BigDecimal amount = null;
                if (entity.getAmount() == null) {
                    amount = new BigDecimal(num);
                } else {
                    amount = entity.getAmount();
                }
                amountNum = amountNum.add(amount);
                BigDecimal actualAmount;
                if (entity.getActualAmount() == null) {
                    actualAmount = new BigDecimal(num);
                } else {
                    actualAmount = entity.getActualAmount();
                }
                actualAmountNum = actualAmountNum.add(actualAmount);
            }
            OrderEntryEntity entity = new OrderEntryEntity();
            //最后一行合计
            entity.setGoodsName("合计");
            entity.setQty(qtyNum);
            entity.setAmount(amountNum);
            entity.setActualAmount(actualAmountNum);
            orderEntryList.add(entity);
            map.put("orderEntry", orderEntryList);
            //模板
            TemplateExportParams param = new TemplateExportParams(fileApi.getPath(FileTypeEnum.TEMPLATEFILE) + "orderInfo_export_template.xlsx", true);
            Workbook workbook = ExcelExportUtil.exportExcel(param, map);
            String name = "订单信息-" + orderEntity.getOrderCode() + ".xlsx";
            String fileName = fileApi.getPath(FileTypeEnum.TEMPORARY) + name;
            DownloadVO vo = DownloadVO.builder().build();
            try {
                FileOutputStream output = new FileOutputStream(fileName);
                workbook.write(output);
                vo.setName(name);
                vo.setUrl(UploaderUtil.uploaderFile(userInfo.getId() + "#" + name));
            } catch (Exception e) {
                log.error("信息导出Excel错误:{}", e.getMessage());
            }
            return ActionResult.success(vo);
        }
        return ActionResult.fail("未能找到此订单");
    }

    /**
     * 新建订单信息
     *
     * @return
     */
    @ApiOperation("新建订单信息")
    @PostMapping
    public ActionResult create(@RequestBody @Valid OrderForm orderForm) throws WorkFlowException {
        OrderEntity orderEntity = JsonUtil.getJsonToBean(orderForm, OrderEntity.class);
        List<OrderEntryEntity> orderEntryList = JsonUtil.getJsonToList(orderForm.getGoodsList(), OrderEntryEntity.class);
        List<OrderReceivableEntity> orderReceivableList = JsonUtil.getJsonToList(orderForm.getCollectionPlanList(), OrderReceivableEntity.class);
        orderService.create(orderEntity, orderEntryList, orderReceivableList, orderForm);
        String msg = "保存成功";
        if(FlowStatusEnum.submit.getMessage().equals((orderForm.getStatus()))){
            msg = "提交成功，请耐心等待";
        }
        return ActionResult.success(msg);
    }

    /**
     * 更新订单信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新订单信息")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid OrderForm orderForm) throws WorkFlowException {
        OrderEntity orderEntity = JsonUtil.getJsonToBean(orderForm, OrderEntity.class);
        List<OrderEntryEntity> orderEntryList = JsonUtil.getJsonToList(orderForm.getGoodsList(), OrderEntryEntity.class);
        List<OrderReceivableEntity> orderReceivableList = JsonUtil.getJsonToList(orderForm.getCollectionPlanList(), OrderReceivableEntity.class);
        OrderEntity entity = orderService.getInfo(id);
        if (entity != null) {
            orderService.update(id, orderEntity, orderEntryList, orderReceivableList, orderForm);
            String msg = "更新成功";
            if(FlowStatusEnum.submit.getMessage().equals(orderForm.getStatus())){
                msg = "提交成功，请耐心等待";
            }
            return ActionResult.success(msg);
        }
        return ActionResult.success("更新失败，数据不存在");
    }

    /**
     * 删除订单信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除订单信息")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        OrderEntity entity = orderService.getInfo(id);
        if (entity != null) {
            orderService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 获取客户列表
     *
     * @param page 关键字
     * @return
     */
    @ApiOperation("获取客户列表")
    @GetMapping("/Customer")
    public ActionResult customerList(Page page) {
        String json = "[{\"id\":\"8094826d-dfe0-4a05-9023-4d441c304c61\",\"text\":\"广东雪莱特光电科技股份有限公司\",\"code\":\"100000\",\"keyword\":\"GDXLTGDKJGFYXGS,ADXLTGDKJGFYXGS\"},{\"id\":\"b20e965b-9f18-4228-9532-e506caf751e3\",\"text\":\"广州旭之日电子科技有限公司\",\"code\":\"100001\",\"keyword\":\"GZXZRDZKJYXGS,AZXZRDZKJYXGS\"},{\"id\":\"563326c0-ad5f-4221-9ff7-dbf687c4b802\",\"text\":\"东莞市博力威汽车配件有限公司\",\"code\":\"100002\",\"keyword\":\"DWSBLWQJPJYXGS,DWSBLWQCPJYXGS,DGSBLWQJPJYXGS,DGSBLWQCPJYXGS\"},{\"id\":\"b28691a9-acdc-4fdd-9e5f-c2fd04e5a795\",\"text\":\"佛山市恒威汽车动力转向器公司\",\"code\":\"100003\",\"keyword\":\"BSSHWQJDLZXQGS,BSSHWQCDLZXQGS\"},{\"id\":\"37143c66-93a0-4ec2-8625-5dc3f0059e5c\",\"text\":\"广州三锦照明电器公司\",\"code\":\"100004\",\"keyword\":\"GZSJZMDQGS,AZSJZMDQGS\"},{\"id\":\"712a390f-deb7-4d6d-b1bf-cb93b2221306\",\"text\":\"广州鑫正源汽配有限公司\",\"code\":\"100005\",\"keyword\":\"GZXZYQPYXGS,AZXZYQPYXGS\"},{\"id\":\"a10fa05a-7954-4500-a3e9-2b42837a6ee9\",\"text\":\"广州市德霸照明电器有限公司\",\"code\":\"100006\",\"keyword\":\"GZSDBZMDQYXGS,AZSDBZMDQYXGS\"},{\"id\":\"cb715ab8-8940-480a-b1f2-cc61b73ae44b\",\"text\":\"深圳星际服饰有限公司\",\"code\":\"100007\",\"keyword\":\"SZXJFSYXGS\"},{\"id\":\"6c265977-5480-4e3e-82a3-0f71f750f109\",\"text\":\"深圳市博锐纵横科技有限公司\",\"code\":\"100008\",\"keyword\":\"SZSBRZHKJYXGS\"},{\"id\":\"2bbc5699-1968-42d0-b950-39df42b6718f\",\"text\":\"广州融星商贸有限公司\",\"code\":\"100009\",\"keyword\":\"GZRXSMYXGS,AZRXSMYXGS\"},{\"id\":\"64e597d4-0a8e-4bf7-b8d7-64ca8a2e6c20\",\"text\":\"广州顶诚科技电子有限公司\",\"code\":\"100010\",\"keyword\":\"GZDCKJDZYXGS,AZDCKJDZYXGS\"},{\"id\":\"15337f7e-3fff-4403-96b9-eaebbef85534\",\"text\":\"烟台孚瑞克森汽车部件有限公司\",\"code\":\"100011\",\"keyword\":\"YTFRKSQJBJYXGS,YTFRKSQCBJYXGS\"},{\"id\":\"46746ff8-c48b-43c7-a9e9-51a19d1d2499\",\"text\":\"茌平信发铝电集团有限公司\",\"code\":\"100012\",\"keyword\":\"CPXFLDJTYXGS\"},{\"id\":\"6f752d0c-b416-4552-8ef9-5f2136e77689\",\"text\":\"济南澳商贸易有限公司\",\"code\":\"100013\",\"keyword\":\"JNASMYYXGS\"},{\"id\":\"8ab80843-e007-4dd4-8d39-d28f05ba1643\",\"text\":\"济南澳润商贸有限公司\",\"code\":\"100014\",\"keyword\":\"JNARSMYXGS\"},{\"id\":\"2d10551d-6f72-4c00-abd4-8c9856a2aeba\",\"text\":\"山东凯诺汽车配件有限公司\",\"code\":\"100015\",\"keyword\":\"SDKNQJPJYXGS,SDKNQCPJYXGS\"},{\"id\":\"30d89988-00f5-4abc-88ea-592f058c8d89\",\"text\":\"烟台瑞福汽车部件有限公司\",\"code\":\"100016\",\"keyword\":\"YTRFQJBJYXGS,YTRFQCBJYXGS\"},{\"id\":\"812ccd19-f7d5-4c24-9e11-b31bfee50d41\",\"text\":\"茌平日兴达汽车部件有限公司\",\"code\":\"100017\",\"keyword\":\"CPRXDQJBJYXGS,CPRXDQCBJYXGS\"},{\"id\":\"89f05f5f-5ca2-4f59-a41a-abc3a444a77c\",\"text\":\"永正汽车配件有限责任公司\",\"code\":\"100018\",\"keyword\":\"YZQJPJYXZRGS,YZQCPJYXZRGS\"},{\"id\":\"5be62e54-c3cf-4927-a8fc-61e6d6534e24\",\"text\":\"青岛华安正信国际贸易有限公司\",\"code\":\"100019\",\"keyword\":\"QDHAZXGJMYYXGS\"},{\"id\":\"45e990c7-ffc2-4df8-8b83-2af4d4b07fbb\",\"text\":\"广饶中策橡胶有限公司\",\"code\":\"100020\",\"keyword\":\"GRZCXJYXGS,ARZCXJYXGS\"},{\"id\":\"c865c9fb-135e-40b8-bb38-9a909f5c3a8b\",\"text\":\"山东张驰橡胶有限公司\",\"code\":\"100021\",\"keyword\":\"SDZCXJYXGS\"},{\"id\":\"802c13c6-a89c-4351-98a6-956f48fd3c72\",\"text\":\"济南易久自动化有限公司\",\"code\":\"100022\",\"keyword\":\"JNYJZDHYXGS\"},{\"id\":\"49ddaa6c-ba14-48af-9782-4c780914cd30\",\"text\":\"山东汇丰汽车配件有限公司\",\"code\":\"100023\",\"keyword\":\"SDHFQJPJYXGS,SDHFQCPJYXGS\"},{\"id\":\"179ac5c2-6ea3-4b7e-994f-0e6d846394d6\",\"text\":\"烟台浩阳机械制造有限公司\",\"code\":\"100024\",\"keyword\":\"YTHYJXZZYXGS\"},{\"id\":\"2a8d22e0-97a5-49e4-8929-f850d4e207c4\",\"text\":\"山东跃恒轮胎化工有限公司\",\"code\":\"100025\",\"keyword\":\"SDYHLTHGYXGS\"},{\"id\":\"8ade7cd2-59f0-435d-a5ed-6e5c47566dab\",\"text\":\"东营博瑞制动系统有限公司\",\"code\":\"100026\",\"keyword\":\"DYBRZDXTYXGS,DYBRZDJTYXGS\"},{\"id\":\"37b52c55-85c1-4a8a-acb7-217231d21404\",\"text\":\"泰安玥欣工贸有限公司\",\"code\":\"100027\",\"keyword\":\"TAYXGMYXGS\"},{\"id\":\"145b9423-bcda-45c9-ab9c-ecda841a5f97\",\"text\":\"山东世途轮胎有限公司\",\"code\":\"100028\",\"keyword\":\"SDSTLTYXGS\"},{\"id\":\"53e40ffa-b566-4280-bac0-70839df36ae9\",\"text\":\"山东世通轮胎有限公司\",\"code\":\"100029\",\"keyword\":\"SDSTLTYXGS\"},{\"id\":\"aabc07b9-a067-4a6d-8979-8bacf09b6601\",\"text\":\"龙口海盟机械有限公司\",\"code\":\"100030\",\"keyword\":\"LKHMJXYXGS\"},{\"id\":\"b01061f5-fc4b-4a6c-8c9d-e74bbc6d65f0\",\"text\":\"枣庄市正邦新型建材有限公司\",\"code\":\"100031\",\"keyword\":\"ZZSZBXXJCYXGS\"},{\"id\":\"0f6dc939-632f-4318-9887-831aadf2f016\",\"text\":\"济南宏岺汽车配件公司\",\"code\":\"100032\",\"keyword\":\"JNHLQJPJGS,JNHLQCPJGS\"},{\"id\":\"44134ad0-84ca-47e5-bdb2-a90701d3b526\",\"text\":\"山东富华汽车配件公司\",\"code\":\"100033\",\"keyword\":\"SDFHQJPJGS,SDFHQCPJGS\"},{\"id\":\"123b6bac-7638-457b-a920-83cc0fa0c2dc\",\"text\":\"聊城贝尔汽车散热器有限公司\",\"code\":\"100034\",\"keyword\":\"LCBEQJSRQYXGS,LCBEQCSRQYXGS\"},{\"id\":\"7b3ee427-6785-4a55-a6e2-8b92c3c756ec\",\"text\":\"枣庄市正邦新型建材有限公司\",\"code\":\"100035\",\"keyword\":\"ZZSZBXXJCYXGS\"},{\"id\":\"abef7a36-c1aa-4324-893e-5ee0cf12ba06\",\"text\":\"山东军泰化工有限公司\",\"code\":\"100036\",\"keyword\":\"SDJTHGYXGS\"},{\"id\":\"579f7530-c82d-4eea-b98d-7ac27db0d92c\",\"text\":\"山东鑫月昶汽车配件有限公司\",\"code\":\"100037\",\"keyword\":\"SDXYCQJPJYXGS,SDXYCQCPJYXGS\"},{\"id\":\"12e0e23d-170d-4924-8088-915796555737\",\"text\":\"山东宏马工程机械有限公司\",\"code\":\"100038\",\"keyword\":\"SDHMGCJXYXGS\"},{\"id\":\"047a68cb-2a57-47f0-ab04-5781147a8888\",\"text\":\"青岛祥荣轮胎有限公司\",\"code\":\"100039\",\"keyword\":\"QDXRLTYXGS\"},{\"id\":\"61129e0d-1b4c-4947-b78b-6ac80de557ca\",\"text\":\"斯伯塔克轮胎集团有限公司\",\"code\":\"100040\",\"keyword\":\"SBTKLTJTYXGS,SBDKLTJTYXGS\"},{\"id\":\"87676528-a2c6-46fc-922f-3854ac7b1e3b\",\"text\":\"东营信义汇丰汽车配件有限公司\",\"code\":\"100041\",\"keyword\":\"DYXYHFQJPJYXGS,DYXYHFQCPJYXGS\"},{\"id\":\"63105380-d09b-4506-baa3-6212d8ac95f2\",\"text\":\"青岛征和工业有限公司\",\"code\":\"100042\",\"keyword\":\"QDZHGYYXGS\"},{\"id\":\"1aecaf71-6623-4992-9f7e-fee68efe61a0\",\"text\":\"上海卓美实业有限公司\",\"code\":\"100043\",\"keyword\":\"SHZMSYYXGS\"},{\"id\":\"61fd0246-fd9c-4fe2-a648-7fe745d2b013\",\"text\":\"上海塞蓝帝国际贸易有限公司\",\"code\":\"100044\",\"keyword\":\"SHSLDGJMYYXGS\"},{\"id\":\"8e26ceca-41d6-42d0-bcc9-500768437d4a\",\"text\":\"上海繁颐国际贸易有限公司\",\"code\":\"100045\",\"keyword\":\"SHPYGJMYYXGS,SHFYGJMYYXGS\"},{\"id\":\"9dd3ed1e-ff4b-4317-b1da-b64fd07e6157\",\"text\":\"六安龙啸工艺品有限公司\",\"code\":\"100046\",\"keyword\":\"LALXGYPYXGS\"},{\"id\":\"f1890c89-fb6c-4736-897d-7dc9c905d7f3\",\"text\":\"六安江澎电器商行\",\"code\":\"100047\",\"keyword\":\"LAJPDQSX,LAJPDQSH\"},{\"id\":\"ffbbe328-488b-4873-bfe3-fbe597716a9b\",\"text\":\"安徽法西欧汽车部件有限公司\",\"code\":\"100048\",\"keyword\":\"AHFXOQJBJYXGS,AHFXOQCBJYXGS\"},{\"id\":\"ff620a66-c80b-4b63-b4bd-ef54d488d67d\",\"text\":\"安徽奥丰汽车配件有限公司\",\"code\":\"100049\",\"keyword\":\"AHAFQJPJYXGS,AHAFQCPJYXGS\"},{\"id\":\"13855122-d6b9-40b1-8415-803ea06ff294\",\"text\":\"瑞安市给力汽车配件有限公司\",\"code\":\"100050\",\"keyword\":\"RASGLQJPJYXGS,RASGLQCPJYXGS,RASJLQJPJYXGS,RASJLQCPJYXGS\"},{\"id\":\"5e9f61c4-f95e-4b12-a462-13310a64fef4\",\"text\":\"温州名邦汽车用品有限公司\",\"code\":\"100051\",\"keyword\":\"WZMBQJYPYXGS,WZMBQCYPYXGS\"},{\"id\":\"34d4627c-5e0b-4e67-936a-da8740743430\",\"text\":\"温州拉凡宝汽车泵业有限公司\",\"code\":\"100052\",\"keyword\":\"WZLFBQJBYYXGS,WZLFBQCBYYXGS\"},{\"id\":\"5c496717-738f-45bc-a613-7a6d6086da9c\",\"text\":\"瑞安科普进出口贸易有限公司\",\"code\":\"100053\",\"keyword\":\"RAKPJCKMYYXGS\"},{\"id\":\"3552401c-90f7-4197-91b6-b8735732884e\",\"text\":\"瑞安中申电器有限公司\",\"code\":\"100054\",\"keyword\":\"RAZSDQYXGS\"},{\"id\":\"2656a62a-52b9-4749-8fa9-ffbf8dc5c940\",\"text\":\"温州金品汽车用品有限公司\",\"code\":\"100055\",\"keyword\":\"WZJPQJYPYXGS,WZJPQCYPYXGS\"},{\"id\":\"a7cd833f-e49e-4496-a29c-215c4ef623be\",\"text\":\"瑞安市力邦科众制动器有限公司\",\"code\":\"100056\",\"keyword\":\"RASLBKZZDQYXGS\"},{\"id\":\"133b902b-12c4-4a00-b238-73a71ae24540\",\"text\":\"宁波广良电器有限公司\",\"code\":\"100057\",\"keyword\":\"NBGLDQYXGS,NBALDQYXGS\"},{\"id\":\"13a19f6b-3d0f-4f17-92f6-8bd3df4ac3d5\",\"text\":\"温州杰峰进出口有限公司\",\"code\":\"100058\",\"keyword\":\"WZJFJCKYXGS\"},{\"id\":\"4a88166a-8b55-40d7-bb22-04dbb1883d34\",\"text\":\"温州科达汽车轴瓦有限公司\",\"code\":\"100059\",\"keyword\":\"WZKDQJZWYXGS,WZKDQCZWYXGS\"},{\"id\":\"22c92533-57af-427c-9b3b-7644dc0a86ef\",\"text\":\"宁波钧乔行汽车配件有限公司\",\"code\":\"100060\",\"keyword\":\"NBJQXQJPJYXGS,NBJQXQCPJYXGS,NBJQHQJPJYXGS,NBJQHQCPJYXGS\"},{\"id\":\"45715bdd-1740-4a30-82c8-d9ef3d05cc5a\",\"text\":\"浙江平柴泵业有限公司\",\"code\":\"100061\",\"keyword\":\"ZJPCBYYXGS\"},{\"id\":\"6a5d46f3-20e4-4a27-b097-3ba6beef7981\",\"text\":\"宁波鑫海爱多雨刷制造有限公司\",\"code\":\"100062\",\"keyword\":\"NBXHADYSZZYXGS\"},{\"id\":\"faadbe66-142b-4d52-8229-0b5239ff0d76\",\"text\":\"浙江亚之星汽车部件有限公司\",\"code\":\"100063\",\"keyword\":\"ZJYZXQJBJYXGS,ZJYZXQCBJYXGS\"},{\"id\":\"f8ed65af-ad33-435d-9f99-d63f04c4a6d0\",\"text\":\"浙江立群汽车配件制造有限公司\",\"code\":\"100064\",\"keyword\":\"ZJLQQJPJZZYXGS,ZJLQQCPJZZYXGS\"},{\"id\":\"46a50622-a4a5-46aa-b307-ca530e77b918\",\"text\":\"浙江林氏汽车零部件有限公司\",\"code\":\"100065\",\"keyword\":\"ZJLZQJLBJYXGS,ZJLZQCLBJYXGS,ZJLSQJLBJYXGS,ZJLSQCLBJYXGS\"},{\"id\":\"6dd6c16c-7a50-4a9b-8e3f-4d5e87091046\",\"text\":\"浙江瑞峰汽车零部件有限公司\",\"code\":\"100066\",\"keyword\":\"ZJRFQJLBJYXGS,ZJRFQCLBJYXGS\"},{\"id\":\"9bfbc91f-2e12-4fca-a6f5-55bf0b21f453\",\"text\":\"温州万豪汽配有限公司\",\"code\":\"100067\",\"keyword\":\"WZWHQPYXGS,WZMHQPYXGS\"},{\"id\":\"c86726b0-345d-479b-99d0-f26ab525b690\",\"text\":\"浙江兰德马克汽车配件有限公司\",\"code\":\"100068\",\"keyword\":\"ZJLDMKQJPJYXGS,ZJLDMKQCPJYXGS\"},{\"id\":\"a7af2dc9-4e11-4452-a897-33b696131de4\",\"text\":\"浙江乐鼎波纹管有限公司\",\"code\":\"100069\",\"keyword\":\"ZJYDBWGYXGS,ZJLDBWGYXGS\"},{\"id\":\"46f82e53-ef5b-4b59-bc0e-7a98d4951784\",\"text\":\"浙江奥凯利汽配有限公司\",\"code\":\"100070\",\"keyword\":\"ZJAKLQPYXGS\"},{\"id\":\"03b372b7-adc9-41c1-8db2-db6da049f0b4\",\"text\":\"宁波昂博电器科技有限公司\",\"code\":\"100071\",\"keyword\":\"NBABDQKJYXGS\"},{\"id\":\"069bb158-2719-40e8-9f3e-ea8d14774a98\",\"text\":\"浙江吉尚汽车部件有限公司\",\"code\":\"100072\",\"keyword\":\"ZJJSQJBJYXGS,ZJJSQCBJYXGS\"},{\"id\":\"4caaf81a-925f-471d-b4a6-a330b5e5835b\",\"text\":\"诸暨市国立机械有限公司\",\"code\":\"100073\",\"keyword\":\"ZJSGLJXYXGS\"},{\"id\":\"a24b6a50-1b17-413e-828b-4107410b2a2e\",\"text\":\"玉环冠丰汽车零部件有限公司\",\"code\":\"100074\",\"keyword\":\"YHGFQJLBJYXGS,YHGFQCLBJYXGS\"},{\"id\":\"cabfe7d7-c164-4444-bff0-b547d5a3b9db\",\"text\":\"武义万龙机械制造有限公司\",\"code\":\"100075\",\"keyword\":\"WYWLJXZZYXGS,WYMLJXZZYXGS\"},{\"id\":\"67142c93-f97c-401c-8079-7ebb8159e838\",\"text\":\"浙江安泰汽车部件有限公司\",\"code\":\"100076\",\"keyword\":\"ZJATQJBJYXGS,ZJATQCBJYXGS\"},{\"id\":\"05b20523-ca83-4072-8ea0-84ea3623e436\",\"text\":\"余姚斯威克电器有限公司\",\"code\":\"100077\",\"keyword\":\"YYSWKDQYXGS\"},{\"id\":\"7baa025e-56b7-48a1-bcd2-0f3c1128016e\",\"text\":\"温州鼎程电子科技有限公司\",\"code\":\"100078\",\"keyword\":\"WZDCDZKJYXGS\"},{\"id\":\"f57b707c-f7fe-4c29-b6e3-a4cc607e11f0\",\"text\":\"临海裕隆汽配有限公司\",\"code\":\"100079\",\"keyword\":\"LHYLQPYXGS\"},{\"id\":\"d07308e7-9139-43f2-8e07-bcd69e74a0e1\",\"text\":\"宁波路卡帝电器有限公司\",\"code\":\"100080\",\"keyword\":\"NBLQDDQYXGS,NBLKDDQYXGS\"},{\"id\":\"512eb341-29e5-49fb-8d61-ba4dcc63245e\",\"text\":\"宁波雷自达电器有限公司\",\"code\":\"100081\",\"keyword\":\"NBLZDDQYXGS\"},{\"id\":\"781c1504-487c-4a7c-bf14-cb47d14a646e\",\"text\":\"温州凯特汽车用品制造厂\",\"code\":\"100082\",\"keyword\":\"WZKTQJYPZZC,WZKTQJYPZZA,WZKTQCYPZZC,WZKTQCYPZZA\"},{\"id\":\"01d7eaa2-0d4e-4e16-985b-45afac5d0538\",\"text\":\"宁波博盛汽车电子有限公司\",\"code\":\"100083\",\"keyword\":\"NBBSQJDZYXGS,NBBSQCDZYXGS,NBBCQJDZYXGS,NBBCQCDZYXGS\"},{\"id\":\"41a31860-be6b-4014-a936-72a221779de6\",\"text\":\"温州欧泰汽车用品制造有限公司\",\"code\":\"100084\",\"keyword\":\"WZOTQJYPZZYXGS,WZOTQCYPZZYXGS\"},{\"id\":\"06ccc046-131b-4691-9341-359f7ae5063e\",\"text\":\"永康市蓝特工贸有限公司\",\"code\":\"100085\",\"keyword\":\"YKSLTGMYXGS\"},{\"id\":\"e46a07e6-0936-4e7a-81cc-9c4ddbd03184\",\"text\":\"宁波行泰商贸有限公司\",\"code\":\"100086\",\"keyword\":\"NBXTSMYXGS,NBHTSMYXGS\"},{\"id\":\"48a9c7ce-d6df-4a44-8d0e-3c88ad3cc54d\",\"text\":\"瑞安市路泰汽摩零部件有限公司\",\"code\":\"100087\",\"keyword\":\"RASLTQMLBJYXGS\"},{\"id\":\"19aaab61-5281-4ac7-b5f4-edc520c89653\",\"text\":\"瑞安宏盛汽配厂\",\"code\":\"100088\",\"keyword\":\"RAHSQPC,RAHSQPA,RAHCQPC,RAHCQPA\"},{\"id\":\"04d60fed-56ec-4418-abe4-1a25929fb7b0\",\"text\":\"诸暨市维琪弹簧有限公司\",\"code\":\"100089\",\"keyword\":\"ZJSWQTHYXGS,ZJSWQDHYXGS\"},{\"id\":\"d6f5eec0-aa1a-4017-ac41-05470d3924a4\",\"text\":\"杭州康功轴承有限公司\",\"code\":\"100090\",\"keyword\":\"HZKGZCYXGS\"},{\"id\":\"4c33ee68-a64a-4454-ae3a-e62a2fe74d25\",\"text\":\"余姚航亿电器有限公司\",\"code\":\"100091\",\"keyword\":\"YYHYDQYXGS\"},{\"id\":\"a039923a-bb38-43f7-9d07-f252b139d9ef\",\"text\":\"浙江天元机电有限公司\",\"code\":\"100092\",\"keyword\":\"ZJTYJDYXGS\"},{\"id\":\"0617174b-b904-496a-a179-027504ef3699\",\"text\":\"瑞安市剑达汽车配件有限公司\",\"code\":\"100093\",\"keyword\":\"RASJDQJPJYXGS,RASJDQCPJYXGS\"},{\"id\":\"82364f9a-f9d8-4451-896b-85a2bcbe6902\",\"text\":\"台州华帅汽配有限公司\",\"code\":\"100094\",\"keyword\":\"TZHSQPYXGS\"},{\"id\":\"568872cb-66b8-43fc-a64d-3554186f0c4b\",\"text\":\"浙江铭泰汽车零部件有限公司\",\"code\":\"100095\",\"keyword\":\"ZJMTQJLBJYXGS,ZJMTQCLBJYXGS\"},{\"id\":\"8111675d-7449-4445-b0a1-14602f14ab77\",\"text\":\"浙江戈尔德汽车部件有限公司\",\"code\":\"100096\",\"keyword\":\"ZJGEDQJBJYXGS,ZJGEDQCBJYXGS\"},{\"id\":\"3a6a3be8-4b72-42eb-a872-f3395c81ca1d\",\"text\":\"常州国华电器有限公司\",\"code\":\"100097\",\"keyword\":\"CZGHDQYXGS\"},{\"id\":\"73f61385-1674-4dda-ac8e-950a886c5410\",\"text\":\"瑞安兴润贸易有限公司\",\"code\":\"100098\",\"keyword\":\"RAXRMYYXGS\"},{\"id\":\"1ee6531c-725f-4f44-b37d-8ad787c4922c\",\"text\":\"绍兴明博汽车配件有限公司\",\"code\":\"100099\",\"keyword\":\"SXMBQJPJYXGS,SXMBQCPJYXGS\"},{\"id\":\"6ff18edc-b10e-4349-8f3f-ca68f1eceee8\",\"text\":\"温州良子贸易有限公司\",\"code\":\"100100\",\"keyword\":\"WZLZMYYXGS\"},{\"id\":\"f87234e3-7199-4262-be6a-1a8f77524efc\",\"text\":\"宁波力富特牵引机制造有限公司\",\"code\":\"100101\",\"keyword\":\"NBLFTQYJZZYXGS\"},{\"id\":\"799826d9-6f8b-42a0-a713-37b9739b68df\",\"text\":\"宁波市江北名正电机有限公司\",\"code\":\"100102\",\"keyword\":\"NBSJBMZDJYXGS\"},{\"id\":\"84a17072-2047-477a-9b24-67e98f5d2220\",\"text\":\"宁波茂佳国际贸易有限公司\",\"code\":\"100103\",\"keyword\":\"NBMJGJMYYXGS\"},{\"id\":\"224ba569-fdc5-4705-b4e8-d30bcd36b854\",\"text\":\"瑞安市墉下文峰汽摩配厂\",\"code\":\"100104\",\"keyword\":\"RASYXWFQMPC,RASYXWFQMPA\"},{\"id\":\"e8bd46cd-db81-4c5e-b083-d1c05a0034a6\",\"text\":\"诸暨宇诺汽车配件有限公司\",\"code\":\"100105\",\"keyword\":\"ZJYNQJPJYXGS,ZJYNQCPJYXGS\"},{\"id\":\"781dd8cf-711a-4afa-9e22-f5a0a333637e\",\"text\":\"宁波泰和轴承有限公司\",\"code\":\"100106\",\"keyword\":\"NBTHZCYXGS\"},{\"id\":\"9bbc9c86-290b-4687-8a98-449a1a415266\",\"text\":\"东海县兰天汽车车轮厂\",\"code\":\"100107\",\"keyword\":\"DHXLTQJJLC,DHXLTQJJLA,DHXLTQJCLC,DHXLTQJCLA,DHXLTQCJLC,DHXLTQCJLA,DHXLTQCCLC,DHXLTQCCLA\"},{\"id\":\"679c136c-21c9-4ac3-ad7a-db0a613ca24d\",\"text\":\"扬州阿波罗蓄电池有限公司\",\"code\":\"100108\",\"keyword\":\"YZABLXDCYXGS,YZEBLXDCYXGS\"},{\"id\":\"3dd2eb0f-0097-4b52-9165-78e2a8d474e1\",\"text\":\"常州中隆车辆配件有限公司\",\"code\":\"100109\",\"keyword\":\"CZZLJLPJYXGS,CZZLCLPJYXGS\"},{\"id\":\"760becc2-0253-4f81-879f-970f432620d9\",\"text\":\"无锡吉瑞特机械制造有限公司\",\"code\":\"100110\",\"keyword\":\"WXJRTJXZZYXGS,MXJRTJXZZYXGS\"},{\"id\":\"b8bd4d12-2f85-43e8-b891-94866a18e90f\",\"text\":\"金坛市平江电气设备有限公司\",\"code\":\"100111\",\"keyword\":\"JTSPJDQSBYXGS\"},{\"id\":\"df0c341e-03ca-4900-95ae-fbad4e11cf9b\",\"text\":\"江苏精诚动力工程有限公司\",\"code\":\"100112\",\"keyword\":\"JSJCDLGCYXGS\"},{\"id\":\"348a509b-5f69-457e-9e9c-d28a03b2f13e\",\"text\":\"南京卡安汽车配件有限公司\",\"code\":\"100113\",\"keyword\":\"NJQAQJPJYXGS,NJQAQCPJYXGS,NJKAQJPJYXGS,NJKAQCPJYXGS\"},{\"id\":\"e69574d5-b276-4828-b2c0-c1fa7ae5b4b0\",\"text\":\"丹阳市帅达车业有限公司\",\"code\":\"100114\",\"keyword\":\"DYSSDJYYXGS,DYSSDCYYXGS\"},{\"id\":\"0fbec45d-4064-4a89-9c1e-34476d0f9e1d\",\"text\":\"江苏丹阳市超强汽配有限公司\",\"code\":\"100115\",\"keyword\":\"JSDYSCQQPYXGS,JSDYSCJQPYXGS\"},{\"id\":\"f1e15fa6-d4df-45db-9143-89e5bfd89f72\",\"text\":\"常州云昊汽车用品有限公司\",\"code\":\"100116\",\"keyword\":\"CZYHQJYPYXGS,CZYHQCYPYXGS\"},{\"id\":\"85c9d273-68ef-4dc4-8760-a2c303ab7953\",\"text\":\"常州南舜汽车附件厂\",\"code\":\"100117\",\"keyword\":\"CZNSQJFJC,CZNSQJFJA,CZNSQCFJC,CZNSQCFJA\"},{\"id\":\"7f7a474b-8327-40be-9ef2-711ebd53defc\",\"text\":\"常州顺德车业有限公司\",\"code\":\"100118\",\"keyword\":\"CZSDJYYXGS,CZSDCYYXGS\"},{\"id\":\"90fbf581-6eb9-421a-8105-73a396ac1003\",\"text\":\"常州昊翔车辆饰件有限公司\",\"code\":\"100119\",\"keyword\":\"CZHXJLSJYXGS,CZHXCLSJYXGS\"},{\"id\":\"723358eb-6b04-4fb3-876e-65f374666aa3\",\"text\":\"常州乐丰汽车饰件厂\",\"code\":\"100120\",\"keyword\":\"CZYFQJSJC,CZYFQJSJA,CZYFQCSJC,CZYFQCSJA,CZLFQJSJC,CZLFQJSJA,CZLFQCSJC,CZLFQCSJA\"},{\"id\":\"26823b21-030a-44c1-9d6a-84da4d288057\",\"text\":\"丹阳文明塑业有限公司\",\"code\":\"100121\",\"keyword\":\"DYWMSYYXGS\"},{\"id\":\"8f3c1330-d743-43d7-8f13-6b8b78f03f68\",\"text\":\"南京润特科技有限公司\",\"code\":\"100122\",\"keyword\":\"NJRTKJYXGS\"},{\"id\":\"89693dd1-50d8-4bc6-98c6-773e8220df26\",\"text\":\"重庆子堃进出口贸易有限公司\",\"code\":\"100123\",\"keyword\":\"ZQZKJCKMYYXGS,CQZKJCKMYYXGS\"},{\"id\":\"ac55602f-4526-4c09-b36b-3b0b5e5af107\",\"text\":\"石家庄旺正国际贸易有限公司\",\"code\":\"100124\",\"keyword\":\"SJZWZGJMYYXGS,DJZWZGJMYYXGS\"},{\"id\":\"930e0f13-cdf9-4149-8e79-8a9f18282f65\",\"text\":\"河间津华金属缺口有限公司\",\"code\":\"100125\",\"keyword\":\"HJJHJZQKYXGS,HJJHJSQKYXGS\"},{\"id\":\"0b4d11ab-b31a-4883-af56-037a54566ce4\",\"text\":\"河北蓝博汽车部件制造有限公司\",\"code\":\"100126\",\"keyword\":\"HBLBQJBJZZYXGS,HBLBQCBJZZYXGS\"},{\"id\":\"abe09ea0-ea41-4063-91b3-ae231f0b6724\",\"text\":\"献县亚特汽车附件厂\",\"code\":\"100127\",\"keyword\":\"XXYTQJFJC,XXYTQJFJA,XXYTQCFJC,XXYTQCFJA\"},{\"id\":\"ae0a1cc0-6ddf-45b8-82d0-51d6b34ad630\",\"text\":\"河北兴浦汽车制动器有限公司\",\"code\":\"100128\",\"keyword\":\"HBXPQJZDQYXGS,HBXPQCZDQYXGS\"},{\"id\":\"aa9ddf53-e4b8-4e36-8063-6f215e9b9407\",\"text\":\"邢台市龙洋机械制造有限公司\",\"code\":\"100129\",\"keyword\":\"XTSLYJXZZYXGS\"},{\"id\":\"3de2ed7f-8155-40b5-9bb3-b30313712040\",\"text\":\"湖北楚欣汽车销售公司\",\"code\":\"100130\",\"keyword\":\"HBCXQJXSGS,HBCXQCXSGS\"},{\"id\":\"a9ae4dfa-330f-4d1c-b420-d3ba0ee0b6b6\",\"text\":\"湘潭迅东机电科技有限公司\",\"code\":\"100131\",\"keyword\":\"XTXDJDKJYXGS\"},{\"id\":\"e0c48427-33e0-443e-9b03-3c2042d9e249\",\"text\":\"福州瑞利贸易有限公司\",\"code\":\"100132\",\"keyword\":\"FZRLMYYXGS\"},{\"id\":\"25386c99-9abc-432d-b632-efa796796827\",\"text\":\"厦门市鑫美汽车配件有限公司\",\"code\":\"100133\",\"keyword\":\"XMSXMQJPJYXGS,XMSXMQCPJYXGS,SMSXMQJPJYXGS,SMSXMQCPJYXGS\"},{\"id\":\"f94add9c-2910-439a-af5f-0d0174f75ce8\",\"text\":\"北京纳天科技有限公司\",\"code\":\"100134\",\"keyword\":\"BJNTKJYXGS\"},{\"id\":\"8a89e1bf-9389-40ba-8da1-e09376e2a046\",\"text\":\"北京瑞博汽车零部件有限公司\",\"code\":\"100135\",\"keyword\":\"BJRBQJLBJYXGS,BJRBQCLBJYXGS\"},{\"id\":\"75837bc2-6959-46f3-b1bb-5a66022b21ca\",\"text\":\"天津鑫正拓达轴承贸易有限公司\",\"code\":\"100136\",\"keyword\":\"TJXZTDZCMYYXGS\"},{\"id\":\"8e442488-2c8b-4e03-88ce-13ed5845889f\",\"text\":\"中铝国际工程公司\",\"code\":\"100137\",\"keyword\":\"ZLGJGCGS\"},{\"id\":\"be2a1881-89f4-48be-9c26-25fe0359924d\",\"text\":\"中国瑞林工程技术有限公司\",\"code\":\"100138\",\"keyword\":\"ZGRLGCJZYXGS,ZGRLGCJSYXGS\"},{\"id\":\"36fa9f7e-3281-4725-b284-ddf0a2e65bde\",\"text\":\"山东省冶金设计院\",\"code\":\"100139\",\"keyword\":\"SDSYJSJY,SDXYJSJY\"},{\"id\":\"53fed6f4-b67c-451a-a1a0-b0ed986ce519\",\"text\":\"中国核电工程有限公司\",\"code\":\"100140\",\"keyword\":\"ZGHDGCYXGS\"},{\"id\":\"b40ba0e8-dfe9-431d-9da3-73629037f385\",\"text\":\"中国水电顾问集团华东勘测设计院\",\"code\":\"100141\",\"keyword\":\"ZGSDGWJTHDKCSJY\"},{\"id\":\"8fe4d0e0-9b2a-46c8-9a6b-1a1b01d48c4c\",\"text\":\"中国电力顾问集团西北电力设计院\",\"code\":\"100142\",\"keyword\":\"ZGDLGWJTXBDLSJY\"},{\"id\":\"7f8cc56b-47c0-4afe-98a2-2a7956a8ca4f\",\"text\":\"中国电力顾问集团东北电力设计院\",\"code\":\"100143\",\"keyword\":\"ZGDLGWJTDBDLSJY\"},{\"id\":\"a0cac21c-2f3e-4ae0-9895-b03bc196fe3a\",\"text\":\"中国电力顾问集团西南电力设计院\",\"code\":\"100144\",\"keyword\":\"ZGDLGWJTXNDLSJY\"},{\"id\":\"c4a5b6de-caf8-47b9-98fb-ca2f914d59fe\",\"text\":\"中国电力顾问集团华东电力设计院\",\"code\":\"100145\",\"keyword\":\"ZGDLGWJTHDDLSJY\"},{\"id\":\"b617604d-7dc6-48c6-b982-e0601bf0f720\",\"text\":\"现代设计集团上海建筑设计院\",\"code\":\"100146\",\"keyword\":\"XDSJJTSHJZSJY\"},{\"id\":\"d4ec8f1b-099b-46a9-8e55-961ab8c308db\",\"text\":\"现代设计集团上海都市设计院\",\"code\":\"100147\",\"keyword\":\"XDSJJTSHDSSJY\"},{\"id\":\"c3d8769e-ccaf-4675-8e1f-349199a3ac33\",\"text\":\"山东同圆设计集团\",\"code\":\"100148\",\"keyword\":\"SDTYSJJT\"},{\"id\":\"028e4fbf-5b08-4554-8fa0-9c555db9ed9e\",\"text\":\"广西华蓝设计集团\",\"code\":\"100149\",\"keyword\":\"GXHLSJJT,AXHLSJJT\"},{\"id\":\"b42b46a3-26fa-47b7-94e2-12909f6c7bf0\",\"text\":\"济南人防建筑设计院\",\"code\":\"100150\",\"keyword\":\"JNRFJZSJY\"},{\"id\":\"36815670-af42-4756-ae30-b1b1b29e2036\",\"text\":\"北京市园林古建设计院\",\"code\":\"100151\",\"keyword\":\"BJSYLGJSJY\"},{\"id\":\"c029e27a-9741-4a10-a630-459be9b3ed90\",\"text\":\"中铁城市规划设计院\",\"code\":\"100152\",\"keyword\":\"ZTCSGHSJY\"},{\"id\":\"e137e6a7-5a80-4a85-8505-f5dac87902d3\",\"text\":\"四川国恒建筑设计有限公司\",\"code\":\"100153\",\"keyword\":\"SCGHJZSJYXGS\"},{\"id\":\"e03bc67b-5caf-4b2d-9b76-7a2c092d3e5d\",\"text\":\"北京三磊建筑设计有限公司\",\"code\":\"100154\",\"keyword\":\"BJSLJZSJYXGS\"},{\"id\":\"78162e75-475f-4540-a87f-4af3e9ac62c0\",\"text\":\"大连市建筑设计研究院有限公司\",\"code\":\"100155\",\"keyword\":\"DLSJZSJYJYYXGS\"},{\"id\":\"d4b3e726-9f8a-42f2-a4e2-8a2ec73ea065\",\"text\":\"安徽省电力设计院\",\"code\":\"100156\",\"keyword\":\"AHSDLSJY,AHXDLSJY\"},{\"id\":\"3c6e4d26-d665-4466-b315-3f7aceda7cf1\",\"text\":\"贵州省交通规划勘察设计研究院股份有限公司\",\"code\":\"100157\",\"keyword\":\"GZSJTGHKCSJYJYGFYXGS,GZXJTGHKCSJYJYGFYXGS\"},{\"id\":\"39778e60-e443-4787-b6c1-e07763725def\",\"text\":\"中交路桥技术有限公司\",\"code\":\"100158\",\"keyword\":\"ZJLQJZYXGS,ZJLQJSYXGS\"},{\"id\":\"5b238085-2c0a-48dc-b82e-09fe1f808575\",\"text\":\"河海大学设计院\",\"code\":\"100159\",\"keyword\":\"HHDXSJY\"},{\"id\":\"6b2e3047-3127-4620-94f2-0c887fc37831\",\"text\":\"重庆市水利电力建筑勘测设计研究院\",\"code\":\"100160\",\"keyword\":\"ZQSSLDLJZKCSJYJY,CQSSLDLJZKCSJYJY\"},{\"id\":\"014065ac-2ebb-465c-ad6e-415ff534d47f\",\"text\":\"神华宁煤能源工程公司\",\"code\":\"100161\",\"keyword\":\"SHNMNYGCGS\"},{\"id\":\"027123ad-94d5-4788-9fbf-138e9472bb0b\",\"text\":\"江西省天驰高速科技发展有限公司\",\"code\":\"100162\",\"keyword\":\"JXSTCGSKJFZYXGS,JXXTCGSKJFZYXGS\"},{\"id\":\"2571e2b5-1c4e-44e4-a700-18828fdc3f50\",\"text\":\"陕西省公路勘察设计研究院\",\"code\":\"100163\",\"keyword\":\"SXSGLKCSJYJY,SXXGLKCSJYJY\"},{\"id\":\"0dcfdd5b-2c0a-4272-93e6-e5d2b4989da6\",\"text\":\"大连市市政设计研究院有限公司\",\"code\":\"100164\",\"keyword\":\"DLSSZSJYJYYXGS\"},{\"id\":\"cd112ba0-abe0-46b6-b8e3-b2bdde2b028d\",\"text\":\"济南市政设计院集团有限公司\",\"code\":\"100165\",\"keyword\":\"JNSZSJYJTYXGS\"},{\"id\":\"5b734763-2085-4fd0-ba4e-16c08c3db4f9\",\"text\":\"天津华淼工程设计有限公司\",\"code\":\"100166\",\"keyword\":\"TJHMGCSJYXGS\"},{\"id\":\"5f3dd251-3df4-4b15-8be6-7171d99589a9\",\"text\":\"福州城建设计院\",\"code\":\"100167\",\"keyword\":\"FZCJSJY\"},{\"id\":\"070d83fa-65fd-4bde-a58a-1abff0dc3fd1\",\"text\":\"上海浦东新区规划设计院\",\"code\":\"100168\",\"keyword\":\"SHPDXQGHSJY,SHPDXOGHSJY\"},{\"id\":\"ea0ce956-24b3-4286-83be-f3cf9d12927b\",\"text\":\"厦门市政设计院有限公司\",\"code\":\"100169\",\"keyword\":\"XMSZSJYYXGS,SMSZSJYYXGS\"},{\"id\":\"7811d748-fa8e-4624-aa42-089c479872ea\",\"text\":\"长春城乡规划设计研究院\",\"code\":\"100170\",\"keyword\":\"ZCCXGHSJYJY,CCCXGHSJYJY\"},{\"id\":\"3a33c63f-4cda-4a78-b2a0-f5f64c9c2e6b\",\"text\":\"太原理工大学建筑设计研究院\",\"code\":\"100171\",\"keyword\":\"TYLGDXJZSJYJY\"},{\"id\":\"b25a5046-f6eb-4d6c-9cf4-28f8b15efa50\",\"text\":\"云南省交通规划设计院\",\"code\":\"100172\",\"keyword\":\"YNSJTGHSJY,YNXJTGHSJY\"},{\"id\":\"3999746f-8a66-4878-ab60-56ebdc7b7e4d\",\"text\":\"新疆石油勘察设计研究院（有限公司）\",\"code\":\"100173\",\"keyword\":\"XJSYKCSJYJYYXGS,XJDYKCSJYJYYXGS\"},{\"id\":\"910efec6-d547-43e8-8ed3-432893dba5a9\",\"text\":\"天津电力设计院\",\"code\":\"100174\",\"keyword\":\"TJDLSJY\"},{\"id\":\"193b480f-2e20-4af4-96d4-42368f3ad500\",\"text\":\"中国联合工程公司\",\"code\":\"100175\",\"keyword\":\"ZGLHGCGS,ZGLGGCGS\"},{\"id\":\"01f2cf7c-8eeb-4373-9c1d-f4755262ea00\",\"text\":\"机械第一设计研究院\",\"code\":\"100176\",\"keyword\":\"JXDYSJYJY\"},{\"id\":\"dde2ceae-8f99-4a15-92af-15b83dff4d6c\",\"text\":\"中交公路规划设计院\",\"code\":\"100177\",\"keyword\":\"ZJGLGHSJY\"},{\"id\":\"6ce05827-8237-4ede-9d1f-b2bd997e5f4e\",\"text\":\"中交第一公路设计院\",\"code\":\"100178\",\"keyword\":\"ZJDYGLSJY\"},{\"id\":\"05dcec72-d731-4c32-87f8-077783c9e5c9\",\"text\":\"中交水运规划设计院\",\"code\":\"100179\",\"keyword\":\"ZJSYGHSJY\"},{\"id\":\"0eb8ed9f-33e3-499c-b1af-88770a394733\",\"text\":\"中交第一航务工程勘察设计院\",\"code\":\"100180\",\"keyword\":\"ZJDYHWGCKCSJY\"},{\"id\":\"de9eb0f8-4bf3-4e83-b34f-58d280f82a54\",\"text\":\"江苏省交通规划设计院\",\"code\":\"100181\",\"keyword\":\"JSSJTGHSJY,JSXJTGHSJY\"},{\"id\":\"42976dad-b069-415e-9b82-eb82687a0337\",\"text\":\"中铁第四勘察设计院\",\"code\":\"100182\",\"keyword\":\"ZTDSKCSJY\"},{\"id\":\"f69a0aa8-eacd-4c0e-80e9-2a86b810c2e7\",\"text\":\"中煤西安设计工程公司\",\"code\":\"100183\",\"keyword\":\"ZMXASJGCGS\"},{\"id\":\"c86e5f6e-e520-479b-aeab-9d8ccf6df582\",\"text\":\"煤炭工业济南设计院\",\"code\":\"100184\",\"keyword\":\"MTGYJNSJY\"},{\"id\":\"389f6b7c-d9e6-40cf-bcde-441a59d4e39f\",\"text\":\"长江勘测规划设计研究院\",\"code\":\"100185\",\"keyword\":\"ZJKCGHSJYJY,CJKCGHSJYJY\"},{\"id\":\"347c8975-6572-48af-9853-93f13b719e0c\",\"text\":\"浙江省水利电力勘测设计院\",\"code\":\"100186\",\"keyword\":\"ZJSSLDLKCSJY,ZJXSLDLKCSJY\"},{\"id\":\"e1e1d8f9-230c-4eaf-86fd-34f044182147\",\"text\":\"中石化胜利油田设计院\",\"code\":\"100187\",\"keyword\":\"ZSHSLYTSJY,ZDHSLYTSJY\"},{\"id\":\"565f7b52-5b3c-423d-ba27-2b8db5bb1bc0\",\"text\":\"浙江天正工程设计公司\",\"code\":\"100188\",\"keyword\":\"ZJTZGCSJGS\"},{\"id\":\"6328e535-035f-4b86-ad1e-975d2c7bd71d\",\"text\":\"中国市政西北设计院\",\"code\":\"100189\",\"keyword\":\"ZGSZXBSJY\"},{\"id\":\"c5ea1162-19df-496b-b5a5-f7b793e6236c\",\"text\":\"中国医药联合工程公司\",\"code\":\"100190\",\"keyword\":\"ZGYYLHGCGS,ZGYYLGGCGS\"},{\"id\":\"fdc00925-0259-4b52-9580-14d9f7a974a6\",\"text\":\"上海杰作设计有限公司（方案公司）\",\"code\":\"100191\",\"keyword\":\"SHJZSJYXGSFAGS\"},{\"id\":\"9e73316c-6d78-4fa6-a468-9718b102aee2\",\"text\":\"哈尔滨市城市规划设计院\",\"code\":\"100192\",\"keyword\":\"HEBSCSGHSJY\"},{\"id\":\"77085a8a-7ed6-4838-9669-9c2f925f8c1d\",\"text\":\"北京玉龙石化工程有限公司\",\"code\":\"100193\",\"keyword\":\"BJYLSHGCYXGS,BJYLDHGCYXGS\"},{\"id\":\"8c3c4f29-b6b3-4381-acf3-5dfe82e4d626\",\"text\":\"长岭炼化岳阳工程设计有限公司\",\"code\":\"100194\",\"keyword\":\"ZLLHYYGCSJYXGS,CLLHYYGCSJYXGS\"},{\"id\":\"f16b42f9-62c7-4c0c-bde6-70ae9f40ca34\",\"text\":\"沈阳测绘勘测设计研究院\",\"code\":\"100195\",\"keyword\":\"SYCHKCSJYJY,CYCHKCSJYJY\"},{\"id\":\"bb615453-1bc4-44e0-afa0-6e93b28f7c82\",\"text\":\"合肥勘察测绘设计院\",\"code\":\"100196\",\"keyword\":\"HFKCCHSJY,GFKCCHSJY\"},{\"id\":\"39ff5412-5966-4a6e-9e87-433ee28dd4bb\",\"text\":\"贵阳铝镁设计院\",\"code\":\"100197\",\"keyword\":\"GYLMSJY\"},{\"id\":\"0dd735fe-2839-4363-a17d-f3282f1895c4\",\"text\":\"中国船舶及海洋工程设计研究院\",\"code\":\"100198\",\"keyword\":\"ZGCBJHYGCSJYJY\"},{\"id\":\"14808a17-cb67-4443-be92-2ece27501797\",\"text\":\"青岛市人防建筑设计研究院\",\"code\":\"100199\",\"keyword\":\"QDSRFJZSJYJY\"},{\"id\":\"41dd4bee-cd7d-413b-bbe4-55cba9e88aa6\",\"text\":\"中国舰船研究设计中心701研究所\",\"code\":\"100200\",\"keyword\":\"ZGJCYJSJZXYJS\"},{\"id\":\"14935fff-d00f-4dfb-97c6-bda5e6a38a9d\",\"text\":\"中国有色金属长沙勘察设计研究院\",\"code\":\"100201\",\"keyword\":\"ZGYSJZZSKCSJYJY,ZGYSJZCSKCSJYJY,ZGYSJSZSKCSJYJY,ZGYSJSCSKCSJYJY\"},{\"id\":\"39d07d25-3d56-4029-b0e6-32ce913e51a7\",\"text\":\"昆明诚信勘察设计院\",\"code\":\"100202\",\"keyword\":\"KMCXKCSJY\"},{\"id\":\"96e6e579-fb95-4b36-9345-28e6e8ff9821\",\"text\":\"中冶建研设计院环保事业部\",\"code\":\"100203\",\"keyword\":\"ZYJYSJYHBSYB\"},{\"id\":\"e89d7d36-985f-4cba-9e19-d0976b15be95\",\"text\":\"福建省建筑设计研究院\",\"code\":\"100204\",\"keyword\":\"FJSJZSJYJY,FJXJZSJYJY\"},{\"id\":\"452efea8-13b8-43c2-8ef4-7a263184f766\",\"text\":\"四川海辰工程设计院有限公司\",\"code\":\"100205\",\"keyword\":\"SCHCGCSJYYXGS\"},{\"id\":\"0e27510e-e430-4f75-a058-cd0dfa15672c\",\"text\":\"中冶建筑设计研究总院\",\"code\":\"100206\",\"keyword\":\"ZYJZSJYJZY\"},{\"id\":\"1fb79091-4f4d-44ee-9cfa-3bf5d9945407\",\"text\":\"中南建筑设计院股份有限公司 \",\"code\":\"100207\",\"keyword\":\"ZNJZSJYGFYXGS\"},{\"id\":\"af2fb3c8-c51f-4cb2-b2d0-367d52308e11\",\"text\":\"云南怡成建筑设计公司\",\"code\":\"100208\",\"keyword\":\"YNYCJZSJGS\"},{\"id\":\"7962e095-0e8b-4c2d-9df2-6998d53d9337\",\"text\":\"浙江华坤建筑设计院有限公司\",\"code\":\"100209\",\"keyword\":\"ZJHKJZSJYYXGS\"},{\"id\":\"c61406ed-0fd5-4df5-bf3b-8d0fca20e726\",\"text\":\"唐山铭嘉建筑设计咨询有限公司\",\"code\":\"100210\",\"keyword\":\"TSMJJZSJZXYXGS\"},{\"id\":\"aa207db4-6530-4ede-9599-61464967888b\",\"text\":\"河南省电力勘测设计院\",\"code\":\"100211\",\"keyword\":\"HNSDLKCSJY,HNXDLKCSJY\"},{\"id\":\"1209412d-95bb-4c40-845d-28ba5634ffdd\",\"text\":\"河北省电力勘测设计院\",\"code\":\"100212\",\"keyword\":\"HBSDLKCSJY,HBXDLKCSJY\"}]";
        List<Map<String, Object>> data = JsonUtil.getJsonToListMap(json);
        if (!StringUtils.isEmpty(page.getKeyword())) {
            data = data.stream().filter(t -> String.valueOf(t.get("code")).contains(page.getKeyword()) || String.valueOf(t.get("keyword")).contains(page.getKeyword()) || String.valueOf(t.get("text")).contains(page.getKeyword())).collect(Collectors.toList());
        }
        data = data.stream().limit(10).collect(Collectors.toList());
        List<OrderCustomerVO> list = JsonUtil.getJsonToList(data, OrderCustomerVO.class);
        ListVO vo = new ListVO();
        vo.setList(list);
        return ActionResult.success(vo);
    }

    /**
     * 获取商品列表
     *
     * @param page 关键字
     * @return
     */
    @ApiOperation("获取商品列表")
    @GetMapping("/Goods")
    public ActionResult goodsList(Page page) {
        String json = "[{\"id\":\"692110120107\",\"code\":\"106423\",\"text\":\"蜡笔小新棒棒冰\",\"specifications\":\"85g\",\"unit\":\"支\",\"price\":\"0.81\",\"keyword\":\"ZBXXBBB,LBXXBBB\"},{\"id\":\"692364427858\",\"code\":\"974498\",\"text\":\"蒙牛纯甄酸牛奶原味\",\"specifications\":\"200g\",\"unit\":\"盒\",\"price\":\"4.46\",\"keyword\":\"MNCZSNNYW\"},{\"id\":\"690799251257\",\"code\":\"416821\",\"text\":\"伊利安慕希希腊酸奶\",\"specifications\":\"205g\",\"unit\":\"包\",\"price\":\"4.46\",\"keyword\":\"YLAMXXXSN,YLAMXXLSN\"},{\"id\":\"690102819104\",\"code\":\"524868\",\"text\":\"白沙精品香烟\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"7.29\",\"keyword\":\"BSJPXY\"},{\"id\":\"690040452111\",\"code\":\"828691\",\"text\":\"天友新鲜杯大红枣酸奶\",\"specifications\":\"160g\",\"unit\":\"杯\",\"price\":\"2.43\",\"keyword\":\"TYXXBDHZSN,TYXXBDGZSN\"},{\"id\":\"692364422345\",\"code\":\"107961\",\"text\":\"蒙牛纯牛奶\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"2.27\",\"keyword\":\"MNCNN\"},{\"id\":\"690102819349\",\"code\":\"532214\",\"text\":\"芙蓉王翻盖香烟\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"19.44\",\"keyword\":\"FRWFGXY\"},{\"id\":\"690799210027\",\"code\":\"111311\",\"text\":\"伊利纯牛奶\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"2.35\",\"keyword\":\"YLCNN\"},{\"id\":\"692586153106\",\"code\":\"111594\",\"text\":\"旺旺碎冰冰草莓味\",\"specifications\":\"78g\",\"unit\":\"支\",\"price\":\"0.81\",\"keyword\":\"WWSBBCMW\"},{\"id\":\"690103561369\",\"code\":\"469391\",\"text\":\"青岛清醇啤酒8度\",\"specifications\":\"330ml\",\"unit\":\"厅\",\"price\":\"2.03\",\"keyword\":\"QDQCPJD\"},{\"id\":\"694935220140\",\"code\":\"898237\",\"text\":\"雪花清爽8度听装啤酒\",\"specifications\":\"330ml\",\"unit\":\"厅\",\"price\":\"2.35\",\"keyword\":\"XHQSDTZPJ\"},{\"id\":\"693262090005\",\"code\":\"869640\",\"text\":\"徐七二白凉粉\",\"specifications\":\"50g\",\"unit\":\"盒\",\"price\":\"1.62\",\"keyword\":\"XQEBLF\"},{\"id\":\"693139200639\",\"code\":\"449683\",\"text\":\"蓝舰果味啤酒饮料\",\"specifications\":\"320ml\",\"unit\":\"厅\",\"price\":\"1.62\",\"keyword\":\"LJGWPJYL\"},{\"id\":\"692416071401\",\"code\":\"448472\",\"text\":\"无穷农场盐焗鸡蛋\",\"specifications\":\"30g\",\"unit\":\"盒\",\"price\":\"1.62\",\"keyword\":\"WQNCYJJD,MQNCYJJD\"},{\"id\":\"692586153183\",\"code\":\"111593\",\"text\":\"旺旺碎冰冰乳酸原味\",\"specifications\":\"78g\",\"unit\":\"支\",\"price\":\"0.81\",\"keyword\":\"WWSBBRSYW\"},{\"id\":\"692586153114\",\"code\":\"111595\",\"text\":\"旺旺碎冰冰葡萄味\",\"specifications\":\"78g\",\"unit\":\"支\",\"price\":\"0.81\",\"keyword\":\"WWSBBPTW\"},{\"id\":\"693273131006\",\"code\":\"473563\",\"text\":\"北京蓝氏经典菠萝啤\",\"specifications\":\"320ml\",\"unit\":\"厅\",\"price\":\"1.62\",\"keyword\":\"BJLZJDBLP,BJLSJDBLP\"},{\"id\":\"690103520063\",\"code\":\"493758\",\"text\":\"青岛崂山啤酒8度\",\"specifications\":\"330ml\",\"unit\":\"厅\",\"price\":\"1.62\",\"keyword\":\"QDLSPJD\"},{\"id\":\"692116850925\",\"code\":\"111576\",\"text\":\"农夫山泉\",\"specifications\":\"550ml\",\"unit\":\"瓶\",\"price\":\"1.46\",\"keyword\":\"NFSQ\"},{\"id\":\"692586153117\",\"code\":\"111598\",\"text\":\"旺旺碎冰冰菠萝\",\"specifications\":\"78g\",\"unit\":\"支\",\"price\":\"0.81\",\"keyword\":\"WWSBBBL\"},{\"id\":\"692250782275\",\"code\":\"466655\",\"text\":\"陈克明鸡蛋精制挂面\",\"specifications\":\"750g\",\"unit\":\"包\",\"price\":\"5.27\",\"keyword\":\"CKMJDJZGM\"},{\"id\":\"690102819101\",\"code\":\"107319\",\"text\":\"白沙软包香烟\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"4.05\",\"keyword\":\"BSRBXY\"},{\"id\":\"693686921537\",\"code\":\"465668\",\"text\":\"味芝元卤鸭掌\",\"specifications\":\"32g\",\"unit\":\"包\",\"price\":\"1.62\",\"keyword\":\"WZYLYZ\"},{\"id\":\"692293923162\",\"code\":\"570758\",\"text\":\"旺旺碎碎冰可乐味\",\"specifications\":\"78g\",\"unit\":\"支\",\"price\":\"0.81\",\"keyword\":\"WWSSBKYW,WWSSBKLW\"},{\"id\":\"690799251205\",\"code\":\"896808\",\"text\":\"伊利果粒优酸乳草莓味\",\"specifications\":\"245ml\",\"unit\":\"盒\",\"price\":\"2.43\",\"keyword\":\"YLGLYSRCMW\"},{\"id\":\"692364426850\",\"code\":\"781761\",\"text\":\"蒙牛真果粒草莓味\",\"specifications\":\"250g\",\"unit\":\"盒\",\"price\":\"2.84\",\"keyword\":\"MNZGLCMW\"},{\"id\":\"690289023413\",\"code\":\"860522\",\"text\":\"双汇香辣热狗肠\",\"specifications\":\"35g\",\"unit\":\"支\",\"price\":\"0.81\",\"keyword\":\"SHXLRGC\"},{\"id\":\"690289023416\",\"code\":\"860521\",\"text\":\"双汇玉米热狗肠\",\"specifications\":\"40g\",\"unit\":\"支\",\"price\":\"0.81\",\"keyword\":\"SHYMRGC\"},{\"id\":\"692586153116\",\"code\":\"111597\",\"text\":\"旺旺碎冰冰柑桔味\",\"specifications\":\"78g\",\"unit\":\"支\",\"price\":\"0.81\",\"keyword\":\"WWSBBGJW\"},{\"id\":\"694896010156\",\"code\":\"866442\",\"text\":\"哈尔滨冰爽啤酒\",\"specifications\":\"330ml\",\"unit\":\"瓶\",\"price\":\"2.27\",\"keyword\":\"HEBBSPJ\"},{\"id\":\"692586153182\",\"code\":\"111592\",\"text\":\"旺旺碎冰冰桃子味\",\"specifications\":\"78g\",\"unit\":\"支\",\"price\":\"0.81\",\"keyword\":\"WWSBBTZW\"},{\"id\":\"695476743038\",\"code\":\"898085\",\"text\":\"可口可乐雪碧\",\"specifications\":\"330ml\",\"unit\":\"厅\",\"price\":\"2.03\",\"keyword\":\"KKKYXB,KKKLXB\"},{\"id\":\"690102822742\",\"code\":\"464956\",\"text\":\"龙凤呈祥（新朝天门)\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"8.10\",\"keyword\":\"LFCXXZTM,LFCXXCTM\"},{\"id\":\"695056110013\",\"code\":\"465775\",\"text\":\"迪怩司脆脆冰水蜜桃味\",\"specifications\":\"82ml\",\"unit\":\"支\",\"price\":\"0.81\",\"keyword\":\"DNSCCBSMTW\"},{\"id\":\"690799251355\",\"code\":\"468959\",\"text\":\"伊利安慕希希腊酸奶香草味\",\"specifications\":\"205g\",\"unit\":\"盒\",\"price\":\"4.46\",\"keyword\":\"YLAMXXXSNXCW,YLAMXXLSNXCW\"},{\"id\":\"690128599121\",\"code\":\"807160\",\"text\":\"怡宝饮用纯净水\",\"specifications\":\"555ml\",\"unit\":\"瓶\",\"price\":\"1.46\",\"keyword\":\"YBYYCJS\"},{\"id\":\"690442290629\",\"code\":\"490952\",\"text\":\"科迪原生牛奶\",\"specifications\":\"180ml\",\"unit\":\"包\",\"price\":\"2.43\",\"keyword\":\"KDYSNN\"},{\"id\":\"695476741038\",\"code\":\"898083\",\"text\":\"可口可乐\",\"specifications\":\"330ml\",\"unit\":\"厅\",\"price\":\"2.03\",\"keyword\":\"KKKY,KKKL\"},{\"id\":\"694443701605\",\"code\":\"418138\",\"text\":\"小样乳酸菌乳原味\",\"specifications\":\"4*100ml\",\"unit\":\"板\",\"price\":\"6.48\",\"keyword\":\"XYRSJRYW\"},{\"id\":\"692481080131\",\"code\":\"827499\",\"text\":\"卡士调味鲜酪乳原味\",\"specifications\":\"120g\",\"unit\":\"杯\",\"price\":\"2.84\",\"keyword\":\"QSTWXLRYW,QSDWXLRYW,KSTWXLRYW,KSDWXLRYW\"},{\"id\":\"690152428238\",\"code\":\"532431\",\"text\":\"津威强化锌酸奶\",\"specifications\":\"95ml\",\"unit\":\"板\",\"price\":\"3.65\",\"keyword\":\"JWQHXSN,JWJHXSN\"},{\"id\":\"690799251285\",\"code\":\"438652\",\"text\":\"伊利安慕希蓝莓味金属包\",\"specifications\":\"205g\",\"unit\":\"盒\",\"price\":\"4.70\",\"keyword\":\"YLAMXLMWJZB,YLAMXLMWJSB\"},{\"id\":\"692364428269\",\"code\":\"480106\",\"text\":\"蒙牛纯甄风味酸奶牛奶芝士味\",\"specifications\":\"200g\",\"unit\":\"盒\",\"price\":\"4.86\",\"keyword\":\"MNCZFWSNNNZSW\"},{\"id\":\"690208389864\",\"code\":\"469458\",\"text\":\"娃哈哈桂圆莲子八宝粥\",\"specifications\":\"280g\",\"unit\":\"瓶\",\"price\":\"3.24\",\"keyword\":\"WHHGYLZBBZ,WHHGYLZBBY\"},{\"id\":\"690102807576\",\"code\":\"524873\",\"text\":\"中华翻盖香烟\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"36.45\",\"keyword\":\"ZHFGXY\"},{\"id\":\"692364428357\",\"code\":\"485055\",\"text\":\"蒙牛真果粒蓝莓\",\"specifications\":\"\",\"unit\":\"盒\",\"price\":\"2.84\",\"keyword\":\"MNZGLLM\"},{\"id\":\"692777090182\",\"code\":\"868462\",\"text\":\"山花草莓杯酸\",\"specifications\":\"100g\",\"unit\":\"杯\",\"price\":\"1.62\",\"keyword\":\"SHCMBS\"},{\"id\":\"695606400042\",\"code\":\"458194\",\"text\":\"雪天绿色加碘精制深井矿盐\",\"specifications\":\"400g\",\"unit\":\"盒\",\"price\":\"1.62\",\"keyword\":\"XTLSJDJZSJKY\"},{\"id\":\"692777090069\",\"code\":\"532429\",\"text\":\"山花纯牛奶\",\"specifications\":\"220ml\",\"unit\":\"包\",\"price\":\"2.03\",\"keyword\":\"SHCNN\"},{\"id\":\"690208388108\",\"code\":\"106964\",\"text\":\"娃哈哈AD钙\",\"specifications\":\"220g\",\"unit\":\"排\",\"price\":\"5.67\",\"keyword\":\"WHHG\"},{\"id\":\"692777090181\",\"code\":\"868460\",\"text\":\"山花原味杯酸\",\"specifications\":\"100g\",\"unit\":\"杯\",\"price\":\"1.62\",\"keyword\":\"SHYWBS\"},{\"id\":\"695636733868\",\"code\":\"916213\",\"text\":\"王老吉凉茶\",\"specifications\":\"310ml\",\"unit\":\"灌\",\"price\":\"3.24\",\"keyword\":\"WLJLC\"},{\"id\":\"690152413833\",\"code\":\"871920\",\"text\":\"津威彩装乳酸菌饮品\",\"specifications\":\"100ml\",\"unit\":\"排\",\"price\":\"6.89\",\"keyword\":\"JWCZRSJYP\"},{\"id\":\"692356788010\",\"code\":\"917231\",\"text\":\"咪咪是味条\",\"specifications\":\"20g\",\"unit\":\"包\",\"price\":\"0.41\",\"keyword\":\"MMSWT\"},{\"id\":\"692586153122\",\"code\":\"845220\",\"text\":\"旺旺碎碎冰家庭号\",\"specifications\":\"85ml*8\",\"unit\":\"袋\",\"price\":\"6.48\",\"keyword\":\"WWSSBJTH\"},{\"id\":\"690799251139\",\"code\":\"862009\",\"text\":\"伊利红谷礼盒苗条装\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"2.43\",\"keyword\":\"YLHYLHMTZ,YLHGLHMTZ,YLGYLHMTZ,YLGGLHMTZ\"},{\"id\":\"697046974031\",\"code\":\"491631\",\"text\":\"盐小厨加碘食盐绿袋\",\"specifications\":\"400g\",\"unit\":\"包\",\"price\":\"1.62\",\"keyword\":\"YXCJDSYLD\"},{\"id\":\"694791590801\",\"code\":\"825860\",\"text\":\"小号购物袋\",\"specifications\":\"24*40\",\"unit\":\"个\",\"price\":\"0.16\",\"keyword\":\"XHGWD\"},{\"id\":\"693359561616\",\"code\":\"906227\",\"text\":\"恭兵合装豆干\",\"specifications\":\"25g\",\"unit\":\"包\",\"price\":\"0.81\",\"keyword\":\"GBHZDG,GBGZDG\"},{\"id\":\"692364426851\",\"code\":\"781762\",\"text\":\"蒙牛真果粒黄桃味\",\"specifications\":\"250g\",\"unit\":\"盒\",\"price\":\"2.84\",\"keyword\":\"MNZGLHTW\"},{\"id\":\"695606400043\",\"code\":\"460279\",\"text\":\"雪天海藻碘盐\",\"specifications\":\"320g\",\"unit\":\"包\",\"price\":\"2.03\",\"keyword\":\"XTHZDY\"},{\"id\":\"693686770007\",\"code\":\"446585\",\"text\":\"土豆粉\",\"specifications\":\"\",\"unit\":\"袋\",\"price\":\"2.03\",\"keyword\":\"TDF\"},{\"id\":\"693466508765\",\"code\":\"856955\",\"text\":\"蒙牛优益C活性乳酸菌原味\",\"specifications\":\"340ml\",\"unit\":\"瓶\",\"price\":\"5.27\",\"keyword\":\"MNYYHXRSJYW\"},{\"id\":\"690799210001\",\"code\":\"106924\",\"text\":\"伊利优酸乳草莓味\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"1.62\",\"keyword\":\"YLYSRCMW\"},{\"id\":\"692364424293\",\"code\":\"106906\",\"text\":\"蒙牛酸酸乳草莓味\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"1.46\",\"keyword\":\"MNSSRCMW\"},{\"id\":\"690102819102\",\"code\":\"107278\",\"text\":\"白沙翻盖白沙\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"4.46\",\"keyword\":\"BSFGBS\"},{\"id\":\"694791590802\",\"code\":\"825861\",\"text\":\"中号购物袋\",\"specifications\":\"30*50\",\"unit\":\"个\",\"price\":\"0.16\",\"keyword\":\"ZHGWD\"},{\"id\":\"690102803974\",\"code\":\"860194\",\"text\":\"贵烟(硬黄精品)\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"10.13\",\"keyword\":\"GYYHJP\"},{\"id\":\"693472810060\",\"code\":\"862349\",\"text\":\"福太子石锅剁椒鱼条\",\"specifications\":\"15g\",\"unit\":\"包\",\"price\":\"0.81\",\"keyword\":\"FTZSGDJYT,FTZDGDJYT\"},{\"id\":\"695010380149\",\"code\":\"947876\",\"text\":\"丝美乐青花系列3层软抽\",\"specifications\":\"402张\",\"unit\":\"包\",\"price\":\"3.24\",\"keyword\":\"SMYQHXLCRC,SMYQHJLCRC,SMLQHXLCRC,SMLQHJLCRC\"},{\"id\":\"690799251195\",\"code\":\"896814\",\"text\":\"伊利QQ星营养果汁酸奶草莓味\",\"specifications\":\"200ml\",\"unit\":\"瓶\",\"price\":\"2.43\",\"keyword\":\"YLXYYGZSNCMW\"},{\"id\":\"693472810059\",\"code\":\"862351\",\"text\":\"福太子老坛山椒鱼条\",\"specifications\":\"15g\",\"unit\":\"包\",\"price\":\"0.81\",\"keyword\":\"FTZLTSJYT\"},{\"id\":\"693472810066\",\"code\":\"862353\",\"text\":\"福太子爽辣鱼条\",\"specifications\":\"15g\",\"unit\":\"包\",\"price\":\"0.81\",\"keyword\":\"FTZSLYT\"},{\"id\":\"694021188960\",\"code\":\"927737\",\"text\":\"养乐多\",\"specifications\":\"5*100ml\",\"unit\":\"排\",\"price\":\"10.37\",\"keyword\":\"YYD,YLD\"},{\"id\":\"693466508645\",\"code\":\"864162\",\"text\":\"蒙牛红枣酸牛奶\",\"specifications\":\"8*100g\",\"unit\":\"条\",\"price\":\"13.61\",\"keyword\":\"MNHZSNN,MNGZSNN\"},{\"id\":\"690799250013\",\"code\":\"107205\",\"text\":\"伊利高钙低脂奶\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"2.51\",\"keyword\":\"YLGGDZN\"},{\"id\":\"692364424292\",\"code\":\"106798\",\"text\":\"蒙牛酸酸乳原味\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"1.46\",\"keyword\":\"MNSSRYW\"},{\"id\":\"690102819385\",\"code\":\"524879\",\"text\":\"芙蓉王极品香烟\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"27.54\",\"keyword\":\"FRWJPXY\"},{\"id\":\"693686921522\",\"code\":\"465670\",\"text\":\"味芝元香辣鱼尾\",\"specifications\":\"32g\",\"unit\":\"包\",\"price\":\"1.62\",\"keyword\":\"WZYXLYY,WZYXLYW\"},{\"id\":\"690799250001\",\"code\":\"106870\",\"text\":\"伊利原味优酸乳饮料\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"1.62\",\"keyword\":\"YLYWYSRYL\"},{\"id\":\"693468857834\",\"code\":\"488594\",\"text\":\"家佳食加碘盐\",\"specifications\":\"350g\",\"unit\":\"袋\",\"price\":\"0.81\",\"keyword\":\"JJSJDY\"},{\"id\":\"690799251258\",\"code\":\"970744\",\"text\":\"伊利味可滋香蕉牛奶\",\"specifications\":\"240ml\",\"unit\":\"盒\",\"price\":\"4.05\",\"keyword\":\"YLWKZXQNN,YLWKZXJNN\"},{\"id\":\"692364425048\",\"code\":\"105698\",\"text\":\"蒙牛草莓果粒酸牛奶\",\"specifications\":\"100g\",\"unit\":\"杯\",\"price\":\"1.81\",\"keyword\":\"MNCMGLSNN\"},{\"id\":\"690799251336\",\"code\":\"469128\",\"text\":\"伊利100%畅意乳酸菌饮品原味\",\"specifications\":\"100ml\",\"unit\":\"排\",\"price\":\"6.48\",\"keyword\":\"YLCYRSJYPYW\"},{\"id\":\"692364426411\",\"code\":\"106702\",\"text\":\"蒙牛早餐核桃味牛奶\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"2.27\",\"keyword\":\"MNZCHTWNN\"},{\"id\":\"693686921523\",\"code\":\"465669\",\"text\":\"味芝元香辣鱼排\",\"specifications\":\"32g\",\"unit\":\"包\",\"price\":\"1.62\",\"keyword\":\"WZYXLYP\"},{\"id\":\"690040452354\",\"code\":\"466499\",\"text\":\"天友纸杯经典原味酸牛奶\",\"specifications\":\"160ml\",\"unit\":\"杯\",\"price\":\"3.24\",\"keyword\":\"TYZBJDYWSNN\"},{\"id\":\"692364426595\",\"code\":\"5454\",\"text\":\"蒙牛利乐砖优智成长奶\",\"specifications\":\"125ml\",\"unit\":\"瓶\",\"price\":\"2.03\",\"keyword\":\"MNLYZYZCZN,MNLYZYZCCN,MNLLZYZCZN,MNLLZYZCCN\"},{\"id\":\"694593067884\",\"code\":\"916757\",\"text\":\"泰香米\",\"specifications\":\"10kg\",\"unit\":\"袋\",\"price\":\"40.42\",\"keyword\":\"TXM\"},{\"id\":\"692245680503\",\"code\":\"659499\",\"text\":\"康师傅矿物质水\",\"specifications\":\"550ml\",\"unit\":\"瓶\",\"price\":\"0.81\",\"keyword\":\"KSFKWZS\"},{\"id\":\"692481080221\",\"code\":\"988651\",\"text\":\"卡士原味鲜酪乳\",\"specifications\":\"100g\",\"unit\":\"杯\",\"price\":\"3.56\",\"keyword\":\"QSYWXLR,KSYWXLR\"},{\"id\":\"692648763781\",\"code\":\"411382\",\"text\":\"属我棒山楂卷\",\"specifications\":\"\",\"unit\":\"个\",\"price\":\"1.22\",\"keyword\":\"ZWBSZJ,ZWBSCJ,SWBSZJ,SWBSCJ\"},{\"id\":\"695476747057\",\"code\":\"898096\",\"text\":\"可口可乐冰露饮用水\",\"specifications\":\"550ml\",\"unit\":\"瓶\",\"price\":\"0.41\",\"keyword\":\"KKKYBLYYS,KKKLBLYYS\"},{\"id\":\"690799251217\",\"code\":\"896809\",\"text\":\"伊利优酸乳果粒酸奶芒果味\",\"specifications\":\"245g\",\"unit\":\"盒\",\"price\":\"2.43\",\"keyword\":\"YLYSRGLSNWGW,YLYSRGLSNMGW\"},{\"id\":\"692777090043\",\"code\":\"557926\",\"text\":\"山花利乐砖纯牛奶\",\"specifications\":\"243ml\",\"unit\":\"盒\",\"price\":\"2.35\",\"keyword\":\"SHLYZCNN,SHLLZCNN\"},{\"id\":\"690799210355\",\"code\":\"480157\",\"text\":\"伊利红枣16连杯\",\"specifications\":\"100g*16\",\"unit\":\"条\",\"price\":\"24.14\",\"keyword\":\"YLHZLB,YLGZLB\"},{\"id\":\"690799251045\",\"code\":\"821001\",\"text\":\"伊利儿童全聪型成长奶\",\"specifications\":\"190ml\",\"unit\":\"盒\",\"price\":\"2.92\",\"keyword\":\"YLETQCXCZN,YLETQCXCCN\"},{\"id\":\"690799251321\",\"code\":\"467896\",\"text\":\"伊利QQ儿童风味酸奶\",\"specifications\":\"205g\",\"unit\":\"盒\",\"price\":\"4.46\",\"keyword\":\"YLETFWSN\"},{\"id\":\"690102819692\",\"code\":\"740635\",\"text\":\"白沙和天下烟\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"81.00\",\"keyword\":\"BSHTXY\"},{\"id\":\"692180470079\",\"code\":\"761202\",\"text\":\"陶华碧老干妈风味辣子鸡\",\"specifications\":\"280g\",\"unit\":\"瓶\",\"price\":\"7.21\",\"keyword\":\"YHBLGMFWLZJ,THBLGMFWLZJ\"},{\"id\":\"692364426849\",\"code\":\"781760\",\"text\":\"蒙牛真果粒芦荟味\",\"specifications\":\"250g\",\"unit\":\"盒\",\"price\":\"2.84\",\"keyword\":\"MNZGLLHW\"},{\"id\":\"692777090184\",\"code\":\"868464\",\"text\":\"山花菠萝杯酸\",\"specifications\":\"100g\",\"unit\":\"杯\",\"price\":\"1.62\",\"keyword\":\"SHBLBS\"},{\"id\":\"693466509111\",\"code\":\"471536\",\"text\":\"蒙牛风味酸牛奶原味\",\"specifications\":\"1*8*100g\",\"unit\":\"条\",\"price\":\"10.45\",\"keyword\":\"MNFWSNNYW\"},{\"id\":\"692180470064\",\"code\":\"526219\",\"text\":\"陶华碧老干妈香辣菜\",\"specifications\":\"80g\",\"unit\":\"包\",\"price\":\"1.54\",\"keyword\":\"YHBLGMXLC,THBLGMXLC\"},{\"id\":\"690799251293\",\"code\":\"439194\",\"text\":\"伊利谷粒多颗粒燕麦牛奶\",\"specifications\":\"200ml\",\"unit\":\"瓶\",\"price\":\"3.24\",\"keyword\":\"YLYLDKLYMNN,YLGLDKLYMNN\"},{\"id\":\"489159933839\",\"code\":\"840384\",\"text\":\"加多宝凉茶\",\"specifications\":\"310ml\",\"unit\":\"厅\",\"price\":\"2.84\",\"keyword\":\"JDBLC\"},{\"id\":\"695084919153\",\"code\":\"496658\",\"text\":\"竹纯量贩家庭装10包装\",\"specifications\":\"10包装\",\"unit\":\"提\",\"price\":\"7.21\",\"keyword\":\"ZCLFJTZBZ\"},{\"id\":\"692020288888\",\"code\":\"107087\",\"text\":\"红牛\",\"specifications\":\"250ml\",\"unit\":\"厅\",\"price\":\"4.78\",\"keyword\":\"HN,GN\"},{\"id\":\"692364426848\",\"code\":\"781758\",\"text\":\"蒙牛真果粒椰果味\",\"specifications\":\"250g\",\"unit\":\"盒\",\"price\":\"2.84\",\"keyword\":\"MNZGLYGW\"},{\"id\":\"692226644473\",\"code\":\"871631\",\"text\":\"清风无芯长卷纸\",\"specifications\":\"160g\",\"unit\":\"提\",\"price\":\"16.93\",\"keyword\":\"QFWXZJZ,QFWXCJZ,QFMXZJZ,QFMXCJZ\"},{\"id\":\"690128599124\",\"code\":\"819864\",\"text\":\"怡宝纯净水\",\"specifications\":\"350ml\",\"unit\":\"瓶\",\"price\":\"1.22\",\"keyword\":\"YBCJS\"},{\"id\":\"692364424074\",\"code\":\"105679\",\"text\":\"蒙牛黄桃杯酸奶\",\"specifications\":\"100g\",\"unit\":\"杯\",\"price\":\"1.81\",\"keyword\":\"MNHTBSN\"},{\"id\":\"695476744148\",\"code\":\"898084\",\"text\":\"可口可乐芬达\",\"specifications\":\"330ml\",\"unit\":\"厅\",\"price\":\"2.03\",\"keyword\":\"KKKYFD,KKKLFD\"},{\"id\":\"692257770418\",\"code\":\"868833\",\"text\":\"君乐宝老酸奶\",\"specifications\":\"139g\",\"unit\":\"杯\",\"price\":\"3.97\",\"keyword\":\"JYBLSN,JLBLSN\"},{\"id\":\"693686921560\",\"code\":\"484363\",\"text\":\"味芝元香辣鸭腿\",\"specifications\":\"38g\",\"unit\":\"袋\",\"price\":\"1.62\",\"keyword\":\"WZYXLYT\"},{\"id\":\"692180470075\",\"code\":\"702596\",\"text\":\"陶华碧老干妈风味豆豉\",\"specifications\":\"280g\",\"unit\":\"瓶\",\"price\":\"6.40\",\"keyword\":\"YHBLGMFWDC,THBLGMFWDC\"},{\"id\":\"692250780650\",\"code\":\"836475\",\"text\":\"陈克明鸡蛋面\",\"specifications\":\"150g\",\"unit\":\"包\",\"price\":\"1.22\",\"keyword\":\"CKMJDM\"},{\"id\":\"694898891870\",\"code\":\"492632\",\"text\":\"喜步佳男式凉拖鞋\",\"specifications\":\"1870\",\"unit\":\"双\",\"price\":\"6.40\",\"keyword\":\"XBJNSLTX\"},{\"id\":\"690102804688\",\"code\":\"532395\",\"text\":\"云烟紫红云烟\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"8.10\",\"keyword\":\"YYZHYY,YYZGYY\"},{\"id\":\"693153490038\",\"code\":\"482836\",\"text\":\"深井盐\",\"specifications\":\"500g\",\"unit\":\"袋\",\"price\":\"1.22\",\"keyword\":\"SJY\"},{\"id\":\"694898891869\",\"code\":\"492631\",\"text\":\"喜步佳女式凉拖鞋\",\"specifications\":\"1869\",\"unit\":\"双\",\"price\":\"6.40\",\"keyword\":\"XBJNSLTX\"},{\"id\":\"690324437095\",\"code\":\"105155\",\"text\":\"七度空间少女装超薄日用\",\"specifications\":\"10片\",\"unit\":\"包\",\"price\":\"6.89\",\"keyword\":\"QDKJSNZCBRY\"},{\"id\":\"690847100470\",\"code\":\"930487\",\"text\":\"养元精研型六个核桃\",\"specifications\":\"240ml\",\"unit\":\"瓶\",\"price\":\"3.24\",\"keyword\":\"YYJYXLGHT\"},{\"id\":\"694791590803\",\"code\":\"825862\",\"text\":\"大号购物袋\",\"specifications\":\"38*60\",\"unit\":\"个\",\"price\":\"0.41\",\"keyword\":\"DHGWD\"},{\"id\":\"692777090183\",\"code\":\"868465\",\"text\":\"山花芦荟杯酸\",\"specifications\":\"100g\",\"unit\":\"杯\",\"price\":\"1.62\",\"keyword\":\"SHLHBS\"},{\"id\":\"695311906083\",\"code\":\"471893\",\"text\":\"雪花金威优选易拉罐\",\"specifications\":\"330ml\",\"unit\":\"罐\",\"price\":\"2.35\",\"keyword\":\"XHJWYXYLG\"},{\"id\":\"691198802529\",\"code\":\"494075\",\"text\":\"达利园豆本豆利乐包原味豆奶\",\"specifications\":\"250ml\",\"unit\":\"袋\",\"price\":\"2.84\",\"keyword\":\"DLYDBDLYBYWDN,DLYDBDLLBYWDN\"},{\"id\":\"695606400059\",\"code\":\"484289\",\"text\":\"雪天精制加碘深井矿盐\",\"specifications\":\"500g\",\"unit\":\"包\",\"price\":\"1.62\",\"keyword\":\"XTJZJDSJKY\"},{\"id\":\"690102807577\",\"code\":\"524934\",\"text\":\"中华软包香烟\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"52.65\",\"keyword\":\"ZHRBXY\"},{\"id\":\"692647520207\",\"code\":\"111617\",\"text\":\"喜之郎原味辣味美好时光海苔\",\"specifications\":\"4.5g\",\"unit\":\"袋\",\"price\":\"3.97\",\"keyword\":\"XZLYWLWMHSGHT\"},{\"id\":\"692231840011\",\"code\":\"806347\",\"text\":\"华辉白凉粉\",\"specifications\":\"50g\",\"unit\":\"盒\",\"price\":\"1.62\",\"keyword\":\"HHBLF\"},{\"id\":\"690040452163\",\"code\":\"860710\",\"text\":\"天友经典老酸奶\",\"specifications\":\"160g\",\"unit\":\"袋\",\"price\":\"4.05\",\"keyword\":\"TYJDLSN\"},{\"id\":\"690324498125\",\"code\":\"118302\",\"text\":\"心相印双层压花餐巾纸\",\"specifications\":\"50张\",\"unit\":\"包\",\"price\":\"1.62\",\"keyword\":\"XXYSCYHCJZ\"},{\"id\":\"695515040031\",\"code\":\"434146\",\"text\":\"圣牧全程有机酸奶\",\"specifications\":\"205g\",\"unit\":\"瓶\",\"price\":\"5.35\",\"keyword\":\"SMQCYJSN\"},{\"id\":\"690799250037\",\"code\":\"107986\",\"text\":\"伊利高钙奶\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"2.51\",\"keyword\":\"YLGGN\"},{\"id\":\"690799251196\",\"code\":\"896815\",\"text\":\"伊利QQ星营养果汁酸奶香蕉味\",\"specifications\":\"200ml\",\"unit\":\"瓶\",\"price\":\"2.43\",\"keyword\":\"YLXYYGZSNXQW,YLXYYGZSNXJW\"},{\"id\":\"692213010110\",\"code\":\"106608\",\"text\":\"太太乐鸡精\",\"specifications\":\"40g\",\"unit\":\"袋\",\"price\":\"1.78\",\"keyword\":\"TTYJJ,TTLJJ\"},{\"id\":\"693427299207\",\"code\":\"469726\",\"text\":\"维邦竹琨卷纸\",\"specifications\":\"1800g\",\"unit\":\"提\",\"price\":\"25.84\",\"keyword\":\"WBZKJZ\"},{\"id\":\"692364424031\",\"code\":\"1072\",\"text\":\"蒙牛高钙低脂奶\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"2.59\",\"keyword\":\"MNGGDZN\"},{\"id\":\"690799251043\",\"code\":\"821003\",\"text\":\"伊利儿童健固型成长奶\",\"specifications\":\"190ml\",\"unit\":\"盒\",\"price\":\"2.92\",\"keyword\":\"YLETJGXCZN,YLETJGXCCN\"},{\"id\":\"694673506009\",\"code\":\"448873\",\"text\":\"华雄荔枝爽\",\"specifications\":\"240g\",\"unit\":\"厅\",\"price\":\"1.62\",\"keyword\":\"HXLZS\"},{\"id\":\"690799210391\",\"code\":\"480147\",\"text\":\"伊利原味发酵乳8连杯特惠装\",\"specifications\":\"8*100g\",\"unit\":\"条\",\"price\":\"10.37\",\"keyword\":\"YLYWFJRLBTHZ\"},{\"id\":\"690289023418\",\"code\":\"862032\",\"text\":\"双汇台式烤肠\",\"specifications\":\"48g\",\"unit\":\"支\",\"price\":\"1.62\",\"keyword\":\"SHTSKC\"},{\"id\":\"692364427215\",\"code\":\"825088\",\"text\":\"蒙牛早餐奶红枣味\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"2.27\",\"keyword\":\"MNZCNHZW,MNZCNGZW\"},{\"id\":\"690040452283\",\"code\":\"934348\",\"text\":\"天友低乳糖酸牛奶\",\"specifications\":\"160g\",\"unit\":\"杯\",\"price\":\"3.24\",\"keyword\":\"TYDRTSNN\"},{\"id\":\"692530758886\",\"code\":\"495342\",\"text\":\"网袋白蒜\",\"specifications\":\"\",\"unit\":\"袋\",\"price\":\"2.43\",\"keyword\":\"WDBS\"},{\"id\":\"691001901115\",\"code\":\"873329\",\"text\":\"雕牌洗衣皂\",\"specifications\":\"2*202g\",\"unit\":\"组\",\"price\":\"5.27\",\"keyword\":\"DPXYZ\"},{\"id\":\"692224181017\",\"code\":\"489962\",\"text\":\"一起旺冰冰家庭号\",\"specifications\":\"78ml*8\",\"unit\":\"包\",\"price\":\"5.59\",\"keyword\":\"YQWBBJTH\"},{\"id\":\"691896200584\",\"code\":\"901215\",\"text\":\"裕湘绿豆面\",\"specifications\":\"900g\",\"unit\":\"包\",\"price\":\"5.59\",\"keyword\":\"YXLDM\"},{\"id\":\"690799251154\",\"code\":\"863552\",\"text\":\"伊利QQ星儿童成长奶营养均膳型\",\"specifications\":\"190ml\",\"unit\":\"盒\",\"price\":\"3.24\",\"keyword\":\"YLXETCZNYYYSX,YLXETCZNYYJSX,YLXETCCNYYYSX,YLXETCCNYYJSX\"},{\"id\":\"692777090198\",\"code\":\"890737\",\"text\":\"山花红枣杯酸奶\",\"specifications\":\"100g\",\"unit\":\"杯\",\"price\":\"1.62\",\"keyword\":\"SHHZBSN,SHGZBSN\"},{\"id\":\"506047840047\",\"code\":\"488292\",\"text\":\"牛轰轰阙宇豪小弟弟吸吸棒\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"1.22\",\"keyword\":\"NHHQYHXDDXXB\"},{\"id\":\"692840952176\",\"code\":\"440493\",\"text\":\"佳益新蓝风8连包\",\"specifications\":\"2880抽\",\"unit\":\"提\",\"price\":\"16.93\",\"keyword\":\"JYXLFLB\"},{\"id\":\"690799251318\",\"code\":\"458554\",\"text\":\"伊利畅意100乳酸菌\",\"specifications\":\"330ml\",\"unit\":\"瓶\",\"price\":\"4.05\",\"keyword\":\"YLCYRSJ\"},{\"id\":\"690107060057\",\"code\":\"826686\",\"text\":\"云南白药牙膏留兰香型\",\"specifications\":\"180g\",\"unit\":\"支\",\"price\":\"27.38\",\"keyword\":\"YNBYYGLLXX\"},{\"id\":\"692225545142\",\"code\":\"870998\",\"text\":\"百岁山矿泉水\",\"specifications\":\"570ml\",\"unit\":\"瓶\",\"price\":\"2.43\",\"keyword\":\"BSSKQS\"},{\"id\":\"692416071354\",\"code\":\"422905\",\"text\":\"无穷香辣烤小腿\",\"specifications\":\"13g\",\"unit\":\"包\",\"price\":\"2.03\",\"keyword\":\"WQXLKXT,MQXLKXT\"},{\"id\":\"692481080080\",\"code\":\"827494\",\"text\":\"卡士鲜酪乳原味\",\"specifications\":\"100g\",\"unit\":\"杯\",\"price\":\"4.05\",\"keyword\":\"QSXLRYW,KSXLRYW\"},{\"id\":\"692530377310\",\"code\":\"756872\",\"text\":\"统一来一桶老坛酸菜牛肉面\",\"specifications\":\"127g\",\"unit\":\"桶\",\"price\":\"3.24\",\"keyword\":\"TYLYTLTSCNRM\"},{\"id\":\"692737000340\",\"code\":\"482398\",\"text\":\"咪咪虾条Q包\",\"specifications\":\"240g\",\"unit\":\"袋\",\"price\":\"8.02\",\"keyword\":\"MMXTB,MMHTB\"},{\"id\":\"692364427188\",\"code\":\"824226\",\"text\":\"蒙牛妙妙乳饮料草莓味\",\"specifications\":\"125ml\",\"unit\":\"盒\",\"price\":\"1.22\",\"keyword\":\"MNMMRYLCMW\"},{\"id\":\"690799210004\",\"code\":\"520760\",\"text\":\"伊利AD钙奶\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"1.62\",\"keyword\":\"YLGN\"},{\"id\":\"690102803691\",\"code\":\"532259\",\"text\":\"黄果树佳品84全硬烤\",\"specifications\":\"\",\"unit\":\"包\",\"price\":\"5.27\",\"keyword\":\"HGSJPQYK\"},{\"id\":\"690799210358\",\"code\":\"480148\",\"text\":\"伊利U型杯红枣\",\"specifications\":\"160g\",\"unit\":\"杯\",\"price\":\"2.43\",\"keyword\":\"YLXBHZ,YLXBGZ\"},{\"id\":\"693686921566\",\"code\":\"484358\",\"text\":\"味芝元酱鸭脖\",\"specifications\":\"30g\",\"unit\":\"袋\",\"price\":\"1.62\",\"keyword\":\"WZYJYB\"},{\"id\":\"692689252205\",\"code\":\"5710\",\"text\":\"银鹭花生牛奶\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"1.62\",\"keyword\":\"YLHSNN\"},{\"id\":\"693816692032\",\"code\":\"807070\",\"text\":\"和丝露苹果醋(无糖)\",\"specifications\":\"488ml\",\"unit\":\"瓶\",\"price\":\"4.05\",\"keyword\":\"HSLPGCWT,HSLPGCMT\"},{\"id\":\"692347610944\",\"code\":\"104624\",\"text\":\"倍加洁超柔弹力护齿牙刷\",\"specifications\":\"\",\"unit\":\"支\",\"price\":\"4.21\",\"keyword\":\"BJJCRTLHCYS,BJJCRDLHCYS\"},{\"id\":\"690324437097\",\"code\":\"118364\",\"text\":\"七度空间纯棉夜用卫生巾\",\"specifications\":\"10片\",\"unit\":\"包\",\"price\":\"8.02\",\"keyword\":\"QDKJCMYYWSJ\"},{\"id\":\"692461580128\",\"code\":\"420954\",\"text\":\"湘盐绿色加碘精制盐\",\"specifications\":\"400g\",\"unit\":\"包\",\"price\":\"0.81\",\"keyword\":\"XYLSJDJZY\"},{\"id\":\"693483413044\",\"code\":\"855270\",\"text\":\"南山老酸奶\",\"specifications\":\"180g\",\"unit\":\"杯\",\"price\":\"4.46\",\"keyword\":\"NSLSN\"},{\"id\":\"694015941002\",\"code\":\"733810\",\"text\":\"百事可乐\",\"specifications\":\"600ml\",\"unit\":\"瓶\",\"price\":\"2.43\",\"keyword\":\"BSKY,BSKL\"},{\"id\":\"489102816445\",\"code\":\"968165\",\"text\":\"维他柠檬茶\",\"specifications\":\"250ml\",\"unit\":\"瓶\",\"price\":\"2.27\",\"keyword\":\"WTNMC\"},{\"id\":\"690799251316\",\"code\":\"438459\",\"text\":\"伊利铁罐金装核桃乳\",\"specifications\":\"240ml\",\"unit\":\"瓶\",\"price\":\"3.65\",\"keyword\":\"YLTGJZHTR\"},{\"id\":\"692364424135\",\"code\":\"106707\",\"text\":\"蒙牛早餐麦香味牛奶\",\"specifications\":\"250ml\",\"unit\":\"盒\",\"price\":\"2.27\",\"keyword\":\"MNZCMXWNN\"},{\"id\":\"691643220001\",\"code\":\"117213\",\"text\":\"长康白醋\",\"specifications\":\"500ml\",\"unit\":\"瓶\",\"price\":\"3.24\",\"keyword\":\"ZKBC,CKBC\"}]";
        List<Map<String, Object>> data = JsonUtil.getJsonToListMap(json);
        if (!StringUtils.isEmpty(page.getKeyword())) {
            data = data.stream().filter(t -> String.valueOf(t.get("code")).contains(page.getKeyword()) || String.valueOf(t.get("keyword")).contains(page.getKeyword()) || String.valueOf(t.get("text")).contains(page.getKeyword())).collect(Collectors.toList());
        }
        List<OrderGoodsVO> list = JsonUtil.getJsonToList(data, OrderGoodsVO.class);
        ListVO vo = new ListVO();
        vo.setList(list);
        return ActionResult.success(vo);
    }
}

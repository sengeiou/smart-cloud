package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.*;
import smart.base.model.dbtable.*;
import smart.base.vo.ListVO;
import smart.base.vo.PaginationVO;
import smart.exception.DataException;
import smart.base.service.DbTableService;
import smart.util.JsonUtil;
import smart.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据建模
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "数据建模", value = "DataModel")
@RestController
@RequestMapping("/Base/DataModel")
public class DataModelController {

    @Autowired
    private DbTableService dbTableService;

    /**
     * 列表
     *
     * @param id         连接id
     * @param page 关键词
     * @return
     */
    @ApiOperation("获取数据库表列表")
    @GetMapping("/{id}/Tables")
    public ActionResult getList(@PathVariable("id") String id, Page page) throws DataException {
        List<DbTableModel> data = dbTableService.getList(id).stream().filter(
                t -> !StringUtil.isEmpty(page.getKeyword()) ? t.getDescription().toLowerCase().contains(page.getKeyword().toLowerCase())
                        ||t.getTable().toLowerCase().contains(page.getKeyword().toLowerCase()):t.getTable()!=null
        ).sorted(Comparator.comparing(DbTableModel::getTable)).collect(Collectors.toList());
        ListVO vo = new ListVO();
        vo.setList(data);
        return ActionResult.success(vo);
    }



    /**
     * 预览数据库表
     *
     * @param dbTableDataForm 查询条件
     * @param dbId      连接Id
     * @param tableName 表名
     * @return
     */
    @ApiOperation("预览数据库表")
    @GetMapping("/{DBId}/Table/{tableName}/Preview")
    public ActionResult data(DbTableDataForm dbTableDataForm, @PathVariable("DBId") String dbId, @PathVariable("tableName") String tableName) throws DataException {
        List<Map<String, Object>> data = dbTableService.getData(dbTableDataForm, dbId, tableName);
        PaginationVO paginationVO= JsonUtil.getJsonToBeanEx(dbTableDataForm,PaginationVO.class);
        return ActionResult.page(data,paginationVO);
    }


    /**
     * 列表
     * @return
     */
    @GetMapping("/{DBId}/Tables/{tableName}/Fields/Selector")
    @ApiOperation("获取数据库表字段下拉框列表")
    public ActionResult selectorList(@PathVariable("DBId") String dbId, @PathVariable("tableName") String tableName) throws DataException {
        List<DbTableFieldModel> data = dbTableService.getFieldList(dbId, tableName);
        List<DbTableFieldSeleVO> vos= JsonUtil.getJsonToList(data,DbTableFieldSeleVO.class);
        ListVO vo= new ListVO();
        vo.setList(vos);
        return ActionResult.success(vo);
    }
    /**
     * 字段列表
     *
     * @param dbId      连接Id
     * @param tableName 表名
     * @return
     */
    @ApiOperation("获取数据库表字段列表")
    @GetMapping("/{DBId}/Tables/{tableName}/Fields")
    public ActionResult fieldList(@PathVariable("DBId") String dbId, @PathVariable("tableName") String tableName) throws DataException {
        List<DbTableFieldModel> data = dbTableService.getFieldList(dbId, tableName);
        List<DbTableFieldVO> vos= JsonUtil.getJsonToList(data,DbTableFieldVO.class);
        for(DbTableFieldVO vo:vos){
            vo.setField(vo.getField().toLowerCase());
        }
        ListVO vo= new ListVO();
        vo.setList(vos);
        return ActionResult.success(vo);
    }

    /**
     * 信息
     *
     * @param dbId      连接Id
     * @param tableName 表名
     * @return
     */
    @ApiOperation("获取数据表")
    @GetMapping("/{DBId}/Table/{tableName}")
    public ActionResult get(@PathVariable("DBId") String dbId, @PathVariable("tableName") String tableName) throws DataException {
        DbTableModel dbTableModel = dbTableService.getList(dbId).stream().filter(m -> m.getTable().equals(tableName)).findFirst().get();
        //转换
        DbTableInfoVO tableInfo= JsonUtil.getJsonToBeanEx(dbTableModel,DbTableInfoVO.class);
        List<DbTableFieldModel> tableFieldList = dbTableService.getFieldList(dbId, tableName);
        List<DbTableFieldVO>  fieldList = JsonUtil.getJsonToList(tableFieldList,DbTableFieldVO.class);
        DbTableVO vo = DbTableVO.builder().tableFieldList(fieldList).tableInfo(tableInfo).build();
        return ActionResult.success(vo);
    }

    /**
     * 删除
     *
     * @param dbId      连接Id
     * @param tableName 连接Id
     * @return
     */
    @ApiOperation("删除")
    @DeleteMapping("/{DBId}/Table/{tableName}")
    public ActionResult delete(@PathVariable("DBId") String dbId, @PathVariable("tableName") String tableName) throws DataException {
        List<DbTableModel> list = dbTableService.getList(dbId);
        int sum = list.stream().filter(m -> m.getTable().equals(tableName)).findFirst().isPresent() ? list.stream().filter(m -> m.getTable().equals(tableName)).findFirst().get().getSum() : 0;
        if (sum == 0) {
            String byoTable = "base_authorize,base_comfields,base_billrule,base_dbbackup,base_dblink,base_dictionarydata,base_dictionarytype,base_imcontent,base_languagemap,base_languagetype,base_menu,base_message,base_messagereceive,base_module,base_modulebutton,base_modulecolumn,base_moduledataauthorize,base_moduledataauthorizescheme,base_organize,base_position,base_province,base_role,base_sysconfig,base_syslog,base_timetask,base_timetasklog,base_user,base_userrelation,crm_busines,crm_businesproduct,crm_clue,crm_contract,crm_contractinvoice,crm_contractmoney,crm_contractproduct,crm_customer,crm_customercontacts,crm_followlog,crm_invoice,crm_product,crm_receivable,ext_bigdata,ext_document,ext_documentshare,ext_emailconfig,ext_emailreceive,ext_emailsend,ext_employee,ext_order,ext_orderentry,ext_orderreceivable,ext_projectgantt,ext_schedule,ext_tableexample,ext_worklog,ext_worklogshare,flow_delegate,flow_engine,flow_engineform,flow_enginevisible,flow_task,flow_taskcirculate,flow_tasknode,flow_taskoperator,flow_taskoperatorrecord,wechat_mpeventcontent,wechat_mpmaterial,wechat_mpmessage,wechat_qydepartment,wechat_qymessage,wechat_qyuser,wform_applybanquet,wform_applydelivergoods,wform_applydelivergoodsentry,wform_applymeeting,wform_archivalborrow,wform_articleswarehous,wform_batchpack,wform_batchtable,wform_conbilling,wform_contractapproval,wform_contractapprovalsheet,wform_debitbill,wform_documentapproval,wform_documentsigning,wform_expenseexpenditure,wform_finishedproduct,wform_finishedproductentry,wform_incomerecognition,wform_leaveapply,wform_letterservice,wform_materialrequisition,wform_materialrequisitionentry,wform_monthlyreport,wform_officesupplies,wform_outboundorder,wform_outboundorderentry,wform_outgoingapply,wform_paydistribution,wform_paymentapply,wform_postbatchtab,wform_procurementmaterial,wform_procurementmaterialentry,wform_purchaselist,wform_purchaselistentry,wform_quotationapproval,wform_receiptprocessing,wform_receiptsign,wform_rewardpunishment,wform_salesorder,wform_salesorderentry,wform_salessupport,wform_staffovertime,wform_supplementcard,wform_travelapply,wform_travelreimbursement,wform_vehicleapply,wform_violationhandling,wform_warehousereceipt,wform_warehousereceiptentry,wform_workcontactsheet";
            boolean exists = byoTable.contains(tableName.toLowerCase());
            if (exists) {
                return ActionResult.success("系统自带表,不允许被删除");
            }
            dbTableService.delete(dbId, tableName);
            return ActionResult.success("删除成功");
        } else {
            return ActionResult.fail("表已经被使用,不允许被删除");
        }
    }

    /**
     * 新建
     *
     * @param dbId 连接Id
     * @return
     */
    @ApiOperation("新建")
    @PostMapping("{DBId}/Table")
    public ActionResult create(@PathVariable("DBId") String dbId, @RequestBody @Valid DbTableCrForm dbTableCrForm) throws DataException {
        DbTableModel dbTableModel= JsonUtil.getJsonToBean(dbTableCrForm.getTableInfo(),DbTableModel.class);
        List<DbTableFieldModel> list= JsonUtil.getJsonToList(dbTableCrForm.getTableFieldList(),DbTableFieldModel.class);
        if (dbTableService.isExistByFullName(dbId,dbTableCrForm.getTableInfo().getTable(),dbTableModel.getId())){
            return ActionResult.fail("表名称不能重复");
        }
        ActionResult actionResult = dbTableService.create(dbId, dbTableModel, list);
        return actionResult;
    }

    /**
     * 更新
     *
     * @param dbId      连接Id
     * @return
     */
    @ApiOperation("更新")
    @PutMapping("/{DBId}/Table")
    public ActionResult update(@PathVariable("DBId") String dbId, @RequestBody @Valid DbTableUpForm dbTableUpForm) throws DataException {
        DbTableModel dbTableModel= JsonUtil.getJsonToBean(dbTableUpForm.getTableInfo(),DbTableModel.class);
        List<DbTableFieldModel> list= JsonUtil.getJsonToList(dbTableUpForm.getTableFieldList(),DbTableFieldModel.class);
        if(!dbTableUpForm.getTableInfo().getNewTable().equals(dbTableUpForm.getTableInfo().getTable())){
            if (dbTableService.isExistByFullName(dbId,dbTableUpForm.getTableInfo().getNewTable(),dbTableUpForm.getTableInfo().getTable())){
                return ActionResult.fail("表名称不能重复");
            }
        }

        String oldTable=dbTableModel.getTable();
        int sum = dbTableService.getList(dbId).stream().filter(m -> m.getTable().equals(oldTable)).findFirst().get().getSum();
        if (sum == 0) {
            String byoTable = "base_authorize,base_comfields,base_billrule,base_dbbackup,base_dblink,base_dictionarydata,base_dictionarytype,base_imcontent,base_languagemap,base_languagetype,base_menu,base_message,base_messagereceive,base_module,base_modulebutton,base_modulecolumn,base_moduledataauthorize,base_moduledataauthorizescheme,base_organize,base_position,base_province,base_role,base_sysconfig,base_syslog,base_timetask,base_timetasklog,base_user,base_userrelation,crm_busines,crm_businesproduct,crm_clue,crm_contract,crm_contractinvoice,crm_contractmoney,crm_contractproduct,crm_customer,crm_customercontacts,crm_followlog,crm_invoice,crm_product,crm_receivable,ext_bigdata,ext_document,ext_documentshare,ext_emailconfig,ext_emailreceive,ext_emailsend,ext_employee,ext_order,ext_orderentry,ext_orderreceivable,ext_projectgantt,ext_schedule,ext_tableexample,ext_worklog,ext_worklogshare,flow_delegate,flow_engine,flow_engineform,flow_enginevisible,flow_task,flow_taskcirculate,flow_tasknode,flow_taskoperator,flow_taskoperatorrecord,wechat_mpeventcontent,wechat_mpmaterial,wechat_mpmessage,wechat_qydepartment,wechat_qymessage,wechat_qyuser,wform_applybanquet,wform_applydelivergoods,wform_applydelivergoodsentry,wform_applymeeting,wform_archivalborrow,wform_articleswarehous,wform_batchpack,wform_batchtable,wform_conbilling,wform_contractapproval,wform_contractapprovalsheet,wform_debitbill,wform_documentapproval,wform_documentsigning,wform_expenseexpenditure,wform_finishedproduct,wform_finishedproductentry,wform_incomerecognition,wform_leaveapply,wform_letterservice,wform_materialrequisition,wform_materialrequisitionentry,wform_monthlyreport,wform_officesupplies,wform_outboundorder,wform_outboundorderentry,wform_outgoingapply,wform_paydistribution,wform_paymentapply,wform_postbatchtab,wform_procurementmaterial,wform_procurementmaterialentry,wform_purchaselist,wform_purchaselistentry,wform_quotationapproval,wform_receiptprocessing,wform_receiptsign,wform_rewardpunishment,wform_salesorder,wform_salesorderentry,wform_salessupport,wform_staffovertime,wform_supplementcard,wform_travelapply,wform_travelreimbursement,wform_vehicleapply,wform_violationhandling,wform_warehousereceipt,wform_warehousereceiptentry,wform_workcontactsheet";
            String[] tables=byoTable.split(",");
            boolean exists;
            for(String table:tables){
                exists=dbTableUpForm.getTableInfo().getNewTable().toLowerCase().equals(table);
                if (exists) {
                    return ActionResult.fail("系统自带表,不允许被修改");
                }
            }
            dbTableService.update(dbId, dbTableModel, list);
            return ActionResult.success("修改成功");
        } else {
            return ActionResult.fail("表已经被使用,不允许被编辑");
        }
    }
}

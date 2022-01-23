package smart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import smart.entity.EmployeeEntity;
import smart.mapper.EmployeeMapper;
import smart.model.EmployeeModel;
import smart.model.employee.EmployeeImportVO;
import smart.model.employee.PaginationEmployee;
import smart.service.EmployeeService;
import smart.util.DateUtil;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import smart.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.*;

/**
 * 职员信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 */
@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, EmployeeEntity> implements EmployeeService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<EmployeeEntity> getList() {
        QueryWrapper<EmployeeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(EmployeeEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<EmployeeEntity> getList(PaginationEmployee paginationEmployee) {
//        Map<String, Object> queryParam = OptimizeUtil.queryParam(pagination);
        QueryWrapper<EmployeeEntity> queryWrapper = new QueryWrapper<>();
        //查询条件
        String propertyName = paginationEmployee.getCondition() != null ? paginationEmployee.getCondition() : null;

        String propertyValue = paginationEmployee.getKeyword() != null ? paginationEmployee.getKeyword() : null;
        if (!StringUtils.isEmpty(propertyName) && !StringUtils.isEmpty(propertyValue)) {
            switch (propertyName) {
                //工号
                case "EnCode":
                    queryWrapper.lambda().like(EmployeeEntity::getEnCode, propertyValue);
                    break;
                //姓名
                case "FullName":
                    queryWrapper.lambda().like(EmployeeEntity::getFullName, propertyValue);
                    break;
                //电话
                case "Telephone":
                    queryWrapper.lambda().like(EmployeeEntity::getTelephone, propertyValue);
                    break;
                //部门
                case "DepartmentName":
                    queryWrapper.lambda().like(EmployeeEntity::getDepartmentName, propertyValue);
                    break;
                //岗位
                case "PositionName":
                    queryWrapper.lambda().like(EmployeeEntity::getPositionName, propertyValue);
                    break;
                default:
                    break;
            }
        }
        //排序
        if (StringUtils.isEmpty(paginationEmployee.getSidx())) {
            queryWrapper.lambda().orderByDesc(EmployeeEntity::getCreatorTime);
        } else {
            queryWrapper = "asc".equals(paginationEmployee.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationEmployee.getSidx()) : queryWrapper.orderByDesc(paginationEmployee.getSidx());
        }
        Page page = new Page(paginationEmployee.getCurrentPage(), paginationEmployee.getPageSize());
        IPage<EmployeeEntity> userIPage = this.page(page, queryWrapper);
        return paginationEmployee.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public EmployeeEntity getInfo(String id) {
        QueryWrapper<EmployeeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmployeeEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(EmployeeEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public void create(EmployeeEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setSortCode(RandomUtil.parses());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public void update(String id, EmployeeEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        this.updateById(entity);
    }

    @Override
    public Map<String, Object> importPreview(List<EmployeeModel> personList) {
        List<Map<String, Object>> dataRow = new ArrayList<>();
        List<Map<String, Object>> columns = new ArrayList<>();
        for (int i = 0; i < personList.size(); i++) {
            Map<String, Object> dataRowMap = new HashMap<>();
            EmployeeModel model = personList.get(i);
            dataRowMap.put("enCode", model.getEnCode());
            dataRowMap.put("fullName", model.getFullName());
            dataRowMap.put("gender", model.getGender());
            dataRowMap.put("departmentName", model.getDepartmentName());
            dataRowMap.put("positionName", model.getPositionName());
            dataRowMap.put("workingNature", model.getWorkingNature());
            dataRowMap.put("idNumber", model.getIdNumber());
            dataRowMap.put("telephone", model.getTelephone());
            dataRowMap.put("attendWorkTime", model.getAttendWorkTime());
            dataRowMap.put("birthday", model.getBirthday());
            dataRowMap.put("education", model.getEducation());
            dataRowMap.put("major", model.getMajor());
            dataRowMap.put("graduationAcademy", model.getGraduationAcademy());
            dataRowMap.put("graduationTime", model.getGraduationTime());
            dataRow.add(dataRowMap);
        }
        for (int i = 1; i < 15; i++) {
            Map<String, Object> columnsMap = new HashMap<>();
            columnsMap.put("AllowDBNull", true);
            columnsMap.put("AutoIncrement", false);
            columnsMap.put("AutoIncrementSeed", 0);
            columnsMap.put("AutoIncrementStep", 1);
            columnsMap.put("Caption", this.getColumns(i));
            columnsMap.put("ColumnMapping", 1);
            columnsMap.put("ColumnName", this.getColumns(i));
            columnsMap.put("Container", null);
            columnsMap.put("DataType", "System.String, mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089");
            columnsMap.put("DateTimeMode", 3);
            columnsMap.put("DefaultValue", null);
            columnsMap.put("DesignMode", false);
            columnsMap.put("Expression", "");
            columnsMap.put("ExtendedProperties", "");
            columnsMap.put("MaxLength", -1);
            columnsMap.put("Namespace", "");
            columnsMap.put("Ordinal", 0);
            columnsMap.put("Prefix", "");
            columnsMap.put("ReadOnly", false);
            columnsMap.put("Site", null);
            columnsMap.put("Table", personList);
            columnsMap.put("Unique", false);
            columns.add(columnsMap);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("dataRow", dataRow);
        map.put("columns", columns);
        return map;
    }

    @Override
    public EmployeeImportVO importData(List<EmployeeModel> dt) {

        for(EmployeeModel model :dt){
            model.setAttendWorkTime(DateUtil.cstFormat(model.getAttendWorkTime()));
            model.setBirthday(DateUtil.cstFormat(model.getBirthday()));
            model.setGraduationTime(DateUtil.cstFormat(model.getGraduationTime()));
        }
        List<EmployeeEntity> entitys = JsonUtil.getJsonToList(dt, EmployeeEntity.class);
        //记录成功了几条
        int sum=0;
        //记录第几条失败
        int num=0;
        List<EmployeeEntity> errList = new ArrayList<>();
        for (EmployeeEntity entity : entitys) {
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUserId(userProvider.get().getUserId());
            entity.setCreatorTime(new Date());
            try {
                this.baseMapper.insert(entity);
                sum++;
            }catch (Exception e){
                errList.add(entity);
                num++;
                log.error("导入第"+(num+1)+"条数据失败");
            }

        }
        EmployeeImportVO vo=new EmployeeImportVO();
        vo.setSnum(sum);
        vo.setFnum(num);
        if(vo.getFnum()>0){
            vo.setResultType(1);
            vo.setFailResult(JsonUtil.getJsonToList(errList,EmployeeModel.class));
            return vo;
        }else{
            vo.setResultType(0);
            return vo;
        }
    }

    @Override
    public void exportPdf(List<EmployeeEntity> list, String outputUrl) {
        try {
            Document document = new Document();
            BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
            Font font = new Font(bfChinese, 11, Font.NORMAL);
            PdfWriter.getInstance(document, new FileOutputStream(outputUrl));
            document.open();
            PdfPTable row;
            row = new PdfPTable(13);
            //表占页面100%宽度
            row.setWidthPercentage(100f);
            //标题
            String[] titles = {"姓名", "性别", "部门", "职位", "用工性质", "身份证号", "联系电话", "出生年月", "参加工作", "最高学历", "所学专业", "毕业院校", "毕业时间"};
            for (String title : titles) {
                row.addCell(createCell(title, font));
            }
            document.add(row);
            //内容
            for (EmployeeEntity entity : list) {
                row = new PdfPTable(13);
                //表占页面100%宽度
                row.setWidthPercentage(100f);
                row.addCell(createCell(entity.getFullName(), font));
                row.addCell(createCell(entity.getGender(), font));
                row.addCell(createCell(entity.getDepartmentName(), font));
                row.addCell(createCell(entity.getPositionName(), font));
                row.addCell(createCell(entity.getWorkingNature(), font));
                row.addCell(createCell(entity.getIdNumber(), font));
                row.addCell(createCell(entity.getTelephone(), font));
                row.addCell(createCell(entity.getAttendWorkTime() != null ? DateUtil.daFormat(entity.getAttendWorkTime()) : "", font));
                row.addCell(createCell(entity.getBirthday() != null ? DateUtil.daFormat(entity.getBirthday()) : "", font));
                row.addCell(createCell(entity.getEducation(), font));
                row.addCell(createCell(entity.getMajor(), font));
                row.addCell(createCell(entity.getGraduationAcademy(), font));
                row.addCell(createCell(entity.getGraduationTime() != null ? DateUtil.daFormat(entity.getGraduationTime()) : "", font));
                document.add(row);
            }
            document.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private PdfPCell createCell(String value, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    private String getKey(String key) {
        Map<String, String> map = new HashMap<>();
        map.put("工号", "F_EnCode");
        map.put("姓名", "F_FullName");
        map.put("性别", "F_Gender");
        map.put("部门", "F_DepartmentName");
        map.put("岗位", "F_PositionName");
        map.put("用工性质", "F_WorkingNature");
        map.put("身份证号", "F_IDNumber");
        map.put("联系电话", "F_Telephone");
        map.put("出生年月", "F_Birthday");
        map.put("参加工作", "F_AttendWorkTime");
        map.put("最高学历", "F_Education");
        map.put("所学专业", "F_Major");
        map.put("毕业院校", "F_GraduationAcademy");
        map.put("毕业时间", "F_GraduationTime");
        return map.get(key);
    }

    private String getColumns(Integer key) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "工号");
        map.put(2, "姓名");
        map.put(3, "性别");
        map.put(4, "部门");
        map.put(5, "岗位");
        map.put(6, "用工性质");
        map.put(7, "身份证号");
        map.put(8, "联系电话");
        map.put(9, "出生年月");
        map.put(10, "参加工作");
        map.put(11, "最高学历");
        map.put(12, "所学专业");
        map.put(13, "毕业院校");
        map.put(14, "毕业时间");
        return map.get(key);
    }
}

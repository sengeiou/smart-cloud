package com.bstek.ureport.console.ureport.util;

import com.bstek.ureport.Utils;
import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.console.ureport.entity.ReportEntity;
import com.bstek.ureport.definition.Orientation;
import com.bstek.ureport.definition.Paper;
import com.bstek.ureport.definition.PaperType;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.export.excel.high.CellStyleContext;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.model.Row;
import com.bstek.ureport.model.*;
import com.bstek.ureport.utils.ImageUtils;
import com.bstek.ureport.utils.UnitUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;

@Slf4j
public class UreportExcelUtil {

    private ReportBuilder reportBuilder = new ReportBuilder();

    public Workbook buildExcel(ReportEntity map, boolean withPaging, boolean withSheet, Connection connection) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        try {
            if (map != null) {
                byte[] content = map.getContent().getBytes();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
                ReportDefinition reportDefinition = UreportUtil.parseReport(inputStream, map.getFullName());
                Map<String, Object> parameters = new HashMap<>();
                Report report = reportBuilder.buildReportToConnection(reportDefinition, parameters,connection);
                if (withPaging) {
                    workbook = build(report, withSheet);
                } else {
                    workbook = build(report);
                }
            }
        } catch (Exception e) {
            log.error("生成excel报表错误:" + e.getMessage());
        }
        return workbook;
    }

    //---------------------------------------生成excel------------------------------------------

    public SXSSFWorkbook build(Report report, boolean withSheet) throws Exception {
        CellStyleContext cellStyleContext = new CellStyleContext();
        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        CreationHelper creationHelper = wb.getCreationHelper();
        Paper paper = report.getPaper();
        List<Column> columns = report.getColumns();
        Map<Row, Map<Column, Cell>> cellMap = report.getRowColCellMap();
        int columnSize = columns.size();
        List<Page> pages = report.getPages();
        int rowNumber = 0;
        int pageIndex = 1;
        Sheet sheet = null;
        Iterator var15 = pages.iterator();
        while (var15.hasNext()) {
            Page page = (Page) var15.next();
            if (withSheet) {
                sheet = createSheet(wb, paper, "第" + pageIndex + "页");
                rowNumber = 0;
            } else if (sheet == null) {
                sheet = createSheet(wb, paper, (String) null);
            }
            ++pageIndex;
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            List<Row> rows = page.getRows();
            for (int rowIndex = 0; rowIndex < rows.size(); ++rowIndex) {
                Row r = (Row) rows.get(rowIndex);
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(rowNumber);
                if (row == null) {
                    row = sheet.createRow(rowNumber);
                }
                Map<Column, Cell> colCell = (Map) cellMap.get(r);
                int skipCol = 0;
                for (int i = 0; i < columnSize; ++i) {
                    Column col = (Column) columns.get(i);
                    int w = col.getWidth();
                    if (w < 1) {
                        ++skipCol;
                    } else {
                        int colNum = i - skipCol;
                        double colWidth = (double) UnitUtils.pointToPixel((double) w) * 37.5D;
                        sheet.setColumnWidth(colNum, (short) ((int) colWidth));
                        org.apache.poi.ss.usermodel.Cell cell = row.getCell(colNum);
                        if (cell == null) {
                            cell = row.createCell(colNum);
                            Cell cellInfo = null;
                            if (colCell != null) {
                                cellInfo = (Cell) colCell.get(col);
                            }
                            if (cellInfo != null) {
                                XSSFCellStyle style = cellStyleContext.produceXSSFCellStyle(wb, cellInfo);
                                int colSpan = cellInfo.getColSpan();
                                int rowSpan = cellInfo.getPageRowSpan();
                                int rowEnd = rowSpan;
                                if (rowSpan == 0) {
                                    rowEnd = rowSpan + 1;
                                }
                                rowEnd += rowNumber;
                                int colStart = i;
                                int colEnd = colSpan;
                                if (colSpan == 0) {
                                    colEnd = colSpan + 1;
                                }
                                colEnd += i;
                                for (int j = rowNumber; j < rowEnd; ++j) {
                                    org.apache.poi.ss.usermodel.Row rr = sheet.getRow(j);
                                    if (rr == null) {
                                        rr = sheet.createRow(j);
                                    }
                                    for (int c = colStart; c < colEnd; ++c) {
                                        org.apache.poi.ss.usermodel.Cell cc = rr.getCell(c - skipCol);
                                        if (cc == null) {
                                            cc = rr.createCell(c - skipCol);
                                        }
                                        cc.setCellStyle(style);
                                    }
                                }
                                if (colSpan > 0 || rowSpan > 0) {
                                    if (rowSpan > 0) {
                                        --rowSpan;
                                    }
                                    if (colSpan > 0) {
                                        --colSpan;
                                    }
                                    CellRangeAddress cellRegion = new CellRangeAddress(rowNumber, rowNumber + rowSpan, i - skipCol, i - skipCol + colSpan);
                                    sheet.addMergedRegion(cellRegion);
                                }
                                Object obj = cellInfo.getFormatData();
                                if (obj != null) {
                                    if (obj instanceof String) {
                                        cell.setCellValue((String) obj);
                                        cell.setCellType(CellType.STRING);
                                    } else if (obj instanceof Number) {
                                        BigDecimal bigDecimal = Utils.toBigDecimal(obj);
                                        cell.setCellValue((double) bigDecimal.floatValue());
                                        cell.setCellType(CellType.NUMERIC);
                                    } else if (obj instanceof Boolean) {
                                        cell.setCellValue((Boolean) obj);
                                        cell.setCellType(CellType.BOOLEAN);
                                    } else {
                                        int width;
                                        int height;
                                        int leftMargin;
                                        int topMargin;
                                        if (obj instanceof Image) {
                                            Image img = (Image) obj;
                                            InputStream inputStream = ImageUtils.base64DataToInputStream(img.getBase64Data());
                                            BufferedImage bufferedImage = ImageIO.read(inputStream);
                                            width = bufferedImage.getWidth();
                                            height = bufferedImage.getHeight();
                                            IOUtils.closeQuietly(inputStream);
                                            inputStream = ImageUtils.base64DataToInputStream(img.getBase64Data());
                                            width = 0;
                                            height = 0;
                                            leftMargin = getWholeWidth(columns, i, cellInfo.getColSpan());
                                            topMargin = getWholeHeight(rows, rowIndex, cellInfo.getRowSpan());
                                            HorizontalAlignment align = style.getAlignmentEnum();
                                            if (align.equals(HorizontalAlignment.CENTER)) {
                                                width = (leftMargin - width) / 2;
                                            } else if (align.equals(HorizontalAlignment.RIGHT)) {
                                                width = leftMargin - width;
                                            }
                                            VerticalAlignment valign = style.getVerticalAlignmentEnum();
                                            if (valign.equals(VerticalAlignment.CENTER)) {
                                                height = (topMargin - height) / 2;
                                            } else if (valign.equals(VerticalAlignment.BOTTOM)) {
                                                height = topMargin - height;
                                            }
                                            try {
                                                XSSFClientAnchor anchor = (XSSFClientAnchor) creationHelper.createClientAnchor();
                                                byte[] bytes = IOUtils.toByteArray(inputStream);
                                                int pictureFormat = buildImageFormat(img);
                                                int pictureIndex = wb.addPicture(bytes, pictureFormat);
                                                anchor.setCol1(i);
                                                anchor.setCol2(i + colSpan);
                                                anchor.setRow1(rowNumber);
                                                anchor.setRow2(rowNumber + rowSpan);
                                                anchor.setDx1(width * 9525);
                                                anchor.setDx2(width * 9525);
                                                anchor.setDy1(height * 9525);
                                                anchor.setDy2(height * 9525);
                                                drawing.createPicture(anchor, pictureIndex);
                                            } finally {
                                                IOUtils.closeQuietly(inputStream);
                                            }
                                        } else if (obj instanceof ChartData) {
                                            ChartData chartData = (ChartData) obj;
                                            String base64Data = chartData.retriveBase64Data();
                                            if (base64Data != null) {
                                                Image img = new Image(base64Data, chartData.getWidth(), chartData.getHeight());
                                                InputStream inputStream = ImageUtils.base64DataToInputStream(img.getBase64Data());
                                                BufferedImage bufferedImage = ImageIO.read(inputStream);
                                                width = bufferedImage.getWidth();
                                                height = bufferedImage.getHeight();
                                                IOUtils.closeQuietly(inputStream);
                                                inputStream = ImageUtils.base64DataToInputStream(img.getBase64Data());
                                                leftMargin = 0;
                                                topMargin = 0;
                                                int wholeWidth = getWholeWidth(columns, i, cellInfo.getColSpan());
                                                int wholeHeight = getWholeHeight(rows, rowIndex, cellInfo.getRowSpan());
                                                HorizontalAlignment align = style.getAlignmentEnum();
                                                if (align.equals(HorizontalAlignment.CENTER)) {
                                                    leftMargin = (wholeWidth - width) / 2;
                                                } else if (align.equals(HorizontalAlignment.RIGHT)) {
                                                    leftMargin = wholeWidth - width;
                                                }

                                                VerticalAlignment valign = style.getVerticalAlignmentEnum();
                                                if (valign.equals(VerticalAlignment.CENTER)) {
                                                    topMargin = (wholeHeight - height) / 2;
                                                } else if (valign.equals(VerticalAlignment.BOTTOM)) {
                                                    topMargin = wholeHeight - height;
                                                }

                                                try {
                                                    XSSFClientAnchor anchor = (XSSFClientAnchor) creationHelper.createClientAnchor();
                                                    byte[] bytes = IOUtils.toByteArray(inputStream);
                                                    int pictureFormat = buildImageFormat(img);
                                                    int pictureIndex = wb.addPicture(bytes, pictureFormat);
                                                    anchor.setCol1(i);
                                                    anchor.setCol2(i + colSpan);
                                                    anchor.setRow1(rowNumber);
                                                    anchor.setRow2(rowNumber + rowSpan);
                                                    anchor.setDx1(leftMargin * 9525);
                                                    anchor.setDx2(width * 9525);
                                                    anchor.setDy1(topMargin * 9525);
                                                    anchor.setDy2(height * 9525);
                                                    drawing.createPicture(anchor, pictureIndex);
                                                } finally {
                                                    IOUtils.closeQuietly(inputStream);
                                                }
                                            }
                                        } else if (obj instanceof Date) {
                                            cell.setCellValue((Date) obj);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                row.setHeight((short) UnitUtils.pointToTwip(r.getHeight()));
                ++rowNumber;
            }
            sheet.setRowBreak(rowNumber - 1);
        }
        return wb;
    }

    public SXSSFWorkbook build(Report report) throws Exception {
        CellStyleContext cellStyleContext = new CellStyleContext();
        SXSSFWorkbook wb = new SXSSFWorkbook(100000);
        CreationHelper creationHelper = wb.getCreationHelper();
        Paper paper = report.getPaper();

        List<Column> columns = report.getColumns();
        Map<Row, Map<Column, Cell>> cellMap = report.getRowColCellMap();
        int columnSize = columns.size();
        Sheet sheet = createSheet(wb, paper, (String) null);
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        List<Row> rows = report.getRows();
        int rowNumber = 0;
        Iterator var14 = rows.iterator();
        while (var14.hasNext()) {
            Row r = (Row) var14.next();
            int realHeight = r.getRealHeight();
            if (realHeight >= 1) {
                if (r.isForPaging()) {
                    return wb;
                }
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(rowNumber);
                if (row == null) {
                    row = sheet.createRow(rowNumber);
                }
                Map<Column, Cell> colCell = (Map) cellMap.get(r);
                int skipCol = 0;
                for (int i = 0; i < columnSize; ++i) {
                    Column col = (Column) columns.get(i);
                    int w = col.getWidth();
                    if (w < 1) {
                        ++skipCol;
                    } else {
                        double colWidth = (double) UnitUtils.pointToPixel((double) w) * 37.5D;
                        int colNum = i - skipCol;
                        sheet.setColumnWidth(colNum, (short) ((int) colWidth));
                        org.apache.poi.ss.usermodel.Cell cell = row.getCell(colNum);
                        if (cell == null) {
                            cell = row.createCell(colNum);
                            Cell cellInfo = null;
                            if (colCell != null) {
                                cellInfo = (Cell) colCell.get(col);
                            }
                            if (cellInfo != null && !cellInfo.isForPaging()) {
                                XSSFCellStyle style = cellStyleContext.produceXSSFCellStyle(wb, cellInfo);
                                int colSpan = cellInfo.getColSpan();
                                int rowSpan = cellInfo.getRowSpan();
                                int rowEnd = rowSpan;
                                if (rowSpan == 0) {
                                    rowEnd = rowSpan + 1;
                                }
                                rowEnd += rowNumber;
                                int colStart = i;
                                int colEnd = colSpan;
                                if (colSpan == 0) {
                                    colEnd = colSpan + 1;
                                }
                                colEnd += i;
                                for (int j = rowNumber; j < rowEnd; ++j) {
                                    org.apache.poi.ss.usermodel.Row rr = sheet.getRow(j);
                                    if (rr == null) {
                                        rr = sheet.createRow(j);
                                    }
                                    for (int c = colStart; c < colEnd; ++c) {
                                        org.apache.poi.ss.usermodel.Cell cc = rr.getCell(c - skipCol);
                                        if (cc == null) {
                                            cc = rr.createCell(c - skipCol);
                                        }
                                        cc.setCellStyle(style);
                                    }
                                }
                                if (colSpan > 0 || rowSpan > 0) {
                                    if (rowSpan > 0) {
                                        --rowSpan;
                                    }
                                    if (colSpan > 0) {
                                        --colSpan;
                                    }
                                    CellRangeAddress cellRegion = new CellRangeAddress(rowNumber, rowNumber + rowSpan, i - skipCol, i - skipCol + colSpan);
                                    sheet.addMergedRegion(cellRegion);
                                }
                                Object obj = cellInfo.getFormatData();
                                if (obj != null) {
                                    if (obj instanceof String) {
                                        cell.setCellValue((String) obj);
                                        cell.setCellType(CellType.STRING);
                                    } else if (obj instanceof Number) {
                                        BigDecimal bigDecimal = Utils.toBigDecimal(obj);
                                        cell.setCellValue(bigDecimal.doubleValue());
                                        cell.setCellType(CellType.NUMERIC);
                                    } else if (obj instanceof Boolean) {
                                        cell.setCellValue((Boolean) obj);
                                        cell.setCellType(CellType.BOOLEAN);
                                    } else {
                                        int width;
                                        int height;
                                        int leftMargin;
                                        int topMargin;
                                        if (obj instanceof Image) {
                                            Image img = (Image) obj;
                                            InputStream inputStream = ImageUtils.base64DataToInputStream(img.getBase64Data());
                                            BufferedImage bufferedImage = ImageIO.read(inputStream);
                                            width = bufferedImage.getWidth();
                                            height = bufferedImage.getHeight();
                                            IOUtils.closeQuietly(inputStream);
                                            inputStream = ImageUtils.base64DataToInputStream(img.getBase64Data());
                                            width = 0;
                                            height = 0;
                                            leftMargin = getWholeWidth(columns, i, cellInfo.getColSpan());
                                            topMargin = getWholeHeight(rows, rowNumber, cellInfo.getRowSpan());
                                            HorizontalAlignment align = style.getAlignmentEnum();
                                            if (align.equals(HorizontalAlignment.CENTER)) {
                                                width = (leftMargin - width) / 2;
                                            } else if (align.equals(HorizontalAlignment.RIGHT)) {
                                                width = leftMargin - width;
                                            }
                                            VerticalAlignment valign = style.getVerticalAlignmentEnum();
                                            if (valign.equals(VerticalAlignment.CENTER)) {
                                                height = (topMargin - height) / 2;
                                            } else if (valign.equals(VerticalAlignment.BOTTOM)) {
                                                height = topMargin - height;
                                            }
                                            try {
                                                XSSFClientAnchor anchor = (XSSFClientAnchor) creationHelper.createClientAnchor();
                                                byte[] bytes = IOUtils.toByteArray(inputStream);
                                                int pictureFormat = buildImageFormat(img);
                                                int pictureIndex = wb.addPicture(bytes, pictureFormat);
                                                anchor.setCol1(i);
                                                anchor.setCol2(i + colSpan);
                                                anchor.setRow1(rowNumber);
                                                anchor.setRow2(rowNumber + rowSpan);
                                                anchor.setDx1(width * 9525);
                                                anchor.setDx2(width * 9525);
                                                anchor.setDy1(height * 9525);
                                                anchor.setDy2(height * 9525);
                                                drawing.createPicture(anchor, pictureIndex);
                                            } finally {
                                                IOUtils.closeQuietly(inputStream);
                                            }
                                        } else if (obj instanceof ChartData) {
                                            ChartData chartData = (ChartData) obj;
                                            String base64Data = chartData.retriveBase64Data();
                                            if (base64Data != null) {
                                                Image img = new Image(base64Data, chartData.getWidth(), chartData.getHeight());
                                                InputStream inputStream = ImageUtils.base64DataToInputStream(img.getBase64Data());
                                                BufferedImage bufferedImage = ImageIO.read(inputStream);
                                                width = bufferedImage.getWidth();
                                                height = bufferedImage.getHeight();
                                                IOUtils.closeQuietly(inputStream);
                                                inputStream = ImageUtils.base64DataToInputStream(img.getBase64Data());
                                                leftMargin = 0;
                                                topMargin = 0;
                                                int wholeWidth = getWholeWidth(columns, i, cellInfo.getColSpan());
                                                int wholeHeight = getWholeHeight(rows, rowNumber, cellInfo.getRowSpan());
                                                HorizontalAlignment align = style.getAlignmentEnum();
                                                if (align.equals(HorizontalAlignment.CENTER)) {
                                                    leftMargin = (wholeWidth - width) / 2;
                                                } else if (align.equals(HorizontalAlignment.RIGHT)) {
                                                    leftMargin = wholeWidth - width;
                                                }
                                                VerticalAlignment valign = style.getVerticalAlignmentEnum();
                                                if (valign.equals(VerticalAlignment.CENTER)) {
                                                    topMargin = (wholeHeight - height) / 2;
                                                } else if (valign.equals(VerticalAlignment.BOTTOM)) {
                                                    topMargin = wholeHeight - height;
                                                }
                                                try {
                                                    XSSFClientAnchor anchor = (XSSFClientAnchor) creationHelper.createClientAnchor();
                                                    byte[] bytes = IOUtils.toByteArray(inputStream);
                                                    int pictureFormat = buildImageFormat(img);
                                                    int pictureIndex = wb.addPicture(bytes, pictureFormat);
                                                    anchor.setCol1(i);
                                                    anchor.setCol2(i + colSpan);
                                                    anchor.setRow1(rowNumber);
                                                    anchor.setRow2(rowNumber + rowSpan);
                                                    anchor.setDx1(leftMargin * 9525);
                                                    anchor.setDx2(width * 9525);
                                                    anchor.setDy1(topMargin * 9525);
                                                    anchor.setDy2(height * 9525);
                                                    drawing.createPicture(anchor, pictureIndex);
                                                } finally {
                                                    IOUtils.closeQuietly(inputStream);
                                                }
                                            }
                                        } else if (obj instanceof Date) {
                                            cell.setCellValue((Date) obj);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                row.setHeight((short) UnitUtils.pointToTwip(r.getRealHeight()));
                ++rowNumber;
            }
        }
        sheet.setRowBreak(rowNumber - 1);
        return wb;
    }

    private int getWholeWidth(List<Column> columns, int colNumber, int colSpan) {
        Column col = (Column) columns.get(colNumber);
        int start = colNumber + 1;
        int end = colNumber + colSpan;
        int w = col.getWidth();

        for (int i = start; i < end; ++i) {
            Column c = (Column) columns.get(i);
            w += c.getWidth();
        }

        w = UnitUtils.pointToPixel((double) w);
        return w;
    }

    private int getWholeHeight(List<Row> rows, int rowNumber, int rowSpan) {
        Row row = (Row) rows.get(rowNumber);
        int start = rowNumber + 1;
        int end = rowNumber + rowSpan;
        int h = row.getRealHeight();

        for (int i = start; i < end; ++i) {
            Row r = (Row) rows.get(i);
            h += r.getRealHeight();
        }

        h = UnitUtils.pointToPixel((double) h);
        return h;
    }

    private Sheet createSheet(SXSSFWorkbook wb, Paper paper, String name) {
        Sheet sheet = null;
        if (name == null) {
            sheet = wb.createSheet();
        } else {
            sheet = wb.createSheet(name);
        }

        PaperType paperType = paper.getPaperType();
        XSSFPrintSetup printSetup = (XSSFPrintSetup) sheet.getPrintSetup();
        Orientation orientation = paper.getOrientation();
        if (orientation.equals(Orientation.landscape)) {
            printSetup.setOrientation(PrintOrientation.LANDSCAPE);
        }

        setupPaper(paperType, printSetup);
        int leftMargin = paper.getLeftMargin();
        int rightMargin = paper.getRightMargin();
        int topMargin = paper.getTopMargin();
        int bottomMargin = paper.getBottomMargin();
        sheet.setMargin((short) 0, (double) UnitUtils.pointToInche((float) leftMargin));
        sheet.setMargin((short) 1, (double) UnitUtils.pointToInche((float) rightMargin));
        sheet.setMargin((short) 2, (double) UnitUtils.pointToInche((float) topMargin));
        sheet.setMargin((short) 3, (double) UnitUtils.pointToInche((float) bottomMargin));
        return sheet;
    }

    private int buildImageFormat(Image img) {
        int type = 6;
        String path = img.getPath();
        if (path == null) {
            return type;
        } else {
            path = path.toLowerCase();
            if (path.endsWith("jpg") || path.endsWith("jpeg")) {
                type = 5;
            }

            return type;
        }
    }

    private boolean setupPaper(PaperType paperType, XSSFPrintSetup printSetup) {
        boolean setup = false;
        switch (paperType) {
            case A0:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case A1:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case A2:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case A3:
                printSetup.setPaperSize(PaperSize.A3_PAPER);
                setup = true;
                break;
            case A4:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                setup = true;
                break;
            case A5:
                printSetup.setPaperSize(PaperSize.A5_PAPER);
                setup = true;
                break;
            case A6:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case A7:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case A8:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case A9:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case A10:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case B0:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case B1:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case B2:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case B3:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case B4:
                printSetup.setPaperSize(PaperSize.B4_PAPER);
                setup = true;
                break;
            case B5:
                printSetup.setPaperSize(PaperSize.B4_PAPER);
                setup = true;
                break;
            case B6:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case B7:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case B8:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case B9:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case B10:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
                break;
            case CUSTOM:
                printSetup.setPaperSize(PaperSize.A4_PAPER);
        }
        return setup;
    }
}

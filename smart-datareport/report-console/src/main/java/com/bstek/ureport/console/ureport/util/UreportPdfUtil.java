package com.bstek.ureport.console.ureport.util;

import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.console.ureport.entity.ReportEntity;
import com.bstek.ureport.definition.*;
import com.bstek.ureport.export.FullPageData;
import com.bstek.ureport.export.PageBuilder;
import com.bstek.ureport.export.pdf.CellBorderEvent;
import com.bstek.ureport.export.pdf.CellPhrase;
import com.bstek.ureport.export.pdf.PageHeaderFooterEvent;
import com.bstek.ureport.model.Image;
import com.bstek.ureport.model.*;
import com.bstek.ureport.utils.ImageUtils;
import com.bstek.ureport.utils.UnitUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;
import java.util.*;

@Slf4j
public class UreportPdfUtil {
    CellPhrase cellPhrase = new CellPhrase();

    private ReportBuilder reportBuilder = new ReportBuilder();

    //手动带入数据库连接
    public void buildPdfToConnection(ReportEntity map, OutputStream outputStream, Connection connection) {
        if (map != null) {
            byte[] content = map.getContent().getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
            ReportDefinition reportDefinition = UreportUtil.parseReport(inputStream, map.getFullName());
            Map<String, Object> parameters = new HashMap<>();
            Report report = reportBuilder.buildReportToConnection(reportDefinition, parameters,connection);
            build(report, outputStream);
        }
    }

    public void buildPdf(ReportDefinition reportDefinition, OutputStream outputStream,Connection connection) {
        if (reportDefinition != null) {
            Map<String, Object> parameters = new HashMap<>();
            Report report = reportBuilder.buildReportToConnection(reportDefinition, parameters,connection);
            build(report, outputStream);
        }
    }

    //---------------------------------------生成pdf------------------------------------------
    public void build(Report report, OutputStream outputStream) {
        Paper paper = report.getPaper();
        int width = paper.getWidth();
        int height = paper.getHeight();
        Rectangle pageSize = new RectangleReadOnly((float) width, (float) height);
        if (paper.getOrientation().equals(Orientation.landscape)) {
            pageSize = ((Rectangle) pageSize).rotate();
        }
        int leftMargin = paper.getLeftMargin();
        int rightMargin = paper.getRightMargin();
        int topMargin = paper.getTopMargin();
        int bottomMargin = paper.getBottomMargin();
        Document document = new Document((Rectangle) pageSize, (float) leftMargin, (float) rightMargin, (float) topMargin, (float) bottomMargin);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            PageHeaderFooterEvent headerFooterEvent = new PageHeaderFooterEvent(report);
            writer.setPageEvent(headerFooterEvent);
            document.open();
            List<Column> columns = report.getColumns();
            List<Integer> columnsWidthList = new ArrayList();
            int[] intArr = this.buildColumnSizeAndTotalWidth(columns, columnsWidthList);
            int colSize = intArr[0];
            int totalWidth = intArr[1];
            int[] columnsWidth = new int[columnsWidthList.size()];
            for (int i = 0; i < columnsWidthList.size(); ++i) {
                columnsWidth[i] = (Integer) columnsWidthList.get(i);
            }
            FullPageData pageData = PageBuilder.buildFullPageData(report);
            List<List<Page>> list = pageData.getPageList();
            Iterator var28;
            if (list.size() > 0) {
                int columnCount = paper.getColumnCount();
                int w = columnCount * totalWidth + (columnCount - 1) * paper.getColumnMargin();
                int size = columnCount + (columnCount - 1);
                int[] widths = new int[size];
                for (int i = 0; i < size; ++i) {
                    int mode = (i + 1) % 2;
                    if (mode == 0) {
                        widths[i] = paper.getColumnMargin();
                    } else {
                        widths[i] = totalWidth;
                    }
                }
                float tableHeight = ((Rectangle) pageSize).getHeight() - (float) paper.getTopMargin() - (float) paper.getBottomMargin();
                Map<Row, Map<Column, Cell>> cellMap = report.getRowColCellMap();
                var28 = list.iterator();
                while (var28.hasNext()) {
                    List<Page> pages = (List) var28.next();
                    PdfPTable table = new PdfPTable(size);
                    table.setLockedWidth(true);
                    table.setTotalWidth((float) w);
                    table.setWidths(widths);
                    table.setHorizontalAlignment(0);
                    int ps = pages.size();
                    int left;
                    label124:
                    for (left = 0; left < ps; ++left) {
                        if (left > 0) {
                            PdfPCell pdfMarginCell = new PdfPCell();
                            pdfMarginCell.setBorder(0);
                            table.addCell(pdfMarginCell);
                        }
                        Page page = (Page) pages.get(left);
                        PdfPTable childTable = new PdfPTable(colSize);
                        childTable.setLockedWidth(true);
                        childTable.setTotalWidth((float) totalWidth);
                        childTable.setWidths(columnsWidth);
                        childTable.setHorizontalAlignment(0);
                        List<Row> rows = page.getRows();
                        Iterator var36 = rows.iterator();
                        while (true) {
                            Map colMap;
                            do {
                                if (!var36.hasNext()) {
                                    float childTableHeight = childTable.calculateHeights();
                                    if (tableHeight > childTableHeight) {
                                        for (int j = 0; j < columns.size(); ++j) {
                                            PdfPCell lastCell = new PdfPCell();
                                            lastCell.setBorder(0);
                                            childTable.addCell(lastCell);
                                        }
                                    }
                                    PdfPCell pdfContainerCell = new PdfPCell(childTable);
                                    pdfContainerCell.setBorder(0);
                                    table.addCell(pdfContainerCell);
                                    continue label124;
                                }
                                Row row = (Row) var36.next();
                                colMap = (Map) cellMap.get(row);
                            } while (colMap == null);
                            Iterator var39 = columns.iterator();
                            while (var39.hasNext()) {
                                Column col = (Column) var39.next();
                                if (col.getWidth() >= 1) {
                                    Cell cell = (Cell) colMap.get(col);
                                    if (cell != null) {
                                        int cellHeight = this.buildCellHeight(cell, rows);
                                        PdfPCell pdfcell = this.buildPdfPCell(cell, cellHeight);
                                        childTable.addCell(pdfcell);
                                    }
                                }
                            }
                        }
                    }
                    if (ps < columnCount) {
                        left = columnCount - ps;
                        for (int i = 0; i < left; ++i) {
                            PdfPCell pdfMarginCell = new PdfPCell();
                            pdfMarginCell.setBorder(0);
                            table.addCell(pdfMarginCell);
                            pdfMarginCell = new PdfPCell();
                            pdfMarginCell.setBorder(0);
                            table.addCell(pdfMarginCell);
                        }
                    }
                    document.add(table);
                    document.newPage();
                }
            } else {
                List<Page> pages = report.getPages();
                Map<Row, Map<Column, Cell>> cellMap = report.getRowColCellMap();
                Iterator var48 = pages.iterator();
                label93:
                while (var48.hasNext()) {
                    Page page = (Page) var48.next();
                    PdfPTable table = new PdfPTable(colSize);
                    table.setLockedWidth(true);
                    table.setTotalWidth((float) totalWidth);
                    table.setWidths(columnsWidth);
                    table.setHorizontalAlignment(0);
                    List<Row> rows = page.getRows();
                    var28 = rows.iterator();
                    while (true) {
                        Map colMap;
                        do {
                            if (!var28.hasNext()) {
                                document.add(table);
                                document.newPage();
                                continue label93;
                            }
                            Row row = (Row) var28.next();
                            colMap = (Map) cellMap.get(row);
                        } while (colMap == null);
                        Iterator var56 = columns.iterator();
                        while (var56.hasNext()) {
                            Column col = (Column) var56.next();
                            if (col.getWidth() >= 1) {
                                Cell cell = (Cell) colMap.get(col);
                                if (cell != null) {
                                    int cellHeight = this.buildCellHeight(cell, rows);
                                    PdfPCell pdfcell = this.buildPdfPCell(cell, cellHeight);
                                    table.addCell(pdfcell);
                                }
                            }
                        }
                    }
                }
            }
            document.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private int buildCellHeight(Cell cell, List<Row> rows) {
        int height = cell.getRow().getRealHeight();
        int rowSpan = cell.getPageRowSpan();
        if (rowSpan > 0) {
            int pos = rows.indexOf(cell.getRow());
            int start = pos + 1;
            int end = start + rowSpan - 1;

            for (int i = start; i < end; ++i) {
                height += ((Row) rows.get(i)).getRealHeight();
            }
        }

        return height;
    }

    private PdfPCell buildPdfPCell(Cell cellInfo, int cellHeight) throws Exception {
        CellStyle style = cellInfo.getCellStyle();
        CellStyle customStyle = cellInfo.getCustomCellStyle();
        CellStyle rowStyle = cellInfo.getRow().getCustomCellStyle();
        CellStyle colStyle = cellInfo.getColumn().getCustomCellStyle();
        PdfPCell cell = this.newPdfCell(cellInfo, cellHeight);
        cell.setPadding(0);
        cell.setBorder(0);
        cell.setCellEvent(new CellBorderEvent(style, customStyle));
        int rowSpan = cellInfo.getPageRowSpan();
        if (rowSpan > 0) {
            cell.setRowspan(rowSpan);
        }

        int colSpan = cellInfo.getColSpan();
        if (colSpan > 0) {
            cell.setColspan(colSpan);
        }

        Alignment align = style.getAlign();
        if (customStyle != null && customStyle.getAlign() != null) {
            align = customStyle.getAlign();
        }

        if (rowStyle != null && rowStyle.getAlign() != null) {
            align = rowStyle.getAlign();
        }

        if (colStyle != null && colStyle.getAlign() != null) {
            align = colStyle.getAlign();
        }

        if (align != null) {
            if (align.equals(Alignment.left)) {
                cell.setHorizontalAlignment(0);
            } else if (align.equals(Alignment.center)) {
                cell.setHorizontalAlignment(1);
            } else if (align.equals(Alignment.right)) {
                cell.setHorizontalAlignment(2);
            }
        }

        Alignment valign = style.getValign();
        if (customStyle != null && customStyle.getValign() != null) {
            valign = customStyle.getValign();
        }

        if (rowStyle != null && rowStyle.getValign() != null) {
            valign = rowStyle.getValign();
        }

        if (colStyle != null && colStyle.getValign() != null) {
            valign = colStyle.getValign();
        }

        if (valign != null) {
            if (valign.equals(Alignment.top)) {
                cell.setVerticalAlignment(4);
            } else if (valign.equals(Alignment.middle)) {
                cell.setVerticalAlignment(5);
            } else if (valign.equals(Alignment.bottom)) {
                cell.setVerticalAlignment(6);
            }
        }

        String bgcolor = style.getBgcolor();
        if (customStyle != null && StringUtils.isNotBlank(customStyle.getBgcolor())) {
            bgcolor = customStyle.getBgcolor();
        }

        if (rowStyle != null && StringUtils.isNotBlank(rowStyle.getBgcolor())) {
            bgcolor = rowStyle.getBgcolor();
        }

        if (colStyle != null && StringUtils.isNotBlank(colStyle.getBgcolor())) {
            bgcolor = colStyle.getBgcolor();
        }

        if (StringUtils.isNotEmpty(bgcolor)) {
            String[] colors = bgcolor.split(",");
            cell.setBackgroundColor(new BaseColor(Integer.valueOf(colors[0]), Integer.valueOf(colors[1]), Integer.valueOf(colors[2])));
        }

        return cell;
    }

    private int[] buildColumnSizeAndTotalWidth(List<Column> columns, List<Integer> list) {
        int count = 0;
        int totalWidth = 0;

        for (int i = 0; i < columns.size(); ++i) {
            Column col = (Column) columns.get(i);
            int width = col.getWidth();
            if (width >= 1) {
                ++count;
                list.add(width);
                totalWidth += width;
            }
        }

        return new int[]{count, totalWidth};
    }

    private PdfPCell newPdfCell(Cell cellInfo, int cellHeight) throws Exception {
        PdfPCell cell = null;
        Object cellData = cellInfo.getFormatData();
        if (cellData instanceof Image) {
            Image img = (Image) cellData;
            cell = new PdfPCell(this.buildPdfImage(img.getBase64Data(), 0, 0));
        } else if (cellData instanceof ChartData) {
            ChartData chartData = (ChartData) cellData;
            String base64Data = chartData.retriveBase64Data();
            if (base64Data != null) {
                Image img = new Image(base64Data, chartData.getWidth(), chartData.getHeight());
                cell = new PdfPCell(this.buildPdfImage(img.getBase64Data(), 0, 0));
            } else {
                cell = new PdfPCell();
                CellPhrase pargraph = new CellPhrase(cellInfo, "");
                cell.setPhrase(pargraph);
                cell.setFixedHeight((float) cellHeight);
            }
        } else {
            Font font = cellPhrase.buildPdfFont(cellInfo);
            cell = new PdfPCell();
            Phrase phrase = new Phrase(String.valueOf(cellData), font);
            cell.setPhrase(phrase);
            cell.setFixedHeight((float) cellHeight);
        }

        CellStyle style = cellInfo.getCellStyle();
        if (style != null && style.getLineHeight() > 0.0F) {
            cell.setLeading(style.getLineHeight(), style.getLineHeight());
        }
        return cell;
    }

    private com.itextpdf.text.Image buildPdfImage(String base64Data, int width, int height) throws Exception {
        com.itextpdf.text.Image pdfImg = null;
        InputStream input = ImageUtils.base64DataToInputStream(base64Data);

        try {
            byte[] bytes = IOUtils.toByteArray(input);
            pdfImg = com.itextpdf.text.Image.getInstance(bytes);
            float imgWidth = pdfImg.getWidth();
            float imgHeight = pdfImg.getHeight();
            if (width == 0) {
                width = Float.valueOf(imgWidth).intValue();
            }

            if (height == 0) {
                height = Float.valueOf(imgHeight).intValue();
            }

            width = UnitUtils.pixelToPoint((double) (width - 2));
            height = UnitUtils.pixelToPoint((double) (height - 2));
            pdfImg.scaleToFit((float) width, (float) height);
        } finally {
            IOUtils.closeQuietly(input);
        }

        return pdfImg;
    }

}

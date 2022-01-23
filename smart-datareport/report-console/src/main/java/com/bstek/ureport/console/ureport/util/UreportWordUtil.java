package com.bstek.ureport.console.ureport.util;

import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.console.ureport.entity.ReportEntity;
import com.bstek.ureport.definition.*;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.export.word.DxaUtils;
import com.bstek.ureport.model.*;
import com.bstek.ureport.utils.ImageUtils;
import com.bstek.ureport.utils.UnitUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class UreportWordUtil {

    private ReportBuilder reportBuilder = new ReportBuilder();

    public XWPFDocument buildWord(ReportEntity map, Connection connection) {
        XWPFDocument document = new XWPFDocument();
        if (map != null) {
            byte[] content = map.getContent().getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
            ReportDefinition reportDefinition = UreportUtil.parseReport(inputStream, map.getFullName());
            Map<String, Object> parameters = new HashMap<>();
            Report report = reportBuilder.buildReportToConnection(reportDefinition, parameters,connection);
            document = this.build(report);
        }
        return document;
    }

    //---------------------------------------生成word------------------------------------------
    public XWPFDocument build(Report report) {
        XWPFDocument document = new XWPFDocument();
        try {
            CTSectPr sectpr = document.getDocument().getBody().addNewSectPr();
            if (!sectpr.isSetPgSz()) {
                sectpr.addNewPgSz();
            }

            CTPageSz pageSize = sectpr.getPgSz();
            Paper paper = report.getPaper();
            Orientation orientation = paper.getOrientation();
            if (orientation.equals(Orientation.landscape)) {
                pageSize.setOrient(STPageOrientation.LANDSCAPE);
                pageSize.setH(BigInteger.valueOf((long) DxaUtils.points2dxa(paper.getWidth())));
                pageSize.setW(BigInteger.valueOf((long) DxaUtils.points2dxa(paper.getHeight())));
            } else {
                pageSize.setOrient(STPageOrientation.PORTRAIT);
                pageSize.setW(BigInteger.valueOf((long) DxaUtils.points2dxa(paper.getWidth())));
                pageSize.setH(BigInteger.valueOf((long) DxaUtils.points2dxa(paper.getHeight())));
            }

            int columnCount = paper.getColumnCount();
            if (paper.isColumnEnabled() && columnCount > 0) {
                CTColumns cols = CTColumns.Factory.newInstance();
                cols.setNum(new BigInteger(String.valueOf(columnCount)));
                int columnMargin = paper.getColumnMargin();
                cols.setSpace(new BigInteger(String.valueOf(DxaUtils.points2dxa(columnMargin))));
                sectpr.setCols(cols);
            }

            CTPageMar pageMar = sectpr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf((long) DxaUtils.points2dxa(paper.getLeftMargin())));
            pageMar.setRight(BigInteger.valueOf((long) DxaUtils.points2dxa(paper.getRightMargin())));
            pageMar.setTop(BigInteger.valueOf((long) DxaUtils.points2dxa(paper.getTopMargin())));
            pageMar.setBottom(BigInteger.valueOf((long) DxaUtils.points2dxa(paper.getBottomMargin())));
            List<Column> columns = report.getColumns();
            int[] intArr = this.buildColumnSizeAndTotalWidth(columns);
            int totalColumn = intArr[0];
            int tableWidth = intArr[1];
            List<Page> pages = report.getPages();
            Map<Row, Map<Column, Cell>> cellMap = report.getRowColCellMap();
            int totalPages = pages.size();
            int pageIndex = 1;

            for (Iterator var18 = pages.iterator(); var18.hasNext(); ++pageIndex) {
                Page page = (Page) var18.next();
                List<Row> rows = page.getRows();
                XWPFTable table = document.createTable(rows.size(), totalColumn);
                table.getCTTbl().getTblPr().unsetTblBorders();
                table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf((long) DxaUtils.points2dxa(tableWidth)));

                for (int rowNumber = 0; rowNumber < rows.size(); ++rowNumber) {
                    Row row = (Row) rows.get(rowNumber);
                    int height = row.getRealHeight();
                    XWPFTableRow tableRow = table.getRow(rowNumber);
                    tableRow.setHeight(DxaUtils.points2dxa(height));
                    Map<Column, Cell> colCell = (Map) cellMap.get(row);
                    if (colCell != null) {
                        int skipCol = 0;
                        Iterator var28 = columns.iterator();

                        while (var28.hasNext()) {
                            Column col = (Column) var28.next();
                            int width = col.getWidth();
                            if (width < 1) {
                                ++skipCol;
                            } else {
                                int colNumber = col.getColumnNumber() - 1 - skipCol;
                                Cell cell = (Cell) colCell.get(col);
                                if (cell != null) {
                                    XWPFTableCell tableCell = tableRow.getCell(colNumber);
                                    if (tableCell != null) {
                                        tableCell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf((long) DxaUtils.points2dxa(width)));
                                        this.buildTableCellStyle(table, tableCell, cell, rowNumber, colNumber);
                                    }
                                }
                            }
                        }
                    }
                }

                if (pageIndex < totalPages) {
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setFontSize(0);
                    run.addBreak(BreakType.PAGE);
                }
            }
        } catch (Exception var41) {
            throw new ReportComputeException(var41);
        }
        return document;
    }

    private int[] buildColumnSizeAndTotalWidth(List<Column> columns) {
        int count = 0;
        int totalWidth = 0;

        for (int i = 0; i < columns.size(); ++i) {
            Column col = (Column) columns.get(i);
            int width = col.getWidth();
            if (width >= 1) {
                ++count;
                totalWidth += width;
            }
        }

        return new int[]{count, totalWidth};
    }

    private void buildTableCellStyle(XWPFTable table, XWPFTableCell tableCell, Cell cell, int rowNumber, int columnNumber) {
        CellStyle style = cell.getCellStyle();
        CellStyle customStyle = cell.getCustomCellStyle();
        CellStyle rowStyle = cell.getRow().getCustomCellStyle();
        CellStyle colStyle = cell.getColumn().getCustomCellStyle();
        CTTcPr cellProperties = tableCell.getCTTc().addNewTcPr();
        Border leftBorder = style.getLeftBorder();
        Border rightBorder = style.getRightBorder();
        Border topBorder = style.getTopBorder();
        Border bottomBorder = style.getBottomBorder();
        if (customStyle != null) {
            if (customStyle.getLeftBorder() != null) {
                leftBorder = customStyle.getLeftBorder();
            }

            if (customStyle.getRightBorder() != null) {
                rightBorder = customStyle.getRightBorder();
            }

            if (customStyle.getTopBorder() != null) {
                topBorder = customStyle.getTopBorder();
            }

            if (customStyle.getBottomBorder() != null) {
                bottomBorder = customStyle.getBottomBorder();
            }
        }

        int rowSpan = cell.getPageRowSpan();
        int colSpan = cell.getColSpan();
        int end;
        XWPFTableCell xwpfTableCell;
        if (style.getLeftBorder() != null) {
            if (rowSpan > 0) {
                end = rowNumber + rowSpan;

                for (end = rowNumber; end < end; ++end) {
                    xwpfTableCell = table.getRow(end).getCell(columnNumber);
                    this.buildCellBorder(leftBorder, xwpfTableCell, 1);
                }
            } else {
                this.buildCellBorder(leftBorder, tableCell, 1);
            }
        }

        int lastRow;
        XWPFTableCell c;
        int i;
        if (rightBorder != null) {
            lastRow = columnNumber;
            if (colSpan > 0) {
                lastRow = columnNumber + (colSpan - 1);
            }

            if (rowSpan > 0) {
                end = rowNumber + rowSpan;

                for (i = rowNumber; i < end; ++i) {
                    c = table.getRow(i).getCell(lastRow);
                    this.buildCellBorder(style.getRightBorder(), c, 2);
                }
            } else {
                c = table.getRow(rowNumber).getCell(lastRow);
                this.buildCellBorder(rightBorder, c, 2);
            }
        }

        if (topBorder != null) {
            if (colSpan > 0) {
                end = columnNumber + colSpan;

                for (end = columnNumber; end < end; ++end) {
                    xwpfTableCell = table.getRow(rowNumber).getCell(end);
                    this.buildCellBorder(topBorder, xwpfTableCell, 3);
                }
            } else {
                this.buildCellBorder(topBorder, tableCell, 3);
            }
        }

        if (bottomBorder != null) {
            lastRow = rowNumber;
            if (rowSpan > 0) {
                lastRow = rowNumber + (rowSpan - 1);
            }

            if (colSpan > 0) {
                end = columnNumber + colSpan;

                for (i = columnNumber; i < end; ++i) {
                    c = table.getRow(lastRow).getCell(i);
                    this.buildCellBorder(bottomBorder, c, 4);
                }
            } else {
                c = table.getRow(lastRow).getCell(columnNumber);
                this.buildCellBorder(bottomBorder, c, 4);
            }
        }

        List<XWPFParagraph> paras = tableCell.getParagraphs();
        c = null;
        XWPFParagraph para;
        if (paras != null && paras.size() > 0) {
            para = (XWPFParagraph) paras.get(0);
        } else {
            para = tableCell.addParagraph();
        }

        List<XWPFRun> runs = para.getRuns();
        xwpfTableCell = null;
        XWPFRun run;
        if (runs != null && runs.size() > 0) {
            run = (XWPFRun) runs.get(0);
        } else {
            run = para.createRun();
        }

        Object value = cell.getFormatData();
        String fontFamily;
        if (value instanceof String) {
            fontFamily = value.toString();
            if (fontFamily.contains("\n")) {
                String[] line = fontFamily.split("\n");
                run.setText(line[0], 0);

                for (int k = 1; k < line.length; ++k) {
                    run.addBreak();
                    run.setText(line[k], k);
                }
            } else {
                run.setText(fontFamily);
            }
        } else if (value instanceof Number) {
            run.setText(String.valueOf(value));
        } else if (value instanceof Boolean) {
            run.setText(value.toString());
        } else if (!(value instanceof Image) && !(value instanceof ChartData)) {
            if (value instanceof Date) {
                Date date = (Date) value;
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                run.setText(sd.format(date));
            }
        } else {
            fontFamily = null;
            Image img;
            String imageType;
            if (value instanceof Image) {
                img = (Image) value;
            } else {
                ChartData chartData = (ChartData) value;
                imageType = chartData.retriveBase64Data();
                if (imageType != null) {
                    img = new Image(imageType, chartData.getWidth(), chartData.getHeight());
                } else {
                    img = new Image("", chartData.getWidth(), chartData.getHeight());
                }
            }

            String path = img.getPath();
            imageType = "png";
            if (StringUtils.isNotBlank(path)) {
                path = path.toLowerCase();
                if (!path.endsWith(".jpg") && !path.endsWith(".jpeg")) {
                    if (path.endsWith(".gif")) {
                        imageType = "gif";
                    }
                } else {
                    imageType = "jpeg";
                }
            }

            String base64Data = img.getBase64Data();
            if (StringUtils.isNotBlank(base64Data)) {
                InputStream inputStream = null;

                try {
                    inputStream = ImageUtils.base64DataToInputStream(base64Data);
                    BufferedImage bufferedImage = ImageIO.read(inputStream);
                    int width = bufferedImage.getWidth();
                    int height = bufferedImage.getHeight();
                    IOUtils.closeQuietly(inputStream);
                    inputStream = ImageUtils.base64DataToInputStream(base64Data);
                    width = UnitUtils.pixelToPoint((double) width);
                    height = UnitUtils.pixelToPoint((double) height);
                    if (imageType.equals("jpeg")) {
                        run.addPicture(inputStream, 5, "ureport-" + rowNumber + "-" + columnNumber + ".jpg", Units.toEMU((double) width), Units.toEMU((double) height));
                    } else if (imageType.equals("png")) {
                        run.addPicture(inputStream, 6, "ureport-" + rowNumber + "-" + columnNumber + ".png", Units.toEMU((double) width), Units.toEMU((double) height));
                    } else if (imageType.equals("gif")) {
                        run.addPicture(inputStream, 8, "ureport-" + rowNumber + "-" + columnNumber + ".gif", Units.toEMU((double) width), Units.toEMU((double) height));
                    }
                } catch (Exception var37) {
                    throw new ReportComputeException(var37);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                }
            }
        }

        fontFamily = style.getFontFamily();
        if (customStyle != null && StringUtils.isNotBlank(customStyle.getFontFamily())) {
            fontFamily = customStyle.getFontFamily();
        }

        if (rowStyle != null && StringUtils.isNotBlank(rowStyle.getFontFamily())) {
            fontFamily = rowStyle.getFontFamily();
        }

        if (colStyle != null && StringUtils.isNotBlank(colStyle.getFontFamily())) {
            fontFamily = colStyle.getFontFamily();
        }

        if (StringUtils.isNotBlank(fontFamily)) {
            run.setFontFamily(fontFamily);
        }

        int fontSize = style.getFontSize();
        if (customStyle != null && customStyle.getFontSize() > 0) {
            fontSize = customStyle.getFontSize();
        }

        if (rowStyle != null && rowStyle.getFontSize() > 0) {
            fontSize = rowStyle.getFontSize();
        }

        if (colStyle != null && colStyle.getFontSize() > 0) {
            fontSize = colStyle.getFontSize();
        }

        if (fontSize > 0) {
            run.setFontSize(fontSize);
        }

        boolean bold = style.getBold() == null ? false : style.getBold();
        if (customStyle != null && customStyle.getBold() != null) {
            bold = customStyle.getBold();
        }

        if (rowStyle != null && rowStyle.getBold() != null) {
            bold = rowStyle.getBold();
        }

        if (colStyle != null && colStyle.getBold() != null) {
            bold = colStyle.getBold();
        }

        if (bold) {
            run.setBold(true);
        }

        boolean italic = style.getItalic() == null ? false : style.getItalic();
        if (customStyle != null && customStyle.getItalic() != null) {
            italic = customStyle.getItalic();
        }

        if (rowStyle != null && rowStyle.getItalic() != null) {
            italic = rowStyle.getItalic();
        }

        if (colStyle != null && colStyle.getItalic() != null) {
            italic = colStyle.getItalic();
        }

        if (italic) {
            run.setItalic(true);
        }

        boolean underline = style.getUnderline() == null ? false : style.getUnderline();
        if (customStyle != null && customStyle.getUnderline() != null) {
            underline = customStyle.getUnderline();
        }

        if (rowStyle != null && rowStyle.getUnderline() != null) {
            underline = rowStyle.getUnderline();
        }

        if (colStyle != null && colStyle.getUnderline() != null) {
            underline = colStyle.getUnderline();
        }

        if (underline) {
            run.setUnderline(UnderlinePatterns.SINGLE);
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

        if (bgcolor != null) {
            CTShd ctshd = cellProperties.addNewShd();
            ctshd.setFill(this.toHex(bgcolor.split(",")));
        }

        String forecolor = style.getForecolor();
        if (customStyle != null && StringUtils.isNotBlank(customStyle.getForecolor())) {
            forecolor = customStyle.getForecolor();
        }

        if (rowStyle != null && StringUtils.isNotBlank(rowStyle.getForecolor())) {
            forecolor = rowStyle.getForecolor();
        }

        if (colStyle != null && StringUtils.isNotBlank(colStyle.getForecolor())) {
            forecolor = colStyle.getForecolor();
        }

        if (forecolor != null) {
            run.setColor(this.toHex(forecolor.split(",")));
        }

        Alignment align = style.getAlign();
        if (customStyle != null && customStyle.getAlign() != null) {
            align = customStyle.getAlign();
        }

        if (rowStyle != null && rowStyle.getAlign() != null) {
            align = rowStyle.getAlign();
        }

        if (align != null) {
            if (align.equals(Alignment.left)) {
                para.setAlignment(ParagraphAlignment.LEFT);
            } else if (align.equals(Alignment.right)) {
                para.setAlignment(ParagraphAlignment.RIGHT);
            } else if (align.equals(Alignment.center)) {
                para.setAlignment(ParagraphAlignment.CENTER);
            }
        }

        if (style.getLineHeight() > 0.0F) {
            para.setSpacingBetween((double) style.getLineHeight());
        }

        align = style.getValign();
        if (customStyle != null && customStyle.getValign() != null) {
            align = customStyle.getValign();
        }

        if (rowStyle != null && rowStyle.getValign() != null) {
            align = rowStyle.getValign();
        }

        if (colStyle != null && colStyle.getValign() != null) {
            align = colStyle.getValign();
        }

        if (align != null) {
            CTVerticalJc verticalAlign = cellProperties.addNewVAlign();
            if (align.equals(Alignment.top)) {
                verticalAlign.setVal(STVerticalJc.TOP);
            } else if (align.equals(Alignment.middle)) {
                verticalAlign.setVal(STVerticalJc.CENTER);
            } else if (align.equals(Alignment.bottom)) {
                verticalAlign.setVal(STVerticalJc.BOTTOM);
            }
        }

        int startCol = columnNumber;
        int startRow = rowNumber;
        int endRow = rowNumber;
        int endCol = columnNumber;
        if (colSpan > 0) {
            endCol = columnNumber + colSpan - 1;
        }

        if (rowSpan > 0) {
            endRow = rowNumber + rowSpan - 1;
        }

        if (columnNumber != endCol) {
            if (rowSpan > 0) {
                for (i = rowNumber; i <= endRow; ++i) {
                    this.mergeCellsHorizontal(table, i, startCol, endCol);
                }
            } else {
                this.mergeCellsHorizontal(table, rowNumber, columnNumber, endCol);
            }
        }

        if (rowNumber != endRow) {
            if (colSpan > 0) {
                for (i = startCol; i <= endCol; ++i) {
                    this.mergeCellsVertically(table, i, startRow, endRow);
                }
            } else {
                this.mergeCellsVertically(table, startCol, rowNumber, endRow);
            }
        }

    }

    private void mergeCellsHorizontal(XWPFTable table, int row, int startCol, int endCol) {
        for (int cellIndex = startCol; cellIndex <= endCol; ++cellIndex) {
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
            if (cellIndex == startCol) {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }

    }

    private void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; ++rowIndex) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            if (rowIndex == fromRow) {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }

    }

    private void buildCellBorder(Border border, XWPFTableCell tableCell, int type) {
        CTTcPr cellPropertie = tableCell.getCTTc().getTcPr();
        if (cellPropertie == null) {
            cellPropertie = tableCell.getCTTc().addNewTcPr();
        }

        CTTcBorders borders = cellPropertie.getTcBorders();
        if (borders == null) {
            borders = cellPropertie.addNewTcBorders();
        }

        BorderStyle borderStyle = border.getStyle();
        CTBorder ctborder = null;
        if (type == 1) {
            ctborder = borders.addNewLeft();
        } else if (type == 2) {
            ctborder = borders.addNewRight();
        } else if (type == 3) {
            ctborder = borders.addNewTop();
        } else if (type == 4) {
            ctborder = borders.addNewBottom();
        }

        if (borderStyle.equals(BorderStyle.dashed)) {
            ctborder.setVal(STBorder.DASHED);
        } else if (borderStyle.equals(BorderStyle.doublesolid)) {
            ctborder.setVal(STBorder.DOUBLE);
        } else {
            ctborder.setVal(STBorder.SINGLE);
        }

        int borderWidth = border.getWidth();
        if (borderWidth > 1) {
            ctborder.setSz(BigInteger.valueOf((long) DxaUtils.points2dxa(borderWidth)));
        }

        String color = border.getColor();
        if (StringUtils.isNotBlank(color)) {
            ctborder.setColor(this.toHex(color.split(",")));
        }

    }

    private String toHex(String[] rgb) {
        StringBuffer sb = new StringBuffer();
        String R = Integer.toHexString(Integer.valueOf(rgb[0]));
        String G = Integer.toHexString(Integer.valueOf(rgb[1]));
        String B = Integer.toHexString(Integer.valueOf(rgb[2]));
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        sb.append(R);
        sb.append(G);
        sb.append(B);
        return sb.toString();
    }
}

package com.bstek.ureport.console.ureport.util;

import com.bstek.ureport.cache.CacheUtils;
import com.bstek.ureport.definition.CellDefinition;
import com.bstek.ureport.definition.Expand;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.exception.ReportParseException;
import com.bstek.ureport.export.builder.down.DownCellbuilder;
import com.bstek.ureport.export.builder.right.RightCellbuilder;
import com.bstek.ureport.parser.ReportParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;


public class UreportUtil {

    private static DownCellbuilder downCellParentbuilder = new DownCellbuilder();
    private static RightCellbuilder rightCellParentbuilder = new RightCellbuilder();

    //转成report内容
    public static ReportDefinition parseReport(InputStream inputStream, String fileName) {
        ReportParser reportParser = new ReportParser();
        ReportDefinition var4;
        try {
            ReportDefinition reportDefinition = reportParser.parse(inputStream, fileName);
            rebuildReportDefinition(reportDefinition);
            CacheUtils.cacheReportDefinition(fileName, reportDefinition);
            var4 = reportDefinition;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException var11) {
                throw new ReportParseException(var11);
            }
        }
        return var4;
    }

    //转成utf-8格式
    public static String decodeContent(String content) {
        if (content == null) {
            return content;
        }
        try {
            content = URLDecoder.decode(content, "utf-8");
            return content;
        } catch (Exception ex) {
            return content;
        }
    }

    private static void rebuildReportDefinition(ReportDefinition reportDefinition) {
        List<CellDefinition> cells = reportDefinition.getCells();
        for (CellDefinition cell : cells) {
            addRowChildCell(cell, cell);
            addColumnChildCell(cell, cell);
        }
        for (CellDefinition cell : cells) {
            Expand expand = cell.getExpand();
            if (expand.equals(Expand.Down)) {
                downCellParentbuilder.buildParentCell(cell, cells);
            } else if (expand.equals(Expand.Right)) {
                rightCellParentbuilder.buildParentCell(cell, cells);
            }
        }
    }

    private static void addRowChildCell(CellDefinition cell, CellDefinition childCell) {
        CellDefinition leftCell = cell.getLeftParentCell();
        if (leftCell == null) {
            return;
        }
        List<CellDefinition> childrenCells = leftCell.getRowChildrenCells();
        childrenCells.add(childCell);
        addRowChildCell(leftCell, childCell);
    }

    private static void addColumnChildCell(CellDefinition cell, CellDefinition childCell) {
        CellDefinition topCell = cell.getTopParentCell();
        if (topCell == null) {
            return;
        }
        List<CellDefinition> childrenCells = topCell.getColumnChildrenCells();
        childrenCells.add(childCell);
        addColumnChildCell(topCell, childCell);
    }

}

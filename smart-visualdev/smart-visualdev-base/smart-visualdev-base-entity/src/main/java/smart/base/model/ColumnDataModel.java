package smart.base.model;

import lombok.Data;

@Data
public class ColumnDataModel {
    private String searchList;
    private String columnList;
    private String sortList;
    private Integer type;
    private String defaultSidx;
    private String sort;
    private String hasPage;
    private Integer pageSize;
    private String treeTitle;
    private String treeDataSource;
    private String treeDictionary;
    private String treeDbTableFieldRelation;
    private String treeDbTable;
    private String treeDbTableField;
    private String treeDbTableFieldParentId;
    private String treeDbTableFieldShow;
    private String groupField;
    private String btnsList;
    private String columnBtnsList;

}

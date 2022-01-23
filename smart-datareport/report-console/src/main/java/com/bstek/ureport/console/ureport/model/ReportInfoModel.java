package com.bstek.ureport.console.ureport.model;

import lombok.Data;

@Data
public class ReportInfoModel {
    private String id;
    private String fullName;
    private String categoryId;
    private String enCode;
    private Integer enabledMark;
    private Long sortCode;
    private String description;
    private String creatorUser;
}

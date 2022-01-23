package com.bstek.ureport.console.ureport.model;

import lombok.Data;

@Data
public class ReportListVO {
    private String id;
    private String fullName;
    private String enCode;
    private String creatorUser;
    private Long creatorTime;
    private String categoryId;
    private String lastModifyUser;
    private Long lastModifyTime;
    private Integer enabledMark;
}

package com.bstek.ureport.console.ureport.model;

import lombok.Data;

@Data
public class ReportInfoVO {
    //报表内容
    private Object content;
    //报表信息
    private ReportInfoModel baseInfo;
}

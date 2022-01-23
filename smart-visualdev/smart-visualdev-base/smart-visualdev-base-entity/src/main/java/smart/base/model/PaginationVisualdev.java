package smart.base.model;


import lombok.Data;

@Data
public class PaginationVisualdev{
   private Integer type=1;
   private String keyword="";
   /**
    *0-在线开发(无表)，1-表单设计(有表)
    */
   private String  model="0";
}

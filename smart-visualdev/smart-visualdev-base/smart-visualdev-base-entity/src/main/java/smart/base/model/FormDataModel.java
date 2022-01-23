package smart.base.model;

import smart.onlinedev.model.fields.FieLdsModel;
import lombok.Data;

@Data
public class FormDataModel {
    //模块
    private String areasName;
    //功能名称
    private String className;
    //后端目录
    private String serviceDirectory;
    /**
     * 所属模块
     */
    private String module;
    /**
     * 子表名称集合
     */
    private String subClassName;


    private String formRef;
    private String formModel;
    private String size;
    private String labelPosition;
    private Integer labelWidth;
    private String formRules;
    private Integer gutter;
    private Boolean disabled;
    private String span;
    private Boolean formBtns;
    private Integer idGlobal;
    private String fields;
    private String popupType;
    private String fullScreenWidth;
    private String formStyle;
    private String generalWidth;
    private String cancelButtonText;
    private String confirmButtonText;


    private FieLdsModel children;
}

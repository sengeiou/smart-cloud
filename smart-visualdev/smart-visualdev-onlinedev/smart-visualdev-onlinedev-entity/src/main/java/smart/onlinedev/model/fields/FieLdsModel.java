package smart.onlinedev.model.fields;


import smart.onlinedev.model.fields.config.ConfigModel;
import smart.onlinedev.model.fields.props.PropsModel;
import smart.onlinedev.model.fields.slot.SlotModel;
import lombok.Data;

@Data
public class FieLdsModel {
    private ConfigModel config;
    private SlotModel slot;
    private String placeholder;
    private Object style;
    private Boolean clearable;
    private String prefixicon;
    private String suffixicon;
    private String maxlength;
    private Boolean showWordLimit;
    private Boolean readonly;
    private Boolean disabled;
    //设置默认值为空字符串
    private String vModel="";
    //关联表单id
    private String modelId="";
    //关联表单字段
    private String relationField;
    private String type;
    private Object autosize;
    private Integer step;
    private Boolean stepstrictly;
    private String controlsposition;
    private Object textStyle;
    private Integer lineHeight;
    private Integer fontSize;
    private Boolean showChinese;
    private Boolean showPassword;
    private String size;
    private Boolean filterable;
    private String multiple;//待定
    private PropsModel props;//待定
    private Boolean showAllLevels;//待定
    private String separator;
    private Boolean isrange;
    private String rangeseparator;
    private String startplaceholder;
    private String endplaceholder;
    private String format;
    private String valueformat;
    private Object pickeroptions;
    private Integer max;
    private Boolean allowhalf;
    private Boolean showText;
    private Boolean showScore;
    private Boolean showAlpha;
    private String colorformat;
    private String activetext;
    private String inactivetext;
    private String activecolor;
    private String inactivecolor;
    private Boolean activevalue;
    private Boolean inactivevalue;
    private Integer min;
    private Boolean showStops;
    private Boolean range;
    private String accept;//未找到
    private Boolean showTip;
    private Integer fileSize;
    private String sizeUnit;
    private Integer limit;
    private String contentposition;
    private String buttonText;
    private Integer level;
//    private List<OptionsModel> options;
    private String options;
    private String actionText;
    private String shadow;
}


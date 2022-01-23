package smart.base.model.Template7;


import smart.onlinedev.model.fields.FieLdsModel;
import smart.util.treeutil.SumTree;
import lombok.Data;

import java.util.List;

@Data
public class HtmlModel extends SumTree {

    //类型 栅格row,卡片card,子表table,主表mast
    private String jnpfkey;
    //json原始名称
    private String vmodel;
    //主表属性
    private FieLdsModel fieLdsModel;
    //子表list属性
    private List<FieLdsModel> tablFieLdsModel;
    //控件宽度
    private String span;
    //结束
    private String end="0";

}

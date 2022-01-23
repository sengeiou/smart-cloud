package smart.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import smart.base.vo.PageListVO;
import smart.base.vo.PaginationVO;
import lombok.Data;

import java.util.List;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:45
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionResult<T> {

    private Integer code;

    private String msg;

    private T data;
    public boolean isSuccess(){
        return this.code!=null && ActionResultCode.Success.getCode().equals(this.code);
    }
    public static ActionResult success() {
        ActionResult jsonData = new ActionResult();
        jsonData.setCode(200);
        jsonData.setMsg("Success");
        return jsonData;
    }

    public static ActionResult success(String msg) {
        ActionResult jsonData = new ActionResult();
        jsonData.setCode(200);
        jsonData.setMsg(msg);
        return jsonData;
    }

    public static ActionResult success(Object object) {
        ActionResult jsonData = new ActionResult();
        jsonData.setData(object);
        jsonData.setCode(200);
        jsonData.setMsg("Success");
        return jsonData;
    }
    public static<T> ActionResult page(List<T> list, PaginationVO pagination) {
        ActionResult jsonData = new ActionResult();
        PageListVO<T> vo = new PageListVO<>();
        vo.setList(list);
        vo.setPagination(pagination);
        jsonData.setData(vo);
        jsonData.setCode(200);
        jsonData.setMsg("Success");
        return jsonData;
    }

    public static ActionResult success(String msg, Object object) {
        ActionResult jsonData = new ActionResult();
        jsonData.setData(object);
        jsonData.setCode(200);
        jsonData.setMsg(msg);
        return jsonData;
    }

    public static ActionResult fail(Integer code, String message) {
        ActionResult jsonData = new ActionResult();
        jsonData.setCode(code);
        jsonData.setMsg(message);
        return jsonData;
    }

    public static ActionResult fail(String msg, String data) {
        ActionResult jsonData = new ActionResult();
        jsonData.setMsg(msg);
        jsonData.setData(data);
        return jsonData;
    }

    public static ActionResult fail(String msg) {
        ActionResult jsonData = new ActionResult();
        jsonData.setMsg(msg);
        jsonData.setCode(400);
        return jsonData;
    }

    public static ActionResult initialization(ApplicationCode code) {
        ActionResult jsonData = new ActionResult();
        jsonData.setMsg(code.getMessage());
        jsonData.setCode(code.getCode());
        return jsonData;
    }


}

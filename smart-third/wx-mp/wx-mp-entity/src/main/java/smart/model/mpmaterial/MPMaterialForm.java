package smart.model.mpmaterial;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class MPMaterialForm {
    //2-图片,3-语音,4-视频，5-图文
    @NotBlank(message = "必填")
    private String materialsType;
    private String fileJson;
    private String title;
    private String introduction;
    private String digest;
    private String author;
    private String content;
    private String contentSourceUrl;
}

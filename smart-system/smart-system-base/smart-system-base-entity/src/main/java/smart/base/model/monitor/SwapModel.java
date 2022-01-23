package smart.base.model.monitor;

import lombok.Data;

@Data
public class SwapModel {
    private String total;
    private String available;
    private String used;
    private String usageRate;
}

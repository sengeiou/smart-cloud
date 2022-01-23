package smart.geomagnetism;

import io.swagger.annotations.Api;
import smart.geomagnetism.model.EntryORExitModel;
import smart.geomagnetism.service.GeomagnetismService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 路牙设备
 */
@RestController
@RequestMapping("/device/geomagnetism")
@Slf4j
@Api(tags = "路牙设备接入", description = "device")
public class GeomagnetismController {

    @Autowired
    private GeomagnetismService roadToothService;


    /**
     * @description 车辆进出场
     * @param: request
     * @param: imei
     * @param: sign  modify deviceStatus   0 进场  1 出场
     * @param: images  多个图片路径使用 逗号，隔开
     * @param: plate
     */
    @PostMapping("/status")
    public Object status(EntryORExitModel model) {
        log.info("status start,model:{}", model);
        return roadToothService.status(model);
    }
    /**
     * @description 车辆进出场
     * @param: request
     * @param: imei
     * @param: sign  modify deviceStatus   0 进场  1 出场
     * @param: images  多个图片路径使用 逗号，隔开
     * @param: plate
     */
    @PostMapping("/statusInfo")
    public Object statusInfo(String sn) {
        log.info("entryORExit start,model:{}", sn);
        try {
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * @description 取证接口
     * @param: request
     * @param: imei
     * @param: sign  modify deviceStatus   0 进场  1 出场
     * @param: images  多个图片路径使用 逗号，隔开
     * @param: plate
     */
    @PostMapping("/forensics")
    public Object forensics(EntryORExitModel model) {
        log.info("forensics start,model:{}", model);
        if (StringUtils.isBlank(model.getImei())) {
            return null;
        }
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

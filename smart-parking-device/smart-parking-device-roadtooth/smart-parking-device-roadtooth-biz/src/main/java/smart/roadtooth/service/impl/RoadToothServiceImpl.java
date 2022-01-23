package smart.roadtooth.service.impl;

import smart.base.ActionResult;
import smart.entity.DeviceEntity;
import smart.enums.ExchangeEnum;
import smart.enums.RoutingKeyEnum;
import smart.service.DeviceService;
import smart.util.MQMsgModel;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import smart.roadtooth.model.EntryORExitModel;
import smart.roadtooth.service.RoadToothService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoadToothServiceImpl implements RoadToothService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private DeviceService deviceService;

    //路牙设备路由KEY
    public static String ROUTE_KEY = RoutingKeyEnum.DIRECT_ROUTE_KEY_DEVICE_ROAD_TOOTH;

    /**
     * 设备状态数据
     */
    @Override
    public ActionResult status(EntryORExitModel model) {
        ActionResult rs = deviceService.deviceVerification(model.getImei());
        if(!rs.isSuccess()){
            return rs;
        }
        businessMsgPushMQ(model);
        return ActionResult.success();
    }

    /**
     * MQ业务数据推送
     *
     * @param model
     */
    private void businessMsgPushMQ(EntryORExitModel model) {
        MQMsgModel msg = new MQMsgModel();
        msg.setMsg(model);
        rabbitTemplate.convertAndSend(ExchangeEnum.DEVICE_MSG_DIRECT_EXCHANGE, ROUTE_KEY, msg, new CorrelationData(UUID.randomUUID().toString()));
    }

}

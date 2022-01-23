package smart.configure;

import smart.enums.ExchangeEnum;
import smart.enums.QueueEnum;
import smart.enums.RoutingKeyEnum;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectRabbitConfig {
    /**
     * DeviceMsgDirectExchange的Direct类型的交换机
     * @return
     */
    @Bean
    DirectExchange DeviceMsgDirectExchange() {
        return new DirectExchange(ExchangeEnum.DEVICE_MSG_DIRECT_EXCHANGE, true, false);
    }

    /**
     * 路牙队列
     * @return
     */
    @Bean
    public Queue RoadToothDeviceMsgDirectQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，有消息者订阅本队列，然后所有消费者都解除订阅此队列，会自动删除。
        // arguments：队列携带的参数，比如设置队列的死信队列，消息的过期时间等等。
        return new Queue(QueueEnum.ROADTOOTH_DEVICE_MSG_DIRECT_QUEUE, true);
    }

    /**
     * 道闸队列
     * @return
     */
    @Bean
    public Queue LotRailingDeviceMsgDirectQueue() {
        return new Queue(QueueEnum.LOT_RAILING_DEVICE_MSG_DIRECT_QUEUE, true);
    }

    /**
     * 地磁设备队列
     * @return
     */
    @Bean
    public Queue GeomagnetismDeviceMsgDirectQueue() {
        return new Queue(QueueEnum.GEOMAGNETISM_DEVICE_MSG_DIRECT_QUEUE, true);
    }


    /**
     * 路牙设备
     * 绑定交换机和队列
     * @return
     */
    @Bean
    Binding bindingRoadToothDirect() {
        //bind队列to交换机中with路由key（routing key）
        return BindingBuilder.bind(RoadToothDeviceMsgDirectQueue()).to(DeviceMsgDirectExchange()).with(RoutingKeyEnum.DIRECT_ROUTE_KEY_DEVICE_ROAD_TOOTH);
    }
    /**
     * 道闸设备
     * 绑定交换机和队列
     * @return
     */
    @Bean
    Binding bindingLotRailingDirect() {
        //bind队列to交换机中with路由key（routing key）
        return BindingBuilder.bind(LotRailingDeviceMsgDirectQueue()).to(DeviceMsgDirectExchange()).with(RoutingKeyEnum.DIRECT_ROUTE_KEY_DEVICE_LOT_RAILING);
    }
    /**
     * 地磁设备
     * 绑定交换机和队列
     * @return
     */
    @Bean
    Binding bindingGeomagnetismDirect() {
        //bind队列to交换机中with路由key（routing key）
        return BindingBuilder.bind(GeomagnetismDeviceMsgDirectQueue()).to(DeviceMsgDirectExchange()).with(RoutingKeyEnum.DIRECT_ROUTE_KEY_DEVICE_GEOMAGNETISM);
    }
}

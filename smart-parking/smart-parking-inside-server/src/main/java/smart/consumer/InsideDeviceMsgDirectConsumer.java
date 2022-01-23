package smart.consumer;

import com.rabbitmq.client.Channel;
import smart.enums.ExchangeEnum;
import smart.enums.MsgAction;
import smart.enums.QueueEnum;
import smart.enums.RoutingKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 路内设备消息订阅
 */
@Slf4j
@Component
public class InsideDeviceMsgDirectConsumer {


    /**
     * 路牙设备消息订阅
     * 手动确认模式
     *
     * @param message
     * @param channel
     */

    @RabbitHandler
    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = QueueEnum.ROADTOOTH_DEVICE_MSG_DIRECT_QUEUE, durable = "true"),//如果括号中不指定队列名称，那么这时候创建的就是临时队列，当消费者连接断开的时候，该队列就会消失
            exchange = @Exchange(value = ExchangeEnum.DEVICE_MSG_DIRECT_EXCHANGE, durable = "true", type = "direct"),
            key = RoutingKeyEnum.DIRECT_ROUTE_KEY_DEVICE_ROAD_TOOTH)})
    public void process(Channel channel, Message message) {
        log.debug("message:{}", message);
        MsgAction action = MsgAction.ACCEPT;
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody(), "UTF-8");
            log.info("订阅到低位视频设备消息：body:{}", body);
            action = MsgAction.ACCEPT;

        } catch (Exception e) {
            //无需重试的错误 根据异常种类决定是ACCEPT、RETRY还是 REJECT
            action = MsgAction.REJECT;
            e.printStackTrace();
        } finally {
            try {
                if (action == MsgAction.ACCEPT) {
                    //由于配置设置了手动应答，所以这里要进行一个手动应答。注意：如果设置了自动应答，这里又进行手动应答，会出现double ack，那么程序会报错。
                    //确认收到消息，消息将被队列移除；false只确认当前consumer一个消息收到，true确认所有consumer获得的消息。
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                } else if (action == MsgAction.RETRY) {
                    //确认否定消息，第一个boolean表示一个consumer还是所有，第二个boolean表示requeue是否重新回到队列，true重新入队
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                } else {
                    //拒绝消息，requeue=false 表示不再重新入队，如果配置了死信队列则进入死信队列。
                    channel.basicNack(tag, false, false);
                }
            } catch (IOException e) {
                //异常处理
                e.printStackTrace();
            }
        }
    }

    /**
     * 地磁设备消息订阅
     * 手动确认模式
     *
     * @param message
     * @param channel
     */

    @RabbitHandler
    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = QueueEnum.GEOMAGNETISM_DEVICE_MSG_DIRECT_QUEUE, durable = "true"),//如果括号中不指定队列名称，那么这时候创建的就是临时队列，当消费者连接断开的时候，该队列就会消失
            exchange = @Exchange(value = ExchangeEnum.DEVICE_MSG_DIRECT_EXCHANGE, durable = "true", type = "direct"),
            key = RoutingKeyEnum.DIRECT_ROUTE_KEY_DEVICE_GEOMAGNETISM)})
    public void process2(Channel channel, Message message) {
        log.debug("message:{}", message);
        MsgAction action = MsgAction.ACCEPT;
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody(), "UTF-8");
            log.info("订阅到地磁设备消息：body:{}", body);
            action = MsgAction.ACCEPT;

        } catch (Exception e) {
            //根据异常种类决定是ACCEPT、RETRY还是 REJECT
            action = MsgAction.REJECT;
            e.printStackTrace();
        } finally {
            try {
                if (action == MsgAction.ACCEPT) {
                    //由于配置设置了手动应答，所以这里要进行一个手动应答。注意：如果设置了自动应答，这里又进行手动应答，会出现double ack，那么程序会报错。
                    //确认收到消息，消息将被队列移除；false只确认当前consumer一个消息收到，true确认所有consumer获得的消息。
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                } else if (action == MsgAction.RETRY) {
                    //确认否定消息，第一个boolean表示一个consumer还是所有，第二个boolean表示requeue是否重新回到队列，true重新入队
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                } else {
                    //拒绝消息，requeue=false 表示不再重新入队，如果配置了死信队列则进入死信队列。
                    channel.basicNack(tag, false, false);
                }
            } catch (IOException e) {
                //异常处理
                e.printStackTrace();
            }
        }
    }

}

package smart.geomagnetism.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import smart.geomagnetism.model.EntryORExitModel;
import smart.geomagnetism.service.GeomagnetismService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeomagnetismServiceImpl implements GeomagnetismService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 设备状态数据
     */
    @Override
    public String status(EntryORExitModel model) {

        String sn = model.getImei();
        //设备校验是否存在、是否绑定泊位

        return "successs";
    }


    /*
     *//**
     * 订阅模式测试
     *//*
    public String testSendMsg(){
        RabbitMQProducer producer=new RabbitMQProducer(rabbitTemplate);
        producer.sendRabbitmqDirect(RoutingKeyEnum.DIRECT_KEY1.getCode(), "I'm Direct!");
        return "successs";
    }

    *//**
     * 主题模式测试
     *
     *//*
    @RequestMapping("/testTopic")
    public String topicSendMsg (){
        RabbitMQProducer producer=new RabbitMQProducer(rabbitTemplate);
        *//*producer.sendRabbitmqTopic("1.TOPIC.1", "测试 1.TOPIC.1" );
        producer.sendRabbitmqTopic("TOPIC.1", "测试 TOPIC.1" );
        producer.sendRabbitmqTopic("cc", "测试 cc" );*//*

        //测试是否执行returncallback
        producer.sendRabbitmqTopic("1.1", "测试1.1" );
        producer.sendRabbitmqTopic("2.2", "测试2.2" );
        return "successs";
    }

    *//**
     * 广播模式测试
     *//*
    public String fanoutSendMsg(){
        RabbitMQProducer producer=new RabbitMQProducer(rabbitTemplate);
        //发送对象
        User user=new User();
        user.setUserName("李四");
        user.setUserAge(20);
        user.setUserSchool("测试学校");
        producer.sendRabbitmqFanout(user);

        //发送json数据
        JSONObject json=new JSONObject();
        json.put("aa","i'm aa");
        json.put("bb","i'm bb");
        json.put("cc","i'm cc");

        JSONObject jsonSon=new JSONObject();
        jsonSon.put("dd","i'm dd");
        json.put("jsonSon",jsonSon);
        producer.sendRabbitmqFanout(json);
        return "successs";
    }*/

}

package com.atguigu.gmall.order.mq;

import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

@Component
public class OrderListener {

    @Autowired
    private OrderService orderService;

    @JmsListener(containerFactory = "jmsQueueListener", destination = "PAYMENT_SUCCESS_QUEUE")
    public void consumePaymentSuccess(MapMessage mapMessage) throws JMSException {

        String trackingNo = mapMessage.getString("trackingNo");
        String outTradeNo = mapMessage.getString("outTradeNo");

        //修改订单状态
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setTrackingNo(trackingNo);
        orderInfo.setOrderStatus("已支付");
        orderInfo.setProcessStatus("准备出库");

        orderService.updateOrderStatus(orderInfo);

        //发送订单消息给库存
        orderService.sentOrderResultQueue(orderInfo.getOutTradeNo());


        System.out.println("订单支付成功的监听器。。。trackingNo:" + trackingNo + "。。。outTradeNo:" + outTradeNo);
    }
}

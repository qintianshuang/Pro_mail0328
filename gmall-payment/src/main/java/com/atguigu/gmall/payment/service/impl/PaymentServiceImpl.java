package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import com.atguigu.gmall.util.ActiveMQUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Autowired
    private AlipayClient alipayClient;

    @Override
    public void savePayment(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    /***
     * 订单完成后保存订单信息
     * @param paymentInfo
     */
    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("outTradeNo");

        paymentInfoMapper.updateByExampleSelective(paymentInfo, example);
    }


    /***
     *
     * @param tradeNo
     * @param outTradeNo
     */
    @Override
    public void sendPaymentSuccessQueue(String tradeNo, String outTradeNo, String callbackContent) {

        //修改支付信息
        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setCallbackContent(callbackContent);//
        paymentInfo.setAlipayTradeNo(tradeNo);
        updatePayment(paymentInfo);


        try {
            //建立mq连接
            Connection connection = activeMQUtil.getConnection();
            connection.start();

            //通过连接创建一次mp会话任务
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue testqueue = session.createQueue("PAYMENT_SUCCESS_QUEUE");

            //通过mp的会话任务将队列消息发送出去
            MessageProducer producer = session.createProducer(testqueue);

            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("trackingNo", tradeNo);
            mapMessage.setString("outTradeNo", outTradeNo);

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mapMessage);

            //提交任务
            session.commit();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
        System.out.println("支付成功，发送支付成功消息队列。。。。。");
    }

    /***
     * 延迟消息队列
     * @param outTradeNo
     * @param count
     */
    @Override
    public void sendPaymentCheckQueue(String outTradeNo, int count) {


        try {
            //建立mq连接
            Connection connection = activeMQUtil.getConnection();
            connection.start();

            //通过连接创建一次mp会话任务
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue testqueue = session.createQueue("PAYMENT_CHECK_QUEUE");

            //通过mp的会话任务将队列消息发送出去
            MessageProducer producer = session.createProducer(testqueue);

            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setInt("count", count);
            mapMessage.setString("outTradeNo", outTradeNo);
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000 * 20);

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mapMessage);

            //提交任务
            session.commit();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
        System.out.println("。。。发送第" + (6 - count) + "次的消息队列。。。。");
    }


    /***
     * 调用支付宝检查接口看是否支付成功
     * @param outTradeNo
     * @return
     */
    @Override
    public Map<String, String> checkPaymentStatus(String outTradeNo) {

        HashMap<String, String> stringStringMap = new HashMap<>();

        //调用支付宝检查接口
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("out_trade_no", outTradeNo);
        String s = JSON.toJSONString(stringObjectHashMap);
        request.setBizContent(s);

        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //判断是否支付
        if (response.isSuccess()) {
            //支付成功状态
            String tradeStatus = response.getTradeStatus();
            String tradeNo = response.getTradeNo();
            String callbackContent = response.getBody();
            String status = tradeStatus;

            stringStringMap.put("status", status);
            stringStringMap.put("tradeNo", tradeNo);
            stringStringMap.put("callbackContent", callbackContent);
        } else {
            //若用户未支付或停留在支付页面
            System.out.println("用户未扫码");
        }
        return stringStringMap;
    }

    /***
     * 通过数据库信息判断是否支付成功
     * @param outTradeNo
     * @return
     */
    @Override
    public boolean checkPaied(String outTradeNo) {
        boolean b = false;

        if (outTradeNo != null) {
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOutTradeNo(outTradeNo);
            PaymentInfo paymentInfo1 = paymentInfoMapper.selectOne(paymentInfo);

            if (paymentInfo1 != null && paymentInfo1.getPaymentStatus().equals("已支付")) {
                b = true;
            }
        }
        return b;
    }
}

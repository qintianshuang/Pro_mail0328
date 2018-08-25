package com.atguigu.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.payment.service.PaymentService;
import com.atguigu.gmall.payment.util.AlipayConfig;
import com.atguigu.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

    @Reference
    private OrderService orderService;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private PaymentService paymentService;

    @RequestMapping("alipay/callback/return")
    public String callbackReturn(HttpServletRequest request, String orderId, ModelMap map) {

        //将异步通知中收到的所有参数都存放到map中
        Map<String, String> paramsMap = null;
        boolean signVerified = true;
        try {
            //调用SDK验证签名
            signVerified = AlipaySignature.rsaCheckV1(paramsMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        } catch (Exception e) {
            System.out.println("此处支付宝的签名验证通过。。。");
        }

        if (signVerified) {
            //TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，
            // 校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure

            String tradeNo = request.getParameter("trade_no");
            String outTradeNo = request.getParameter("out_trade_no");
            String tradeStatus = request.getParameter("trade_status");

            String callbackContent = request.getQueryString();

            //进行幂等性检查
            boolean b = paymentService.checkPaied(outTradeNo);
            //发送支付成功的消息PAYMENT_SECCESS_QUEUE
            if (!b){
                paymentService.sendPaymentSuccessQueue(tradeNo, outTradeNo, callbackContent);
            }
        } else {
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            //返回失败的页面
        }
        return "testPaySuccess";
    }


    @RequestMapping("index")
    public String index(String orderId, ModelMap map) {
        String userId = "1";

        OrderInfo orderInfo = orderService.getOrderById(orderId);

        map.put("orderId", orderId);
        map.put("outTradeNo", orderInfo.getOutTradeNo());
        map.put("totalAmount", orderInfo.getTotalAmount());

        return "index";
    }


    @RequestMapping("alipay/submit")
    @ResponseBody
    public String alipaySubmit(String orderId, ModelMap map) {

        String userId = "1";

        OrderInfo orderById = orderService.getOrderById(orderId);

        //生成和保存支付信息
        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setOutTradeNo(orderById.getOutTradeNo());
        paymentInfo.setTotalAmount(orderById.getTotalAmount());
        paymentInfo.setOrderId(orderById.getId());
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setSubject(orderById.getOrderDetailList().get(0).getSkuName());
        paymentInfo.setCreateTime(new Date());

        paymentService.savePayment(paymentInfo);

        //重定向到支付宝
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.return_payment_url);//在公共参数中设置回跳和通知地址

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("out_trade_no", orderById.getOutTradeNo());
        stringObjectHashMap.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //stringObjectHashMap.put("total_amount",orderById.getTotalAmount());
        stringObjectHashMap.put("total_amount", 0.01);
        stringObjectHashMap.put("subject", "测试硅谷手机");

        String json = JSON.toJSONString(stringObjectHashMap);

        alipayRequest.setBizContent(json);//填充业务参数
        String form = "";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //System.out.println(form);

        System.out.println("设置一个定时巡检订单" + paymentInfo.getOutTradeNo() + "的支付状态的延迟队列");
        paymentService.sendPaymentCheckQueue(paymentInfo.getOutTradeNo(), 5);
        return form;
    }


    /***
     * 微信支付
     * @param orderId
     * @param map
     * @return
     */
    @RequestMapping("mx/submit")
    public String mxSubmit(String orderId, ModelMap map) {

        return "mxTest";
    }

}

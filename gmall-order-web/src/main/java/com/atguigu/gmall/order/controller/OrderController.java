package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.OrderDetail;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.enums.PaymentWay;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    private CartService cartService;

    @Reference
    private UserService userService;

    @Reference
    private OrderService orderService;

    @Reference
    private SkuService skuService;

    /***
     * 订单模块，必须登录才能访问
     * @param request
     * @param response
     * @param map
     * @return
     */

    @LoginRequire(ifNeedSuccess = true)
    @RequestMapping("submitOrder")
    private String submitOrder(String tradeCode,HttpServletRequest request, HttpServletResponse response, ModelMap map){

        String userId = (String) request.getAttribute("userId");
        //比较交易码
        boolean b = orderService.checkTradeCode(tradeCode,userId);

        //订单对象
        OrderInfo orderInfo = new OrderInfo();
        List<OrderDetail> orderDetails = new ArrayList<>();

        if (b){
            //执行提交订单业务

            //获取购物车中被选中的的商品数据
            List<CartInfo> cartInfos = cartService.getCartCacheByCheck(userId);
            //生成订单数据
            //验单验库存

            for (CartInfo cartInfo : cartInfos) {
                OrderDetail orderDetail = new OrderDetail();
                BigDecimal skuPrice = cartInfo.getSkuPrice();
                String skuId = cartInfo.getSkuId();
                //验价
                boolean bprice = skuService.checkPrice(skuPrice,skuId);

                //验库存

                if (bprice){
                    orderDetail.setSkuId(cartInfo.getSkuId());
                    orderDetail.setSkuName(cartInfo.getSkuName());
                    orderDetail.setOrderPrice(cartInfo.getCartPrice());
                    orderDetail.setImgUrl(cartInfo.getImgUrl());
                    orderDetail.setSkuNum(cartInfo.getSkuNum());

                    orderDetails.add(orderDetail);
                }else {
                    //sku校验失败
                    map.put("errMsg","订单中的商品价格发生变化，请重新下单！");
                    return "tradeFail";
                }
            }

            //订单商品详情
            orderInfo.setOrderDetailList(orderDetails);

            //封装订单信息
            orderInfo.setProcessStatus("订单未支付");

            //订单过期时间
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE,1);
            orderInfo.setCreateTime(c.getTime());

            //支付状态
            orderInfo.setOrderStatus("未支付");

            //收件人
            String consignee  = "测试收件人";
            orderInfo.setConsignee(consignee);

            //外部订单号
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String format = sdf.format(new Date());//订单时间
            String outTradeNo = "ATGUIGU" + format + System.currentTimeMillis();
            orderInfo.setOutTradeNo(outTradeNo);

            //支付方式
            orderInfo.setPaymentWay(PaymentWay.ONLINE);

            //用户id
            orderInfo.setUserId(userId);

            //订单名
            orderInfo.setOrderComment("硅谷订单");

            //价格
            orderInfo.setTotalAmount(getTotalPrice(cartInfos));

            //收件人联系方式
            String deliveryAddress = "测试收件人地址";
            orderInfo.setDeliveryAddress(deliveryAddress);

            //订单日期
            orderInfo.setCreateTime(new Date());

            //联系电话
            String Tel = "1365728757";
            orderInfo.setConsigneeTel(Tel);


            //保存订单
            String orderInfoId = orderService.saveOrder(orderInfo);
            //删除购物车中的提交的商品信息
            cartService.deleteCartById(cartInfos);
            //对接支付系统接口
            return "redirect:http://payment.gmall.com:8087/index?orderId=" + orderInfoId;

        }else{
            map.put("errMsg","获取订单失败");
            return "tradeFail";
        }
    }


    /***
     * 订单模块，必须登录才能访问
     * @param request
     * @param response
     * @param map
     * @return
     */

    @LoginRequire(ifNeedSuccess = true)
    @RequestMapping("toTrade")
    private String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap map){

        //需要单点登录的拦截器拦截下来
        String userId = (String) request.getAttribute("userId");

        //将被选中购物车对象转化为订单对象
        List<CartInfo> cartInfos = cartService.getCartCacheByCheck(userId);
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (CartInfo cartInfo : cartInfos) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetailList.add(orderDetail);
        }

        //查询用户地址列表，让用户选择
        List<UserAddress> userAddressList = userService.getUserAddressList(userId);

        //生成动态交易码
        String tradeCode = orderService.genTradeCode(userId);

        map.put("tradeCode",tradeCode);
        map.put("userAddressList",userAddressList);
        map.put("orderDetailList",orderDetailList);
        map.put("totalAmount",getTotalPrice(cartInfos));

        return "trade";
    }


    private BigDecimal getTotalPrice(List<CartInfo> cartInfos) {
        BigDecimal b = new BigDecimal("0");

        for (CartInfo cartInfo : cartInfos) {
            if (cartInfo.getIsChecked().equals("1")){
                b = b.add(cartInfo.getCartPrice());
            }
        }

        return b;
    }
}

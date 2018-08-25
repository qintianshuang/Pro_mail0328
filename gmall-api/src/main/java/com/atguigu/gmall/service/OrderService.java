package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OrderInfo;

public interface OrderService {

    String genTradeCode(String userId);

    boolean checkTradeCode(String tradeCode, String userId);

    String saveOrder(OrderInfo orderInfo);

    OrderInfo getOrderById(String orderId);

    void updateOrderStatus(OrderInfo orderInfo);

    void sentOrderResultQueue(String outTradeNo);
}

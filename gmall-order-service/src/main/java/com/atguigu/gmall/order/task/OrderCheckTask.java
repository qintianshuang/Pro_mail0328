package com.atguigu.gmall.order.task;

import com.atguigu.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class OrderCheckTask {

    @Autowired
    private OrderService orderService;

    @Scheduled(cron = "0/5 * * * * ?")//每隔10秒扫描一次过期订单
    public void work() throws InterruptedException{

        System.out.println("定时检查过期订单，删除过期订单，由orderService来执行");
    }
}

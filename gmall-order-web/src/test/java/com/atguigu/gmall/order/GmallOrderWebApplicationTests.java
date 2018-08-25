package com.atguigu.gmall.order;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallOrderWebApplicationTests {

    public static void main(String[] args) {
        //日期的计算
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,1);
        System.out.println(c.getTime());


        //日期的格式化
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date());
        System.out.println(format);

    }

    @Test
    public void contextLoads() {
    }

}

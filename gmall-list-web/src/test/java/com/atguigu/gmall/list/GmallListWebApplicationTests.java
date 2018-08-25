package com.atguigu.gmall.list;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListWebApplicationTests {

    public static void main(String[] args) throws IOException {
        File file = new File("");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write("json".getBytes("UTF-8"));
    }

    @Test
    public void contextLoads() {
    }

}

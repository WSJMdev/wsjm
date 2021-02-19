package com.project.wsjm;

import org.junit.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.runner.RunWith;

@SpringBootTest
@RunWith(SpringRunner.class)
public class test {
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Test
    public void contextLoads(){

    }
    @Test
    public void testSqlSession() throws Exception{
        System.out.println(sqlSessionTemplate.toString());
    }
}

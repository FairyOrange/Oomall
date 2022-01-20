package cn.edu.xmu.oomall.other.dao;

import cn.edu.xmu.oomall.share.ShareApplication;
import cn.edu.xmu.oomall.share.dao.BeShareDao;
import cn.edu.xmu.oomall.util.JwtHelper;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Qiuyan Qian
 * @date Created in 2020/12/02 21:05
 */
@Slf4j
@SpringBootTest(classes = ShareApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
public class BeShareDaoTest {
    @Autowired
    BeShareDao beShareDao;


    /**
     * 测试dao层getAdminBeShares方法
     * @param
     * @return void
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/2 下午10:39
    */
    @Test
    public void getBeSharesBySpuIds1() throws Exception{
        Long spuId = 422L;

        ReturnObject retObj = beShareDao.getAdminBeShares(spuId,null,null,1,10);
        assertEquals(ResponseCode.OK,retObj.getCode());
    }


}

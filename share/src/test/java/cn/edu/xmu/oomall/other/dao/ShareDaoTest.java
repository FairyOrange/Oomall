package cn.edu.xmu.oomall.other.dao;

import cn.edu.xmu.oomall.share.ShareApplication;
import cn.edu.xmu.oomall.share.dao.ShareDao;
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
 * @date Created in 2020/12/02 22:53
 */
@Slf4j
@SpringBootTest(classes = ShareApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
public class ShareDaoTest {
    @Autowired
    private ShareDao shareDao;

    @Test
    public void getAllShareTest() throws Exception{
        Long spuId = 422L;

        ReturnObject retObj = shareDao.getAllShares(spuId, 1,10);
        assertEquals(ResponseCode.OK,retObj.getCode());
    }
}

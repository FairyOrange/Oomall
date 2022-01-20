package cn.edu.xmu.oomall.cart.controller;


import cn.edu.xmu.oomall.cart.CartApplication;
import cn.edu.xmu.oomall.util.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


/**
 * 买家将商品加入购物车 测试用例类
 *
 * @author yang8miao
 * createdBy yang8miao 2020/11/26 21:34
 * modifiedBy yang8miao 2020/12/1 21:35
 */
@Slf4j
@SpringBootTest(classes = CartApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CartControllerTest1 {

    @Autowired
    private MockMvc mvc;

    /**
     * 创建测试用token
     *
     * @author yang8miao
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy yang8miao 2020/11/29 00:12
     * modifiedBy yang8miao 2020/11/29 00:12
     */
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }


}

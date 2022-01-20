package cn.edu.xmu.oomall.cart.controller;


import cn.edu.xmu.oomall.cart.CartApplication;
import cn.edu.xmu.oomall.cart.model.vo.CartVo;
import cn.edu.xmu.oomall.util.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSONObject;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * 买家修改购物车单个商品的数量或规格 测试用例类
 *
 * @author yang8miao
 * createdBy yang8miao 2020/11/26 21:34
 * modifiedBy yang8miao 2020/12/1 21:35
 */
@Slf4j
@SpringBootTest(classes = CartApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CartControllerTest3 {

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

    /**
     * 买家修改购物车单个商品的数量或规格  普通测试,修改成功1
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/29 16:26
     */
    @Test
    public void putCartsId1() throws Exception{
        String responseString = null;
        String token = this.createTestToken(4L, 2L, 10000);

        CartVo testVo = new CartVo();
        testVo.setGoodsSkuId((long)77);
        testVo.setQuantity(20);

        try {
            responseString = this.mvc.perform(put("/other/carts/491508")
                    .header("authorization", token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JSONObject.toJSONString(testVo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 买家修改购物车单个商品的数量或规格  普通测试,修改成功2
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/29 16:32
     */
    @Test
    public void putCartsId2() throws Exception{
        String responseString = null;
        String token = this.createTestToken(10L, 2L, 10000);

        CartVo testVo = new CartVo();
        testVo.setGoodsSkuId((long)641);
        testVo.setQuantity(200);

        try {
            responseString = this.mvc.perform(put("/other/carts/491515")
                    .header("authorization", token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JSONObject.toJSONString(testVo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 买家修改购物车单个商品的数量或规格  普通测试,该购物车id不存在,修改失败
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/29 16:32
     */
    @Test
    public void putCartsId3() throws Exception{
        String responseString = null;
        String token = this.createTestToken(10L, 2L, 10000);

        CartVo testVo = new CartVo();
        testVo.setGoodsSkuId((long)641);
        testVo.setQuantity(200);

        try {
            responseString = this.mvc.perform(put("/other/carts/4")
                    .header("authorization", token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JSONObject.toJSONString(testVo)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\n" +
                "  \"errno\": 504,\n" +
                "  \"errmsg\": \"该购物车id不存在\"\n" +
                "}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 买家修改购物车单个商品的数量或规格  普通测试,该购物车id所属买家与操作用户不一致,修改失败
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/29 16:32
     */
    @Test
    public void putCartsId4() throws Exception{
        String responseString = null;
        String token = this.createTestToken(100L, 2L, 10000);

        CartVo testVo = new CartVo();
        testVo.setGoodsSkuId((long)641);
        testVo.setQuantity(200);

        try {
            responseString = this.mvc.perform(put("/other/carts/491515")
                    .header("authorization", token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JSONObject.toJSONString(testVo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\n" +
                "  \"errno\": 505,\n" +
                "  \"errmsg\": \"该购物车id所属买家与操作用户不一致\"\n" +
                "}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}

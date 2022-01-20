package cn.edu.xmu.oomall.favorite.controller;


import cn.edu.xmu.oomall.favorite.FavoriteApplication;
import cn.edu.xmu.oomall.util.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * 买家查看所有收藏的商品 测试用例类
 *
 * @author yang8miao
 * createdBy yang8miao 2020/11/26 21:34
 * modifiedBy yang8miao 2020/12/1 21:35
 */
@Slf4j
@SpringBootTest(classes = FavoriteApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FavoriteControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * 创建测试用token
     *
     * @author  yang8miao
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy yang8miao 2020/11/28 20:14
     * modifiedBy yang8miao 2020/11/28 20:14
     */
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }

    /**
     * 买家查看所有收藏的商品  普通测试，查询成功 page=1&pageSize=1
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/28 20:49
     */
    @Test
    public void getFavorites1() throws Exception{
        String responseString = null;
        String token = this.createTestToken(20L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/favorites?page=1&pageSize=1")
                    .header("authorization", token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\":\"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 3,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 491525,\n" +
                "        \"goodsSkuId\": 528,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 买家查看所有收藏的商品  普通测试，查询成功 page=1&pageSize=10
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/28 20:49
     */
    @Test
    public void getFavorites2() throws Exception{
        String responseString = null;
        String token = this.createTestToken(11026L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/favorites?page=1&pageSize=10")
                    .header("authorization", token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\":\"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 502442,\n" +
                "        \"goodsSkuId\": 371,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 567976,\n" +
                "        \"goodsSkuId\": 277,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:29\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 535209,\n" +
                "        \"goodsSkuId\": 447,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:28\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}

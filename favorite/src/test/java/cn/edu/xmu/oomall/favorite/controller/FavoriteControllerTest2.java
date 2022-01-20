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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * 买家删除某个收藏的商品 测试用例类
 *
 * @author yang8miao
 * createdBy yang8miao 2020/11/26 21:34
 * modifiedBy yang8miao 2020/12/1 21:35
 */
@Slf4j
@SpringBootTest(classes = FavoriteApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FavoriteControllerTest2 {

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
     * 买家删除某个收藏的商品  普通测试，删除成功1
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/28 22:40
     */
    @Test
    public void deleteFavoritesId1() throws Exception{
        String responseString = null;
        String token = this.createTestToken(20L, 2L, 10000);

        try {
            responseString = this.mvc.perform(delete("/other/favorites/528")
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
                "  \"errmsg\": \"成功\"\n" +
                "}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 买家删除某个收藏的商品  普通测试，删除成功2
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/28 22:40
     */
    @Test
    public void deleteFavoritesId2() throws Exception{
        String responseString = null;
        String token = this.createTestToken(14706L, 2L, 10000);

        try {
            responseString = this.mvc.perform(delete("/other/favorites/54")
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
                "  \"errmsg\": \"成功\"\n" +
                "}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 买家删除某个收藏的商品  普通测试，删除失败，该用户未收藏该商品
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/28 22:40
     */
    @Test
    public void deleteFavoritesId3() throws Exception{
        String responseString = null;
        String token = this.createTestToken(14712L, 2L, 10000);

        try {
            responseString = this.mvc.perform(delete("/other/favorites/210")
                    .header("authorization", token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\n" +
                "  \"errno\": 504,\n" +
                "  \"errmsg\": \"该用户未收藏该商品\"\n" +
                "}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}

package cn.edu.xmu.oomall.other;

import cn.edu.xmu.oomall.share.ShareApplication;
import cn.edu.xmu.oomall.share.model.vo.BeSharedCreateVo;
import cn.edu.xmu.oomall.util.JacksonUtil;
import cn.edu.xmu.oomall.util.JwtHelper;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @auther Qiuyan Qian
 * @date Created in 2020/11/25 21:37
 */
@Slf4j
@SpringBootTest(classes = ShareApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
public class ShareApplicationTests2 {

    @Autowired
    private MockMvc mvc;

    /**
     * @description 创建测试用token
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * @author Qiuyan Qian
     * @date Created in 2020/11/25 15:35
     *
     */
    private final String createTestToken(Long userId, Long departId, int expireTime){
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.debug("test token: "+token);
        return token;
    }

    /**
     * @desctiption 查询所有分享活动 无query参数 成功
     * @throws Exception
     * @author Qiuyan Qian
     * @date Created in 2020/11/25 下午9:38
     */
//    @Test
//    public void getShareActivities1() throws Exception{
//        String responseString = null;
//        String token = this.createTestToken(9L, 2L, 10000);
//
//        try {
//            responseString = this.mvc.perform(get("/other/shareactivities").header("authorization", token))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        String expectedResponse = "{\"errno\": 0,\n" +
//                "  \"data\": {\n" +
//                "    \"total\": 2,\n" +
//                "    \"pages\": 1,\n" +
//                "    \"pageSize\": 2,\n" +
//                "    \"page\": 1,\n" +
//                "    \"list\": [\n" +
//                "      {\n" +
//                "        \"id\": 1,\n" +
//                "        \"shopId\": 2,\n" +
//                "        \"goodsSpuId\": 3,\n" +
//                "        \"beginTime\": \"2020-11-24T17:06:26\",\n" +
//                "        \"endTime\": \"2020-11-24T17:06:26\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": 2,\n" +
//                "        \"shopId\": 3,\n" +
//                "        \"goodsSpuId\": 4,\n" +
//                "        \"beginTime\": \"2020-11-24T17:06:26\",\n" +
//                "        \"endTime\": \"2020-11-24T17:06:26\"\n" +
//                "      }\n" +
//                "    ]\n" +
//                "  },\n" +
//                "  \"errmsg\": \"成功\"}";
//        try{
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//    }

    /**
     * @description 查询所有分享活动 用spuId 和shopId 筛选
     * @author Qiuyan Qian
     * @date  Created in 2020/11/25 下午10:05
     */
    @Test
    public void getShareActivities2() throws Exception {
        String result = null;
        String token = this.createTestToken(9L, 0L, 10000);

        try {
            result = this.mvc.perform(get("/other/shareactivities?shopId=0&spuId=385").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$..shopId").exists())
                    .andExpect(jsonPath("$..goodsSpuId").exists())
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug(result);
    }

    /**
     * @description 下线分享活动
     * @throws Exception
     * @auther  Qiuyan Qian
     * @date  Created in 2020/11/26 上午12:01
    */
    @Test
    public void deleteShareActivity() throws Exception{
        String responseString = null;
        String token = this.createTestToken(9L, 0L, 10000);

        try {
            responseString = this.mvc.perform(delete("/other/shops/0/shareactivities/212967").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{ \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上线分享活动 抛出已上线错误
     * @param
     * @return void
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/2 下午7:55
    */
    @Test
    public void putShareActivityOnline() throws Exception{
        String responseString = null;
        String token = this.createTestToken(9L, 0L, 10000);
        try{
            responseString = this.mvc.perform(put("/other/shops/0/shareactivities/212967/online").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{ \"errno\": 615,\n" +
                "  \"errmsg\": \"该分享活动已上线\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void postBeShare1() throws Exception{
//        String responseString = null;
//        String token = this.createTestToken(9L, 0L, 10000);
//        BeSharedCreateVo vo = new BeSharedCreateVo();
//        vo.setShareId(245732L);
//        String requireJson = JacksonUtil.toJson(vo);
//        try{
//            responseString = this.mvc.perform(post("/other/goods/442/beshared")
//                    .header("authorization", token)
//                    .contentType("application/json;charset=UTF-8").content(requireJson))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String expectedResponse = "{ \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
}

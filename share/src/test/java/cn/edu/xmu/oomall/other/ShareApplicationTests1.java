package cn.edu.xmu.oomall.other;

import cn.edu.xmu.oomall.share.ShareApplication;
import cn.edu.xmu.oomall.util.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @auther Qiuyan Qian
 * @date Created in 2020/11/25 20:14
 */
@Slf4j
@SpringBootTest(classes = ShareApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
public class ShareApplicationTests1 {
    @Autowired
    private MockMvc mvc;

    /**
     * 创建测试用token
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
     * 分享者查询所有分享成功记录 无query参数 成功
     * @throws Exception
     * @author Qiuyan Qian
     * @date Created in 2020/11/25 下午8:28
     */
    @Test
    public void getBeShared1() throws Exception{
        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/beshared").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 40956,\n" +
                "        \"goodsSpuId\": 577,\n" +
                "        \"sharerId\": 9,\n" +
                "        \"customerId\": 5,\n" +
                "        \"orderItemId\": null,\n" +
                "        \"rebate\": null,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:26\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 41797,\n" +
                "        \"goodsSpuId\": 539,\n" +
                "        \"sharerId\": 9,\n" +
                "        \"customerId\": 4896,\n" +
                "        \"orderItemId\": null,\n" +
                "        \"rebate\": null,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:26\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 45583,\n" +
                "        \"goodsSpuId\": 153,\n" +
                "        \"sharerId\": 9,\n" +
                "        \"customerId\": 17322,\n" +
                "        \"orderItemId\": null,\n" +
                "        \"rebate\": null,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:26\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 分享者查询所有分享成功记录 开始时间在结束时间之后
     * @throws Exception
     * @author Qiuyan Qian
     * @date Created in 2020/11/25 下午8:29
     */
    @Test
    public void getBeShared2() throws Exception{
        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/beshared?beginTime=2020-11-25 00:00:00&endTime=2020-11-24 00:00:00").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{ \"errno\": 610,\n" +
                "  \"errmsg\": \"开始时间在结束时间之后\"}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    /**
     * @description 分享者查询所有分享成功记录 用spuId 筛选
     * @author Qiuyan Qian
     * @date  Created in 2020/11/25 下午8:41
     */
    @Test
    public void getBeShared3() throws Exception{
        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/beshared?spuId=577").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{ \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 40956,\n" +
                "        \"goodsSpuId\": 577,\n" +
                "        \"sharerId\": 9,\n" +
                "        \"customerId\": 5,\n" +
                "        \"orderItemId\": null,\n" +
                "        \"rebate\": null,\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:26\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 查询所有分享记录 传入时间格式错误
     * @param
     * @return void
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/5 下午7:40
    */
    @Test
    public void getBeShared4() throws Exception{
        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);
        try {
            responseString = this.mvc.perform(get("/other/beshared?beginTime=2020-11-24 00:00:61&endTime=2020-11-25 00:00:00").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{ \"errno\": 503,\n" +
                "  \"errmsg\": \"传入时间格式错误\"}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}

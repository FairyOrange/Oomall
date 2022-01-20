package cn.edu.xmu.oomall.time.controller;

import cn.edu.xmu.oomall.time.TimeApplication;
import cn.edu.xmu.oomall.util.JwtHelper;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest(classes = TimeApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TimeControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * 创建测试用token
     *
     * @author 24320182203281 王纬策
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }

    /**
     * 平台管理员新增广告时间段测试 成功
     * @auth cxr
     * @createTime 2020/12/4 16:04
     * @modifyTime 2020/12/4 16:04
     */
    @Test
    public void postAdvertisementTimesegmentsTest1(){
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;

        log.debug("管理员新增广告时间段");
        try {
            responseString = this.mvc.perform(MockMvcRequestBuilders.post("http://localhost:8082/other/advertisement/timesegments")
                    .header("authorization", token)
                    .contentType("application/json;charset=UTF-8")
                    .content("{\"beginTime\":\"2020-10-01 18:00:00\",\"endTime\":\"2020-10-02 23:00:00\"}"))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\n" +
                "  \"code\": \"OK\",\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 3,\n" +
                "    \"beginTime\": \"2020-10-01 18:00:00\",\n" +
                "    \"endTime\": \"2020-10-02 23:00:00\",\n" +
                //"    \"gmtCreate\": \"2020-12-04 21:51:29\",\n" +
                "    \"gmtModified\": null\n" +
                "  }\n" +
                "}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 平台管理员新增广告时间段测试 添加的时段重合
     * @auth cxr
     * @createTime 2020/12/4 16:04
     * @modifyTime 2020/12/4 16:04
     */
    @Test
    public void postAdvertisementTimesegmentsTest2(){
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;

        log.debug("管理员新增广告时间段");
        try {
            responseString = this.mvc.perform(MockMvcRequestBuilders.post("http://localhost:8082/other/advertisement/timesegments")
                    .header("authorization", token)
                    .contentType("application/json;charset=UTF-8")
                    .content("{\"beginTime\":\"2020-10-01 19:00:00\",\"endTime\":\"2020-10-02 21:00:00\"}"))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\n" +
                "  \"code\": \"TIMESEG_CONFLICT\",\n" +
                "  \"errmsg\": \"时段冲突\",\n" +
                "  \"data\": null\n" +
                "}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

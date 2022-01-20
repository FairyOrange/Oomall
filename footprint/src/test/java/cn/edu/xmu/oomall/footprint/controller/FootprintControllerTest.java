package cn.edu.xmu.oomall.footprint.controller;

import cn.edu.xmu.oomall.footprint.FootprintApplication;
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
 * 管理员查看浏览记录 测试用例类
 *
 * @author yang8miao
 * createdBy yang8miao 2020/11/26 21:34
 * modifiedBy yang8miao 2020/12/1 21:35
 */
@Slf4j
@SpringBootTest(classes = FootprintApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FootprintControllerTest {

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
     * createdBy yang8miao 2020/11/26 21:34
     * modifiedBy yang8miao 2020/11/26 21:34
     */
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }


    /**
     * 管理员查看浏览记录  普通测试，查询成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/26 21:07
     */
    @Test
    public void getFootprints1() throws Exception{
        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/footprints?userId=220&page=1&pageSize=10").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1212599,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 291\n" +
//                "          \"name\": \"+\",\n" +
//                "          \"skuSn\": null,\n" +
//                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\n" +
//                "          \"inventory\": 1,\n" +
//                "          \"originalPrice\": 130000,\n" +
//                "          \"price\": 130000,\n" +
//                "          \"disable\": 0\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-12-07T21:47:22\"\n" +
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
     * 管理员查看浏览记录 普通测试，查询成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/28 19:19
     */
    @Test
    public void getFootprints2() throws Exception{
        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/footprints?userId=134&page=1&pageSize=1")
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
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 134,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 355,\n" +
//                "          \"name\": \"+\",\n" +
//                "          \"skuSn\": null,\n" +
//                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59708f23dcbc8.jpg\",\n" +
//                "          \"inventory\": 1,\n" +
//                "          \"originalPrice\": 18000,\n" +
//                "          \"price\": 18000,\n" +
//                "          \"disable\": 4\n" +
                "        },\n" +
                "        \"gmtCreate\": \"2020-11-24T17:06:27\"\n" +
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
     * 管理员查看浏览记录 普通测试，查询成功，但未查到任何足迹
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/11/28 19:19
     */
    @Test
    public void getFootprints3() throws Exception{
        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/footprints?userId=17330&page=10&pageSize=10").header("authorization", token))
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
                "    \"page\": 10,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"list\": [\n" +
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
     * 管理员查看浏览记录 普通测试，开始时间大于结束时间,返回错误码
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 21:11
     */
    @Test
    public void getFootprints4() throws Exception{
        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/footprints?beginTime=2022-11-24 12:00:00&endTime=2020-11-11 12:00:00")
                    .header("authorization", token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\n" +
                "  \"errno\": 610,\n" +
                "  \"errmsg\":\"开始时间大于结束时间\"\n" +
                "}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}

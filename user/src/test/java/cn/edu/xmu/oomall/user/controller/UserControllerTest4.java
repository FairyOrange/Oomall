package cn.edu.xmu.oomall.user.controller;

import cn.edu.xmu.oomall.user.UserApplication;
import cn.edu.xmu.oomall.user.model.vo.LoginVo;
import cn.edu.xmu.oomall.user.model.vo.UserVo;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * 平台管理员获取所有用户列表 测试用例类
 *
 * @author yang8miao
 * createdBy yang8miao 2020/12/3 21:14
 * modifiedBy yang8miao 2020/12/3 21:14
 */
@Slf4j
@SpringBootTest(classes = UserApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest4 {

    @Autowired
    private MockMvc mvc;

    /**
     * 创建测试用token
     *
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy yang8miao 2020/11/30 19:20
     * modifiedBy yang8miao 2020/11/30 19:20
     * @author yang8miao
     */
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }

    /**
     * 平台管理员获取所有用户列表
     *
     * @author yang8miao
     * @date 2020/12/3 22:47
     * @version 1.0
     */
    @Test
    public void getUsersAll1() throws Exception {

        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/users/all?userName=1&page=1&pageSize=10").header("authorization", token))
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
                "        \"id\": 1,\n" +
                "        \"userName\": \"1\",\n" +
                "        \"realName\": \"87874178390\"\n" +
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
     * 平台管理员获取所有用户列表
     *
     * @author yang8miao
     * @date 2020/12/3 22:47
     * @version 1.0
     */
    @Test
    public void getUsersAll2() throws Exception {

        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/users/all?page=2&pageSize=3").header("authorization", token))
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
                "    \"page\": 2,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"total\": 20,\n" +
                "    \"pages\": 7,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 4,\n" +
                "        \"userName\": \"4\",\n" +
                "        \"realName\": \"39088919422\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 5,\n" +
                "        \"userName\": \"5\",\n" +
                "        \"realName\": \"38220212271\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 6,\n" +
                "        \"userName\": \"yang8miao\",\n" +
                "        \"realName\": \"yang8miao\"\n" +
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
     * 平台管理员获取所有用户列表
     *
     * @author yang8miao
     * @date 2020/12/3 22:47
     * @version 1.0
     */
    @Test
    public void getUsersAll3() throws Exception {

        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/users/all?email=1982567430@qq.com").header("authorization", token))
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
                "        \"id\": 6,\n" +
                "        \"userName\": \"yang8miao\",\n" +
                "        \"realName\": \"yang8miao\"\n" +
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

}

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
 * 获得买家的所有状态 测试用例类
 *
 * @author yang8miao
 * createdBy yang8miao 2020/12/3 21:14
 * modifiedBy yang8miao 2020/12/3 21:14
 */
@Slf4j
@SpringBootTest(classes = UserApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest1 {

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
     * 获得买家的所有状态
     *
     * @author yang8miao
     * @date 2020/12/3 21:54
     * @version 1.0
     */
    @Test
    public void getUsersState1() throws Exception {

        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        try {
            responseString = this.mvc.perform(get("/other/users/states").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"code\": 0,\n" +
                "      \"name\": \"后台用户\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": 4,\n" +
                "      \"name\": \"正常用户\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": 6,\n" +
                "      \"name\": \"被封禁用户\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        try{
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


}

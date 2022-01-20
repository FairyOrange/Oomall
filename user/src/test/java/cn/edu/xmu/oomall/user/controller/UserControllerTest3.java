package cn.edu.xmu.oomall.user.controller;

import cn.edu.xmu.oomall.user.UserApplication;
import cn.edu.xmu.oomall.user.model.vo.LoginVo;
import cn.edu.xmu.oomall.user.model.vo.UserModifyInfoVo;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * 买家修改自己的信息 测试用例类
 *
 * @author yang8miao
 * createdBy yang8miao 2020/12/3 22:33
 * modifiedBy yang8miao 2020/12/3 22:33
 */
@Slf4j
@SpringBootTest(classes = UserApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest3 {

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
     * 买家修改自己的信息  修改测试1
     *
     * @author yang8miao
     * @date 2020/12/3 22:34
     * @version 1.0
     */
    @Test
    public void putUsers1() throws Exception {

        String responseString = null;
        String token = this.createTestToken(9L, 2L, 10000);

        UserModifyInfoVo testVo = new UserModifyInfoVo();
        testVo.setRealName("yang8miao");
        testVo.setGender((byte)0);
        testVo.setBirthday(LocalDate.of(2000,1,25));

        try {
            responseString = this.mvc.perform(put("/other/users").header("authorization", token)
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
     * 买家修改自己的信息  修改测试2
     *
     * @author yang8miao
     * @date 2020/12/3 22:34
     * @version 1.0
     */
    @Test
    public void putUsers2() throws Exception {

        String responseString = null;
        String token = this.createTestToken(1L, 2L, 10000);

        UserModifyInfoVo testVo = new UserModifyInfoVo();
        testVo.setRealName("yang");
        testVo.setGender((byte)1);
        testVo.setBirthday(LocalDate.of(2020,1,25));

        try {
            responseString = this.mvc.perform(put("/other/users").header("authorization", token)
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

}

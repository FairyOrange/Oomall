package cn.edu.xmu.oomall.user.controller;

import cn.edu.xmu.oomall.user.UserApplication;
import cn.edu.xmu.oomall.user.model.vo.LoginVo;
import cn.edu.xmu.oomall.user.model.vo.UserVo;
import cn.edu.xmu.oomall.util.JwtHelper;
import cn.edu.xmu.oomall.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Slf4j
@SpringBootTest(classes = UserApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    /**
     * 创建测试用token
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }
    private static String token;
    /**
     * 用户注册
     */
    @Test
    @Order(1)
    public void userTest1() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        JSONObject body = new JSONObject();
        body.put("userName", "testu");
        body.put("realName", "testr");
        body.put("password", "1a2B3_");
        body.put("birthday", "1999-11-26");
        body.put("mobile", "13912347890");
        body.put("email", "1234@4321.com");
        body.put("gender", 0);
        requireJson = body.toJSONString();
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .post("/other/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":{\"id\":17330,\"userName\":\"testu\",\"name\":\"testr\",\"mobile\":\"13912347890\",\"email\":\"1234@4321.com\",\"gender\":0,\"birthday\":\"1999-11-26\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 商城用户注册用户名重复
     */
    @Test
    @Order(2)
    public void userTest2() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        JSONObject body = new JSONObject();
        body.put("userName", "testu");
        body.put("realName", "testr1");
        body.put("password", "1a2B3_");
        body.put("birthday", "1999-11-26");
        body.put("mobile", "13912347891");
        body.put("email", "1234@43211.com");
        body.put("gender", 0);
        requireJson = body.toJSONString();
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .post("/other/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":731,\"errmsg\":\"用户名已被注册\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 商城用户邮箱重复
     */
    @Test
    @Order(2)
    public void userTest3() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        JSONObject body = new JSONObject();
        body.put("userName", "testu1");
        body.put("realName", "testr1");
        body.put("password", "1a2B3_");
        body.put("birthday", "1999-11-26");
        body.put("mobile", "13912347891");
        body.put("email", "1234@4321.com");
        body.put("gender", 0);
        requireJson = body.toJSONString();
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .post("/other/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":732,\"errmsg\":\"邮箱已被注册\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 商城用户手机号重复
     */
    @Test
    @Order(2)
    public void userTest4() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        JSONObject body = new JSONObject();
        body.put("userName", "testu1");
        body.put("realName", "testr1");
        body.put("password", "1a2B3_");
        body.put("birthday", "1999-11-26");
        body.put("mobile", "13912347890");
        body.put("email", "1234@43211.com");
        body.put("gender", 0);
        requireJson = body.toJSONString();
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .post("/other/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":733,\"errmsg\":\"电话已被注册\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 商城用户注册信息为空
     */
    @Test
    @Order(2)
    public void userTest5() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        JSONObject body = new JSONObject();
        body.put("userName", "");
        body.put("realName", "");
        body.put("password", "1a2B3_");
        body.put("birthday", "");
        body.put("mobile", "");
        body.put("email", "");
        body.put("gender", 0);
        requireJson = body.toJSONString();
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .post("/other/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":503}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 商城用户注册信息格式不正确
     */
    @Test
    @Order(2)
    public void userTest6() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        JSONObject body = new JSONObject();
        body.put("userName", "testu2");
        body.put("realName", "testr2");
        body.put("password", "123456");
        body.put("birthday", "1999-11-50");
        body.put("mobile", "139123478902");
        body.put("email", "123443212.com");
        body.put("gender", -1);
        requireJson = body.toJSONString();
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .post("/other/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 商城用户登录
     */
    @Test
    @Order(3)
    public void userTest7() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        JSONObject body = new JSONObject();
        body.put("userName", "testu");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .post("/other/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        this.token = JSONObject.parseObject(responseString).getString("data");
    }

    /**
     * 商城用户登录密码错误
     */
    @Test
    @Order(3)
    public void userTest8() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        JSONObject body = new JSONObject();
        body.put("userName", "testu");
        body.put("password", "123456");
        requireJson = body.toJSONString();
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .post("/other/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":700,\"errmsg\":\"用户名不存在或者密码错误\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 商城用户查看状态
     */
    @Test
    @Order(4)
    public void userTest9() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .get("/other/users/states")
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"后台用户\"},{\"code\":4,\"name\":\"正常用户\"},{\"code\":6,\"name\":\"被封禁用户\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 买家查看自己信息
     */
    @Test
    @Order(4)
    public void userTest10() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .get("/other/users")
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"testu\",\"name\":\"testr\",\"mobile\":\"13912347890\",\"email\":\"1234@4321.com\",\"gender\":0,\"birthday\":\"1999-11-26\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 买家修改自己信息
     */
    @Test
    @Order(5)
    public void userTest11() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        JSONObject body = new JSONObject();
        body.put("realName", "testr2");
        body.put("birthday", "1999-11-27");
        body.put("gender", 1);
        requireJson = body.toJSONString();
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .put("/other/users")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 用户重置密码
     */
    @Test
    @Order(5)
    public void userTest12() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        JSONObject body = new JSONObject();
        body.put("userName", "testu");
        body.put("email", "1234@4321.com");
        requireJson = body.toJSONString();
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .put("/other/users/password/reset")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 用户修改密码
     */
    @Test
    @Order(5)
    public void userTest13() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        JSONObject body = new JSONObject();
        body.put("captcha", "RVOenW");
        body.put("newPassword", "123#Aa");
        requireJson = body.toJSONString();
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .put("/other/users/password")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":700,\"errmsg\":\"用户名不存在或者密码错误\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 用户登出
     */
    @Test
    @Order(6)
    public void userTest14() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .get("/other/users/logout")
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 平台管理员获取所有用户列表
     */
    @Test
    @Order(7)
    public void userTest15() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        String jwt = creatTestToken(1L, 0L, 100);
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .get("/other/users/all")
                .header("authorization", jwt))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":{\"total\":17232,\"pages\":1724,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"userName\":\"8606245097\",\"realName\":\"16886485849\"},{\"id\":2,\"userName\":\"36040122840\",\"realName\":\"92371771011\"},{\"id\":3,\"userName\":\"7306155755\",\"realName\":\"3085304240\"},{\"id\":4,\"userName\":\"14455881448\",\"realName\":\"27274869128\"},{\"id\":5,\"userName\":\"8906373389\",\"realName\":\"90454802932\"},{\"id\":6,\"userName\":\"39118189028\",\"realName\":\"73167461224\"},{\"id\":7,\"userName\":\"63088258694\",\"realName\":\"76088564541\"},{\"id\":8,\"userName\":\"46613241589\",\"realName\":\"87175602919\"},{\"id\":9,\"userName\":\"17857289610\",\"realName\":\"71539402260\"},{\"id\":10,\"userName\":\"19769355952\",\"realName\":\"74668244342\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员查看任意买家信息
     */
    @Test
    @Order(7)
    public void userTest16() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        String jwt = creatTestToken(1L, 0L, 100);
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .get("/other/users/{id}", 1)
                .header("authorization", jwt))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"userName\":\"8606245097\",\"name\":\"16886485849\",\"mobile\":\"13959288888\",\"email\":null,\"gender\":0,\"birthday\":null,\"state\":4,\"gmtCreate\":\"2020-12-07 21:47:15\",\"gmtModified\":\"2020-12-07 21:47:15\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 平台管理员封禁买家
     */
    @Test
    @Order(7)
    public void userTest17() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        String jwt = creatTestToken(1L, 0L, 100);
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .put("/other/users/{id}/ban", 1)
                .header("authorization", jwt))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    /**
     * 平台管理员解禁买家
     */
    @Test
    @Order(7)
    public void userTest18() throws Exception {
        String requireJson = null;
        String responseString = null;
        String expectedResponse = null;
        String jwt = creatTestToken(1L, 0L, 100);
        responseString = this.mvc.perform(MockMvcRequestBuilders
                .put("/other/users/{id}/release", 1)
                .header("authorization", jwt))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
}

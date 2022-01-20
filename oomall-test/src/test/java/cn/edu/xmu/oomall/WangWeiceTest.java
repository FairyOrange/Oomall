package cn.edu.xmu.oomall;

import cn.edu.xmu.oomall.util.JacksonUtil;
import cn.edu.xmu.oomall.util.JwtHelper;
import cn.edu.xmu.oomall.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * 测试
 * @author 王纬策
 * @date 2020/12/07 12:28
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WangWeiceTest {

    private String managementGate = "http://127.0.0.1:8881";

    private String mallGate = "http://127.0.0.1:8880";

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    public WangWeiceTest(){
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl(managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl(mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }

    /**
     * 商城用户注册并登录成功
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest1() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "1111");
        body.put("realName", "11111");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12332111111");
        body.put("email", "11111@11111.com");
        body.put("gender", 0);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"1111\",\"name\":\"11111\",\"mobile\":\"12332111111\",\"email\":\"11111@11111.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "1111");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String token = JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    /**
     * 商城用户注册用户名重复
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest2() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "2222");
        body.put("realName", "22222");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12342112222");
        body.put("email", "22222@22222.com");
        body.put("gender", 0);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"2222\",\"name\":\"22222\",\"mobile\":\"12342112222\",\"email\":\"22222@22222.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        log.debug("尝试再次注册");
        body = new JSONObject();
        body.put("userName", "2222");
        body.put("realName", "222222");
        body.put("password", "1a2B4_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12342122222");
        body.put("email", "222222@22222.com");
        body.put("gender", 1);
        requireJson = body.toJSONString();
        responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.USER_NAME_REGISTERED.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.USER_NAME_REGISTERED.getMessage())
                .returnResult()
                .getResponseBodyContent();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "2222");
        body.put("password", "1a2B4_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户注册手机号重复
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest3() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "3333");
        body.put("realName", "33333");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12342113333");
        body.put("email", "33333@33333.com");
        body.put("gender", 0);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"3333\",\"name\":\"33333\",\"mobile\":\"12342113333\",\"email\":\"33333@33333.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        log.debug("尝试再次注册");
        body = new JSONObject();
        body.put("userName", "33333");
        body.put("realName", "333333");
        body.put("password", "1a2B4_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12342113333");
        body.put("email", "333333@333333.com");
        body.put("gender", 1);
        requireJson = body.toJSONString();
        responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.MOBILE_REGISTERED.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.MOBILE_REGISTERED.getMessage())
                .returnResult()
                .getResponseBodyContent();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "33333");
        body.put("password", "1a2B4_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户注册邮箱重复
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest4() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "4444");
        body.put("realName", "44444");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12342114444");
        body.put("email", "44444@44444.com");
        body.put("gender", 0);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"4444\",\"name\":\"44444\",\"mobile\":\"12342114444\",\"email\":\"44444@44444.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        log.debug("尝试再次注册");
        body = new JSONObject();
        body.put("userName", "44444");
        body.put("realName", "444444");
        body.put("password", "1a2B4_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12342444444");
        body.put("email", "44444@44444.com");
        body.put("gender", 1);
        requireJson = body.toJSONString();
        responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.EMAIL_REGISTERED.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.EMAIL_REGISTERED.getMessage())
                .returnResult()
                .getResponseBodyContent();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "44444");
        body.put("password", "1a2B4_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户注册用户名为空
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest5() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "");
        body.put("realName", "testR2");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12332112347");
        body.put("email", "123@321.com");
        body.put("gender", 1);
        String requireJson = body.toJSONString();
        mallClient.post().uri("/users").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
    }

    /**
     * 商城用户注册真实姓名为空
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest6() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "123");
        body.put("realName", "");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12332112347");
        body.put("email", "123@321.com");
        body.put("gender", 1);
        String requireJson = body.toJSONString();
        mallClient.post().uri("/users").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "123");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户注册真实姓名为空
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest7() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "123");
        body.put("realName", "");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12332112347");
        body.put("email", "123@321.com");
        body.put("gender", 1);
        String requireJson = body.toJSONString();
        mallClient.post().uri("/users").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "123");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户注册密码为空
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest8() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "123");
        body.put("realName", "321");
        body.put("password", "");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12332112347");
        body.put("email", "123@321.com");
        body.put("gender", 1);
        String requireJson = body.toJSONString();
        mallClient.post().uri("/users").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "123");
        body.put("password", "");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
    }

    /**
     * 商城用户注册生日为空
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest9() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "123");
        body.put("realName", "321");
        body.put("password", "1a2B3_");
        body.put("birthday", "");
        body.put("mobile", "12332112347");
        body.put("email", "123@321.com");
        body.put("gender", 1);
        String requireJson = body.toJSONString();
        mallClient.post().uri("/users").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "123");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户注册生日格式不合法
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest10() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "123");
        body.put("realName", "321");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-40");
        body.put("mobile", "12332112347");
        body.put("email", "123@321.com");
        body.put("gender", 1);
        String requireJson = body.toJSONString();
        mallClient.post().uri("/users").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "123");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户注册手机号为空
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest11() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "123");
        body.put("realName", "321");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-05");
        body.put("mobile", "");
        body.put("email", "123@321.com");
        body.put("gender", 1);
        String requireJson = body.toJSONString();
        mallClient.post().uri("/users").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "123");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户注册手机号格式不合法
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest12() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "123");
        body.put("realName", "321");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-05");
        body.put("mobile", "1234567891011");
        body.put("email", "123@321.com");
        body.put("gender", 1);
        String requireJson = body.toJSONString();
        mallClient.post().uri("/users").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "123");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户注册邮箱为空
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest13() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "123");
        body.put("realName", "321");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-05");
        body.put("mobile", "12345678910");
        body.put("email", "");
        body.put("gender", 1);
        String requireJson = body.toJSONString();
        mallClient.post().uri("/users").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "123");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户注册邮箱格式不合法
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest14() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "123");
        body.put("realName", "321");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-05");
        body.put("mobile", "12345678910");
        body.put("email", "123#321.com");
        body.put("gender", 1);
        String requireJson = body.toJSONString();
        mallClient.post().uri("/users").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "123");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户注册性别为空
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest15() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "123");
        body.put("realName", "321");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-05");
        body.put("mobile", "12345678910");
        body.put("email", "123@321.com");
        body.put("gender", "");
        String requireJson = body.toJSONString();
        mallClient.post().uri("/users").bodyValue(requireJson).exchange().expectStatus().isBadRequest();
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "123");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage());
    }

    /**
     * 商城用户登录密码错误
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest16() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "1616");
        body.put("realName", "161616");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12332116161");
        body.put("email", "1616@1616.com");
        body.put("gender", 0);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"1616\",\"name\":\"161616\",\"mobile\":\"12332116161\",\"email\":\"1616@1616.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "1616");
        body.put("password", "123456");
        requireJson = body.toJSONString();
        responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 商城用户登录成功
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest17() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "1717");
        body.put("realName", "171717");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12332117171");
        body.put("email", "1717@1717.com");
        body.put("gender", 0);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"1717\",\"name\":\"171717\",\"mobile\":\"12332117171\",\"email\":\"1717@1717.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "1717");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String token = JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    /**
     * 商城用户查看个人信息
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest18() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "testU");
        body.put("realName", "testR");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12332112345");
        body.put("email", "123@321.com");
        body.put("gender", 0);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"testU\",\"name\":\"testR\",\"mobile\":\"12332112345\",\"email\":\"123@321.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "testU");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String token = JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
        responseString = mallClient.get().uri("/users").header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"testU\",\"name\":\"testR\",\"mobile\":\"12332112345\",\"email\":\"123@321.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 商城用户查看个人信息，用户ID不存在
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest19() throws Exception {
        byte[] responseString = mallClient.get().uri("/users").header("authorization", creatTestToken(-1L, -2L, 100)).exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.INTERNAL_SERVER_ERR.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 商城用户注销
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest20() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "testU20");
        body.put("realName", "testR20");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12332112320");
        body.put("email", "2020@2020.com");
        body.put("gender", 0);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"testU20\",\"name\":\"testR20\",\"mobile\":\"12332112320\",\"email\":\"2020@2020.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "testU20");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String token = JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
        responseString = mallClient.get().uri("/users").header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"testU20\",\"name\":\"testR20\",\"mobile\":\"12332112320\",\"email\":\"2020@2020.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        mallClient.get().uri("/users/logout").header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 商城用户注销后查看个人信息
     *
     * @author 24320182203281 王纬策
     */
    @Test
    public void userTest21() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "testU21");
        body.put("realName", "testR21");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "12332112321");
        body.put("email", "2121@2121.com");
        body.put("gender", 0);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"testU21\",\"name\":\"testR21\",\"mobile\":\"12332112321\",\"email\":\"2121@2121.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        log.debug("尝试登录");
        body = new JSONObject();
        body.put("userName", "testU21");
        body.put("password", "1a2B3_");
        requireJson = body.toJSONString();
        responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String token = JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
        responseString = mallClient.get().uri("/users").header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        expectedResponse = "{\"errno\":0,\"data\":{\"userName\":\"testU21\",\"name\":\"testR21\",\"mobile\":\"12332112321\",\"email\":\"2121@2121.com\",\"gender\":0,\"birthday\":\"2020-12-09\",\"state\":4,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
        mallClient.get().uri("/users/logout").header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        mallClient.get().uri("/users").header("authorization", token).exchange().expectStatus().isUnauthorized();
    }

    /**
     * 创建测试用token
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }

    /**
     * 测试其他人
     */
    @Test
    public void testTokenNeededSuccessWithOldToken() throws Exception {
        String token = createToken(5);
        mallClient.get().uri("/users").header("authorization", token).exchange().
                expectStatus().isOk().expectHeader().value("authorization", s -> {
            Assertions.assertNotEquals(s, token);
        });
    }

    @Test
    public void testTokenNeededSuccessWithNewToken() throws Exception {
        String token = createToken(1000000);
        mallClient.get().uri("/users").header("authorization", token).exchange().
                expectStatus().isOk().expectHeader().value("authorization", s -> {
            Assertions.assertEquals(s, token);
        });
    }

    @Test
    public void testTokenNeededWithWrongToken() throws Exception {
        mallClient.get().uri("/users").header("authorization", "test").exchange().
                expectStatus().isUnauthorized().expectBody().jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode());
    }

    @Test
    public void testTokenNeededFailedTimeExpired() throws Exception {
        String token = createToken(0);
        Thread.sleep(1000);
        mallClient.get().uri("/users").header("authorization", token).exchange().
                expectStatus().isUnauthorized().expectBody().
                jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode());

    }

    @Test
    public void testTokenUnneededWithToken() throws Exception {
        String token = createToken(100);
        String requiredJson = "{\n" +
                "\t\"userName\": \"ThisNameCantExist\",\n" +
                "    \"password\": \"wrongPass\"\n" +
                "}";
        mallClient.post().uri("/users/login").header("authorization", token).bodyValue(requiredJson).
                exchange().expectBody().jsonPath("$.errno").isEqualTo(700);
    }

    @Test
    public void testTokenUnneededNormally() throws Exception {
        String requiredJson = "{\n" +
                "\t\"userName\": \"ThisNameCantExist\",\n" +
                "    \"password\": \"wrongPass\"\n" +
                "}";
        mallClient.post().uri("/users/login").bodyValue(requiredJson).
                exchange().expectBody().jsonPath("$.errno").isEqualTo(700);
    }

    @Test
    public void testTokenWithPlatformDepartId() throws Exception {
        String token = createWrongToken(0L, 100);
        mallClient.get().uri("/users").header("authorization", token).exchange().
                expectStatus().isUnauthorized().expectBody().
                jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode());
    }
    @Test
    public void testTokenWithShopDepartId() throws Exception {
        String token = createWrongToken(0L, 100);
        mallClient.get().uri("/users").header("authorization", token).exchange().
                expectStatus().isUnauthorized().expectBody().
                jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode());
    }

    String createToken(int expireTime){
        JwtHelper jwtHelper = new JwtHelper();
        String token = jwtHelper.createToken(1L, -2L, expireTime);
        return token;
    }

    String createWrongToken(Long departId,int expireTime){
        JwtHelper jwtHelper=new JwtHelper();
        String token=jwtHelper.createToken(1L,departId,expireTime);
        return token;
    }

}

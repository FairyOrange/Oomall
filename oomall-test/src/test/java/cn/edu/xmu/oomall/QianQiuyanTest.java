package cn.edu.xmu.oomall;

import cn.edu.xmu.oomall.util.JacksonUtil;
import cn.edu.xmu.oomall.util.JwtHelper;
import cn.edu.xmu.oomall.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 测试
 * @author
 * @date 2020/12/07 12:28
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QianQiuyanTest {

    @Value("${oomall-test.managementgate}")
    private String managementGate = "http://192.168.43.73:8881";

    @Value("${oomall-test.mallgate}")
    private String mallGate = "http://192.168.43.73:8880";

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    public QianQiuyanTest(){
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
     * 创建测试用token
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }

    /**
     * 用户登录
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/11 下午2:54
    */
    public String userLogin(String userName, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString)).getString("data");
    }

    /**
     * 管理员登录
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/11 下午2:54
    */
    private String adminLogin(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

    /**
     * 查询售后单的所有状态 用户 1
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午7:30
    */
    @Test
    public void getAllStateTest() throws Exception {
        byte[] responseString = mallClient.get().uri("/aftersales/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"待管理员审核\"},{\"code\":1,\"name\":\"待买家发货\"},{\"code\":2,\"name\":\"买家已发货\"},{\"code\":3,\"name\":\"待店家退款\"},{\"code\":4,\"name\":\"待店家发货\"},{\"code\":5,\"name\":\"店家已发货\"},{\"code\":6,\"name\":\"审核不通过\"},{\"code\":7,\"name\":\"已取消\"},{\"code\":8,\"name\":\"已结束\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 买家提交售后单 成功 2
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午8:33
    */
    @Test
    public void postUserAftersales1() throws Exception{
        String token = userLogin("8606245097","123456");

        JSONObject body = new JSONObject();
        body.put("consignee","testC");
        body.put("detail","testD");
        body.put("mobile","13912345678");
        body.put("quantity",5);
        body.put("reason","testR");
        body.put("regionId",(long)1);
        body.put("type",(byte)0);

        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/orderItems/1/aftersales").header("authorization", token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"orderItemId\":1,\"customerId\":1,\"type\":0,\"reason\":\"testR\",\"quantity\":5,\"regionId\":1,\"detail\":\"testD\",\"consignee\":\"testC\",\"mobile\":\"13912345678\"},\"errmsg\":\"成功\"}";
        log.info(new String(responseString, StandardCharsets.UTF_8));
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        //查询是否插入成功
        String str = JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
        Long id = Long.parseLong(JSONObject.parseObject(str).getString("id"));

        responseString = mallClient.get().uri("/aftersales/"+id).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家提交售后单 数量不能小于1 3
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午8:26
    */
    @Test
    public void postUserAftersales2() throws Exception{
        String token = userLogin("8606245097","123456");

        JSONObject body = new JSONObject();
        body.put("consignee","testC");
        body.put("detail","testD");
        body.put("mobile","13912345678");
        body.put("quantity",0);
        body.put("reason","testR");
        body.put("regionId",(long)1);
        body.put("type",(byte)0);

        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/orderItems/5/aftersales").header("authorization", token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家提交售后单 原因为空 4
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午8:34
    */
    @Test
    public void postUserAftersales3() throws Exception{
        String token = userLogin("8606245097","123456");

        JSONObject body = new JSONObject();
        body.put("consignee","testC");
        body.put("detail","testD");
        body.put("mobile","13912345678");
        body.put("quantity",5);
        body.put("reason","");
        body.put("regionId",(long)1);
        body.put("type",(byte)0);

        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/orderItems/5/aftersales").header("authorization", token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家提交售后单 联系人为空 5
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午8:35
    */
    @Test
    public void postUserAftersales4() throws Exception{
        String token = userLogin("8606245097","123456");

        JSONObject body = new JSONObject();
//        body.put("consignee","testC");
        body.put("detail","testD");
        body.put("mobile","13912345678");
        body.put("quantity",5);
        body.put("reason","testR");
        body.put("regionId",(long)1);
        body.put("type",(byte)0);

        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/orderItems/5/aftersales").header("authorization", token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家提交售后单 没有详细信息 6
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午8:37
    */
    @Test
    public void postUserAftersales5() throws Exception{
        String token = userLogin("8606245097","123456");

        JSONObject body = new JSONObject();
        body.put("consignee","testC");
//        body.put("detail","testD");
        body.put("mobile","13912345678");
        body.put("quantity",5);
        body.put("reason","testR");
        body.put("regionId",(long)1);
        body.put("type",(byte)0);

        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/orderItems/5/aftersales").header("authorization", token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家提交售后 电话不符合要求 7
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午8:41
    */
    @Test
    public void postUserAftersales6() throws Exception{
        String token = userLogin("8606245097","123456");

        JSONObject body = new JSONObject();
        body.put("consignee","testC");
        body.put("detail","testD");
        body.put("mobile","139123456789");
        body.put("quantity",5);
        body.put("reason","testR");
        body.put("regionId",(long)1);
        body.put("type",(byte)0);

        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/orderItems/5/aftersales").header("authorization", token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家提交售后 电话不能为空 8
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午8:42
    */
    @Test
    public void postUserAftersales7() throws Exception{
        String token = userLogin("8606245097","123456");

        JSONObject body = new JSONObject();
        body.put("consignee","testC");
        body.put("detail","testD");
//        body.put("mobile","13912345678");
        body.put("quantity",5);
        body.put("reason","testR");
        body.put("regionId",(long)1);
        body.put("type",(byte)0);

        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/orderItems/5/aftersales").header("authorization", token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家提交售后单 数量不能为空 9
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午8:46
    */
    @Test
    public void postUserAftersales8() throws Exception{
        String token = userLogin("8606245097","123456");

        JSONObject body = new JSONObject();
        body.put("consignee","testC");
        body.put("detail","testD");
        body.put("mobile","13912345678");
//        body.put("quantity",5);
        body.put("reason","testR");
        body.put("regionId",(long)1);
        body.put("type",(byte)0);

        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/orderItems/5/aftersales").header("authorization", token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家提交售后单 原因不能为空 10
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午8:51
    */
    @Test
    public void postUserAftersales9() throws Exception{
        String token = userLogin("8606245097","123456");

        JSONObject body = new JSONObject();
        body.put("consignee","testC");
        body.put("detail","testD");
        body.put("mobile","13912345678");
        body.put("quantity",5);
//        body.put("reason","testR");
        body.put("regionId",(long)1);
        body.put("type",(byte)0);

        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/orderItems/5/aftersales").header("authorization", token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家提交售后单 地区码不能为空 11
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午8:52
    */
    @Test
    public void postUserAftersales10() throws Exception{
        String token = userLogin("8606245097","123456");

        JSONObject body = new JSONObject();
        body.put("consignee","testC");
        body.put("detail","testD");
        body.put("mobile","13912345678");
        body.put("quantity",5);
        body.put("reason","testR");
//        body.put("regionId",(long)1);
        body.put("type",(byte)0);

        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/orderItems/5/aftersales").header("authorization", token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家提交售后单 服务类型不为空 12
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午8:53
    */
    @Test
    public void postUserAftersales11() throws Exception{
        String token = userLogin("8606245097","123456");

        JSONObject body = new JSONObject();
        body.put("consignee","testC");
        body.put("detail","testD");
        body.put("mobile","13912345678");
        body.put("quantity",5);
        body.put("reason","testR");
        body.put("regionId",(long)1);
//        body.put("type",(byte)0);

        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/orderItems/5/aftersales").header("authorization", token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家获取所有售后单信息 成功 13
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午9:33
    */
    @Test
    public void getAllUserAftersales1() throws Exception{
        String token = userLogin("8606245097","123456");
        byte[] responseString = mallClient.get().uri("/aftersales").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult().getResponseBodyContent();


    }

    /**
     * 买家查询所有售后信息 开始时间大于结束时间 返回为空 14
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午9:40
    */
    @Test
    public void getAllUserAftersales2() throws Exception{
        String token = userLogin("8606245097","123456");
        byte[] responseString = mallClient.get().uri("/aftersales/?beginTime=2020-12-07 22:00:00&endTime=2020-12-06 22:00:00").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..total").isEqualTo(0)
                .returnResult().getResponseBodyContent();
    }

    /**
     * 买家查询所有售后信息 时间格式错误 15
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/10 下午10:46
    */
    @Test
    public void getAllUserAftersales3() throws Exception{
        String token = userLogin("8606245097","123456");
        byte[] responseString = mallClient.get().uri("/aftersales/?beginTime=2020-12-06&endTime=2020-12-06 22:00:00").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..total").isEqualTo(0)
                .returnResult().getResponseBodyContent();
    }

    /**
     * 买家查询所有的售后信息 时间格式错误 16
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/11 上午12:52
    */
    @Test
    public void getAllUserAftersales4() throws Exception{
        String token = userLogin("8606245097","123456");
        byte[] responseString = mallClient.get().uri("/aftersales/?beginTime=2020-12-06 18:00&endTime=2020-12-06 22:00:00").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..total").isEqualTo(0)
                .returnResult().getResponseBodyContent();
    }

    /**
     * 买家查询根据id查询售后信息 查不到 返回404 17
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/9 下午10:49
    */
    @Test
    public void getAftersalesById() throws Exception{
        String token = userLogin("8606245097","123456");
        byte[] responseString = mallClient.get().uri("/aftersales/54328758").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 买家修改售后单信息 修改电话号码 成功 18
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/10 下午12:41
    */
    @Test
    public void  updateAftersales1() throws Exception{
        String token = userLogin("8606245097","123456");
        JSONObject body = new JSONObject();
//        body.put("consignee","testC");
//        body.put("detail","testD");
        body.put("mobile","13912345689");
//        body.put("quantity",5);
//        body.put("reason","testR");
//        body.put("regionId",(long)1);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.put().uri("/aftersales/100").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 买家修改售后单信息 电话号码格式错误 19
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/10 下午10:41
    */
    @Test
    public void  updateAftersales2() throws Exception{
        String token = userLogin("8606245097","123456");
        JSONObject body = new JSONObject();
//        body.put("consignee","testC");
//        body.put("detail","testD");
        body.put("mobile","139123456890");
//        body.put("quantity",5);
//        body.put("reason","testR");
//        body.put("regionId",(long)1);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.put().uri("/aftersales/100").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 买家修改售后单信息 数量不能小于1 20
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/10 下午10:43
    */
    @Test
    public void  updateAftersales3() throws Exception{
        String token = userLogin("8606245097","123456");
        JSONObject body = new JSONObject();
//        body.put("consignee","testC");
//        body.put("detail","testD");
//        body.put("mobile","13912345689");
        body.put("quantity",0);
//        body.put("reason","testR");
//        body.put("regionId",(long)1);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.put().uri("/aftersales/100").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 买家修改售后单信息 找不到售后单 21
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/14 上午2:42
    */
    @Test
    public void  updateAftersales4() throws Exception{
        String token = userLogin("8606245097","123456");
        JSONObject body = new JSONObject();
//        body.put("consignee","testC");
//        body.put("detail","testD");
//        body.put("mobile","13912345689");
//        body.put("quantity",0);
        body.put("reason","testR");
//        body.put("regionId",(long)1);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.put().uri("/aftersales/53423564").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 管理员同意售后 成功 22
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午2:45
    */
    @Test
    public void adminAgreeAftersales1() throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
        body.put("confirm",true);
        body.put("price",20);
        body.put("conclusion","同意售后");
        body.put("type",0);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/1/aftersales/100/confirm").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        responseString = manageClient.get().uri("/shops/1/aftersales/100").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..state").isEqualTo(1)
                .returnResult().getResponseBodyContent();
        log.info(new String(responseString, "UTF-8"));
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 管理员不同意售后 成功 23
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午3:38
    */
    public void adminAgreeAftersales2() throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
        body.put("confirm",false);
        body.put("price",20);
        body.put("conclusion","同意售后");
        body.put("type",0);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/1/aftersales/101/confirm").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        responseString = mallClient.get().uri("/shops/1/aftersales/101").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..state").isEqualTo(6)
                .returnResult().getResponseBodyContent();

    }

    /**
     * 买家填写售后运单信息 成功 再次填写 失败 24
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午3:45
    */
    public void userSendback1() throws Exception{
        String token = userLogin("8606245097","123456");
        JSONObject body = new JSONObject();
        body.put("logSn","87654321");
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.put().uri("/aftersales/102/sendback").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        responseString = mallClient.get().uri("aftersales/102").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..state").isEqualTo(2)
                .returnResult().getResponseBodyContent();

        responseString = mallClient.put().uri("/aftersales/102/sendback").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AFTERSALE_STATENOTALLOW.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 买家填写售后运单信息 售后运单号不能为空 25
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午3:53
    */
    public void userSendBack2() throws Exception{
        String token = userLogin("8606245097","123456");
        JSONObject body = new JSONObject();
//        body.put("logSn","87654321");
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.put().uri("/aftersales/102/sendback").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 店家确认收到换货 成功 状态改变 再次测试失败 26
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午3:57
    */
    public void adminAcceptExchange1() throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
        body.put("confirm",true);
        body.put("conclusion","收到换货");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/1/aftersales/103/receive").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK)
                .returnResult().getResponseBodyContent();

        responseString = manageClient.get().uri("/shops/1/aftersales/103").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..state").isEqualTo(4)
                .returnResult().getResponseBodyContent();

        responseString = manageClient.put().uri("/shops/1/aftersales/103/receive").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AFTERSALE_STATENOTALLOW.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 店家确认收到换货 处理操作不能为空 27
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午4:06
    */
    public void adminAcceptExchange2() throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
//        body.put("confirm",true);
        body.put("conclusion","收到换货");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/1/aftersales/103/receive").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 店家确认收到换货 处理意见不能为空 28
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午4:08
    */
    @Test
    public void adminAcceptExchange3() throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
        body.put("confirm",true);
//        body.put("conclusion","收到换货");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/1/aftersales/103/receive").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 店家确认收到退货 成功 状态改变 再次改变状态失败 29
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午5:31
    */
    @Test
    public void adminAcceptRefund1() throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
        body.put("confirm",true);
        body.put("conclusion","收到退款");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/1/aftersales/104/receive").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        responseString = manageClient.get().uri("/shops/1/aftersales/104").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..state").isEqualTo(3)
                .returnResult().getResponseBodyContent();

        responseString = manageClient.put().uri("/shops/1/aftersales/104/receive").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AFTERSALE_STATENOTALLOW.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 店家确认收到退款 处理操作不能为空 30
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午5:39
    */
    @Test
    public void adminAcceptRefund2() throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
//        body.put("confirm",true);
        body.put("conclusion","收到退款");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/1/aftersales/104/receive").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 店家确认收到退款 处理意见不能为空 31
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午5:40
    */
    @Test
    public void adminAcceptRefund3() throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
        body.put("confirm",true);
//        body.put("conclusion","收到退款");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/1/aftersales/104/receive").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 店家不同意换货 成功 状态改变 32
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午5:41
    */
    @Test
    public void adminUnacceptRefund1() throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
        body.put("confirm",false);
        body.put("conclusion","不同意换货");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/1/aftersales/105/receive").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        responseString = manageClient.get().uri("/shops/1/aftersales/105").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..state").isEqualTo(1)
                .returnResult().getResponseBodyContent();
    }

    /**
     * 测试店家寄出维修 成功 查询状态 33
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午5:45
    */
    @Test
    public void adminDeliver() throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
        body.put("shopLogSn","1234567");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/1/aftersales/106/deliver").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        responseString = manageClient.get().uri("/shops/1/aftersales/106").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..state").isEqualTo(5)
                .returnResult().getResponseBodyContent();
    }

    /**
     * 用户确认退货信息 成功 并查询状态 再次确认失败 34
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午5:55
    */
    @Test
    public void userConfirm1() throws Exception{
        String token = userLogin("8606245097","123456");
        byte[] responseString = mallClient.put().uri("/aftersales/107/confirm").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        responseString = mallClient.get().uri("/aftersales/107").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..state").isEqualTo(8)
                .returnResult().getResponseBodyContent();

        responseString = mallClient.put().uri("/aftersales/107/confirm").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AFTERSALE_STATENOTALLOW.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 用户确认换货信息 成功 并查询售后单状态 再次确认失败 35
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午5:59
    */
    @Test
    public void userConfirm2() throws Exception{
        String token = userLogin("8606245097","123456");
        byte[] responseString = mallClient.put().uri("/aftersales/108/confirm").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        responseString = mallClient.get().uri("/aftersales/108").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$..state").isEqualTo(8)
                .returnResult().getResponseBodyContent();

        responseString = mallClient.put().uri("/aftersales/108/confirm").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AFTERSALE_STATENOTALLOW.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 测试买家删除 删除成功 再次删除则失败 36
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午6:01
    */
    @Test
    public void userDelete1() throws Exception{
        String token = userLogin("8606245097","123456");
        byte[] responseString = mallClient.delete().uri("/aftersales/109").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();


        responseString = mallClient.delete().uri("/aftersales/109").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 测试买家删除 删除失败 beDeleted字段已经为1 37
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/15 下午6:06
    */
    @Test
    public void userDelete2() throws Exception{
        String token = userLogin("8606245097","123456");
        byte[] responseString = mallClient.delete().uri("/aftersales/110").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 买家确认售后单信息 售后单不存在 38
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/14 上午2:27
    */
    @Test
    public void  userConfirmAftersales2() throws Exception{
        String token = userLogin("8606245097","123456");
        byte[] responseString = mallClient.put().uri("/aftersales/45346543/confirm").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }


    /**
     * 买家填写售后的运单信息 订单不存在 39
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/14 上午2:31
    */
    @Test
    public void UserWriteAftersales2() throws Exception{
        String token = userLogin("8606245097","123456");
        JSONObject body = new JSONObject();
        body.put("logSn","1245567");
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.put().uri("/aftersales/46546542/sendback").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }


    /**
     * 买家取消或删除售后订单 订单不存在 40
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/14 上午2:31
    */
    @Test
    public void UserDeleteAftersales1() throws Exception{
        String token = userLogin("8606245097","123456");
        byte[] responseString = mallClient.delete().uri("/aftersales/465465344").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }


    /**
     * 管理员根据id查询售后单信息 订单不存在 42
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/14 上午2:48
    */
    @Test
    public void AdminGetAftersales1()throws Exception{
        String token = adminLogin("13088admin","123456");
        byte[] responseString = manageClient.get().uri("/shops/0/aftersales/6343256").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }



    /**
     * 管理员同意 找不到订单 44
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/14 上午2:55
    */
    @Test
    public void AdminConfirmAftersales1()throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
        body.put("confirm",true);
        body.put("price",20);
        body.put("conclusion","同意售后");
        body.put("type",2);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/0/aftersales/563233253/confirm").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }


    /**
     * 管理员确认收到 找不到订单 46
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/14 上午2:56
    */
    @Test
    public void AdminReceiveAftersales1()throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
        body.put("confirm",true);
        body.put("conclusion","收到换货");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/0/aftersales/563233553/receive").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 管理员寄出 找不到订单 48
     * @author  24320182203253 钱秋妍
     * @date  Created in 2020/12/14 上午2:57
    */
    @Test
    public void AdminDeliverAftersales1()throws Exception{
        String token = adminLogin("13088admin","123456");
        JSONObject body = new JSONObject();
        body.put("shopLogSn","1234567");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/0/aftersales/563257253/deliver").header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }

}

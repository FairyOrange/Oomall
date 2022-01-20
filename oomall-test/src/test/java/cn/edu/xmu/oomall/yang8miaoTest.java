package cn.edu.xmu.oomall;

import cn.edu.xmu.oomall.util.JacksonUtil;
import cn.edu.xmu.oomall.util.JwtHelper;
import cn.edu.xmu.oomall.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * 其他模块-足迹服务、商品收藏服务、购物车服务 跑别人的公开测试用例
 * @author  24320182203318 yang8miao
 * @date 2020/12/09 15:15
 */
@SpringBootTest(classes = Application.class)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class yang8miaoTest {

    @Value("${oomall-test.managementgate}")
    private String managementGate;

    @Value("${oomall-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    @BeforeEach
    public void setUp(){

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    /**
     * 买家登录，获取token
     *
     * @author yang8miao
     * @param userName
     * @param password
     * @return token
     * createdBy yang8miao 2020/11/26 21:34
     * modifiedBy yang8miao 2020/11/26 21:34
     */
    private String userLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    /**
     * 管理员登录，获取token
     *
     * @author yang8miao
     * @param userName
     * @param password
     * @return token
     * createdBy yang8miao 2020/12/12 19:48
     * modifiedBy yang8miao 2020/12/12 19:48
     */
    private String adminLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    /**
     * addAddress1
     * 新增地址，参数错误，手机号码为空
     * @throws Exception
     */
    @Test
    public void addAddress0() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": 1,\n" +
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"\"\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses", 1)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 6. 新增地址，买家地址已经达到上限
     * @author: Zeyao Feng
     * @date: Created in 2020/12/15 17:08
     * modified in 2020/12/17 2:08
     */
    @Test
    public void addAddress11() throws Exception{

        //uid=27
        String token = this.userLogin("89972149478", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"12345678910\",\n" +
                "  \"regionId\": 2\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADDRESS_OUTLIMIT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }



    /**
     * 7. 修改地址 地址Id不存在
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 3:55
     * modified in 2020/12/17 2:12
     */
    @Test
    public void updateAddress1() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"12345678910\",\n" +
                "  \"regionId\": 1\n" +
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/20000")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 8. 修改地址 手机号格式错误
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:02
     */
    @Test
    public void updateAddress2() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"123456\",\n" +
                "  \"regionId\": 1\n" +
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/20000")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 9. 修改地址 地区id不存在
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:06
     */
    @Test
    public void updateAddress3() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"12345678910\",\n" +
                "  \"regionId\": -1\n" +
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/20000")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 10. 删除地址，地址Id不存在
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:12
     */
    @Test
    public void deleteAddress1() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.delete().uri("/addresses/10000").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 11. 查询某个地区的所有上级地区，该地区为顶级地区（中国）,pid=0
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:20
     * modified in 2020/12/17 2:13
     */
    @Test
    public void selectAncestorRegion1() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/1/ancestor").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        log.info(new String(responseString, StandardCharsets.UTF_8));

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": []\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 12. 查询某个地区的所有上级地区，该地区为1级地区（例如福建省）,pid>0
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:24
     * modified in 2020/12/17 2:17
     */
    @Test
    public void selectAncestorRegion2() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/14/ancestor").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        log.info(new String(responseString, StandardCharsets.UTF_8));
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"中国\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 13. 查询某个地区的所有上级地区，该地区为2级地区（例如厦门市）,pid>0
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:27
     * modified in 2020/12/17 2:17
     */
    @Test
    public void selectAncestorRegion3() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/151/ancestor").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 14,\n" +
                "      \"pid\": 1,\n" +
                "      \"name\": \"福建省\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"中国\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }



    /**
     * 14. 查询某个地区的所有上级地区，该地区不存在
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:32
     */
    @Test
    public void selectAncestorRegion4() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/140700/ancestor").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }



    /**
     * 15. 删除地址，成功，28号买家仅有一条地址,删除后无地址
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:18
     * modified in 2020/12/17 2:23
     */
    @Test
    public void deleteAddress2() throws Exception{

        //uid=28
        String token = this.userLogin("20137712098", "123456");

        byte[] responseString = mallClient.delete().uri("/addresses/77583").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        //再次查询该买家的地址
        byte[] responseString2 = mallClient.get().uri("/addresses").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": []\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString2, StandardCharsets.UTF_8), false);
    }


    /**
     * 16. 买家查询地址 第一页
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:54
     */
    @Test
    public void selectAddress1() throws Exception{

        //uid=27
        String token = this.userLogin("89972149478", "123456");

        byte[] responseString = mallClient.get().uri("/addresses?page=1&pageSize=7").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 20,\n" +
                "    \"pages\": 3,\n" +
                "    \"pageSize\": 7,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 77563,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77564,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77565,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77566,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77567,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77568,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77569,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 17. 买家查询地址，第二页
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:56
     */
    @Test
    public void selectAddress2() throws Exception{

        //uid=27
        String token = this.userLogin("89972149478", "123456");

        byte[] responseString = mallClient.get().uri("/addresses?page=2&pageSize=3").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 20,\n" +
                "    \"pages\": 7,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 77566,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77567,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77568,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 18. 买家查询地址 第三页显示 未完全显示
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:59
     */
    @Test
    public void selectAddress3() throws Exception{

        //uid=27
        String token = this.userLogin("89972149478", "123456");

        byte[] responseString = mallClient.get().uri("/addresses?page=4&pageSize=6").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 20,\n" +
                "    \"pages\": 4,\n" +
                "    \"pageSize\": 6,\n" +
                "    \"page\": 4,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 77581,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77582,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 19. 查询某个地区的所有上级地区，该地区为3级地区（例如思明区）,pid>0
     * @author: Zeyao Feng
     * @date: Created in 2020/12/17 2:20
     */
    @Test
    public void selectAncestorRegion5() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/1599/ancestor").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 151,\n" +
                "      \"pid\": 14,\n" +
                "      \"name\": \"厦门市\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 14,\n" +
                "      \"pid\": 1,\n" +
                "      \"name\": \"福建省\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"中国\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }



    /**
     * addAddress1
     * 新增地址，参数错误，手机号码错误
     * @throws Exception
     */
    @Test
    public void addAddress1() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": 1,\n" +
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"1232323\"\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses", 1)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    /**addAddress2
     * 新增地址，参数错误，收件人为空
     * @throws Exception
     */
    @Test
    public void addAddress2() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": 1,\n" +
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"\",\n" +
                " \"mobile\":  \"18990897878\"\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * addAddress3
     * 新增地址，参数错误，详情为空
     * @throws Exception
     */

    @Test
    public void addAddress3() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": 1,\n" +
                " \"detail\":  \"\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * addAddress4
     * 新增地址，参数错误，地区id为空
     * @throws Exception
     */


    @Test
    public void addAddress4() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": null,\n" +
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\" \n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * addAddress5
     * 新增地址，地区已废弃
     * 先废弃地区再测试添加地址
     * @throws Exception
     */

    @Test
    public void addAddress5() throws Exception{


        String token1 = this.adminLogin("13088admin", "123456");
        String token2 = this.userLogin("8606245097", "123456");

        byte[] responseString1 = manageClient.delete().uri("/shops/1/regions/302")
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();


        String requireJson="{\n"+
                " \"regionId\": 302,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token2)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();



    }
    /**
     * addAddress6
     * 新增地址，地区不存在
     * @throws Exception
     */
    @Test
    public void addAddress6() throws Exception{

        String token = this.userLogin("8606245097", "123456");
        String requireJson="{\n"+
                " \"regionId\": -1,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();



    }
    /**
     * addAddress7
     * 新增地址
     * @throws Exception
     */
    @Test
    public void addAddress7() throws Exception{

        String token = this.userLogin("8606245097", "123456");
        String requireJson="{\n"+
                " \"regionId\": 1,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse= "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"测试地址1\",\n" +
                "        \"consignee\": \"测试\",\n" +
                "        \"mobile\": \"18990897878\",\n" +
                "        \"beDefault\": false\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);


    }
    /**
     * 修改地址信息，地址id不是自己的
     */

    @Test
    public void updateAddress11() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        String requireJson="{\n"+
                " \"regionId\": 1,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/3")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 修改地址信息，地区不存在
     */

    @Test
    public void updateAddress12() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        String requireJson="{\n"+
                " \"regionId\": -1,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/1")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();

    }
    /**
     * 修改地址信息，地区已废弃
     */

    @Test
    public void updateAddress13() throws Exception{


        String token1 = this.adminLogin("13088admin", "123456");
        String token2 = this.userLogin("8606245097", "123456");

        byte[] responseString1 = manageClient.delete().uri("/shops/1/regions/305")
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String requireJson="{\n"+
                " \"regionId\": 305,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/1")
                .header("authorization", token2)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())

                .returnResult()
                .getResponseBodyContent();

    }
    /**
     * addRegion1
     * 新增地区 父地区不存在
     */
    @Test
    public void addRegion1() throws Exception{

        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = manageClient.post().uri("/shops/1/regions/-1/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();



    }
    /**addRegion2
     * 新增地区 父地区已废弃  先废弃id为300的地区，再测试新增地区
     *
     */
    @Test
    public void addRegion2() throws Exception{

        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/1/regions/104")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();


        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString2 = manageClient.post().uri("/shops/1/regions/104/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .returnResult()
                .getResponseBodyContent();



    }

    /**
     * addRegion3
     * 增地区
     */
    @Test
    public void addRegion3() throws Exception{


        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"test\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = manageClient.post().uri("/shops/1/regions/1/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();



    }

    /**
     * addRegion4
     * 新增地区，地区名字为空
     */
    @Test
    public void addRegion4() throws Exception{


        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = manageClient.post().uri("/shops/1/regions/1/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();



    }


    /**
     * diableRegion
     * 废弃地区 地区id不存在
     */

    @Test
    public void diableRegion1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/1/regions/-1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();



    }
    /**
     * diableRegion
     * 废弃地区 301
     */

    @Test
    public void diableRegion2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/1/regions/301")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        //用新增子地区来测试是否将地区成功修改为无效 和addRegion2同理

        String requireJson2="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString2 = manageClient.post().uri("/shops/1/regions/301/subregions")
                .header("authorization", token)
                .bodyValue(requireJson2)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .returnResult()
                .getResponseBodyContent();


    }




    /**
     * setAsDefault1
     * @throws Exception
     */
    @Test
    public void setAsDefault1() throws Exception {
        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.put().uri("/addresses/1/default").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 地址id不存在
     * setAsDefault2
     * @throws Exception
     */
    @Test
    public void setAsDefault2() throws Exception {
        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.put().uri("/addresses/-1/default").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();

    }

    /**
     * 地址id不是自己的地址
     * setAsDefault3
     * @throws Exception
     */
    @Test
    public void setAsDefault3() throws Exception {
        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.put().uri("/addresses/2/default").header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult().getResponseBodyContent();

    }


    /**
     * updateRegion1
     * 修改地区，地区不存在
     */
    @Test
    public void updateRegion1() throws Exception{

        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = manageClient.put().uri("/shops/1/regions/-1")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();



    }
    /**updateRegion2
     * 修改地区，地区已废弃
     *
     */
    @Test
    public void updateRegion2() throws Exception{

        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/1/regions/303")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();


        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString2 = manageClient.put().uri("/shops/1/regions/303")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .returnResult()
                .getResponseBodyContent();



    }


    /**
     * getRegion1
     * 查询地区，地区不存在
     */
    @Test
    public void getRegion1() throws Exception{

        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.get().uri("/region/-1/ancestor")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }
    /**
     * getRegion2
     * 查询地区，地区已废弃
     *
     */
    @Test
    public void getRegion2() throws Exception{

        String token1 = this.adminLogin("13088admin", "123456");
        String token2 = this.userLogin("8606245097", "123456");

        byte[] responseString1 = manageClient.delete().uri("/shops/1/regions/304")
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();



        byte[] responseString2 = mallClient.get().uri("/region/304/ancestor")
                .header("authorization", token2)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .returnResult()
                .getResponseBodyContent();



    }

}

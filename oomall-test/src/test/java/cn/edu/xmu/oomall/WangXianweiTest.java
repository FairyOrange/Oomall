package cn.edu.xmu.oomall;

import cn.edu.xmu.oomall.util.JacksonUtil;
import cn.edu.xmu.oomall.util.JwtHelper;
import cn.edu.xmu.oomall.util.ResponseCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 测试
 * @author
 * @date 2020/12/07 12:28
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WangXianweiTest {

    private String managementGate;

    private String mallGate;

    private final WebTestClient manageClient;

    private final WebTestClient mallClient;

    /**
     *
     * @param userId
     * @param departId
     * @param expireTime
     * @return java.lang.String
     * @author Fiber W.
     * created at 12/9/20 11:20 AM
     */
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }

    public WangXianweiTest() {
        this.managementGate = "http://192.168.43.194:8881";
        this.mallGate = "http://192.168.43.194:8880";
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl(this.managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl(this.mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }
    private String token = "";
    private Long shareActivityId;


    @Test
    @Order(1)
    public void otherTest1() throws Exception {
        String token = this.userLogin("19769355952", "123456");

        byte[] responseString = mallClient.get().uri("/share/1/skus/517")
                .header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String data = JacksonUtil.parseString(new String(responseString),"data");
        Integer id = JacksonUtil.parseInteger(data, "id");
        assertEquals(517, id);
        Integer originalPrice = JacksonUtil.parseInteger(data, "originalPrice");
        assertEquals(219, originalPrice);
        Integer price = JacksonUtil.parseInteger(data, "originalPrice");
        assertEquals(200, price);
        Integer inventory = JacksonUtil.parseInteger(data, "inventory");
        assertEquals(9997, inventory);
        Integer weight = JacksonUtil.parseInteger(data, "weight");
        assertEquals(1000, weight);
    }

    @Test
    @Order(2)
    public void otherTest2() throws Exception {
        String token = this.userLogin("17857289610", "123456");

        byte[] responseString = mallClient.get().uri("/beshared?skuid=517")
                .header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String data = JacksonUtil.parseString(new String(responseString),"data");
        List<String> list = JacksonUtil.parseObjectList(data, "list", String.class);
        assertEquals(1, list.size());
        String beshared = list.get(0);
        Integer shareId = JacksonUtil.parseInteger(beshared, "shareId");
        assertEquals(1, shareId);
        Integer customerId = JacksonUtil.parseInteger(beshared, "customerId");
        assertEquals(9, customerId);
    }
    /**
     * 查看用户的角色测试1
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getUserRoleTest1() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/50/roles").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"user\": {\n" +
                "                \"id\": 50,\n" +
                "                \"userName\": \"8043300005\"\n" +
                "            },\n" +
                "            \"role\": {\n" +
                "                \"id\": 86,\n" +
                "                \"name\": \"库管\"\n" +
                "            },\n" +
                "            \"creator\": {\n" +
                "                \"id\": 1,\n" +
                "                \"userName\": \"13088admin\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 查看用户的角色测试2
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getUserRoleTest2() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/2/adminusers/49/roles").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 查看用户的角色测试3
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getUserRoleTest3() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/100/roles").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 查看自己的角色测试1
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest1() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/adminusers/self/roles").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": 76,\n" +
                "            \"user\": {\n" +
                "                \"id\": 1,\n" +
                "                \"userName\": \"13088admin\"\n" +
                "            },\n" +
                "            \"role\": {\n" +
                "                \"id\": 23,\n" +
                "                \"name\": \"管理员\"\n" +
                "            },\n" +
                "            \"creator\": {\n" +
                "                \"id\": 1,\n" +
                "                \"userName\": \"13088admin\"\n" +
                "            },\n" +
                "            \"gmtCreate\": \"2020-11-01T09:48:24\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 查看自己的角色测试2
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest2() throws Exception {
        String token = this.login("8532600003", "123456");
        byte[] responseString = manageClient.get().uri("/adminusers/self/roles").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": 79,\n" +
                "            \"user\": {\n" +
                "                \"id\": 48,\n" +
                "                \"userName\": \"8532600003\"\n" +
                "            },\n" +
                "            \"role\": {\n" +
                "                \"id\": 86,\n" +
                "                \"name\": \"库管\"\n" +
                "            },\n" +
                "            \"creator\": {\n" +
                "                \"id\": 1,\n" +
                "                \"userName\": \"13088admin\"\n" +
                "            },\n" +
                "            \"gmtCreate\": \"2020-11-01T09:48:24\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 赋予用户角色测试1
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void assignRoleTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/adminusers/47/roles/1000").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 赋予用户角色测试2
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void assignRoleTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/adminusers/10000/roles/84").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 赋予用户角色测试3
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void assignRoleTest4() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/adminusers/47/roles/84").header("authorization",token)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"user\": {\n" +
                "            \"id\": 47,\n" +
                "            \"userName\": \"2721900002\"\n" +
                "        },\n" +
                "        \"role\": {\n" +
                "            \"id\": 84,\n" +
                "            \"name\": \"文案\"\n" +
                "        },\n" +
                "        \"creator\": {\n" +
                "            \"id\": 1,\n" +
                "            \"userName\": \"13088admin\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 赋予用户角色测试3
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void assignRoleTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/adminusers/48/roles/86").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":737}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 取消用户角色测试1 成功
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void revokeRoleTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/50/roles/87").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 取消用户角色测试2:角色不存在
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void revokeRoleTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/50/roles/1000").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 取消用户角色测试3:用户不存在
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void revokeRoleTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/10000/roles/87").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 取消用户角色测试4:角色无该用户
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void revokeRoleTest4() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/57/roles/84").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    private String login(String userName, String password) throws Exception{
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
    @Test
    @Order(1)
    public void getBesharedTest1() throws Exception{
        String token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?page=1&pageSize=1").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434151,\n" +
                " \"sku\": {\n" +
                "                    \"id\": 505,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"\",\n" +
                "                    \"inventory\": 1000,\n" +
                "                    \"originalPrice\": 0,\n" +
                "                    \"price\": 0\n" +
                "                }," +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    @Test
    @Order(2)
    public void getBesharedTest2() throws Exception {
        String token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?skuId=505&page=1&pageSize=10").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 2,\n" +
                "        \"pages\": 1,\n" +
                "        \"pageSize\": 10,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434151,\n" +
                "\"sku\": {\n" +
                "                    \"id\": 505,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"\",\n" +
                "                    \"inventory\": 1000,\n" +
                "                    \"originalPrice\": 0,\n" +
                "                    \"price\": 0\n" +
                "                }," +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 434542,\n" +
                "\"sku\": {\n" +
                "                    \"id\": 505,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"\",\n" +
                "                    \"inventory\": 1000,\n" +
                "                    \"originalPrice\": 0,\n" +
                "                    \"price\": 0\n" +
                "                }," +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * /beshared测试3 查询条件beginTime和endTime
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(3)
    public void getBesharedTest3() throws Exception {
        String token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?beginTime=2020-12-06 22:00:00&endTime=2020-12-07 22:00:00&page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434151,\n" +
                "\"sku\": {\n" +
                "                    \"id\": 505,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"\",\n" +
                "                    \"inventory\": 1000,\n" +
                "                    \"originalPrice\": 0,\n" +
                "                    \"price\": 0\n" +
                "                }," +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /beshared测试4 查询条件page和pageSize
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(4)
    public void getBesharedTest4() throws Exception {
        String token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?page=2&pageSize=5").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 5,\n" +
                "        \"page\": 2,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434160,\n" +
                "                \"sku\": {\n" +
                "                    \"id\": 420,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_5967792535e80.jpg\",\n" +
                "                    \"inventory\": 150,\n" +
                "                    \"originalPrice\": 118000,\n" +
                "                    \"price\": 118000\n" +
                "                },\n" +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 434171,\n" +
                "                \"sku\": {\n" +
                "                    \"id\": 479,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59708f8030eb8.jpg\",\n" +
                "                    \"inventory\": 1,\n" +
                "                    \"originalPrice\": 102000,\n" +
                "                    \"price\": 102000\n" +
                "                },\n" +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 434526,\n" +
                "                \"sku\": {\n" +
                "                    \"id\": 338,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"http://47.52.88.176/file/images/201610/file_57fb72dc14919.jpg\",\n" +
                "                    \"inventory\": 10,\n" +
                "                    \"originalPrice\": 15120,\n" +
                "                    \"price\": 15120\n" +
                "                },\n" +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 434542,\n" +
                "                \"sku\": {\n" +
                "                    \"id\": 505,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"\",\n" +
                "                    \"inventory\": 1000,\n" +
                "                    \"originalPrice\": 0,\n" +
                "                    \"price\": 0\n" +
                "                },\n" +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 434854,\n" +
                "                \"sku\": {\n" +
                "                    \"id\": 545,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"http://47.52.88.176/file/images/201711/file_5a1185a848b70.jpg\",\n" +
                "                    \"inventory\": 1000,\n" +
                "                    \"originalPrice\": 999,\n" +
                "                    \"price\": 999\n" +
                "                },\n" +
                "                \"sharerId\": 2,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * /beshared测试6 开始时间在结束时间之后,返回空
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(5)
    public void getBesharedTest6() throws Exception {
        String token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?beginTime=2020-12-07 22:00:00&endTime=2019-12-07 22:00:00&page=1&pageSize=10").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 0,\n" +
                "        \"pages\": 0,\n" +
                "        \"pageSize\": 10,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": []\n" +
                "    }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /beshared测试7 开始时间和结束时间格式错误
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(6)
    public void getBesharedTest7() throws Exception {
        String token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/beshared?beginTime=2020-12-22:00:00&endTime=2019-12-44 :00:00&page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": []\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shares测试1
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(7)
    public void getSharesTest1() throws Exception {
        String token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.get().uri("/shares/?page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 442316,\n" +
                "                \"sharerId\": 2,\n" +
                "\"sku\": {\n" +
                "                    \"id\": 505,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"\",\n" +
                "                    \"inventory\": 1000,\n" +
                "                    \"originalPrice\": 0,\n" +
                "                    \"price\": 0\n" +
                "                }," +
                "                \"quantity\": 1,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shops/{shopid}/skus/{skuid}/shareactivities测试1
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(8)
    public void createShareActivity1() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/skus/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2021-11-30 23:58:30\",\n" +
                        "\t\"endTime\":\"2021-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"shopId\": 1,\n" +
                "        \"skuId\": 501,\n" +
                "        \"beginTime\": \"2021-11-30T23:58:30\",\n" +
                "        \"endTime\": \"2021-12-15T23:23:23\",\n" +
                "        \"state\": 0\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString)).getJSONObject("data");
        this.shareActivityId = jsonObject.getLong("id");

    }

    /**
     * /shops/{shopid}/skus/{skuid}/shareactivities测试2 分享规则格式错误，返回503错误码
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(9)
    public void createShareActivity2() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/skus/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shops/{shopid}/skus/{skuid}/shareactivities测试3 开始时间为空，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(10)
    public void createShareActivity3() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/skus/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();

    }



    /**
     * /shops/{shopid}/skus/{skuid}/shareactivities测试4 规则为空，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(11)
    public void createShareActivity4() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/skus/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":null\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shops/{shopid}/skus/{skuid}/shareactivities测试5 分享活动时段冲突，返回605错误码
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(12)
    public void createShareActivity5() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/skus/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2020-12-07 21:47:19\",\n" +
                        "\t\"endTime\":\"2021-10-10 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 605\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * /shops/{shopid}/skus/{skuid}/shareactivities测试6 开始时间格式错误，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(13)
    public void createShareActivity6() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/skus/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"1234-1321-321\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();

    }

    /**
     * /shops/{shopid}/skus/{skuid}/shareactivities测试7 结束时间格式错误，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(14)
    public void createShareActivity7() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/skus/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
                        "\t\"endTime\":\"23432fq\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();

    }

    /**
     * /shops/{shopid}/skus/{skuid}/shareactivities测试8 开始时间在结束时间之后，返回610
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(15)
    public void createShareActivity8() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/1/skus/501/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2021-12-10 21:47:19\",\n" +
                        "\t\"endTime\":\"2021-12-07 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试1
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(16)
    public void updateShareActivity1() throws Exception {
        String token = adminLogin("13088admin", "123456");
        //新建一个用于测试
        byte[] responseString2 = manageClient.post().uri("/shops/1/skus/502/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2021-11-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2021-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        this.shareActivityId = jsonObject.getLong("id");

        byte[] responseString = manageClient.put().uri("/shops/1/shareactivities/"+this.shareActivityId).header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
                        "\t\"endTime\":\"2001-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试2 分享规则格式错误，返回503错误码
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(17)
    public void updateShareActivity2() throws Exception {
        String token = adminLogin("13088admin", "123456");
        //新建一个用于测试
        byte[] responseString2 = manageClient.post().uri("/shops/1/skus/502/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2022-01-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2022-02-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        this.shareActivityId = jsonObject.getLong("id");
        byte[] responseString = manageClient.put().uri("/shops/1/shareactivities/"+this.shareActivityId).header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试3 开始时间为空，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(18)
    public void updateShareActivity3() throws Exception {
        String token = adminLogin("13088admin", "123456");
        //新建一个用于测试
        byte[] responseString2 = manageClient.post().uri("/shops/1/skus/502/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2022-03-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2022-04-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        this.shareActivityId = jsonObject.getLong("id");
        byte[] responseString = manageClient.put().uri("/shops/1/shareactivities/"+this.shareActivityId).header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();

    }



    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试4 规则为空，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(19)
    public void updateShareActivity4() throws Exception {
        String token = adminLogin("13088admin", "123456");
        //新建一个用于测试
        byte[] responseString2 = manageClient.post().uri("/shops/1/skus/502/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2022-05-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2022-06-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        this.shareActivityId = jsonObject.getLong("id");
        byte[] responseString = manageClient.put().uri("/shops/1/shareactivities/"+this.shareActivityId).header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-11-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();

    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试5 分享活动时段冲突，返回605错误码
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(20)
    public void updateShareActivity5() throws Exception {
        String token = adminLogin("13088admin", "123456");
        //新建一个用于测试
        byte[] responseString2 = manageClient.post().uri("/shops/1/skus/502/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2022-07-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2022-08-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        this.shareActivityId = jsonObject.getLong("id");
        byte[] responseString = manageClient.put().uri("/shops/1/shareactivities/"+this.shareActivityId).header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2020-12-07 21:47:19\",\n" +
                        "\t\"endTime\":\"2021-10-10 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 605\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试6 开始时间格式错误，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(21)
    public void updateShareActivity6() throws Exception {
        String token = adminLogin("13088admin", "123456");
        //新建一个用于测试
        byte[] responseString2 = manageClient.post().uri("/shops/1/skus/502/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2022-09-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2022-10-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        this.shareActivityId = jsonObject.getLong("id");
        byte[] responseString = manageClient.put().uri("/shops/1/shareactivities/"+this.shareActivityId).header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"1234-1321-321\",\n" +
                        "\t\"endTime\":\"2000-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();

    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试7 结束时间格式错误，返回503错误码。http状态400
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(22)
    public void updateShareActivity7() throws Exception {
        String token = adminLogin("13088admin", "123456");
        //新建一个用于测试
        byte[] responseString2 = manageClient.post().uri("/shops/1/skus/502/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2022-11-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2022-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        this.shareActivityId = jsonObject.getLong("id");
        byte[] responseString = manageClient.put().uri("/shops/1/shareactivities/"+this.shareActivityId).header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
                        "\t\"endTime\":\"23432fq\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();

    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试8 分享活动id不存在 错误码504 http状态404
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(23)
    public void updateShareActivity8() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/1/shareactivities/0").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2000-12-15 23:23:23\",\n" +
                        "\t\"endTime\":\"2001-12-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}测试9 开始时间在结束时间之后 返回610
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(24)
    public void updateShareActivity9() throws Exception {
        String token = adminLogin("13088admin", "123456");
        //新建一个用于测试
        byte[] responseString2 = manageClient.post().uri("/shops/1/skus/502/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2023-01-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2023-02-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        this.shareActivityId = jsonObject.getLong("id");
        byte[] responseString = manageClient.put().uri("/shops/0/shareactivities/"+this.shareActivityId).header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2021-12-10 21:47:19\",\n" +
                        "\t\"endTime\":\"2021-12-07 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}/online测试1 成功
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(25)
    public void onlineShareActivity1() throws Exception {
        String token = adminLogin("13088admin", "123456");
        //新建一个用于测试
        byte[] responseString2 = manageClient.post().uri("/shops/1/skus/502/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2023-03-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2023-04-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        this.shareActivityId = jsonObject.getLong("id");
        byte[] responseString = manageClient.put().uri("/shops/1/shareactivities/"+this.shareActivityId+"/online").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * DELETE /shops/{shopid}/shareactivities/{id}测试1 分享活动id不存在 错误码504 http状态404
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(26)
    public void deleteShareActivity1() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/1/shareactivities/0").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * DELETE /shops/{shopid}/shareactivities/{id}测试2 分享活动不是该商铺的,返回505 http状态403
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(27)
    public void deleteShareActivity2() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/2/shareactivities/303068").header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 505\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * DELETE /shops/{shopid}/shareactivities/{id}测试3 成功
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(28)
    public void deleteShareActivity3() throws Exception {
        String token = adminLogin("13088admin", "123456");
        //新建一个用于测试
        byte[] responseString2 = manageClient.post().uri("/shops/1/skus/502/shareactivities").header("authorization", token)
                .bodyValue("{\n" +
                        "\t\"beginTime\":\"2023-07-30 23:59:00\",\n" +
                        "\t\"endTime\":\"2023-08-15 23:23:23\",\t\n" +
                        "\t\"strategy\":\"{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1 },{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                        "}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        this.shareActivityId = jsonObject.getLong("id");
        manageClient.put().uri("/shops/1/shareactivities/"+this.shareActivityId+"/online").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        byte[] responseString = manageClient.delete().uri("/shops/1/shareactivities/"+this.shareActivityId).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shops/{did}/skus/{id}/beshared测试1 成功
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(29)
    public void adminSelectBeshared1() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/501/beshared?page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434162,\n" +
                "\"sku\": {\n" +
                "                    \"id\": 501,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
                "                    \"inventory\": 100,\n" +
                "                    \"originalPrice\": 1399,\n" +
                "                    \"price\": 1399\n" +
                "                }," +
                "                \"sharerId\": 1912,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shops/{did}/skus/{id}/beshared测试2 时间条件限制 成功
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(30)
    public void adminSelectBeshared2() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/501/beshared?beginTime=2020-12-06 22:00:00&endTime=2020-12-07 22:00:00&page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 434162,\n" +
                "\"sku\": {\n" +
                "                    \"id\": 501,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
                "                    \"inventory\": 100,\n" +
                "                    \"originalPrice\": 1399,\n" +
                "                    \"price\": 1399\n" +
                "                }," +
                "                \"sharerId\": 1912,\n" +
                "                \"customerId\": null,\n" +
                "                \"orderId\": null,\n" +
                "                \"rebate\": null,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:21\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shops/{did}/skus/{id}/beshared测试3 开始时间在结束时间之后 返回空
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(31)
    public void adminSelectBeshared3() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/505/beshared?beginTime=2020-12-08 22:00:00&endTime=2020-12-07 22:00:00&page=1&pageSize=10").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 0,\n" +
                "        \"pages\": 0,\n" +
                "        \"pageSize\": 10,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": []\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * GET /shops/{did}/skus/{id}/beshared测试4 开始时间格式错误
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(32)
    public void adminSelectBeshared4() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/505/beshared?beginTime=2020-:00&endTime=2020-12-07 22:00:00&page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": []\n" +
                "    }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * GET /shops/{did}/skus/{id}/beshared测试4 结束时间格式错误
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(33)
    public void adminSelectBeshared5() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/505/beshared?beginTime=2020-12-07 22:00:00&endTime=202000:00").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 0,\n" +
                "        \"pages\": 0,\n" +
                "        \"pageSize\": 10,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": []\n" +
                "    }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shops/{did}/skus/{id}/shares测试1
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(34)
    public void adminSelectShares1() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/501/shares?page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 442327,\n" +
                "                \"sharerId\": 1912,\n" +
                "\"sku\": {\n" +
                "                    \"id\": 501,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
                "                    \"inventory\": 100,\n" +
                "                    \"originalPrice\": 1399,\n" +
                "                    \"price\": 1399\n" +
                "                }," +
                "                \"quantity\": 720,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shops/{did}/skus/{id}/shares测试2
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(35)
    public void adminSelectShares2() throws Exception {
        String token = adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/skus/501/shares?page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 442327,\n" +
                "                \"sharerId\": 1912,\n" +
                "\"sku\": {\n" +
                "                    \"id\": 501,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
                "                    \"inventory\": 100,\n" +
                "                    \"originalPrice\": 1399,\n" +
                "                    \"price\": 1399\n" +
                "                }," +
                "                \"quantity\": 720,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * POST /skus/{id}/shares测试1
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(36)
    public void addShares() throws Exception {
        String token = userLogin("36040122840", "123456");
        String tokenAdmin = adminLogin("13088admin", "123456");

        //上线一个501商品已有的分享活动
        manageClient.put().uri("/shops/1/shareactivities/304113/online").header("authorization", tokenAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();

        byte[] responseString = mallClient.post().uri("/skus/501/shares").header("authorization", token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"sharerId\": 2," +
                "        \"sku\": {" +
                "            \"id\": 501," +
                "            \"name\": \"+\",\n" +
                "            \"skuSn\": null,\n" +
                "            \"imageUrl\": \"http://47.52.88.176/file/images/201805/file_5b0bb6e946aa2.jpg\",\n" +
                "            \"inventory\": 100,\n" +
                "            \"originalPrice\": 1399,\n" +
                "            \"price\": 1399\n" +
                "        },\n" +
                "        \"quantity\": 0\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

        //查询是否正确
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString)).getJSONObject("data");
        Long shareId = jsonObject.getLong("id");

        byte[] responseString2 = mallClient.get().uri("/shares?skuId=501&page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();

        JSONObject jsonObject2 = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        JSONArray jsonArray = jsonObject2.getJSONArray("list");
        for (int i = 0; i < jsonArray.size(); i++) {
            Long shareId2 = jsonArray.getJSONObject(i).getLong("id");
            if (shareId.equals(shareId2)) {
                JSONAssert.assertEquals(shareId.toString(), shareId2.toString(), false);
            }
        }




    }

    /**
     * POST /skus/{id}/shares测试2 skuid不存在
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(37)
    public void addShares2() throws Exception {
        String token = userLogin("36040122840", "123456");
        byte[] responseString = mallClient.post().uri("/skus/111111111111111/shares").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{" +
                "    \"errno\": 504" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shareactivities测试1
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(38)
    public void getShareActivity1() throws Exception {
        token = userLogin("36040122840", "123456");
        String tokenAdmin = adminLogin("13088admin", "123456");

        byte[] responseString = mallClient.get().uri("/shareactivities?skuId=501&page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"shopId\": 1,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shareactivities测试2 shopId查询
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(39)
    public void getShareActivity2() throws Exception {
        String token = userLogin("36040122840", "123456");
        String tokenAdmin = adminLogin("13088admin", "123456");


        byte[] responseString = mallClient.get().uri("/shareactivities?shopId=1&skuId=501&page=1&pageSize=1").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"shopId\": 1,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * GET /shareactivities测试3 分页查询
     * @return void
     * @author Xianwei Wang
     * created at 12/9/20 12:45 PM
     */
    @Test
    @Order(39)
    public void getShareActivity3() throws Exception {
        String token = userLogin("36040122840", "123456");


        byte[] responseString = mallClient.get().uri("/shareactivities?skuId=501&page=2&pageSize=5").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 5,\n" +
                "        \"page\": 2,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 306268,\n" +
                "                \"shopId\": 1,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 306288,\n" +
                "                \"shopId\": 1,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 306361,\n" +
                "                \"shopId\": 1,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 306366,\n" +
                "                \"shopId\": 1,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 307518,\n" +
                "                \"shopId\": 1,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * 用户登录
     *
     * @author 王显伟
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
}

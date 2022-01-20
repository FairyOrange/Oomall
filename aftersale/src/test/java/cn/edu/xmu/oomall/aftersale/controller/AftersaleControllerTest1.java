package cn.edu.xmu.oomall.aftersale.controller;

import cn.edu.xmu.oomall.aftersale.AftersaleApplication;
import cn.edu.xmu.oomall.aftersale.model.vo.*;
import cn.edu.xmu.oomall.util.JacksonUtil;
import cn.edu.xmu.oomall.util.JwtHelper;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
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

import java.util.List;

/**
 * @author Qiuyan Qian
 * @date Created in 2020/12/08 23:06
 */
@Slf4j
@SpringBootTest(classes = AftersaleApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AftersaleControllerTest1 {

    @Autowired
    private MockMvc mvc;

    /**
     * 创建测试用Token
     *
     * @param userId
     * @param departId
     * @param expireTime
     * @return java.lang.String
     * @author Qiuyan Qian
     * @date Created in 2020/12/8 下午11:07
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }

    /**
     * 获取所有的状态 测试
     *
     * @author Qiuyan Qian
     * @date Created in 2020/12/8 下午11:07
     */
    @Test
    @Order(0)
    public void getAllStateTest() throws Exception {
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;

        log.debug("获得售后单的所有状态");
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/aftersales/states")
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"待管理员审核\"},{\"code\":1,\"name\":\"待买家发货\"},{\"code\":2,\"name\":\"买家已发货\"},{\"code\":3,\"name\":\"待店家退款\"},{\"code\":4,\"name\":\"待店家发货\"},{\"code\":5,\"name\":\"店家已发货\"},{\"code\":6,\"name\":\"审核不通过\"},{\"code\":7,\"name\":\"已取消\"},{\"code\":8,\"name\":\"已结束\"}],\"errmsg\":\"成功\"}";
        log.debug(responseString);
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家提交售后单
     *
     * @author Qiuyan Qian
     * @date Created in 2020/12/9 上午11:30
     */
    @Test
    @Order(1)
    public void postUserAftersales1() throws Exception {
        String token = creatTestToken(5L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;

        log.debug("买家提交售后单");
        AftersaleVo testVo = new AftersaleVo();
        testVo.setConsignee("testC");
        testVo.setDetail("testD");
        testVo.setMobile("12343211234");
        testVo.setQuantity(5);
        testVo.setReason("testR");
        testVo.setRegionId((long) 1);
        testVo.setType((byte) 0);
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/other/orderItems/{id}/aftersales", 5)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(testVo)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

//        String subString = JSONObject.parseObject(responseString).getString("data");
//        Long id = Long.parseLong(JSONObject.parseObject(subString).getString("id"));
//        log.info("subString: "+subString);
    }

    /**
     * 买家提交售后单 数量不能小于1
     *
     * @author Qiuyan Qian
     * @date Created in 2020/12/9 下午2:41
     */
    @Test
    @Order(1)
    public void postUserAftersales2() throws Exception {
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;

        log.debug("买家提交售后单");
        AftersaleVo testVo = new AftersaleVo();
        testVo.setConsignee("testC1");
        testVo.setDetail("testD1");
        testVo.setMobile("12343211235");
        testVo.setQuantity(0);
        testVo.setReason("testR");
        testVo.setRegionId((long) 1);
        testVo.setType((byte) 0);
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/other/orderItems/{id}/aftersales", 5)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(testVo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":503}";
        log.debug(responseString);
//        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 买家提交售后单 原因不能为空
     * @author Qiuyan Qian
     * @date Created in 2020/12/9 下午3:11
     */
    @Test
    @Order(1)
    public void postUserAftersales3() throws Exception {
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;

        log.debug("买家提交售后单");
        AftersaleVo testVo = new AftersaleVo();
        testVo.setConsignee("testC1");
        testVo.setDetail("testD1");
        testVo.setMobile("12343211235");
        testVo.setQuantity(5);
        testVo.setReason("");
        testVo.setRegionId((long) 1);
        testVo.setType((byte) 0);
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/other/orderItems/{id}/aftersales", 5)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(testVo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":503}";
        log.debug(responseString);
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 买家提交售后 地区码不为空
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/9 下午3:16
    */
    @Test
    @Order(1)
    public void postUserAftersales4() throws Exception {
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;

        log.debug("买家提交售后单");
        AftersaleVo testVo = new AftersaleVo();
        testVo.setConsignee("testC1");
        testVo.setDetail("testD1");
        testVo.setMobile("12343211235");
        testVo.setQuantity(5);
        testVo.setReason("testR");
//        testVo.setRegionId((long) 1);
        testVo.setType((byte) 0);
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/other/orderItems/{id}/aftersales", 5)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(testVo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":503}";
        log.debug(responseString);
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 买家提交售后 详细地址不为空
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/9 下午3:19
    */
    @Test
    @Order(1)
    public void postUserAftersales5() throws Exception {
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;

        log.debug("买家提交售后单");
        AftersaleVo testVo = new AftersaleVo();
        testVo.setConsignee("testC1");
//        testVo.setDetail("testD1");
        testVo.setMobile("12343211235");
        testVo.setQuantity(5);
        testVo.setReason("testR");
        testVo.setRegionId((long) 1);
        testVo.setType((byte) 0);
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/other/orderItems/{id}/aftersales", 5)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(testVo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":503}";
        log.debug(responseString);
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 买家提交售后 联系人不为空
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/9 下午3:20
    */
    @Test
    @Order(1)
    public void postUserAftersales6() throws Exception {
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;

        log.debug("买家提交售后单");
        AftersaleVo testVo = new AftersaleVo();
//        testVo.setConsignee("testC1");
        testVo.setDetail("testD1");
        testVo.setMobile("12343211235");
        testVo.setQuantity(5);
        testVo.setReason("testR");
        testVo.setRegionId((long) 1);
        testVo.setType((byte) 0);
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/other/orderItems/{id}/aftersales", 5)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(testVo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":503}";
        log.debug(responseString);
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 买家提交售后 电话不符合要求
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/9 下午3:22
    */
    @Test
    @Order(1)
    public void postUserAftersales7() throws Exception {
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;

        log.debug("买家提交售后单");
        AftersaleVo testVo = new AftersaleVo();
        testVo.setConsignee("testC1");
        testVo.setDetail("testD1");
        testVo.setMobile("123432112359");
        testVo.setQuantity(5);
        testVo.setReason("testR");
        testVo.setRegionId((long) 1);
        testVo.setType((byte) 0);
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/other/orderItems/{id}/aftersales", 5)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(testVo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":503}";
        log.debug(responseString);
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 买家提交售后 电话不能为空
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/9 下午3:24
    */
    @Test
    @Order(1)
    public void postUserAftersales8() throws Exception {
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;

        log.debug("买家提交售后单");
        AftersaleVo testVo = new AftersaleVo();
        testVo.setConsignee("testC1");
        testVo.setDetail("testD1");
//        testVo.setMobile("12343211235");
        testVo.setQuantity(5);
        testVo.setReason("testR");
        testVo.setRegionId((long) 1);
        testVo.setType((byte) 0);
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/other/orderItems/{id}/aftersales", 5)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(testVo)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":503}";
        log.debug(responseString);
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 买家查询所有的售后单信息 查询成功
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/9 下午3:36
    */
    @Test
    @Order(2)
    public void getAllUserAftersales1() throws Exception{
        String token = creatTestToken(1L, -2L, 10000);
        log.info(token);
        String responseString = null;
        String expectedResponse = null;
        log.debug("买家查询所有的售后单信息");

        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/aftersales")
                .header("authorization", token)
                .queryParam("beginTime", "2020-11-10 18:00:00"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
//        expectedResponse = "{\"errno\":0,\"data\":{\"orderId\":null,\"orderSn\":null,\"orderItemId\":5,\"skuId\":null,\"skuName\":null,\"customerId\":5,\"shopId\":null,\"type\":\"换货\",\"reason\":\"testR\",\"refund\":null,\"quantity\":5,\"regionId\":1,\"detail\":\"testD\",\"consignee\":\"testC\",\"mobile\":\"12343211234\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":\"待管理员审核\"},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 买家查询所有的售后单信息 查询成功
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/9 下午3:36
     */
    @Test
    @Order(2)
    public void getAllUserAftersales2() throws Exception{
        String token = creatTestToken(1L, -2L, 10000);
        String responseString = null;
        String expectedResponse = null;
        log.debug("买家查询所有的售后单信息");

        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/aftersales")
                .header("authorization", token)
                .queryParam("beginTime", "2019-11-17 18:00:51")
                .queryParam("endTime","2021-11-16 00:00:00")
                .queryParam("page","1")
                .queryParam("pageSize","10")
                .queryParam("type","0")
                .queryParam("page","1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
        expectedResponse = "{\"errno\":0}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    /**
     * 买家查询售后单 id查询不到
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/9 下午11:09
    */
    @Test
    @Order(2)
    public void getAftersalesById() throws Exception{
        String token = creatTestToken(1L, -2L, 10000);
        String responseString = null;
        String expectedResponse = null;
        log.debug("买家查询所有的售后单信息");

        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/aftersales/5")
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
//        expectedResponse = "{\"errno\":504}";
//        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 买家更新信息
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/15 下午9:44
    */
    @Test
    public void updateUserAftersales() throws Exception{
        String token = creatTestToken(1L, -2L, 10000);

        AftersaleUpdateVo testVo = new AftersaleUpdateVo();
        testVo.setConsignee("testC");
        testVo.setDetail("testD");
        testVo.setMobile("12343211234");
        testVo.setQuantity(5);
        testVo.setReason("testR");
        testVo.setRegionId((long) 1);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/aftersales/3")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(testVo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }

    /**
     * 买家取消售后和逻辑删除售后单
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/16 上午12:49
    */
    @Test
    public void deleteAftersale1() throws Exception{
        String token = creatTestToken(1L, -2L, 10000);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/other/aftersales/100")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }

    /**
     * 买家取消售后单和逻辑删除售后单
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/16 上午12:51
    */
    @Test
    public void deleteAftersale2() throws Exception{
        String token = creatTestToken(1L, -2L, 10000);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/other/aftersales/110")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }

    /**
     * 买家填写售后的运单信息
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/16 上午12:53
    */
    @Test
    public void userSendback1() throws Exception{
        String token = creatTestToken(1L, -2L, 10000);

        LogSnVo vo = new LogSnVo();
        vo.setLogSn("1243633");
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/aftersales/102")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(vo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }

    /**
     * 买家确认退款结束
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/16 上午12:58
    */
    @Test
    public void userConfirm1() throws Exception{
        String token = creatTestToken(1L, -2L, 10000);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/aftersales/107/confirm")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }

    /**
     * 买家确认换货信息 成功
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/16 上午1:07
    */
    @Test
    public void userConfirm2() throws Exception{
        String token = creatTestToken(1L, -2L, 10000);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/aftersales/108/confirm")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }

    /**
     * 管理员根据id查询
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/16 上午1:10
    */
    @Test
    public void adminGetAftersales1() throws Exception{
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/shops/0/aftersales/100")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }

    /**
     * 管理员同意
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/16 上午1:12
    */
    @Test
    public void adminAgreeAftersales1() throws Exception{
        String token = creatTestToken(1L, 0L, 10000);
        ConfirmVo vo = new ConfirmVo();
        vo.setConfirm(true);
        vo.setConclusion("测试成功");
        vo.setPrice((long)20);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/shops/1/aftersales/100/confirm")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(vo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }

    /**
     * 管理员不同意售后
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/16 上午1:21
    */
    @Test
    public void adminAgreeAftersales2() throws Exception{
        String token = creatTestToken(1L, 0L, 10000);

        ConfirmVo vo = new ConfirmVo();
        vo.setConfirm(false);
        vo.setConclusion("失败");
        vo.setPrice((long)20);

        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/shops/1/aftersales/101/confirm")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(vo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }

    /**
     * 管理员确认收到换货
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/16 上午1:23
    */
    @Test
    public void adminAcceptExchange1() throws Exception{
        String token = creatTestToken(1L, 0L, 10000);

        ReviewVo vo = new ReviewVo();
        vo.setConfirm(true);
        vo.setConclusion("成功");

        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/shops/1/aftersales/103/receive")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(vo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }

    /**
     * 管理员确认收到退货
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/16 上午1:27
    */
    @Test
    public void adminAcceptRefund1() throws Exception{
        String token = creatTestToken(1L, 0L, 10000);
        ReviewVo vo = new ReviewVo();
        vo.setConfirm(true);
        vo.setConclusion("成功");
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/shops/1/aftersales/104/receive")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(vo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }

    /**
     * 管理员寄出维修货物
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/16 上午1:29
    */
    @Test
    public void adminDeliver() throws Exception{
        String token = creatTestToken(1L, 0L, 10000);
        ShopLogSnVo vo = new ShopLogSnVo();
        vo.setShopLogSn("53465257");
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/shops/1/aftersales/106/deliver")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(vo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        log.debug(responseString);
    }
}

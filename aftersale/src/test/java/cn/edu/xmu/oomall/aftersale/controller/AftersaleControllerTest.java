package cn.edu.xmu.oomall.aftersale.controller;

import cn.edu.xmu.oomall.aftersale.AftersaleApplication;
import cn.edu.xmu.oomall.aftersale.model.vo.AftersaleVo;
import cn.edu.xmu.oomall.aftersale.model.vo.ReviewVo;
import cn.edu.xmu.oomall.util.JwtHelper;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest(classes = AftersaleApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AftersaleControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * 创建测试用token
     *
     * @author 24320182203281 王纬策
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }

    /**
     * 售后完整流程测试
     *
     * @author wwc
     * @date 2020/11/26 17:45
     * @version 1.0
     */
    @Test
    public void completeAftersaleTest() throws Exception {
        String token = creatTestToken(1L, -2L, 10000);
        String responseString = null;
        String expectedResponse = null;
        log.debug("获得售后单的所有状态");
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/aftersales/states")
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"待管理员审核\"},{\"code\":1,\"name\":\"待买家发货\"},{\"code\":2,\"name\":\"买家已发货\"},{\"code\":3,\"name\":\"待店家退款\"},{\"code\":4,\"name\":\"待店家发货\"},{\"code\":5,\"name\":\"店家已发货\"},{\"code\":6,\"name\":\"审核不通过\"},{\"code\":7,\"name\":\"已取消\"},{\"code\":8,\"name\":\"已结束\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("买家查询所有的售后单信息");
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/aftersales")
                .header("authorization", token)
                .queryParam("beginTime", "2020-11-17 18:00:00"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"orderId\":null,\"orderSn\":null,\"orderItemId\":1,\"skuId\":null,\"skuName\":null,\"customerId\":1,\"shopId\":1,\"serviceSn\":\"10000000\",\"type\":\"维修\",\"reason\":\"测试维修\",\"refund\":0,\"quantity\":1,\"regionId\":1,\"detail\":\"学生公寓\",\"consignee\":\"wwc1\",\"mobile\":\"13900000001\",\"customerLogSn\":\"12345678901\",\"shopLogSn\":\"09876543211\",\"state\":\"待管理员审核\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("买家根据售后单id查询售后单信息");
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/aftersales/{id}", 1)
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"orderId\":null,\"orderSn\":null,\"orderItemId\":1,\"skuId\":null,\"skuName\":null,\"customerId\":1,\"shopId\":1,\"serviceSn\":\"10000000\",\"type\":\"维修\",\"reason\":\"测试维修\",\"refund\":0,\"quantity\":1,\"regionId\":1,\"detail\":\"学生公寓\",\"consignee\":\"wwc1\",\"mobile\":\"13900000001\",\"customerLogSn\":\"12345678901\",\"shopLogSn\":\"09876543211\",\"state\":\"待管理员审核\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("管理员查看所有售后单");
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/shops/{id}/aftersales", 0)
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":{\"total\":3,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"orderId\":null,\"orderItemId\":1,\"customerId\":1,\"shopId\":1,\"serviceSn\":\"10000000\",\"type\":\"维修\",\"reason\":\"测试维修\",\"refund\":0,\"quantity\":1,\"regionId\":1,\"detail\":\"学生公寓\",\"consignee\":\"wwc1\",\"mobile\":\"13900000001\",\"customerLogSn\":\"12345678901\",\"shopLogSn\":\"09876543211\",\"state\":\"待管理员审核\"},{\"id\":2,\"orderId\":null,\"orderItemId\":2,\"customerId\":2,\"shopId\":2,\"serviceSn\":\"10000001\",\"type\":\"退货\",\"reason\":\"测试退货\",\"refund\":0,\"quantity\":1,\"regionId\":1,\"detail\":\"学生公寓\",\"consignee\":\"wwc2\",\"mobile\":\"13900000002\",\"customerLogSn\":\"12345678902\",\"shopLogSn\":\"09876543212\",\"state\":\"待买家发货\"},{\"id\":3,\"orderId\":null,\"orderItemId\":3,\"customerId\":3,\"shopId\":3,\"serviceSn\":\"10000002\",\"type\":\"换货\",\"reason\":\"测试换货\",\"refund\":0,\"quantity\":1,\"regionId\":1,\"detail\":\"学生公寓\",\"consignee\":\"wwc3\",\"mobile\":\"13900000003\",\"customerLogSn\":\"12345678903\",\"shopLogSn\":\"09876543213\",\"state\":\"待买家发货\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("卖家根据售后单id查询售后单信息");
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/shops/{shopId}/aftersales/{id}", 1, 1)
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"orderId\":null,\"orderSn\":null,\"orderItemId\":1,\"skuId\":null,\"skuName\":null,\"customerId\":1,\"shopId\":1,\"serviceSn\":\"10000000\",\"type\":\"维修\",\"reason\":\"测试维修\",\"refund\":0,\"quantity\":1,\"regionId\":1,\"detail\":\"学生公寓\",\"consignee\":\"wwc1\",\"mobile\":\"13900000001\",\"customerLogSn\":\"12345678901\",\"shopLogSn\":\"09876543211\",\"state\":\"待管理员审核\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
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
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":{\"id\":4,\"orderId\":null,\"orderSn\":null,\"orderItemId\":5,\"skuId\":null,\"skuName\":null,\"customerId\":1,\"shopId\":null,\"type\":\"换货\",\"reason\":\"testR\",\"refund\":null,\"quantity\":5,\"regionId\":1,\"detail\":\"testD\",\"consignee\":\"testC\",\"mobile\":\"12343211234\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":\"待管理员审核\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        log.debug("管理员同意");
        ReviewVo reviewVo = new ReviewVo();
        reviewVo.setConfirm(true);
        reviewVo.setConclusion("string");
        responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/shops/{shopId}/aftersales/{id}/confirm", 1, 1)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(reviewVo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("买家修改售后单");
        responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/aftersales/{id}", 1)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content("{\"consignee\":\"testC\",\"detail\":\"testD\",\"mobile\":\"19843211234\",\"quantity\":10,\"reason\":\"testR\",\"regionId\":1,\"type\":2}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("管理员同意");
        reviewVo = new ReviewVo();
        reviewVo.setConfirm(true);
        reviewVo.setConclusion("string");
        responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/shops/{shopId}/aftersales/{id}/confirm", 1, 1)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content("{\"confirm\":true,\"conclusion\":\"string\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("买家填写运单信息");
        responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/aftersales/{id}/sendback", 1)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content("{\"logSn\":\"1233211234\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("店家确认收货");
        responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/shops/{shopId}/aftersales/{id}/receive", 1, 1)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content("{\"confirm\":true,\"conclusion\":\"string\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("卖家填写运单信息");
        responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/shops/{shopId}/aftersales/{id}/deliver", 1, 1)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content("{\"logSn\":\"1233211234\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("买家确认结束");
        responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/aftersales/{id}/confirm", 1)
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("逻辑删除");
        responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/other/aftersales/{id}", 1)
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("买家根据售后单id查询售后单信息");
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/aftersales/{id}", 1)
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":609,\"errmsg\":\"未找到该售后单\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }
}

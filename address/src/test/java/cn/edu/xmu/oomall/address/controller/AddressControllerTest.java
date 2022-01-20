package cn.edu.xmu.oomall.address.controller;

import cn.edu.xmu.oomall.address.AddressApplication;
import cn.edu.xmu.oomall.address.model.vo.AddressVo;
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
@SpringBootTest(classes = AddressApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AddressControllerTest {

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
     * 地址完整流程测试
     *
     * @author wwc
     * @date 2020/11/27 17:45
     * @version 1.0
     */
    @Test
    public void completeAddressTest() throws Exception {
        String token = creatTestToken(1L, 0L, 10000);
        String responseString = null;
        String expectedResponse = null;
        log.debug("买家查询所有已有的地址信息");
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/addresses")
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"regionId\":3,\"detail\":\"厦大学生公寓\",\"consignee\":\"test1\",\"mobile\":\"13212344321\",\"beDefault\":1,\"gmtCreate\":\"2020-11-24 18:00:00\",\"state\":0},{\"id\":2,\"regionId\":2,\"detail\":\"厦大学生公寓\",\"consignee\":\"test2\",\"mobile\":\"13212344322\",\"beDefault\":0,\"gmtCreate\":\"2020-11-24 18:00:00\",\"state\":0}]},\"errmsg\":\"成功\"}";
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("查询某个地区的所有上级地区");
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/other/region/{id}/ancestor", 3)
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":[{\"id\":1,\"pid\":0,\"name\":\"中国\",\"postalCode\":210000,\"state\":0,\"gmtCreate\":\"2020-11-14 18:00:00\",\"gmtModified\":\"2020-11-25 18:00:00\"},{\"id\":2,\"pid\":1,\"name\":\"福建\",\"postalCode\":210001,\"state\":0,\"gmtCreate\":\"2020-11-15 18:00:00\",\"gmtModified\":\"2020-11-25 18:00:00\"},{\"id\":3,\"pid\":2,\"name\":\"厦门\",\"postalCode\":210011,\"state\":0,\"gmtCreate\":\"2020-11-15 18:00:00\",\"gmtModified\":\"2020-11-25 18:00:00\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("买家新增地址");
        AddressVo testVo = new AddressVo();
        testVo.setConsignee("testC");
        testVo.setDetail("testD");
        testVo.setMobile("12398761234");
        testVo.setRegionId((long)3);
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/other/addresses")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(JSONObject.toJSONString(testVo)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"data\":{\"id\":3,\"regionId\":3,\"detail\":\"testD\",\"consignee\":\"testC\",\"mobile\":\"12398761234\",\"beDefault\":0,\"state\":0},\"errmsg\":\"成功\"}";
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        log.debug("管理员在地区下新增子地区");
        responseString = this.mvc.perform(MockMvcRequestBuilders.post("/other/regions/{id}/subregions", 0)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content("{\"name\":\"testN\",\"postalCode\":\"123456\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("买家修改地址");
        responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/addresses/{id}", 1)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content("{\"consignee\":\"testC\",\"detail\":\"testD\",\"mobile\":\"12312344321\",\"regionId\":\"2\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("买家设置默认地址");
        responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/addresses/{2}/default", 2)
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("管理员修改地区");
        responseString = this.mvc.perform(MockMvcRequestBuilders.put("/other/regions/{id}", 3)
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content("{\"name\":\"厦门T\",\"postalCode\":\"210012\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("买家删除地址");
        responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/other/addresses/{id}", 2)
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        log.debug("管理员废弃地区");
        responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/other/regions/{id}", 2)
                .header("authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
    }
}

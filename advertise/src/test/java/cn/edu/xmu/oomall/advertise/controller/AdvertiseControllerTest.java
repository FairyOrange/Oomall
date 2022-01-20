package cn.edu.xmu.oomall.advertise.controller;

import cn.edu.xmu.oomall.advertise.AdvertiseApplication;
import cn.edu.xmu.oomall.advertise.mapper.AdvertisementPoMapper;
import cn.edu.xmu.oomall.advertise.model.po.AdvertisementPo;
import cn.edu.xmu.oomall.util.JwtHelper;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.encript.AES;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(classes = AdvertiseApplication.class)   //标识本类是一个SpringBootTest
@Transactional
public class AdvertiseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdvertisementPoMapper poMapper;

    /**
     * 未登录获得广告的所有状态
     *
     * @author 24320182203175 陈晓如
     */
    @Test
    public void advertiseTest1() throws Exception {
        String responseString = this.mockMvc.perform(get("/other/advertisement/states"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 使用伪造token获得广告的所有状态
     *
     * @author 24320182203175 陈晓如
     */
    @Test
    public void advertiseTest2() throws Exception {
        String responseString = this.mockMvc.perform(get("/other/advertisement/states")
                .header("authorization", "test"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }


    /**
     * 获得广告的所有状态
     *
     * @author 24320182203175 陈晓如
     */
    @Test
    public void advertiseTest3() throws Exception {
        String token = this.creatTestToken(1L, 0L, 100);
        String responseString = this.mockMvc.perform(get("/other/advertisement/states")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"code\": 0,\n" +
                "      \"name\": \"待审核\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": 4,\n" +
                "      \"name\": \"上架\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": 6,\n" +
                "      \"name\": \"下架\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 管理员设置默认广告 广告id不存在
     *
     * @author 24320182203175 陈晓如
     */
    @Test
    public void advertiseTest4() throws Exception {
        String token = this.creatTestToken(1L, 0L, 100);
        String responseString = this.mockMvc.perform(put("/other/advertisement/1/default")
                .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的广告不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    /**
     * 管理员设置默认广告 广告从默认广告变为非默认广告
     *
     * @author 24320182203175 陈晓如
     */
    @Test
    public void advertiseTest5() throws Exception {
        String token = this.creatTestToken(1L, 0L, 100);
        log.error(token);
        String responseString = this.mockMvc.perform(put("/other/advertisement/144/default")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

        AdvertisementPo updatedPo = poMapper.selectByPrimaryKey(144L);
        Assert.isTrue(updatedPo.getBeDefault().equals((byte)0), "广告没有变为非默认广告");
    }

    /**
     * 管理员设置默认广告 广告从非默认广告变为默认广告
     *
     * @author 24320182203175 陈晓如
     */
    @Test
    public void advertiseTest6() throws Exception {
        String token = this.creatTestToken(1L, 0L, 100);
        String responseString = this.mockMvc.perform(put("/other/advertisement/121/default")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

        AdvertisementPo updatedPo = poMapper.selectByPrimaryKey(121L);
        Assert.isTrue(updatedPo.getBeDefault().equals((byte)1), "广告没有变为默认广告");

    }
    /**
     * 创建测试用token
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }

//    /**
//     * @author cxr
//     * 广告完整流程测试
//     */
//    @Test
//    public void completeAdvertiseTest() {
//        String token = creatTestToken(1L, 0L, 10000);
//        String responseString = null;
//        String expectedResponse = null;
//
////========================= API测试语句编写 ==============================================================================
//        log.debug("管理员审核广告");
//        try {
//            responseString = this.mvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/other/advertisement/1/audit")
//                    .header("authorization", token)
//                    .contentType("application/json;charset=UTF-8")
//                    .content("{\"conclusion\":\"true\",\"message\":\"审核通过\"}"))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, true);
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//
//        log.debug("管理员删除某一个广告");
//        try {
//            responseString = this.mvc.perform(MockMvcRequestBuilders.delete("http://localhost:8080/other/advertisement/1")
//                    .header("authorization", token))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, true);
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//
//        log.debug("管理员修改广告内容");
//        AdvertiseVo testVo = new AdvertiseVo();
//        testVo.setContent("修改后的内容");
//        testVo.setSegId((long)1);
//        testVo.setWeight(2);
//        testVo.setLink("修改后的链接");
//        try {
//            responseString = this.mvc.perform(MockMvcRequestBuilders.put("http://localhost:8080/other/advertisement/2")
//                    .header("authorization", token)
//                    .contentType("application/json;charset=UTF-8")
//                    .content(JSONObject.toJSONString(testVo)))
//                    .andExpect(MockMvcResultMatchers.status().isCreated())
//                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, true);
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//
//        testVo = new AdvertiseVo();
//        testVo.setContent("广告内容5");
//        testVo.setWeight(1);
//        testVo.setLink("广告链接5");
//        log.debug("管理员在广告时段下新建广告");
//        try {
//            responseString = this.mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/other/timesegments/1/advertisement")
//                    .header("authorization", token)
//                    .contentType("application/json;charset=UTF-8")
//                    .content(JSONObject.toJSONString(testVo)))
//                    .andExpect(MockMvcResultMatchers.status().isCreated())
//                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":5," +
//                "\"link\":\"广告链接5\",\"imagePath\":\"广告图片5\",\"content\":\"广告内容5\",\"segId\":1,\"state\":0," +
//                "\"weight\":1, \"beDefault\":false, \"beginDate\":\"2020/12/03 10:00, \"endDate\":\"2020/12/04 10:00\"" +
//                "\"repeat\": false, \"gmtCreated\":\"2020-11-24T18:00:00\",\"gmtModified\":\"2020-11-25T18:00:00\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, true);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        log.debug("管理员查看某一个广告时间段的广告");
//        try {
//            responseString = this.mvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/other/timesegments/1/advertisement")
//                    .header("authorization", token)
//                    .queryParam("page","1")
//                    .queryParam("pageSize","10"))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"data\":{\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":5," +
//                "\"link\":\"广告链接5\",\"imagePath\":\"广告图片5\",\"content\":\"广告内容5\",\"segId\":1,\"state\":0," +
//                "\"weight\":1, \"beDefault\":false, \"beginDate\":\"2020/12/03 10:00, \"endDate\":\"2020/12/04 10:00\"" +
//                "\"repeat\": false, \"gmtCreated\":\"2020-11-24T18:00:00\",\"gmtModified\":\"2020-11-25T18:00:00\"}]},\"errmsg\":\"成功\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, true);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        log.debug("管理员在广告时段下新建广告");
//        try{
//            responseString = this.mvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/other/timesegments/2/advertisement/5")
//                    .header("authorization",token))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":5," +
//                "\"link\":\"广告链接5\",\"imagePath\":\"广告图片5\",\"content\":\"广告内容5\",\"segId\":2,\"state\":0," +
//                "\"weight\":1, \"beDefault\":false, \"beginDate\":\"2020/12/03 10:00, \"endDate\":\"2020/12/04 10:00\"" +
//                "\"repeat\": false, \"gmtCreated\":\"2020-11-24T18:00:00\",\"gmtModified\":\"2020-11-24T18:00:00\"}";
//
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString,true);
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//
//        log.debug("获取当前时段广告列表");
//        try {
//            responseString = this.mvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/other/advertisement/current")
//                    .header("authorization",token))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"id\":5," +
//                "\"link\":\"广告链接5\",\"imagePath\":\"广告图片5\",\"content\":\"广告内容5\",\"segId\":2,\"state\":0," +
//                "\"weight\":1, \"beDefault\":false, \"beginDate\":\"2020/12/03 10:00, \"endDate\":\"2020/12/04 10:00\"" +
//                "\"repeat\": false, \"gmtCreated\":\"2020-11-24T18:00:00\",\"gmtModified\":\"2020-11-25T18:00:00\"}]";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, true);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        log.debug("获得广告的所有状态");
//        try {
//            responseString = this.mvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/other/advertisement/states")
//                    .header("authorization",token))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"code\":0,\"name\":\"审核中\"},{\"code\":1,\"name\":\"下架\"},{\"code\":2,\"name\":\"上架\"}]}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString, true);
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//
//        log.debug("管理员下架广告");
//        try {
//            responseString = this.mvc.perform(MockMvcRequestBuilders.put("http://localhost:8080/other/advertisement/1/onshelves")
//                    .header("authorization", token))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"message\":\"成功\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse,responseString,true);
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//
//        log.debug("管理员上架广告");
//        try {
//            responseString = this.mvc.perform(MockMvcRequestBuilders.put("http://localhost:8080/other/advertisement/1/offshelves")
//                    .header("authorization", token))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"message\":\"成功\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse,responseString,true);
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//
//        log.debug("管理员设置默认广告");
//        try {
//            responseString = this.mvc.perform(MockMvcRequestBuilders.put("http://localhost:8080/other/advertisement/5/default")
//                    .header("authorization", token))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.content().contentType("application/json;cahrset=UTF-8"))
//                    .andReturn().getResponse().getContentAsString();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        expectedResponse = "{\"errno\":0,\"message\":\"成功\"}";
//        try {
//            JSONAssert.assertEquals(expectedResponse, responseString,true);
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//
//        log.debug("管理员上传广告图片")
//    }
}

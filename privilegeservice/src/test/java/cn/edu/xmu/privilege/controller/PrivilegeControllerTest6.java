package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.oomall.util.JacksonUtil;
import cn.edu.xmu.oomall.util.JwtHelper;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import cn.edu.xmu.oomall.util.encript.AES;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.mapper.UserPoMapper;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.po.UserPo;
import cn.edu.xmu.privilege.model.po.UserPoExample;
import cn.edu.xmu.privilege.model.vo.ModifyPwdVo;
import cn.edu.xmu.privilege.model.vo.ResetPwdVo;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author yang ming
 * @date Created in 2020/11/11 20:05
 **/

@SpringBootTest(classes = PrivilegeServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
public class PrivilegeControllerTest6 {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserPoMapper userPoMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
    *resetpassword?????????
    */
    @Test
    public void changePassword1() throws Exception{

        //?????????????????????????????????(?????????????????????)
        UserPo userPo = new UserPo();
        userPo.setEmail(AES.encrypt("925882085@qq.com",User.AESPASS));
        userPo.setMobile(AES.encrypt("13511335577", User.AESPASS));
        userPo.setUserName("test");
        userPo.setPassword(AES.encrypt("123456",User.AESPASS));
        userPo.setGmtCreate(LocalDateTime.now());
        userPoMapper.insertSelective(userPo);

        //???reset??????
        ResetPwdVo resetPwdVo = new ResetPwdVo();
        resetPwdVo.setEmail("925882085@qq.com");
        resetPwdVo.setMobile("13511335577");
        String Json = JacksonUtil.toJson(resetPwdVo);

        String responseString = this.mvc.perform(put("/privilege/adminusers/password/reset").contentType("application/json;charset=UTF-8").content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"??????\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        //????????????????????????????????????
        UserPoExample userPoExample1 = new UserPoExample();
        UserPoExample.Criteria criteria = userPoExample1.createCriteria();
        criteria.andMobileEqualTo(AES.encrypt("13511335577",User.AESPASS));
        List<UserPo> userPo1 = userPoMapper.selectByExample(userPoExample1);
        userPoMapper.deleteByPrimaryKey(userPo1.get(0).getId());


    }

    /**
     * ???????????????????????????????????????
     */
    @Test
    public void changePassword2() throws Exception{

        //??????????????????
        UserPo userPo = userPoMapper.selectByPrimaryKey(1L);

        //???redis??????????????????
        redisTemplate.opsForValue().set("cp_666666","1");
        redisTemplate.expire("cp_666666", 60*1000, TimeUnit.MILLISECONDS);

        //???modify??????
        ModifyPwdVo vo = new ModifyPwdVo();
        vo.setCaptcha("666666");
        vo.setNewPassword(AES.decrypt(userPo.getPassword(),User.AESPASS));
        String Json1 = JacksonUtil.toJson(vo);

        String responseString = this.mvc.perform(put("/privilege/adminusers/password").contentType("application/json;charset=UTF-8").content(Json1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();


        String expectedResponse1 = "{\"errno\": 741, \"errmsg\": \"????????????????????????\"}";
        JSONAssert.assertEquals(expectedResponse1, responseString, true);

    }



    /**
     * ????????????????????????????????????????????????
     */
    @Test
    public void changePassword4() throws Exception{

        //?????????????????????????????????
        UserPo userPoTest =userPoMapper.selectByPrimaryKey(1L);

        //???reset??????
        ResetPwdVo resetPwdVo = new ResetPwdVo();
        resetPwdVo.setEmail("112334455@qq.com");
        resetPwdVo.setMobile(AES.decrypt(userPoTest.getMobile(),User.AESPASS));
        String Json = JacksonUtil.toJson(resetPwdVo);

        String responseString = this.mvc.perform(put("/privilege/adminusers/password/reset").contentType("application/json;charset=UTF-8").content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 745, \"errmsg\": \"?????????????????????????????????\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * ????????????????????????????????????????????????
     */
    @Test
    public void changePassword5() throws Exception{

        //?????????????????????????????????
        UserPo userPoTest =userPoMapper.selectByPrimaryKey(1L);

        //???reset??????
        ResetPwdVo resetPwdVo = new ResetPwdVo();
        resetPwdVo.setEmail(AES.decrypt(userPoTest.getEmail(),User.AESPASS));
        resetPwdVo.setMobile("13511223344");
        String Json = JacksonUtil.toJson(resetPwdVo);

        String responseString = this.mvc.perform(put("/privilege/adminusers/password/reset").contentType("application/json;charset=UTF-8").content(Json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 746, \"errmsg\": \"?????????????????????????????????\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    @Test
    /**
     * ??????????????????????????????
     */
    public void changePassword6() throws Exception{

        //???modify??????
        ModifyPwdVo vo = new ModifyPwdVo();
        vo.setCaptcha("222222");
        vo.setNewPassword("Ooad123456");
        String Json1 = JacksonUtil.toJson(vo);

        String responseString = this.mvc.perform(put("/privilege/adminusers/password").contentType("application/json;charset=UTF-8").content(Json1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 700, \"errmsg\": \"????????????????????????????????????\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * modifypassword?????????
     */
    @Test
    public void changePassword7() throws Exception{

        //??????????????????
        UserPo olduserPo = userPoMapper.selectByPrimaryKey(1L);

        //???redis??????????????????
        redisTemplate.opsForValue().set("cp_666666","1");
        redisTemplate.expire("cp_666666", 60*1000, TimeUnit.MILLISECONDS);

        //???modify??????
        ModifyPwdVo vo = new ModifyPwdVo();
        vo.setCaptcha("666666");
        vo.setNewPassword("Ooad123456");
        String Json1 = JacksonUtil.toJson(vo);

        String responseString = this.mvc.perform(put("/privilege/adminusers/password").contentType("application/json;charset=UTF-8").content(Json1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\": 0, \"errmsg\": \"??????\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);


        // ???????????????????????????????????????
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(1L);
        Assert.state(updatedPo.getPassword().equals(olduserPo.getPassword()), "??????????????????");

    }


}


package cn.edu.xmu.oomall.advertise.controller;

import cn.edu.xmu.oomall.util.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author cxr
 * 用来测试广告上传图片功能
 * @createDate 2020/12/04 11:27
 * @modifiedDate 2020/12/04 11:27
 */
@Slf4j
@SpringBootTest(classes = AdvertiseController.class) //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdvertiseApplicationTest1 {

    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(AdvertiseApplicationTest1.class);
    /**
     * 创建测试用的token
     * @author cxr
     *
     * @param userId 用户id
     * @param departId 部门id
     * @param expireTime 有效时间
     * @return token
     * @createDate 2020/12/04 11:46
     * @modifyDate 2020/12/04 11:46
     */
    private final String createTestToken(Long userId, Long departId, int expireTime){
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.info(token);
        return token;
    }

    /**
     * 管理员上传图片成功
     * @author cxr
     * @throws Exception
     * @createDate 2020/12/04 16:43
     * @modifyDate 2020/12/04 16:43
     */
    @Test
    public void uploadFileSutccess() throws Exception{
        String token = createTestToken(1L,0L,100);
        File file = new File("."+File.separator + "src" + File.separator + "test" + File.separator+"resources" + File.separator + "img" + File.separator+"timg.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "timg.png" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/advertisement/1/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员上传图片失败 操作的广告不存在
     * @author cxr
     * @throws Exception
     * @createDate 2020/12/04 16:47
     * @modifyDate 2020/12/04 16:47
     */
    @Test
    public void UploadFileFail1() throws Exception{
        String token = createTestToken(1L,0L,100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"timg.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "timg.png" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/advertisement/1111/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员上传图片失败 文件格式错误
     * @author cxr
     * @throws Exception
     * @createDate 2020/12/04 16:48
     * @modifyDate 2020/12/04 16:48
     */
    @Test
    public void UploadFileFail2() throws Exception{
        String token = createTestToken(1L, 0L, 100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"文本文件.txt");
        MockMultipartFile firstFile = new MockMultipartFile("img", "文本文件.txt" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/advertisement/1/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":508,\"errmsg\":\"图片格式不正确\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员上传图片失败 文件格式错误，伪装成图片
     * @author cxr
     * @throws Exception
     * @createDate 2020/12/04 16:48
     * @modifyDate 2020/12/04 16:48
     */
    @Test
    public void UploadFileFail3() throws Exception{
        String token = createTestToken(1L, 0L, 100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"伪装的图片.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "伪装的图片.png" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/advertisement/1/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":508,\"errmsg\":\"图片格式不正确\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员上传图片失败 文件格式错误，图片大小过大
     * @author cxr
     * @throws Exception
     * @createDate 2020/12/04 16:48
     * @modifyDate 2020/12/04 16:48
     */
    @Test
    public void UploadFileFail4() throws Exception{
        String token = createTestToken(1L, 0L, 100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"大小超限图片.jpg");
        MockMultipartFile firstFile = new MockMultipartFile("img", "大小超限图片.jpg" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/advertisement/1/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":509,\"errmsg\":\"图片大小超限\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
}

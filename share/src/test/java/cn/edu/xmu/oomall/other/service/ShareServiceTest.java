package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.oomall.share.ShareApplication;
import cn.edu.xmu.oomall.share.model.bo.ShareActivityStrategy;
import cn.edu.xmu.oomall.share.service.ShareService;
import cn.edu.xmu.oomall.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Fiber W.
 * created at 11/27/20 4:06 PM
 * @detail cn.edu.xmu.oomall.other.service
 */
@Slf4j
@SpringBootTest(classes = ShareApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
public class ShareServiceTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ShareService shareService;

    @Test
    public void setShareRebateTest() {
//        String string = "{\n" +
//                "    \"rule\" :[" +
//                "        {" +
//                "            \"num\" :0," +
//                "            \"rate\":0.01" +
//                "        }," +
//                "        {" +
//                "            \"num\" :10," +
//                "            \"rate\":0.1" +
//                "        }" +
//                "    ]," +
//                "    \"firstOrAvg\" : 0" +
//                "}";
        List<Long> result = shareService.setShareRebate(Long.valueOf(321), Long.valueOf(1), 1, Long.valueOf(1000), Long.valueOf(123), LocalDateTime.now()).getData();
        log.debug(result.toString());
        
    }
}

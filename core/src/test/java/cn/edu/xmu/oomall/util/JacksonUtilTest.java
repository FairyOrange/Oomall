package cn.edu.xmu.oomall.util;


import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class JacksonUtilTest {

    public void parseSubnodeToStringListTest(){
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未上架\",\"code\":0},{\"name\":\"上架\",\"code\":4},{\"name\":\"已删除\",\"code\":6}],\"errmsg\":\"成功\"}";

        List<String> states = JacksonUtil.parseSubnodeToStringList(expectedResponse, "/data");
        assertEquals(6, states.size());
        assertEquals("{\"name\":\"未上架\",\"code\":0}", states.get(0));

    }
}

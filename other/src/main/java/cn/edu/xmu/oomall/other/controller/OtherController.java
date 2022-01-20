package cn.edu.xmu.oomall.other.controller;

import cn.edu.xmu.oomall.util.Common;
import cn.edu.xmu.oomall.util.JacksonUtil;
import cn.edu.xmu.oomall.util.ReturnObject;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * OtherController
 *
 * @author WangWeice
 */
@Api
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
@ApiOperation(value = "测试", tags={ "other" })
@Slf4j
public class OtherController {

    @ApiOperation(value = "测试")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PostMapping("test")
    public String test(@RequestBody String json){
        log.debug(json);
        log.debug(JacksonUtil.parseString(json, "name"));
        log.debug(JacksonUtil.parseString(json, "pwd"));
        return "test";
    }
}

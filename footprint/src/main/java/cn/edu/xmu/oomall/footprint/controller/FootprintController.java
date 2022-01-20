package cn.edu.xmu.oomall.footprint.controller;


import cn.edu.xmu.oomall.annotation.Audit;
import cn.edu.xmu.oomall.annotation.LoginUser;
import cn.edu.xmu.oomall.footprint.model.bo.FootPrint;
import cn.edu.xmu.oomall.footprint.model.vo.FootPrintVo;
import cn.edu.xmu.oomall.footprint.service.FootPrintService;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.util.Common;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

/**
 * 足迹Controller
 *
 * @author yang8miao
 * @date 2020/11/26 17:47
 * @version 1.0
 */
@Slf4j
@Api(value = "足迹服务", tags = {"footprint"})
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class FootprintController {

    @Autowired
    private FootPrintService footPrintService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    /**
     * 管理员查看浏览记录
     *
     * @author yang8miao
     * @date 2020/11/26 17:50
     * @version 1.0
     */
    @ApiOperation(value = "管理员查看浏览记录", tags={ "footprint" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "userId", value = "用户id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/shops/{did}/footprints")
    @Audit
    public Object getFootprints(
            @RequestParam(required = false) Long userId,
            @PathVariable Long did,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize){

        FootPrint footPrint=new FootPrint();
        footPrint.setCustomerId(userId);
        ReturnObject returnObject = footPrintService.getFootprints(footPrint, beginTime, endTime, page, pageSize);

        if(returnObject.getCode().equals(ResponseCode.Log_Bigger)){
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        return Common.getPageRetObject(returnObject);
    };

}

package cn.edu.xmu.oomall.other.controller;


import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class FootprintController {

    @ApiOperation(value = "管理员查看浏览记录", tags={ "footprint" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "用户id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/footprints")
    public Object getFootprints(@PathVariable Integer id, @RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

    @ApiOperation(value = "增加足迹", notes = "平台内部调用", tags={ "footprint" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "用户id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "可填写的信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/users/{id}/footprints")
    public Object postUsersIdFootprints(@PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

}

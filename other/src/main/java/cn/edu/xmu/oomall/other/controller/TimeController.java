package cn.edu.xmu.oomall.other.controller;

import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class TimeController {

    @ApiOperation(value = "平台管理员新增广告时间段", notes = "系统内部调用，或者平台管理员", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "起止时间", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 604, message = "时段冲突"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/advertisement/timesegments")
    public Object postAdvertisementTimesegments(@RequestBody Object vo){
        return null;
    };

    @ApiOperation(value = "管理员获取广告时间段列表", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/advertisement/timesegments")
    public Object getAdvertisementTimesegments(@RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

    @ApiOperation(value = "平台管理员新增秒杀时间段", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "起止时间", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 604, message = "时段冲突"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/flashsale/timesegments")
    public Object postFlashsaleTimesegments(@RequestBody Object vo){
        return null;
    };

    @ApiOperation(value = "管理员获取秒杀时间段列表", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/flashsale/timesegments")
    public Object getFlashsaleTimesegments(@RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

    @ApiOperation(value = "平台管理员删除秒杀时间段", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "时段id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/flashsale/timesegments/{id}")
    public Object deleteFlashsaleTimesegmentsId(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "平台管理员删除广告时间段", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "时段id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/advertisement/timesegments/{id}")
    public Object deleteAdvertisementTimesegmentsId(@PathVariable Integer id){
        return null;
    };

}

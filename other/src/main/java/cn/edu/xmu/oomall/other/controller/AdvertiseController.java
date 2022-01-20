package cn.edu.xmu.oomall.other.controller;

import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class AdvertiseController {

    @ApiOperation(value = "管理员审核广告", tags={ "advertise" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "可填写的广告信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 608, message = "广告状态禁止"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/advertisement/{id}/audit")
    public Object putAdvertisementIdAudit(@PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "管理员删除某一个广告", tags={ "advertise" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 608, message = "广告状态禁止"),
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/advertisement/{id}")
    public Object deleteAdvertisementId(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "管理员修改广告内容", tags={ "advertise" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "可修改的广告信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 608, message = "广告状态禁止"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/advertisement/{id}")
    public Object putAdvertisementId(@PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "管理员查看某一个广告时间段的广告", notes = "查询时可以选择按照时间段来查看,当id=0时为未定义时段的广告", tags={ "advertise" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告时间段id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "页码", value = "page", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "每页数目", value = "pageSize", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/timesegments/{id}/advertisement")
    public Object getTimesegmentsIdAdvertisement(@PathVariable Integer id, @RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

    @ApiOperation(value = "管理员在广告时段下新建广告", tags={ "advertise" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告时间段id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "可填写的广告信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 603, message = "达到时段广告上限"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/timesegments/{id}/advertisement")
    public Object postTimesegmentsIdAdvertisement(@PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "管理员在广告时段下增加广告", notes = "若广告有时段则覆盖原时段", tags={ "advertise" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "tid", value = "广告时间段id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 603, message = "达到时段广告上限"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/timesegments/{tid}/advertisement/{id}")
    public Object postTimesegmentsIdAdvertisementId(@PathVariable Integer tid, @PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "获取当前时段广告列表", notes = "无需登录", tags={ "advertise" })
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/advertisement/current")
    public Object getAdvertisementCurrent(){
        return null;
    };

    @ApiOperation(value = "获得广告的所有状态", tags={ "advertise" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/advertisement/states")
    public Object getAdvertisementStates(){
        return null;
    };

    @ApiOperation(value = "管理员下架广告", tags={ "advertise" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 608, message = "广告状态禁止"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/advertisement/{id}/offshelves")
    public Object putAdvertisementIdOffshelves(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "管理员上架广告", tags={ "advertise" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 608, message = "广告状态禁止"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/advertisement/{id}/onshelves")
    public Object putAdvertisementIdOnshelvess(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "管理员设置默认广告", tags={ "advertise" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/advertisement/{id}/default")
    public Object putAdvertisementIdDefault(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "管理员上传广告图片", notes = "如果该广告有图片，需修改该广告的图片，并删除图片文件", tags={ "advertise" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
            @ApiImplicitParam(paramType = "formData", dataType = "file", name = "img", value ="文件", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 506, message = "目录文件夹没有写入权限"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/advertisement/{id}/uploadImg")
    public Object postAdvertisementIdUploadImg(@PathVariable Integer id, @RequestPart(value="img", required=true) MultipartFile img){
        return null;
    };

}

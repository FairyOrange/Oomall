package cn.edu.xmu.oomall.other.controller;

import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class AddressController {

    @ApiOperation(value = "管理员在地区下新增子地区", tags={ "address" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "地区id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "地区信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 602, message = "地区已废弃"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/regions/{id}/subregions")
    public Object postRegionsIdSubregions(@PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "管理员修改某个地区", tags={ "address" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "地区id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "地区信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/regions/{id}")
    public Object putRegionsId(@PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "管理员让某个地区无效", tags={ "address" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "地区id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/regions/{id}")
    public Object deleteRegionsId(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "查询某个地区的所有上级地区", notes = "返回其所有父级地址", tags={ "address" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "该地区id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/region/{id}/ancestor")
    public Object getRegionIdAncestor(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "买家新增地址", notes = "限制每个买家最多有20个地址", tags={ "address" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "地区信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 601, message = "达到地址簿上限"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/addresses")
    public Object postAddresses(@RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "买家修改自己的地址信息", tags={ "address" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "地区id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "可修改的地址信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/addresses/{id}")
    public Object putAddressesId(@PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "买家删除地址", notes = "删除自己的地址", tags={ "address" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "地址id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/addresses/{id}")
    public Object deleteAddressesId(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "买家查询所有已有的地址信息", tags={ "address" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/addresses")
    public Object getAddresses(@RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

    @ApiOperation(value = "买家设置默认地址", notes = "需将原有的默认地址改为普通地址", tags={ "address" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "地址id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/addresses/{id}/default")
    public Object putAddressesIdDefault(@PathVariable Integer id){
        return null;
    };

}

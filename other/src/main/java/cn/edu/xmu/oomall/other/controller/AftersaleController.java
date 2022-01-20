package cn.edu.xmu.oomall.other.controller;

import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class AftersaleController {

    @ApiOperation(value = "买家修改售后单信息（店家生成售后单前）", notes = "在店家生成售后单之前买家可以修改信息，生成之后买家只能进行删除操作", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "买家可修改的信息：地址，售后商品的数量，申请售后的原因，联系人以及联系电话", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/aftersales/{id}")
    public Object putAfterSalesId(@PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "买家确认售后单结束", notes = "修改售后单状态", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/aftersales/{id}/confirm")
    public Object putAfterSalesIdConfirm(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "买家取消售后单和逻辑删除售后单", notes = "售后单完成之前，买家取消售后单；售后单完成之后，买家逻辑删除售后单", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/aftersales/{id}")
    public Object deleteAfterSalesId(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "获得售后单的所有状态", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/aftersales/states")
    public Object getAftersalesStates(){
        return null;
    };

    @ApiOperation(value = "买家根据售后单id查询售后单信息", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/aftersales/{id}")
    public Object getAfterSalesId(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "买家查询所有的售后单信息（可根据售后类型和状态选择）", notes = "默认所有，选择按类型或者状态（买家通过这个API只能查询到自己的售后单）", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "type", value = "售后类型", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "state", value = "售后状态", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/aftersales")
    public Object getAftersales(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam String type, @RequestParam Integer state){
        return null;
    };

    @ApiOperation(value = "买家填写售后的运单信息", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "运单号", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/aftersales/{id}/sendback")
    public Object putAfterSalesIdSendBack(@PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "买家提交售后单", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "订单明细id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "售后服务信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/aftersales/{id}/aftersales")
    public Object postAfterSalesIdAftersales(@PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "管理员同意/不同意（退款，换货，维修）", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "处理意见", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{shopId}/aftersales/{id}/confirm")
    public Object putShopsShopIdAftersalesIdConfirm(@PathVariable Integer shopId, @PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "店家寄出维修好（调换）的货物", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "运单号", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{shopId}/aftersales/{id}/deliver")
    public Object putShopsShopIdAftersalesIdDeliver(@PathVariable Integer shopId, @PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "买家根据售后单id查询售后单信息", notes = "买家通过这个API只能查询到自己的售后单", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/shops/{shopId}/aftersales/{id}")
    public Object getShopsShopIdAftersalesId(@PathVariable Integer shopId, @PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "管理员查看所有售后单（可根据售后类型和状态选择）", notes = "管理员可通过售后单ID查看所有售后单", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "spuId", value = "SPU Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "skuId", value = "SKU Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "type", value = "售后类型", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "state", value = "售后状态", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/shops/{id}/aftersales")
    public Object getShopsIdAftersales(@PathVariable Integer id, @RequestParam Integer spuId, @RequestParam Integer skuId, @RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam String type, @RequestParam Integer state){
        return null;
    };

    @ApiOperation(value = "店家确认收到买家的退（换）货", notes = "如果是退款，则退款给用户，如果换货则产生一个新订单，如果是维修则等待下一个动作", tags={ "aftersale" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "处理意见", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{shopId}/aftersales/{id}/receive")
    public Object putShopsShopIdAftersalesIdReceive(@PathVariable Integer shopId, @PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

}

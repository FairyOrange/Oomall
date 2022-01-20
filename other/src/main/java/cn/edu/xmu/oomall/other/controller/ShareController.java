package cn.edu.xmu.oomall.other.controller;

import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class ShareController {

    @ApiOperation(value = "分享者生成分享链接", notes = "根据买家id和商品id生成唯一的分享链接", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "生成分享链接所需的商品spuId和分享者id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/shares")
    public Object postShares(@RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "买家查询所有分享记录", notes = "买家查询所有分享记录", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "SPU Id", value = "goodsSpuId", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/shares")
    public Object getShares(@RequestParam Integer goodsSpuId, @RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

    @ApiOperation(value = "买家查询所有分享成功记录", notes = "买家查询所有分享成功记录", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "SPU Id", value = "goodsSpuId", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/beshared")
    public Object getBeshared(@RequestParam Integer goodsSpuId, @RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

    @ApiOperation(value = "生成分享成功状态信息", notes = "记录用户打开分享链接", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "SPU Id", value = "goodsSpuId", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "被分享者id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/goods/{spuId}/beshared")
    public Object postGoodsSpuIdBeshared(@PathVariable Integer goodsSpuId, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "查看特定商品分享活动（所有用户均可）", notes = "根据商品id分享活动", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "shopId", value = "店铺Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "spuId", value = "SPU Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/shareactivities")
    public Object getShareactivities(@RequestParam Integer shopId, @RequestParam Integer spuId, @RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

    @ApiOperation(value = "管理员修改平台分享活动的内容", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "spuId", value = "商品SPU id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "分享活动id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "可修改的分享活动信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 605, message = "分享活动时段冲突"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{shopId}/goods/{spuId}/shareactivities/{id}")
    public Object putShopsShopIdGoodsSpuIdShareactivitiesId(@PathVariable Integer shopId, @PathVariable Integer spuId, @PathVariable Integer id, @RequestBody Object vo){
        return null;
    };

    @ApiOperation(value = "平台或店家创建新的分享活动", notes = "两个管理员的身份用shop_id区分，0表示平台", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "spuId", value = "商品SPU id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "商品id和活动的起止时间与活动规则", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/shops/{shopId}/goods/{spuId}/shareactivities")
    public Object postShopsShopIdGoodsSpuIdShareactivities(@PathVariable Integer shopId, @PathVariable Integer spuId, @RequestBody Object vo){
        return null;
    };

    @ApiOperation(value = "管理员终止指定商品的分享活动", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "spuId", value = "商品SPU id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "分享活动id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/shops/{shopId}/goods/{spuId}/shareactivities/{id}")
    public Object deleteShopsShopIdGoodsSpuIdShareactivitiesId(@PathVariable Integer shopId, @PathVariable Integer spuId, @PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "管理员查询所有分享成功记录", notes = "管理查询所有分享成功记录", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "店铺Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "spuId", value = "SPU Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/shops/{id}/beshared")
    public Object getShopsIdBeshared(@PathVariable Integer id, @RequestParam Integer spuId, @RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

    @ApiOperation(value = "管理员查询商品分享记录", notes = "商铺管理员只能查询本店商铺的商品", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "店铺Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "spuId", value = "SPU Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/shops/{id}/shares")
    public Object getShopsIdShares(@PathVariable Integer id, @RequestParam Integer spuId, @RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

    @ApiOperation(value = "管理员设定分享返点", notes = "系统内部调用，或者平台管理员", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "订单明细Id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "订单信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 606, message = "订单明细无分享记录"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{shopId}/orderitems/{id}/beshared")
    public Object putShopsShopIdOrderitemsIdBeshared(@PathVariable Integer shopId, @PathVariable Integer id, @RequestBody Object vo){
        return null;
    };

}

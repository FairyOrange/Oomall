package cn.edu.xmu.oomall.other.controller;

import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class CartController {

    @ApiOperation(value = "买家将商品加入购物车", tags={ "cart" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "可填写的信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/carts")
    public Object postCarts(@RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "买家修改购物车单个商品的数量或规格", tags={ "cart" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "购物车ID", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "修改购物车单个商品信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/carts/{id}")
    public Object putCartsId(@PathVariable Integer id, @RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "买家清空购物车", tags={ "cart" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/carts")
    public Object deleteCarts(){
        return null;
    };

    @ApiOperation(value = "买家删除购物车中商品", notes = "删除任意商品", tags={ "cart" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "购物车ID", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/carts/{id}")
    public Object deleteCartsId(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "买家获得购物车列表", tags={ "cart" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/carts")
    public Object getCarts(@RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

}

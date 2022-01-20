package cn.edu.xmu.oomall.other.controller;

import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class FavoriteController {

    @ApiOperation(value = "买家收藏商品", tags={ "favorite" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "spuId", value = "商品SpuId", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/favorites/goods/{spuId}")
    public Object postFavoritesGoodsSpuId(@PathVariable Integer spuId){
        return null;
    };

    @ApiOperation(value = "买家删除某个收藏的商品", tags={ "favorite" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "收藏商品id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/favorites/{id}")
    public Object deleteFavoritesId(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "买家查看所有收藏的商品", tags={ "favorite" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/favorites")
    public Object getFavorites(@RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

}

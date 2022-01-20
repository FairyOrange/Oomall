package cn.edu.xmu.oomall.favorite.controller;

import cn.edu.xmu.oomall.annotation.Audit;
import cn.edu.xmu.oomall.annotation.LoginUser;
import cn.edu.xmu.oomall.favorite.model.bo.Favorite;
import cn.edu.xmu.oomall.favorite.service.FavoriteService;
import cn.edu.xmu.oomall.util.Common;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

/**
 * 收藏Controller
 *
 * @author yang8miao
 * @date 2020/11/28 20:02
 * @version 1.0
 */
@Slf4j
@Api(value = "商品收藏服务", tags = { "favorite" })
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    /**
     * 买家查看所有收藏的商品
     *
     * @author yang8miao
     * @date 2020/11/28 20:28
     * @version 1.0
     */
    @ApiOperation(value = "买家查看所有收藏的商品", tags={ "favorite" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/favorites")
    @Audit
    public Object getFavorites(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize){

        log.debug("userId="+userId);

        Favorite favorite=new Favorite();
        favorite.setCustomerId(userId);
        ReturnObject returnObject = favoriteService.getFavorites(favorite,page, pageSize);
        return Common.getPageRetObject(returnObject);
    };


    /**
     * 买家收藏商品
     *
     * @author yang8miao
     * @date 2020/11/28 21:48
     * @version 1.0
     */
    @ApiOperation(value = "买家收藏商品", tags={ "favorite" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "spuId", value = "商品SpuId", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/favorites/goods/{skuId}")
    @Audit
    public Object postFavoritesGoodsSkuId(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
            @PathVariable Long skuId){

        Favorite favorite = new Favorite();
        favorite.setCustomerId(userId);
        favorite.setGoodsSkuId(skuId);
        ReturnObject returnObject = favoriteService.postFavoritesGoodsSkuId(favorite);
        if (returnObject.getCode().equals(ResponseCode.OK)) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.getRetObject(returnObject);

    };


    /**
     * 买家删除某个收藏的商品
     *
     * @author yang8miao
     * @date 2020/11/28 22:11
     * @version 1.0
     */
    @ApiOperation(value = "买家删除某个收藏的商品", tags={ "favorite" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "收藏商品id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/favorites/{id}")
    @Audit
    public Object deleteFavoritesId(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
            @PathVariable Long id){

        Favorite favorite = new Favorite();
        favorite.setCustomerId(userId);
        favorite.setId(id);
        ReturnObject returnObject = favoriteService.deleteFavoritesId(favorite);
        return Common.getRetObject(returnObject);
    };

}

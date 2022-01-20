package cn.edu.xmu.oomall.cart.controller;

import cn.edu.xmu.oomall.annotation.Audit;
import cn.edu.xmu.oomall.annotation.LoginUser;
import cn.edu.xmu.oomall.cart.model.bo.Cart;
import cn.edu.xmu.oomall.cart.model.vo.CartVo;
import cn.edu.xmu.oomall.cart.service.CartService;
import cn.edu.xmu.oomall.util.Common;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
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
 * 购物车Controller
 *
 * @author yang8miao
 * @date 2020/11/29 00:00
 * @version 1.0
 */
@Slf4j
@Api(value = "购物车服务", tags = { "cart" })
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    /**
     * 买家获得购物车列表
     *
     * @author yang8miao
     * @date 2020/11/29 12:06
     * @version 1.0
     */
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
    @Audit
    public Object getCarts(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize){

        Cart cart = new Cart();
        cart.setCustomerId(userId);
        ReturnObject returnObject = cartService.getCarts(cart,page, pageSize);
        return Common.getPageRetObject(returnObject);
    };


    /**
     * 买家将商品加入购物车
     *
     * @author yang8miao
     * @date 2020/11/29 12:06
     * @version 1.0
     */
    @ApiOperation(value = "买家将商品加入购物车", tags={ "cart" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CartVo", name = "vo", value = "可填写的信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/carts")
    @Audit
    public Object postCarts(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
            @RequestBody @Validated CartVo vo,
            BindingResult bindingResult){

        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null){
            return object;
        }

        Cart cart = new Cart();
        cart.setCustomerId(userId);
        cart.setGoodsSkuId(vo.getGoodsSkuId());
        cart.setQuantity(vo.getQuantity());
        ReturnObject returnObject = cartService.postCarts(cart);
        if (returnObject.getCode().equals(ResponseCode.OK)) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.getRetObject(returnObject);

    };


    /**
     * 买家清空购物车
     *
     * @author yang8miao
     * @date 2020/11/29 12:13
     * @version 1.0
     */
    @ApiOperation(value = "买家清空购物车", tags={ "cart" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/carts")
    @Audit
    public Object deleteCarts(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId){
        Cart cart = new Cart();
        cart.setCustomerId(userId);
        ReturnObject returnObject = cartService.deleteCarts(cart);
        return Common.getRetObject(returnObject);
    };



    /**
     * 买家修改购物车单个商品的数量或规格
     *
     * @author yang8miao
     * @date 2020/11/29 16:00
     * @version 1.0
     */
    @ApiOperation(value = "买家修改购物车单个商品的数量或规格", tags={ "cart" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "购物车ID", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CartVo", name = "vo", value = "修改购物车单个商品信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/carts/{id}")
    @Audit
    public Object putCartsId(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
            @PathVariable Long id,
            @RequestBody @Validated CartVo vo,
            BindingResult bindingResult){

        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null){
            return object;
        }

        Cart cart = new Cart();
        cart.setId(id);
        cart.setCustomerId(userId);
        cart.setGoodsSkuId(vo.getGoodsSkuId());
        cart.setQuantity(vo.getQuantity());
        ReturnObject returnObject = cartService.putCartsId(cart);
        return Common.getRetObject(returnObject);
    };



    /**
     * 买家删除购物车中商品
     *
     * @author yang8miao
     * @date 2020/11/29 16:44
     * @version 1.0
     */
    @ApiOperation(value = "买家删除购物车中商品", notes = "删除任意商品", tags={ "cart" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "购物车ID", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/carts/{id}")
    @Audit
    public Object deleteCartsId(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
            @PathVariable Long id){

        Cart cart = new Cart();
        cart.setId(id);
        cart.setCustomerId(userId);
        ReturnObject returnObject = cartService.deleteCartsId(cart);
        return Common.getRetObject(returnObject);
    };

}

package cn.edu.xmu.oomall.cart.service;


import cn.edu.xmu.oomall.cart.dao.CartDao;
import cn.edu.xmu.oomall.cart.model.bo.Cart;
import cn.edu.xmu.oomall.cart.model.bo.CouponInfo;
import cn.edu.xmu.oomall.cart.model.bo.GoodsInfo;
import cn.edu.xmu.oomall.goods.model.CouponInfoDTO;
import cn.edu.xmu.oomall.goods.model.GoodsInfoDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车Service
 *
 * @athor yang8miao
 * @date 2020/11/29 00:08
 * @version 1.0
 */
@Slf4j
@Service
public class CartService {

    @Autowired
    private CartDao cartDao;

    @DubboReference(check = false)
    private IGoodsService iGoodsService;


    /**
     * 买家获得购物车列表
     *
     * @param cart 前端传递的参数
     * @param page 页数
     * @param pageSize 每页大小
     * @author yang8miao
     * @date 2020/11/29 11:54
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getCarts(Cart cart, Integer page, Integer pageSize) {

        ReturnObject<PageInfo<Cart>> returnObject =cartDao.getCarts(cart,page, pageSize);
        if(returnObject.getCode() != ResponseCode.OK){
            return returnObject;
        }

        List<Cart> cartList = returnObject.getData().getList();

        List<VoObject> ret = new ArrayList<>(cartList.size());

        for(Cart bo : cartList){

            // TODO 与商品模块对接，集成时需要解除注释
            ReturnObject<GoodsInfoDTO> goods = iGoodsService.getSelectGoodsInfoBySkuId(bo.getGoodsSkuId());
            GoodsInfo goods_new = new GoodsInfo();
            goods_new.setSkuName(goods.getData().getSkuName());
            List<CouponInfo> couponInfoList = new ArrayList<>();
            for(CouponInfoDTO couponInfoDTO:goods.getData().getCouponActivity()){
                CouponInfo coupon_new = new CouponInfo();
                coupon_new.setId(couponInfoDTO.getId());
                coupon_new.setName(couponInfoDTO.getName());
                coupon_new.setBeginTime(couponInfoDTO.getBeginTime());
                coupon_new.setEndTime(couponInfoDTO.getEndTime());
                couponInfoList.add(coupon_new);
            }
            goods_new.setCouponActivity(couponInfoList);
            bo.setSkuName(goods_new.getSkuName());
            bo.setCouponActivity(goods_new.getCouponActivity());

            ret.add(bo);
        }

        PageInfo<VoObject> retObject = new PageInfo<>(ret);

        retObject.setPages(returnObject.getData().getPages());
        retObject.setPageNum(returnObject.getData().getPageNum());
        retObject.setPageSize(returnObject.getData().getPageSize());
        retObject.setTotal(returnObject.getData().getTotal());

        return new ReturnObject<>(retObject);

    }


    /**
     * 买家将商品加入购物车
     *
     * @param cart 前端传递的参数
     * @author yang8miao
     * @date 2020/11/29 12:11
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject postCarts(Cart cart) {

        // TODO 与商品模块对接，集成时需要解除注释
        ReturnObject<GoodsInfoDTO> goods_1 = iGoodsService.getSelectGoodsInfoBySkuId(cart.getGoodsSkuId());
        cart.setPrice(goods_1.getData().getPrice());


        ReturnObject<Cart> returnObject =cartDao.postCarts(cart);
        if(returnObject.getCode() != ResponseCode.OK){
            return returnObject;
        }

        Cart bo = returnObject.getData();

        // TODO 与商品模块对接，集成时需要解除注释
        ReturnObject<GoodsInfoDTO> goods = iGoodsService.getSelectGoodsInfoBySkuId(bo.getGoodsSkuId());
        GoodsInfo goods_new = new GoodsInfo();
        goods_new.setSkuName(goods.getData().getSkuName());
        List<CouponInfo> couponInfoList = new ArrayList<>();
        for(CouponInfoDTO couponInfoDTO:goods.getData().getCouponActivity()){
            CouponInfo coupon_new = new CouponInfo();
            coupon_new.setId(couponInfoDTO.getId());
            coupon_new.setName(couponInfoDTO.getName());
            coupon_new.setBeginTime(couponInfoDTO.getBeginTime());
            coupon_new.setEndTime(couponInfoDTO.getEndTime());
            couponInfoList.add(coupon_new);
        }
        goods_new.setCouponActivity(couponInfoList);
        bo.setSkuName(goods_new.getSkuName());
        bo.setCouponActivity(goods_new.getCouponActivity());

        return new ReturnObject<>(bo);
    }

    /**
     * 买家清空购物车
     *
     * @param cart 前端传递的参数
     * @author yang8miao
     * @date 2020/11/29 12:14
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteCarts(Cart cart) {
        ReturnObject returnObject =cartDao.deleteCarts(cart);
        return returnObject;
    }


    /**
     * 买家修改购物车单个商品的数量或规格
     *
     * @param cart 前端传递的参数
     * @author yang8miao
     * @date 2020/11/29 16:00
     * @version 1.0
     */
    public ReturnObject putCartsId(Cart cart) {

        ReturnObject<Cart> returnObject = cartDao.getCartById(cart);
        if(returnObject.getCode() != ResponseCode.OK){
            return returnObject;
        }

        Cart selectCart = returnObject.getData();

        if(selectCart.getGoodsSkuId().equals(cart.getGoodsSkuId())){
            // TODO 与商品模块对接，集成时需要解除注释
            ReturnObject<GoodsInfoDTO> goods = iGoodsService.getSelectGoodsInfoBySkuId(cart.getGoodsSkuId());
            cart.setPrice(goods.getData().getPrice());

            return cartDao.putCartsId(cart);
        }
        else{

            // TODO 与商品模块对接，集成时需要解除注释
            Long selectCart_spuId = iGoodsService.getSelectGoodsInfoBySkuId(selectCart.getGoodsSkuId()).getData().getSpuId();
            Long cart_spuId = iGoodsService.getSelectGoodsInfoBySkuId(cart.getGoodsSkuId()).getData().getSpuId();

            if(selectCart_spuId.equals(cart_spuId)){
                ReturnObject ret = cartDao.deleteCarts(selectCart);
                if(ret.getCode() != ResponseCode.OK){
                    return ret;
                }

                // TODO 与商品模块对接，集成时需要解除注释
                ReturnObject<GoodsInfoDTO> goods = iGoodsService.getSelectGoodsInfoBySkuId(cart.getGoodsSkuId());
                cart.setPrice(goods.getData().getPrice());

                return cartDao.postCarts(cart);
            }
            else{
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            }

        }
    }



    /**
     * 买家删除购物车中商品
     *
     * @param cart 前端传递的参数
     * @author yang8miao
     * @date 2020/11/29 16:45
     * @version 1.0
     */
    public ReturnObject deleteCartsId(Cart cart) {
        ReturnObject returnObject =cartDao.deleteCartsId(cart);
        return returnObject;
    }
}

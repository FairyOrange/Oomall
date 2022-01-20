package cn.edu.xmu.oomall.cart.dao;


import cn.edu.xmu.oomall.cart.mapper.CartPoMapper;
import cn.edu.xmu.oomall.cart.model.bo.Cart;
import cn.edu.xmu.oomall.cart.model.po.CartPo;
import cn.edu.xmu.oomall.cart.model.po.CartPoExample;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车Dao
 *
 * @author yang8miao
 * @date 2020/11/29 00:09
 * @version 1.0
 */
@Slf4j
@Repository
public class CartDao {

    @Autowired
    private CartPoMapper cartPoMapper;

    /**
     * 买家获得购物车列表
     *
     * @param cart 前端传递的参数
     * @param page     页数
     * @param pageSize 每页大小
     * @author yang8miao
     * @date 2020/11/29 11:56
     * @version 1.0
     */
    public ReturnObject getCarts(Cart cart, Integer page, Integer pageSize) {

        // 构造查询条件
        CartPoExample cartPoExample = new CartPoExample();
        CartPoExample.Criteria cartPoCriteria = cartPoExample.createCriteria();

        log.debug("cart.getCustomerId()=" + cart.getCustomerId());

        if (cart.getCustomerId() != null) {
            cartPoCriteria.andCustomerIdEqualTo(cart.getCustomerId());
        }

        try {
            // 根据条件分页查询
            PageHelper.startPage(page, pageSize);
            List<CartPo> cartPoList = cartPoMapper.selectByExample(cartPoExample);

            log.debug("cartPoList.size()=" + cartPoList.size());
            for (CartPo cartPo : cartPoList) {
                log.debug("查询到：cartPo.getId()=" + cartPo.getId());
                log.debug("查询到：cartPo.getCustomerId()=" + cartPo.getCustomerId());
                log.debug("查询到：cartPo.getGoodsSkuId()=" + cartPo.getGoodsSkuId());
                log.debug("查询到：cartPo.getGmtCreated()=" + cartPo.getGmtCreate());
                log.debug("查询到：cartPo.getGmtModified()=" + cartPo.getGmtModified());
            }

            List<Cart> ret = new ArrayList<>(cartPoList.size());

            for(CartPo po : cartPoList){
                Cart bo = new Cart(po);
                ret.add(bo);
            }

            PageInfo<CartPo> cartPoPageInfo = PageInfo.of(cartPoList);
            PageInfo<Cart> retObject = new PageInfo<>(ret);
            retObject.setPages(cartPoPageInfo.getPages());
            retObject.setPageNum(cartPoPageInfo.getPageNum());
            retObject.setPageSize(cartPoPageInfo.getPageSize());
            retObject.setTotal(cartPoPageInfo.getTotal());
            return new ReturnObject<>(retObject);


        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }


    /**
     * 买家将商品加入购物车
     *
     * @param cart 前端传递的参数
     * @author yang8miao
     * @date 2020/11/29 12:12
     * @version 1.0
     */
    public ReturnObject postCarts(Cart cart) {

        // 构造查询条件
        CartPoExample cartPoExample = new CartPoExample();
        CartPoExample.Criteria cartPoCriteria = cartPoExample.createCriteria();

        if (cart.getCustomerId() != null) {
            cartPoCriteria.andCustomerIdEqualTo(cart.getCustomerId());
        }

        if (cart.getGoodsSkuId() != null) {
            cartPoCriteria.andGoodsSkuIdEqualTo(cart.getGoodsSkuId());
        }

        try {

            // 需要检查 customerId、goodsSkuId在相应表中存不存在(等最后建表)
//            CustomerPo customerPo = CustomerPoMapper.selectByPrimaryKey(cart.getCustomerId());
//            if (customerPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }
//            GoodsSkuPo goodsSkuPo = GoodsSkuPoMapper.selectByPrimaryKey(cart.getGoodsSkuId());
//            if (goodsSkuPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }

            CartPo po = new CartPo();

            List<CartPo> cartPoList = cartPoMapper.selectByExample(cartPoExample);

            // 该用户之前未将该商品加入购物车
            if (cartPoList.size() == 0) {

                po.setCustomerId(cart.getCustomerId());
                po.setGoodsSkuId(cart.getGoodsSkuId());
                po.setQuantity(cart.getQuantity());
                po.setPrice(cart.getPrice());
                po.setGmtCreate(LocalDateTime.now());
                po.setGmtModified(null);

                int state = cartPoMapper.insertSelective(po);
                if(state == 0){
                    log.debug("加入购物车失败：" + po.getCustomerId());
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("加入购物车失败：%s", po.getCustomerId()));
                }

                // 通过反射获取插入时获得的记录信息
                cart.setId(po.getId());

                cart.setGmtCreate(po.getGmtCreate());
                cart.setGmtModified(po.getGmtModified());

                return new ReturnObject<Cart>(cart);

            }
            else if (cartPoList.size() == 1){
                po.setId(cartPoList.get(0).getId());
                po.setQuantity(cart.getQuantity()+cartPoList.get(0).getQuantity());
                po.setPrice(cart.getPrice());
                po.setGmtCreate(cartPoList.get(0).getGmtCreate());
                po.setGmtModified(LocalDateTime.now());

                int state = cartPoMapper.updateByPrimaryKeySelective(po);
                if(state == 0){
                    log.debug("加入购物车失败：" + po.getCustomerId());
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("加入购物车失败：%s", po.getCustomerId()));
                }

                cart.setId(po.getId());
                cart.setQuantity(po.getQuantity());
                cart.setGmtCreate(po.getGmtCreate());
                cart.setGmtModified(po.getGmtModified());

                return new ReturnObject<Cart>(cart);
            }
            else{
                log.warn("购物车表中存在该用户将此商品加入购物车的多条数据，数据库错误！");
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误"));
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }

    }


    /**
     * 买家清空购物车
     *
     * @param cart 前端传递的参数
     * @author yang8miao
     * @date 2020/11/29 12:15
     * @version 1.0
     */
    public ReturnObject deleteCarts(Cart cart) {

        // 构造查询条件
        CartPoExample cartPoExample = new CartPoExample();
        CartPoExample.Criteria cartPoCriteria = cartPoExample.createCriteria();


        if (cart.getId() != null) {
            cartPoCriteria.andIdEqualTo(cart.getId());
        }

        if (cart.getCustomerId() != null) {
            cartPoCriteria.andCustomerIdEqualTo(cart.getCustomerId());
        }

        try {

            // 需要检查 customerId在相应表中存不存在(等最后建表)
//            CustomerPo customerPo = CustomerPoMapper.selectByPrimaryKey(cart.getCustomerId());
//            if (customerPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }

            List<CartPo> cartPoList = cartPoMapper.selectByExample(cartPoExample);

            if (cartPoList.size() == 0) {
                log.warn("该用户的购物车中没有商品！");
                return new ReturnObject<>();
            }
            else {
                for(CartPo po : cartPoList){
                    int state = cartPoMapper.deleteByPrimaryKey(po.getId());
                    if (state == 0) {
                        log.warn("该购物车id不存在！");
                        return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"该购物车id不存在");
                    }
                }
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
        return new ReturnObject<>();
    }


    /**
     * 买家修改购物车单个商品的数量或规格
     *
     * @param cart 前端传递的参数
     * @author yang8miao
     * @date 2020/11/29 16:01
     * @version 1.0
     */
    public ReturnObject putCartsId(Cart cart) {

        try {

            // 需要检查 customerId、goodsSkuId在相应表中存不存在(等最后建表)
//            CustomerPo customerPo = CustomerPoMapper.selectByPrimaryKey(cart.getCustomerId());
//            if (customerPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }
//            GoodsSkuPo goodsSkuPo = GoodsSkuMapper.selectByPrimaryKey(cart.getGoodsSkuId());
//            if (goodsSkuPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }

            CartPo updatePo = new CartPo();
            updatePo.setId(cart.getId());
            updatePo.setGoodsSkuId(cart.getGoodsSkuId());
            updatePo.setQuantity(cart.getQuantity());
            updatePo.setPrice(cart.getPrice());
            updatePo.setGmtModified(LocalDateTime.now());

            int state = cartPoMapper.updateByPrimaryKeySelective(updatePo);
            if(state == 0){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"购物车更新失败");
            }

        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
        return new ReturnObject<>();
    }


    /**
     * 买家删除购物车中商品
     *
     * @param cart 前端传递的参数
     * @author yang8miao
     * @date 2020/11/29 16:46
     * @version 1.0
     */
    public ReturnObject deleteCartsId(Cart cart) {

        try {

            // 需要检查 customerId在相应表中存不存在(等最后建表)
//            CustomerPo customerPo = CustomerPoMapper.selectByPrimaryKey(cart.getCustomerId());
//            if (customerPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }


            // 先查询该条数据，验证customerId是否一致
            CartPo selectPo = cartPoMapper.selectByPrimaryKey(cart.getId());
            if(selectPo == null){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"该购物车id不存在");
            }
            if(!selectPo.getCustomerId().equals(cart.getCustomerId())){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该购物车id所属买家与操作用户不一致");
            }

            int state = cartPoMapper.deleteByPrimaryKey(cart.getId());
            if(state == 0){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"购物车删除失败");
            }

        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
        return new ReturnObject<>();
    }


    /**
     * 查询指定购物车id的数据
     *
     * @param cart 前端传递的参数
     * @author yang8miao
     * @date 2020/12/12 11:23
     * @version 1.0
     */
    public ReturnObject getCartById(Cart cart) {

        try {

            // 需要检查 customerId在相应表中存不存在(等最后建表)
//            CustomerPo customerPo = CustomerPoMapper.selectByPrimaryKey(cart.getCustomerId());
//            if (customerPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }


            // 先查询该条数据，验证customerId是否一致
            CartPo selectPo = cartPoMapper.selectByPrimaryKey(cart.getId());
            if(selectPo == null){
                log.warn("该购物车id不存在");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"该购物车id不存在");
            }

            if(!(selectPo.getCustomerId().equals(cart.getCustomerId()))){
                log.debug("selectPo.getCustomerId()="+selectPo.getCustomerId());
                log.debug("cart.getCustomerId()="+cart.getCustomerId());
                log.warn("该购物车id所属买家与操作用户不一致");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该购物车id所属买家与操作用户不一致");
            }

            Cart bo = new Cart(selectPo);

            return new ReturnObject<>(bo);

        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

    public ReturnObject deleteGoodsInCart(Long customerId, List<Long> skuIdList) {

        int isok = 1;
        for(Long skuId : skuIdList){
            try{
                // 构造查询条件
                CartPoExample cartPoExample = new CartPoExample();
                CartPoExample.Criteria cartPoCriteria = cartPoExample.createCriteria();

                if (customerId != null) {
                    cartPoCriteria.andCustomerIdEqualTo(customerId);
                }

                if (skuId != null) {
                    cartPoCriteria.andGoodsSkuIdEqualTo(skuId);
                }

                List<CartPo> cartPoList = cartPoMapper.selectByExample(cartPoExample);

                if(cartPoList.size() == 1){
                    Long id = cartPoList.get(0).getId();
                    cartPoMapper.deleteByPrimaryKey(id);
                }
                else{
                    isok = 0;
                }
            }catch (DataAccessException e) {
                log.error("数据库错误：" + e.getMessage());
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            } catch (Exception e) {
                log.error("其他错误：" + e.getMessage());
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
            }
        }
        if(isok == 1){
            return new ReturnObject<>();
        }
        else{
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

    }
}

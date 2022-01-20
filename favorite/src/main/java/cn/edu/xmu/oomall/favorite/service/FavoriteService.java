package cn.edu.xmu.oomall.favorite.service;

import cn.edu.xmu.oomall.favorite.dao.FavoriteDao;
import cn.edu.xmu.oomall.favorite.model.bo.Favorite;
import cn.edu.xmu.oomall.favorite.model.bo.SkuInfo;
import cn.edu.xmu.oomall.favorite.model.po.FavoritePo;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
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
 * 收藏Service
 *
 * @athor yang8miao
 * @date 2020/11/28 20:15
 * @version 1.0
 */
@Slf4j
@Service
public class FavoriteService {

    @Autowired
    private FavoriteDao favoriteDao;

    @DubboReference(check = false)
    private IGoodsService iGoodsService;

    /**
     * 买家查看所有收藏的商品
     *
     * @param favorite 前端传递的参数
     * @param page 页数
     * @param pageSize 每页大小
     * @author yang8miao
     * @date 2020/11/28 20:32
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getFavorites(Favorite favorite, Integer page, Integer pageSize) {

        ReturnObject<PageInfo<Favorite>> returnObject =favoriteDao.getFavorites(favorite,page, pageSize);
        if(returnObject.getCode() != ResponseCode.OK){
            return returnObject;
        }

        List<Favorite> favoriteList = returnObject.getData().getList();

        List<VoObject> ret = new ArrayList<>(favoriteList.size());

        for(Favorite bo : favoriteList){

            // TODO 与商品模块对接，集成时需要解除注释
            ReturnObject<SkuInfoDTO> goodsSku = iGoodsService.getSelectSkuInfoBySkuId(bo.getGoodsSku().getId());
            SkuInfo goodsSku_new = new SkuInfo();
            goodsSku_new.setId(goodsSku.getData().getId());
            goodsSku_new.setName(goodsSku.getData().getName());
            goodsSku_new.setSkuSn(goodsSku.getData().getSkuSn());
            goodsSku_new.setImageUrl(goodsSku.getData().getImageUrl());
            goodsSku_new.setInventory(goodsSku.getData().getInventory());
            goodsSku_new.setOriginalPrice(goodsSku.getData().getOriginalPrice());
            goodsSku_new.setPrice(goodsSku.getData().getPrice());
            goodsSku_new.setDisable(goodsSku.getData().getDisable() == 1? true : false);
            bo.setGoodsSku(goodsSku_new);

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
     * 买家收藏商品
     *
     * @param favorite 前端传递的参数
     * @author yang8miao
     * @date 2020/11/28 21:48
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject postFavoritesGoodsSkuId(Favorite favorite) {

        // TODO 与商品模块对接，集成时需要解除注释
        ReturnObject<SkuInfoDTO> goodsSku_before = iGoodsService.getSelectSkuInfoBySkuId(favorite.getGoodsSkuId());
        if(goodsSku_before.getData() == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        ReturnObject<Favorite> returnObject =favoriteDao.postFavoritesGoodsSkuId(favorite);
        if(returnObject.getCode() != ResponseCode.OK){
            return returnObject;
        }

        Favorite bo = returnObject.getData();

        // TODO 与商品模块对接，集成时需要解除注释
        ReturnObject<SkuInfoDTO> goodsSku = iGoodsService.getSelectSkuInfoBySkuId(bo.getGoodsSku().getId());
        SkuInfo goodsSku_new = new SkuInfo();
        goodsSku_new.setId(goodsSku.getData().getId());
        goodsSku_new.setName(goodsSku.getData().getName());
        goodsSku_new.setSkuSn(goodsSku.getData().getSkuSn());
        goodsSku_new.setImageUrl(goodsSku.getData().getImageUrl());
        goodsSku_new.setInventory(goodsSku.getData().getInventory());
        goodsSku_new.setOriginalPrice(goodsSku.getData().getOriginalPrice());
        goodsSku_new.setPrice(goodsSku.getData().getPrice());
        goodsSku_new.setDisable(goodsSku.getData().getDisable() == 1? true : false);
        bo.setGoodsSku(goodsSku_new);

        return new ReturnObject<>(bo);
    }

    /**
     * 买家删除某个收藏的商品
     *
     * @param favorite 前端传递的参数
     * @author yang8miao
     * @date 2020/11/28 22:16
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteFavoritesId(Favorite favorite) {
        ReturnObject returnObject =favoriteDao.deleteFavoritesId(favorite);
        return returnObject;
    }
}

package cn.edu.xmu.oomall.favorite.dao;

import cn.edu.xmu.oomall.favorite.mapper.FavoritePoMapper;
import cn.edu.xmu.oomall.favorite.model.bo.Favorite;
import cn.edu.xmu.oomall.favorite.model.bo.SkuInfo;
import cn.edu.xmu.oomall.favorite.model.po.FavoritePo;
import cn.edu.xmu.oomall.favorite.model.po.FavoritePoExample;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏Dao
 *
 * @author yang8miao
 * @date 2020/11/28 20:08
 * @version 1.0
 */
@Slf4j
@Repository
public class FavoriteDao {

    @Autowired
    private FavoritePoMapper favoritePoMapper;

    /**
     * 买家查看所有收藏的商品
     *
     * @param favorite 前端传递的参数
     * @param page     页数
     * @param pageSize 每页大小
     * @author yang8miao
     * @date 2020/11/28 20:35
     * @version 1.0
     */
    public ReturnObject getFavorites(Favorite favorite, Integer page, Integer pageSize) {

        // 构造查询条件
        FavoritePoExample favoritePoExample = new FavoritePoExample();
        FavoritePoExample.Criteria favoritePoCriteria = favoritePoExample.createCriteria();

        log.debug("favorite.getCustomerId()=" + favorite.getCustomerId());

        if (favorite.getCustomerId() != null) {
            favoritePoCriteria.andCustomerIdEqualTo(favorite.getCustomerId());
        }

        try {
            // 根据条件分页查询
            PageHelper.startPage(page, pageSize);
            List<FavoritePo> favoritePoList = favoritePoMapper.selectByExample(favoritePoExample);

            log.debug("favoritePoList.size()=" + favoritePoList.size());
            for (FavoritePo favoritePo : favoritePoList) {
                log.debug("查询到：favoritePo.getId()=" + favoritePo.getId());
                log.debug("查询到：favoritePo.getCustomerId()=" + favoritePo.getCustomerId());
                log.debug("查询到：favoritePo.getGoodsSkuId()=" + favoritePo.getGoodsSkuId());
                log.debug("查询到：favoritePo.getGmtCreate()=" + favoritePo.getGmtCreate());
                log.debug("查询到：favoritePo.getGmtModified()=" + favoritePo.getGmtModified());
            }

            List<Favorite> ret = new ArrayList<>(favoritePoList.size());
            for (FavoritePo po : favoritePoList) {
                Favorite bo = new Favorite(po);
                SkuInfo goodsSku_new = new SkuInfo();
                goodsSku_new.setId(po.getGoodsSkuId());
                bo.setGoodsSku(goodsSku_new);
                ret.add(bo);
            }

            PageInfo<FavoritePo> favoritePoPageInfo = PageInfo.of(favoritePoList);
            PageInfo<Favorite> retObject = new PageInfo<>(ret);
            retObject.setPages(favoritePoPageInfo.getPages());
            retObject.setPageNum(favoritePoPageInfo.getPageNum());
            retObject.setPageSize(favoritePoPageInfo.getPageSize());
            retObject.setTotal(favoritePoPageInfo.getTotal());
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
     * 买家收藏商品
     *
     * @param favorite 前端传递的参数
     * @author yang8miao
     * @date 2020/11/28 21:49
     * @version 1.0
     */
    public ReturnObject postFavoritesGoodsSkuId(Favorite favorite) {

        FavoritePo po = favorite.createPo();
        po.setGmtCreate(LocalDateTime.now());
        favorite.setGmtCreate(po.getGmtCreate());
        try {
            // 需要检查 customerId、goodsSpuId在相应表中存不存在(等最后建表)
//            CustomerPo customerPo = CustomerPoMapper.selectByPrimaryKey(footPrint.getCustomerId());
//            if (customerPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }
//            GoodsSpuPo goodsSpuPo = GoodsSpuMapper.selectByPrimaryKey(footPrint.getGoodsSpuId());
//            if (goodsSpuPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }


            // 检查改买家之前是否已经收藏过该商品
            FavoritePoExample favoritePoExample = new FavoritePoExample();
            FavoritePoExample.Criteria favoritePoCriteria = favoritePoExample.createCriteria();
            if (favorite.getCustomerId() != null) {
                favoritePoCriteria.andCustomerIdEqualTo(favorite.getCustomerId());
            }
            if (favorite.getGoodsSkuId() != null) {
                favoritePoCriteria.andGoodsSkuIdEqualTo(favorite.getGoodsSkuId());
            }
            List<FavoritePo> favoritePoList = favoritePoMapper.selectByExample(favoritePoExample);
            if(favoritePoList.size()==1){

                // 买家之前已经收藏过该商品
                FavoritePo my_po = favoritePoList.get(0);
                Favorite my_bo = new Favorite(my_po);

                SkuInfo goodsSku = new SkuInfo();
                goodsSku.setId(my_bo.getGoodsSkuId());
                my_bo.setGoodsSku(goodsSku);

                return new ReturnObject<>(my_bo);
            }
            else if(favoritePoList.size()>1){

                // 数据库错误
                log.error("数据库错误！");
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
            }


            int ret = favoritePoMapper.insertSelective(po);

            log.debug("此时po为:");
            log.debug("id=" + po.getId());
            log.debug("CustomerId=" + po.getCustomerId());
            log.debug("GoodsSkuId=" + po.getGoodsSkuId());
            log.debug("GmtCreate=" + po.getGmtCreate());
            log.debug("GmtModified=" + po.getGmtModified());

            if (ret == 0) {
                // 新建收藏失败
                log.debug("新建收藏失败：" + po.getCustomerId());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新建收藏失败：%s", po.getCustomerId()));
            } else {
                // 新建收藏成功
                log.debug("新建收藏成功: " + po.toString());

                // 通过反射获取插入时获得的记录信息
                favorite.setId(po.getId());

                SkuInfo goodsSku = new SkuInfo();
                goodsSku.setId(po.getGoodsSkuId());
                favorite.setGoodsSku(goodsSku);

                return new ReturnObject<>(favorite);

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
     * 买家删除某个收藏的商品
     *
     * @param favorite 前端传递的参数
     * @author yang8miao
     * @date 2020/11/28 22:17
     * @version 1.0
     */
    public ReturnObject deleteFavoritesId(Favorite favorite) {

        // 需要检查 customerId、goodsSpuId在相应表中存不存在(等最后建表)
//            CustomerPo customerPo = CustomerPoMapper.selectByPrimaryKey(footPrint.getCustomerId());
//            if (customerPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }
//            GoodsSpuPo goodsSpuPo = GoodsSpuMapper.selectByPrimaryKey(footPrint.getGoodsSpuId());
//            if (goodsSpuPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }


        try {
            FavoritePo favoritePo = favoritePoMapper.selectByPrimaryKey(favorite.getId());

            if(favoritePo != null){

                if(favoritePo.getCustomerId().equals(favorite.getCustomerId())){
                    int state = favoritePoMapper.deleteByPrimaryKey(favoritePo.getId());
                    if (state == 0) {
                        log.warn("数据库错误");
                        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误!"));
                    }
                }
                else{
                    log.warn("该用户操作的不是自己的id！");
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
                }
            }
            else{
                log.warn("该收藏id不存在！");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"该收藏id不存在");
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
}
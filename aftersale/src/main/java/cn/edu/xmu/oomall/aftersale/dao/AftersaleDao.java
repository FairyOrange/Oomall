package cn.edu.xmu.oomall.aftersale.dao;

import cn.edu.xmu.oomall.aftersale.mapper.AftersaleServicePoMapper;
import cn.edu.xmu.oomall.aftersale.model.bo.Aftersale;
import cn.edu.xmu.oomall.aftersale.model.bo.SimpleAftersale;
import cn.edu.xmu.oomall.aftersale.model.po.AftersaleServicePo;
import cn.edu.xmu.oomall.aftersale.model.po.AftersaleServicePoExample;
import cn.edu.xmu.oomall.aftersale.util.AftersaleCommon;
import cn.edu.xmu.oomall.order.model.OrderDTO;
import cn.edu.xmu.oomall.other.model.AftersaleDTO;
import cn.edu.xmu.oomall.util.Common;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 售后Dao
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/21 23:34
 */
@Slf4j
@Repository
public class AftersaleDao {

    @Autowired
    private AftersaleServicePoMapper aftersaleMapper;

    /**
     * 将售后bo和orderItemId和orderDTO组成的Map组合成售后bo的list
     * @param aftersales
     * @param orderDTOMap
     * @return java.util.List<cn.edu.xmu.oomall.aftersale.model.bo.Aftersale>
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/11 下午10:02
    */
    public List<Aftersale> groupDTOAndAfterSaleBoList(List<Aftersale> aftersales, Map<Long,OrderDTO> orderDTOMap){
        for(Aftersale bo : aftersales){
            OrderDTO temp = orderDTOMap.get(bo.getOrderItemId());
            if(temp != null){
//                bo.setOrderId(temp.getOrderId());
                bo.setShopId(temp.getShopId());
                bo.setSkuName(temp.getSkuName());
                bo.setSkuId(temp.getSkuId());
                bo.setOrderSn(temp.getOrderSn());
            }
        }
        return aftersales;
    }

    /**
     * 将售后bo与orderDTO组合为售后bo返回
     * @param aftersale
     * @param orderDTO
     * @return cn.edu.xmu.oomall.aftersale.model.bo.Aftersale
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/11 下午10:43
    */
    public Aftersale groupDTOAndAfterSaleBo(Aftersale aftersale, OrderDTO orderDTO){
//        aftersale.setOrderId(orderDTO.getOrderId());
        aftersale.setShopId(orderDTO.getShopId());
        aftersale.setSkuName(orderDTO.getSkuName());
        aftersale.setSkuId(orderDTO.getSkuId());
        aftersale.setOrderSn(orderDTO.getOrderSn());
        return aftersale;
    }

    /**
     * 买家提交售后单,建立新的售后记录,state设置为0
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    public ReturnObject insertAftersale(Aftersale bo) {
        AftersaleServicePo po = bo.creatPo();
        // 设置售后Sn号和删除状态
        po.setBeDeleted((byte) 0);
        po.setServiceSn(Common.genSeqNum());
        try {
            int ret = aftersaleMapper.insertSelective(po);
            if (ret == 0) {
                // 新建售后失败
                log.debug("新建售后失败：" + po.getOrderItemId());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新建售后失败：%s", po.getOrderItemId()));
            } else {
                // 新建售后成功
                log.debug("新建售后成功: " + po.toString());
                // 通过反射获取插入时获得的记录信息
                bo.setId(po.getId());
                bo.setServiceSn(po.getServiceSn());
                return new ReturnObject<>(bo);
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
     * 查询所有售后单信息,用户查询时限定查询本人的且未被删除的,管理员查询时限定查询本店铺的
     *
     * @param bo       前端传递的参数
     * @param beginTime
     * @param endTime
     * @param page     页数
     * @param pageSize 每页大小
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    public ReturnObject listSelectAllAftersale(Aftersale bo, String beginTime, String endTime,  Integer page, Integer pageSize) {
        // 构造查询条件
        AftersaleServicePoExample aftersalePoExample = new AftersaleServicePoExample();
        AftersaleServicePoExample.Criteria aftersalePoCriteria = aftersalePoExample.createCriteria();
        if (bo.getCustomerId() != null) {
            // 若是用户查询则已删除订单无法查看
            aftersalePoCriteria.andCustomerIdEqualTo(bo.getCustomerId());
            aftersalePoCriteria.andBeDeletedNotEqualTo((byte) 1);
        }
        if (bo.getShopId() != null) {
            // 若为管理员查询则可查询所有售后单
            if (!bo.getShopId().equals (0L)) {
                // 若不为平台管理员则只能查询该店铺的售后单
                aftersalePoCriteria.andShopIdEqualTo(bo.getShopId());
            }
        }
        // 通过spuId skuId orderItemId筛选的orderItemIds list 查询条件
//        if(orderItemIds != null && orderItemIds.size() != 0){
//            aftersalePoCriteria.andOrderItemIdIn(orderItemIds);
//        }
//        if (bo.getOrderItemId() != null) {
//            aftersalePoCriteria.andOrderItemIdEqualTo(bo.getOrderItemId());
//        }
        // 如果有时间格式不对 则返回一个空列表
        if(!AftersaleCommon.judgeTimeValid(beginTime)||!AftersaleCommon.judgeTimeValid(endTime)){
            PageHelper.startPage(page, pageSize);
            List<AftersaleServicePo> aftersalePoList = new ArrayList<AftersaleServicePo>();
            PageInfo poPage = new PageInfo<>(aftersalePoList);
            List aftersaleBoList = Lists.newArrayList();

            aftersaleBoList = Lists.transform(aftersalePoList, SimpleAftersale::new);

            PageInfo retObject = new PageInfo<>(aftersaleBoList);
            retObject.setPages(poPage.getPages());
            retObject.setPageNum(poPage.getPageNum());
            retObject.setPageSize(poPage.getPageSize());
            retObject.setTotal(poPage.getTotal());
            return new ReturnObject<>(retObject);
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (beginTime != null) {
            aftersalePoCriteria.andGmtCreateGreaterThan(LocalDateTime.parse(beginTime,df));
        }
        if (endTime != null) {
            aftersalePoCriteria.andGmtCreateLessThan(LocalDateTime.parse(endTime,df));
        }
        if (bo.getType() != null) {
            aftersalePoCriteria.andTypeEqualTo(bo.getType().getCode().byteValue());
        }
        if (bo.getState() != null) {
            aftersalePoCriteria.andStateEqualTo(bo.getState().getCode().byteValue());
        }
        try {
            // 根据条件分页查询
            PageHelper.startPage(page, pageSize);
            List<AftersaleServicePo> aftersalePoList = aftersaleMapper.selectByExample(aftersalePoExample);
            PageInfo poPage = new PageInfo<>(aftersalePoList);
            List aftersaleBoList = Lists.newArrayList();
            // 转化成bo对象
            aftersaleBoList = Lists.transform(aftersalePoList, SimpleAftersale::new);

            PageInfo retObject = new PageInfo<>(aftersaleBoList);
            retObject.setPages(poPage.getPages());
            retObject.setPageNum(poPage.getPageNum());
            retObject.setPageSize(poPage.getPageSize());
            retObject.setTotal(poPage.getTotal());
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
     * 买家根据售后单id查询售后单信息,并判断是否是当前用户的订单
     *
     * @param userIdAudit 当前用户id
     * @param id          售后单id
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    public ReturnObject getUserSelectAftersale(Long userIdAudit, Long id) {
        try {
            // 通过主键查询售后单
            AftersaleServicePo aftersalePo = aftersaleMapper.selectByPrimaryKey(id);
            if(aftersalePo == null){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后单id不存在：%s", id));
            }
            if (aftersalePo.getCustomerId().equals(userIdAudit)) {
                // 检查是否是该用户的售后
                if (aftersalePo.getBeDeleted() != null && aftersalePo.getBeDeleted() == (byte) 1) {
                    // 若是该用户的订单且该订单被逻辑删除
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("未找到该售后单"));
                } else {
                    // 返回该售后单信息
                    Aftersale aftersale = new Aftersale(aftersalePo);
                    return new ReturnObject<>(aftersale);
                }
            } else {
                // 若与当前用户id不匹配
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("该售后单与id不匹配"));
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
     * 买家取消售后，或逻辑删除售后, 更新状态或逻辑删除字段
     *
     * @param bo 前端参数
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    public ReturnObject deleteAftersale(Aftersale bo) {
        AftersaleServicePo aftersalePo = bo.creatPo();
        try {
            AftersaleServicePo aftersaleServicePo = aftersaleMapper.selectByPrimaryKey(bo.getId());
            if(aftersaleServicePo == null){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后单id不存在：%s", bo.getId()));
            }
            if(aftersaleServicePo.getBeDeleted() == (byte)1){
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"该售后单已被删除");
            }
            if(!aftersaleServicePo.getCustomerId().equals(bo.getCustomerId())){
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE,"不是自己售后单");
            }
            // 根据id更新售后单状态
            int ret = aftersaleMapper.updateByPrimaryKeySelective(aftersalePo);
            if (ret == 0) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后单id不存在：%s", bo.getId()));
            } else {
                // 根据是否被删除设定返回信息
                return (aftersalePo.getBeDeleted() == (byte) 1)
                        ? new ReturnObject<>(ResponseCode.OK, String.format("删除售后成功：%s", aftersalePo.getId()))
                        : new ReturnObject<>(ResponseCode.OK, String.format("取消售后成功：%s", aftersalePo.getId()));
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
     * 卖家根据售后单id查询售后单信息,只能查询当前店铺的
     *
     * @param shopIdAudit 当前shopId
     * @param id          售后单id
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    public ReturnObject getAdminSelectAftersale(Long shopIdAudit, Long id) {
        try {
            // 通过主键查询售后单
            AftersaleServicePo aftersalePo = aftersaleMapper.selectByPrimaryKey(id);
            if(aftersalePo == null){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后单id不存在：%s", id));
            }
            if (aftersalePo.getShopId().equals(shopIdAudit)) {
                // 检查是否是该商铺的售后单
                if (aftersalePo.getBeDeleted() != null && aftersalePo.getBeDeleted() == (byte) 1) {
                    // 若是该用户的订单且该订单未被逻辑删除
                    return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("未找到该售后单"));
                } else {
                    // 返回售后单信息
                    Aftersale aftersale = new Aftersale(aftersalePo);
                    return new ReturnObject<>(aftersale);
                }
            } else {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("该订单与id不匹配"));
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s" + e.getMessage()));
        }
    }

    /**
     * 通过售后单id查找售后单
     * @param id
     * @return cn.edu.xmu.oomall.util.ReturnObject
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/10 下午8:52
    */
    public ReturnObject getAfterSaleById(Long id){
        try{
            AftersaleServicePo po = aftersaleMapper.selectByPrimaryKey(id);
            if(po == null){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"该id对应的售后单不存在");
            }
            return new ReturnObject<>(po);
        }catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

    /**
     * 买家修改售后单,只可以修改买家寄出前的售后单
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    public ReturnObject updateAftersaleInfo(Aftersale bo) {
        // 构造修改条件
        AftersaleServicePo po = bo.creatPo();
        //修改完后需要切换到待管理员审核状态
        po.setState(Aftersale.State.WAITING_SHOP_REVIEW.getCode().byteValue());
        //查看当前id对应的售后单是否存在
        AftersaleServicePo aftersaleServicePo = aftersaleMapper.selectByPrimaryKey(bo.getId());
        if(aftersaleServicePo == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后单id不存在：%s", bo.getId()));
        }
        if(aftersaleServicePo.getBeDeleted() == (byte)1){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"该售后单已被删除");
        }
        if(!aftersaleServicePo.getCustomerId().equals(bo.getCustomerId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE,"不是自己售后单");
        }
        AftersaleServicePoExample aftersalePoExample = new AftersaleServicePoExample();
        AftersaleServicePoExample.Criteria aftersalePoCriteria = aftersalePoExample.createCriteria();
        // 只可以修改当前用户的且买家未寄出的售后单
        aftersalePoCriteria.andIdEqualTo(bo.getId());
        aftersalePoCriteria.andCustomerIdEqualTo(bo.getCustomerId());
        // 待管理员审核 待买家发货状态可以修改售后单信息
        aftersalePoCriteria.andStateIn(Lists.newArrayList(Aftersale.State.WAITING_SHOP_REVIEW.getCode().byteValue()
                ,Aftersale.State.WAITING_USER_DELIVE.getCode().byteValue()));
        try {
            // 修改售后单信息
            int ret = aftersaleMapper.updateByExampleSelective(po, aftersalePoExample);
            if (ret == 0) {
                // 修改信息失败
                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后订单无法修改：%s", bo.getId()));
            } else {
                // 修改信息成功
                return new ReturnObject<>(ResponseCode.OK, String.format("修改售后成功：%s", po.getId()));
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
     * 买家确认售后单,只可以确认卖家处理完成的售后单
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    public ReturnObject updateAftersaleToFinish(Aftersale bo) {
        AftersaleServicePo po = bo.creatPo();
        AftersaleServicePoExample aftersalePoExample = new AftersaleServicePoExample();
        AftersaleServicePoExample.Criteria aftersalePoCriteria = aftersalePoExample.createCriteria();
        AftersaleServicePo aftersaleServicePo = aftersaleMapper.selectByPrimaryKey(bo.getId());
        if(aftersaleServicePo == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后单id不存在：%s", bo.getId()));
        }
        if(aftersaleServicePo.getBeDeleted() == (byte)1){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"该售后单已被删除");
        }
        if(!aftersaleServicePo.getCustomerId().equals(bo.getCustomerId())){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE,"不是自己售后单");
        }
        // 只可以确认当前用户的且卖家已处理完成的售后单
        aftersalePoCriteria.andIdEqualTo(bo.getId());
        aftersalePoCriteria.andCustomerIdEqualTo(bo.getCustomerId());
        aftersalePoCriteria.andStateIn(Lists.newArrayList(Aftersale.State.SHOP_DELIVE.getCode().byteValue()
                , Aftersale.State.WAITING_SHOP_REFUND.getCode().byteValue()));
        try {
            // 修改售后单信息
            int ret = aftersaleMapper.updateByExampleSelective(po, aftersalePoExample);
            if (ret == 0) {
                // 确认售后失败
                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后订单无法确认：%s", bo.getId()));
            } else {
                // 确认售后成功
                return new ReturnObject<>(ResponseCode.OK, String.format("确认售后成功：%s", po.getId()));
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
     * 买家寄出,只可以寄出审核通过的订单
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    public ReturnObject updateAftersaleToSendback(Aftersale bo) {
        AftersaleServicePo po = bo.creatPo();
        AftersaleServicePoExample aftersalePoExample = new AftersaleServicePoExample();
        AftersaleServicePoExample.Criteria aftersalePoCriteria = aftersalePoExample.createCriteria();
        AftersaleServicePo aftersaleServicePo = aftersaleMapper.selectByPrimaryKey(bo.getId());
        if(aftersaleServicePo == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后单id不存在：%s", bo.getId()));
        }
        if(aftersaleServicePo.getBeDeleted() == (byte)1){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"该售后单已被删除");
        }
        if(! aftersaleServicePo.getCustomerId().equals(bo.getCustomerId())) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE,"不是自己售后单");
        }
        // 只可以寄出当前用户的且卖家已审核的售后单
        aftersalePoCriteria.andIdEqualTo(bo.getId());
        aftersalePoCriteria.andCustomerIdEqualTo(bo.getCustomerId());
        aftersalePoCriteria.andStateEqualTo(Aftersale.State.WAITING_USER_DELIVE.getCode().byteValue());
        try {
            // 修改售后单信息
            int ret = aftersaleMapper.updateByExampleSelective(po, aftersalePoExample);
            if (ret == 0) {
                // 寄出成功
                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后订单无法寄出：%s", bo.getId()));
            } else {
                // 寄出失败
                return new ReturnObject<>(ResponseCode.OK, String.format("寄出售后成功：%s", po.getId()));
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
     * 管理员审核售后单
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    public ReturnObject updateAftersaleToConfirm(Aftersale bo) {
        AftersaleServicePo po = bo.creatPo();
        AftersaleServicePoExample aftersalePoExample = new AftersaleServicePoExample();
        AftersaleServicePoExample.Criteria aftersalePoCriteria = aftersalePoExample.createCriteria();
        AftersaleServicePo aftersaleServicePo = aftersaleMapper.selectByPrimaryKey(bo.getId());
        if(aftersaleServicePo == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后单id不存在：%s", bo.getId()));
        }
        if(aftersaleServicePo.getBeDeleted() == (byte)1){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"该售后单已被删除");
        }
        // 只可以审核用户新建的售后单
        aftersalePoCriteria.andIdEqualTo(bo.getId());
        aftersalePoCriteria.andShopIdEqualTo(bo.getShopId());
        aftersalePoCriteria.andStateEqualTo(Aftersale.State.WAITING_SHOP_REVIEW.getCode().byteValue());
        try {
            // 修改售后单信息
            int ret = aftersaleMapper.updateByExampleSelective(po, aftersalePoExample);
            if (ret == 0) {
                // 审核失败
                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后订单无法审核：%s", bo.getId()));
            } else {
                // 审核成功
                return new ReturnObject<>(ResponseCode.OK, String.format("审核售后完毕：%s", po.getId()));
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
     * 卖家寄出,只可以寄出店家确认收货的订单
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    public ReturnObject updateAftersaleToDelive(Aftersale bo) {
        AftersaleServicePo po = bo.creatPo();
        AftersaleServicePoExample aftersalePoExample = new AftersaleServicePoExample();
        AftersaleServicePoExample.Criteria aftersalePoCriteria = aftersalePoExample.createCriteria();
        AftersaleServicePo aftersaleServicePo = aftersaleMapper.selectByPrimaryKey(bo.getId());
        if(aftersaleServicePo == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后单id不存在：%s", bo.getId()));
        }
        if(aftersaleServicePo.getBeDeleted() == (byte)1){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"该售后单已被删除");
        }
        // 只可以寄出卖家已确认的售后单
        aftersalePoCriteria.andIdEqualTo(bo.getId());
        aftersalePoCriteria.andShopIdEqualTo(bo.getShopId());
        aftersalePoCriteria.andStateEqualTo(Aftersale.State.WAITING_SHOP_DELIVE.getCode().byteValue());
        try {
            // 修改售后单信息
            int ret = aftersaleMapper.updateByExampleSelective(po, aftersalePoExample);
            if (ret == 0) {
                // 寄出失败
                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后订单无法寄出：%s", bo.getId()));
            } else {
                // 寄出成功
                return new ReturnObject<>(ResponseCode.OK, String.format("寄出售后成功：%s", po.getId()));
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

    /**
     * 店家确认收到买家的退（换）货，验收不通过回到等待买家寄出状态
     *
     * @author wwc
     * @date 2020/11/23 23:23
     * @version 1.0
     */
    public ReturnObject updateAftersaleBackToSendback(Aftersale bo) {
        AftersaleServicePo po = bo.creatPo();
        AftersaleServicePoExample aftersalePoExample = new AftersaleServicePoExample();
        AftersaleServicePoExample.Criteria aftersalePoCriteria = aftersalePoExample.createCriteria();
        AftersaleServicePo aftersaleServicePo = aftersaleMapper.selectByPrimaryKey(bo.getId());
        if(aftersaleServicePo == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后单id不存在：%s", bo.getId()));
        }
        if(aftersaleServicePo.getBeDeleted() == (byte)1){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"该售后单已被删除");
        }
        // 仅用户已寄出才能验收
        aftersalePoCriteria.andIdEqualTo(bo.getId());
        aftersalePoCriteria.andShopIdEqualTo(bo.getShopId());
        aftersalePoCriteria.andStateEqualTo(Aftersale.State.USER_DELIVE.getCode().byteValue());
        try {
            // 修改售后单信息
            int ret = aftersaleMapper.updateByExampleSelective(po, aftersalePoExample);
            if (ret == 0) {
                // 确认失败
                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后订单无法确认收货：%s", bo.getId()));
            } else {
                // 验收失败
                return new ReturnObject<>(ResponseCode.OK, String.format("卖家验收不通过：%s", po.getId()));
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

    /**
     * 店家确认收到买家的退（换）货，验收通过并处理
     *
     * @author wwc
     * @date 2020/11/23 23:23
     * @version 1.0
     */
    public ReturnObject updateAftersaleToReceive(Aftersale bo) {
        AftersaleServicePo po = bo.creatPo();
        AftersaleServicePoExample aftersalePoExample = new AftersaleServicePoExample();
        AftersaleServicePoExample.Criteria aftersalePoCriteria = aftersalePoExample.createCriteria();
        AftersaleServicePo aftersaleServicePo = aftersaleMapper.selectByPrimaryKey(bo.getId());
        if(aftersaleServicePo == null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后单id不存在：%s", bo.getId()));
        }
        if(aftersaleServicePo.getBeDeleted() == (byte)1){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"该售后单已被删除");
        }
        // 仅用户已寄出才能确认收货并处理
        aftersalePoCriteria.andIdEqualTo(bo.getId());
        aftersalePoCriteria.andShopIdEqualTo(bo.getShopId());
        aftersalePoCriteria.andStateEqualTo(Aftersale.State.USER_DELIVE.getCode().byteValue());
        try {
            // 修改售后单信息
            int ret = aftersaleMapper.updateByExampleSelective(po, aftersalePoExample);
            if (ret == 0) {
                // 确认失败
                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后订单无法确认收货：%s", bo.getId()));
            } else {
                // 验收失败
                return new ReturnObject<>(ResponseCode.OK, String.format("卖家验收不通过：%s", po.getId()));
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

    /**
     * 通过id查用户id和店铺id
     * 内部调用
     */
    public ReturnObject<AftersaleDTO> getAftersaleDTOByAftersaleId(Long aftersaleId) {
        try {
            AftersaleServicePo po = aftersaleMapper.selectByPrimaryKey(aftersaleId);
            if (po == null) {
                // 没有找到
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该没有该售后订单id：%s", aftersaleId));
            } else {
                return new ReturnObject<>(new AftersaleDTO(po.getShopId(), po.getCustomerId(), po.getOrderItemId()));
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

//    /**
//     * 修改售后单状态
//     *
//     * @param bo 要修改的信息
//     * @author wwc
//     * @date 2020/11/26 14:38
//     * @version 1.0
//     */
//    public ReturnObject updateAftersale(Aftersale bo) {
//        AftersaleServicePo po = bo.creatPo();
//        AftersaleServicePoExample aftersalePoExample = new AftersaleServicePoExample();
//        AftersaleServicePoExample.Criteria aftersalePoCriteria = aftersalePoExample.createCriteria();
//        // 设置修改的条件
//        aftersalePoCriteria.andIdEqualTo(bo.getId());
//        if (bo.getShopId() != null) {
//            aftersalePoCriteria.andShopIdEqualTo(bo.getShopId());
//        }
//        if (bo.getCustomerId() != null) {
//            aftersalePoCriteria.andCustomerIdEqualTo(bo.getCustomerId());
//        }
//        switch (bo.getState()) {
//            // 买家修改售后单内容
//            case WAITING_SHOP_REVIEW:
//                aftersalePoCriteria.andStateEqualTo(Aftersale.State.WAITING_USER_DELIVE.getCode().byteValue());
//                break;
//            // 管理员同意或卖家验收不通过
//            case WAITING_USER_DELIVE:
//                aftersalePoCriteria.andStateIn(Lists.newArrayList(Aftersale.State.WAITING_SHOP_REVIEW.getCode().byteValue()
//                        , Aftersale.State.USER_DELIVE.getCode().byteValue()));
//                break;
//            // 拒绝售后或审核超时
//            case SHOP_REFUSE:
//                aftersalePoCriteria.andStateEqualTo(Aftersale.State.WAITING_SHOP_REVIEW.getCode().byteValue());
//                break;
//            // 买家填写运单信息
//            case USER_DELIVE:
//                aftersalePoCriteria.andStateEqualTo(Aftersale.State.WAITING_USER_DELIVE.getCode().byteValue());
//                break;
//            // 店家确认收到退换货，作为后续查询订单类型并进一步更新的中转
//            case SHOP_RECEIVE:
//                aftersalePoCriteria.andStateEqualTo(Aftersale.State.USER_DELIVE.getCode().byteValue());
//                break;
//            // 进行售后处理
//            case WAITING_SHOP_REFUND:
//            case WAITING_SHOP_DELIVE:
//                aftersalePoCriteria.andStateEqualTo(Aftersale.State.SHOP_RECEIVE.getCode().byteValue());
//                break;
//            // 换货或维修店家寄出货物
//            case SHOP_DELIVE:
//                aftersalePoCriteria.andStateEqualTo(Aftersale.State.WAITING_SHOP_DELIVE.getCode().byteValue());
//                break;
//            // 店家处理完成，暂未使用
//            case SHOP_PROCESS:
//                aftersalePoCriteria.andStateIn(Lists.newArrayList(Aftersale.State.WAITING_SHOP_REFUND.getCode().byteValue()
//                        , Aftersale.State.SHOP_DELIVE.getCode().byteValue()));
//                break;
//            // 买家确认售后单结束
//            case FINISH:
//                aftersalePoCriteria.andStateIn(Lists.newArrayList(Aftersale.State.SHOP_PROCESS.getCode().byteValue()
//                        , Aftersale.State.SHOP_DELIVE.getCode().byteValue()
//                        , Aftersale.State.WAITING_SHOP_REFUND.getCode().byteValue()));
//                break;
//            // 买家取消售后
//            case CANCEL:
//                aftersalePoCriteria.andStateIn(Lists.newArrayList(Aftersale.State.WAITING_SHOP_REVIEW.getCode().byteValue()
//                        , Aftersale.State.WAITING_USER_DELIVE.getCode().byteValue()));
//                break;
//            // 其他
//            default:
//                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后单状态无法修改：%s", bo.getId()));
//        }
//        try {
//            // 修改售后单信息
//            int ret = aftersaleMapper.updateByExampleSelective(po, aftersalePoExample);
//            if (ret == 0) {
//                // 修改失败
//                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后单状态无法修改：%s", bo.getId()));
//            } else {
//                return new ReturnObject<>(ResponseCode.OK, String.format("修改状态成功：%s", po.getId()));
//            }
//        } catch (DataAccessException e) {
//            log.error("数据库错误：" + e.getMessage());
//            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
//        } catch (Exception e) {
//            log.error("其他错误：" + e.getMessage());
//            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
//        }
//    }

}

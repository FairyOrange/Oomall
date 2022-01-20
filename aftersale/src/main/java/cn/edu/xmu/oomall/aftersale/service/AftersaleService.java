package cn.edu.xmu.oomall.aftersale.service;

import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.order.model.OrderDTO;
import cn.edu.xmu.oomall.order.service.IOrderService;
import cn.edu.xmu.oomall.order.service.IPaymentService;
import cn.edu.xmu.oomall.other.service.IAddressService;
import cn.edu.xmu.oomall.aftersale.dao.AftersaleDao;
import cn.edu.xmu.oomall.aftersale.model.bo.Aftersale;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 售后Service
 *
 * @version 1.0
 * @athor wwc
 * @date 2020/11/21 20:15
 */
@Slf4j
@Service
public class AftersaleService {

    @Autowired
    AftersaleDao aftersaleDao;

    // Dubbo远程调用注解
    @DubboReference(check = false)
    private IAddressService addressService;

    @DubboReference(check = false)
    private IOrderService orderService;

    @DubboReference(check = false)
    private IPaymentService paymentService;

    /**
     * 买家提交售后单,建立新的售后记录,state设置为0
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject insertAftersale(Aftersale bo) {
        try {
            ReturnObject<Boolean> regionIsValid = addressService.getValidRegionId(bo.getRegionId());
            if (regionIsValid == null) {
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "地区不存在");
            }
            if (regionIsValid.getCode() == ResponseCode.OK) {
                // 若地区id有效则新建该售后单
                // 如果orderItemId对应的order不为完成态 则无法创建该售后单
                ReturnObject orderIsValid = orderService.judgeOrderitemIdFinished(bo.getOrderItemId());
                if(orderIsValid.getCode() != ResponseCode.OK){
                    return orderIsValid;
                }
                ReturnObject<OrderDTO> retObj = orderService.getUserSelectSOrderInfo(bo.getCustomerId(), bo.getOrderItemId());
                if (retObj.getCode() != ResponseCode.OK) {
                    return retObj;
                }
                OrderDTO orderDTO = retObj.getData();
//                bo.setOrderId(orderDTO.getOrderId());
                bo.setOrderSn(orderDTO.getOrderSn());
                bo.setSkuId(orderDTO.getSkuId());
                bo.setSkuName(orderDTO.getSkuName());
                bo.setShopId(orderDTO.getShopId());
                ReturnObject returnObject = aftersaleDao.insertAftersale(bo);
                return returnObject;
            } else {
                return regionIsValid;
            }

        }catch (Exception e){
            log.error("dubbo调用错误");
        }
        return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "微服务调用错误");
    }

    /**
     * 买家查询所有售后单信息
     *
     * @param bo        前端传递的参数,保证shopId为null
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param page      页数
     * @param pageSize  每页大小
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject listUserSelectAftersale(Aftersale bo, String beginTime, String endTime, Integer page, Integer pageSize) {
        ReturnObject returnObject = aftersaleDao.listSelectAllAftersale(bo, beginTime, endTime, page, pageSize);
        return returnObject;
    }

    /**
     * 买家根据售后单id查询售后单信息 返回完整信息
     *
     * @param userIdAudit 当前用户id
     * @param id          售后单id
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getUserSelectAftersale(Long userIdAudit, Long id) {
        // 通过userId和aftersaleId 唯一找到一个售后单
        ReturnObject<Aftersale> returnObject = aftersaleDao.getUserSelectAftersale(userIdAudit, id);
        if(returnObject.getCode() != ResponseCode.OK){
            return returnObject;
        }
        Aftersale aftersale = returnObject.getData();
        ReturnObject<OrderDTO> retObj = null;
        OrderDTO orderDTO = new OrderDTO();
        try{
            retObj = orderService.getUserSelectSOrderInfo(userIdAudit, aftersale.getOrderItemId());

        if (retObj!= null && retObj.getCode() != ResponseCode.OK) {
            return retObj;
        }
        if(retObj.getData()!=null){
            orderDTO = retObj.getData();
        }else{
            orderDTO = new OrderDTO();
        } }catch (Exception e){
            log.error("dubbo服务调用失败!");
        }

        // 组合
        aftersale = aftersaleDao.groupDTOAndAfterSaleBo(aftersale, orderDTO);
        return new ReturnObject(aftersale);
    }

    /**
     * 售后单完成之前，买家取消售后单；售后单完成之后，买家逻辑删除售后单
     *
     * @param userIdAudit 当前用户id
     * @param id          售后单id
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteAftersale(Long userIdAudit, Long id) {
        // 根据id查询该条售后单
        ReturnObject retObject = aftersaleDao.getUserSelectAftersale(userIdAudit, id);
        if (retObject.getData() != null) {
            // 若查询到该售后单则根据状态进行处理
            Aftersale bo = (Aftersale) retObject.getData();
            bo.setGmtModified(LocalDateTime.now());
            if (bo.getState() == Aftersale.State.WAITING_SHOP_REVIEW
                    || bo.getState() == Aftersale.State.WAITING_USER_DELIVE) {
                // 如果售后单未完成则取消
                bo.setState(Aftersale.State.CANCEL);
            } else if (bo.getState() == Aftersale.State.FINISH
                    || bo.getState() == Aftersale.State.SHOP_REFUSE
                    || bo.getState() == Aftersale.State.CANCEL) {
                // 若售后单已完成或已取消则删除
                bo.setBeDeleted((byte) 1);
            } else {
                // 否则无法取消或删除
                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后单无法取消或删除"));
            }
            // 进行状态更新
            ReturnObject returnObject = aftersaleDao.deleteAftersale(bo);
            return returnObject;
        } else {
            return retObject;
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
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateAftersaleInfo(Aftersale bo) {
        ReturnObject returnObject = aftersaleDao.updateAftersaleInfo(bo);
        return returnObject;
    }

    /**
     * 买家确认售后单,只可以确认卖家寄出后的售后单
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateAftersaleToFinish(Aftersale bo) {
        ReturnObject returnObject = aftersaleDao.updateAftersaleToFinish(bo);
        return returnObject;
    }

    /**
     * 买家寄出,只可以寄出审核通过的订单
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateAftersaleToSendback(Aftersale bo) {
        ReturnObject returnObject = aftersaleDao.updateAftersaleToSendback(bo);
        return returnObject;
    }

    /**
     * 管理员查看所有售后单
     *
     * @param bo        前端传递的参数,保证customerId为null
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param page      页数
     * @param pageSize  每页大小
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject listAdminSelectAftersale(Aftersale bo, String beginTime, String endTime, Integer page, Integer pageSize) {
        ReturnObject returnObject = aftersaleDao.listSelectAllAftersale(bo, beginTime, endTime, page, pageSize);
        return returnObject;
    }

    /**
     * 管理员审核售后单
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateAftersaleToConfirm(Aftersale bo) {
        ReturnObject returnObject = aftersaleDao.updateAftersaleToConfirm(bo);
        return returnObject;
    }

    /**
     * 卖家寄出,只可以寄出已经收货的订单 寄出换货单需要调用外部接口
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateAftersaleToDelive(Aftersale bo) {

        // 查询该售后单进行处理
        ReturnObject ret = aftersaleDao.getAdminSelectAftersale(bo.getShopId(), bo.getId());
        if (ret.getData() != null) {
            // 查询该订单信息进行业务处理
            Aftersale aftersale = (Aftersale) ret.getData();
            // 只有在待店家发货的情况下执行该操作 否则抛出异常
            if (aftersale.getState() != Aftersale.State.WAITING_SHOP_DELIVE) {
                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后订单无法确认收货：%s", bo.getId()));
            }
            // 0换货 1退货 2维修
            switch (aftersale.getType()) {
                // 内部调用
                case EXCHANGE:
                    // 若是如果是换货则填写新订单id到orderId
                    ReturnObject ret01 = null;
                    try {
                        ret01 = orderService.getAdminHandleExchange(aftersale.getCustomerId(), aftersale.getShopId(),
                                aftersale.getOrderItemId(), aftersale.getQuantity(), aftersale.getId(), aftersale.getRegionId(), aftersale.getDetail(), aftersale.getConsignee(), aftersale.getMobile());

                        if (ret01 != null && ret01.getCode() != ResponseCode.OK) {
                            return ret01;
                        }
                        Long newOrderId = (Long) ret01.getData();
                        bo.setOrderId(newOrderId);
                    }catch (Exception e){
                        log.error("dubbo服务调用失败!");
                    }
                    bo.setState(Aftersale.State.SHOP_DELIVE);
                    ReturnObject ret0 = aftersaleDao.updateAftersaleToDelive(bo);
                    if (ret0.getCode() == ResponseCode.OK) {
                        log.debug("换货售后确认售后收货");
                        return new ReturnObject<>(ResponseCode.OK, String.format("换货售后确认售后收货: " + bo.getId()));
                    } else {
                        return ret0;
                    }
                case REPAIR:
                    // 如果是维修则填写logSn
                    ReturnObject ret21 = null;
                    try{
                        ret21 = paymentService.getAdminHandleRepair(aftersale.getCustomerId(),aftersale.getShopId(),
                                aftersale.getOrderItemId(),aftersale.getRefund(),aftersale.getId());
                    }catch (Exception e){
                        log.error("dubbo服务调用失败!");
                    }
                    if(ret21 != null && ret21.getCode() != ResponseCode.OK){
                        return ret21;
                    }
                    bo.setState(Aftersale.State.SHOP_DELIVE);
                    ReturnObject ret2 = aftersaleDao.updateAftersaleToDelive(bo);
                    if (ret2.getCode() == ResponseCode.OK) {
                        log.debug("维修售后确认售后收货");
                        return new ReturnObject<>(ResponseCode.OK, String.format("维修售后确认售后收货: " + bo.getId()));
                    } else {
                        return ret2;
                    }
                default:
                    log.debug("该售后订单类型不符合要求");
                    return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后订单类型不符合要求：" + bo.getId()));
            }
        }else{
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后订单无法寄出：" + bo.getId()));
        }
    }

    /**
     * 卖家根据售后单id查询售后单信息
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getAdminSelectAftersale(Aftersale bo) {
        // 唯一找到一个售后单
        ReturnObject<Aftersale> returnObject = aftersaleDao.getAdminSelectAftersale(bo.getShopId(), bo.getId());
        if(returnObject.getCode() != ResponseCode.OK){
            return returnObject;
        }
        Aftersale aftersale = returnObject.getData();
        ReturnObject<OrderDTO> retObj = null;
        try{
            retObj = orderService.getShopSelectOrderInfo(bo.getShopId(),aftersale.getOrderItemId());
        }catch (Exception e){
            log.error("dubbo服务调用失败!");
        }
        if(retObj != null && retObj.getCode() != ResponseCode.OK){
            return retObj;
        }
        OrderDTO orderDTO = retObj.getData();
        aftersale = aftersaleDao.groupDTOAndAfterSaleBo(aftersale,orderDTO);

        return new ReturnObject(aftersale);
    }

    /**
     * 店家确认收到买家的退（换）货，验收不通过回到等待买家寄出状态
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 23:23
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateAftersaleBackToSendback(Aftersale bo) {
        // 更改状态为已收货
        ReturnObject returnObject = aftersaleDao.updateAftersaleBackToSendback(bo);
        return returnObject;
    }

    /**
     * 店家处理售后单
     *
     * @param bo 前端传递的参数
     * @author wwc
     * @date 2020/11/23 23:23
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject handleAftersale(Aftersale bo) {
        // 查询该售后单进行处理
        ReturnObject ret = aftersaleDao.getAdminSelectAftersale(bo.getShopId(), bo.getId());
        if (ret.getData() != null) {
            // 查询该订单信息进行业务处理
            Aftersale aftersale = (Aftersale) ret.getData();
            // 只有在买家已发货的情况下执行该操作 否则抛出异常
            if(aftersale.getState()!=Aftersale.State.USER_DELIVE){
                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW, String.format("该售后订单无法确认收货：%s", bo.getId()));
            }
            // 0换货 1退货 2维修
            switch (aftersale.getType()) {
                // 内部调用
                case EXCHANGE:
                    // 如果是换货仅修改状态 等待下一步寄出
                    bo.setState(Aftersale.State.WAITING_SHOP_DELIVE);
                    ReturnObject ret0 = aftersaleDao.updateAftersaleToReceive(bo);
                    if (ret0.getCode() == ResponseCode.OK) {
                        log.debug("换货售后确认售后收货");
                        return new ReturnObject<>(ResponseCode.OK, String.format("换货售后确认售后收货: " + bo.getId()));
                    } else {
                        return ret0;
                    }
                case RETURN_BACK:
                    // 若是退款则调用退款api
                    ReturnObject ret11 = null;
                    try{
                        ret11 = paymentService.getAdminHandleRefund(aftersale.getCustomerId(),aftersale.getShopId(),
                                aftersale.getOrderItemId(),aftersale.getRefund(),aftersale.getId(),aftersale.getQuantity());
                    }catch (Exception e){
                        log.error("dubbo服务调用失败!");
                    }
                    if(ret11 != null &&ret11.getCode() != ResponseCode.OK){
                        return ret11;
                    }
                    bo.setState(Aftersale.State.WAITING_SHOP_REFUND);
                    ReturnObject ret1 = aftersaleDao.updateAftersaleToReceive(bo);
                    if (ret1.getCode() == ResponseCode.OK) {
                        log.debug("退款售后确认售后收货");
                        return new ReturnObject<>(ResponseCode.OK, String.format("退款售后确认售后收货: " + bo.getId()));
                    } else {
                        return ret1;
                    }
                case REPAIR:
                    // 维修仅修改状态 等待下一个动作寄回
                    bo.setState(Aftersale.State.WAITING_SHOP_DELIVE);
                    ReturnObject ret2 = aftersaleDao.updateAftersaleToReceive(bo);
                    if (ret2.getCode() == ResponseCode.OK) {
                        log.debug("维修售后确认售后收货");
                        return new ReturnObject<>(ResponseCode.OK, String.format("维修售后确认售后收货: " + bo.getId()));
                    } else {
                        return ret2;
                    }
                default:
                    log.debug("该售后订单类型不符合要求");
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后订单类型不符合要求：" + bo.getId()));
            }
        } else {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该售后订单无法确认收货：" + bo.getId()));
        }
    }

}

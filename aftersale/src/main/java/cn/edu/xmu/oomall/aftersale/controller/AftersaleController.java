package cn.edu.xmu.oomall.aftersale.controller;

import cn.edu.xmu.oomall.aftersale.model.bo.Aftersale;
import cn.edu.xmu.oomall.aftersale.model.vo.*;
import cn.edu.xmu.oomall.aftersale.service.AftersaleService;
import cn.edu.xmu.oomall.aftersale.util.AftersaleCommon;
import cn.edu.xmu.oomall.annotation.*;
import cn.edu.xmu.oomall.util.Common;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ResponseUtil;
import cn.edu.xmu.oomall.util.ReturnObject;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 售后Controller
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/21 23:23
 */
@Slf4j
@Api(value = "售后服务", tags = {"aftersale"})
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
@Validated
public class AftersaleController {

    @Autowired
    private AftersaleService aftersaleService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 获得售后单的所有状态
     *
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    @ApiOperation(value = "获得售后单的所有状态", tags = {"aftersale"})
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
//    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/aftersales/states")
    @AutoLog(title = "获得售后单的所有状态", action = "GET")
    public Object getAftersalesStates() {
        return ResponseUtil.ok(Aftersale.State.getAllState());
    }

    /**
     * 买家提交售后单
     *
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    @ApiOperation(value = "买家提交售后单", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "订单明细id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AftersaleVo", name = "vo", value = "售后服务信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/orderitems/{id}/aftersales")
    @AutoLog(title = "买家提交售后单", action = "POST")
    @Audit
    public Object postOrderItemsIdAftersales(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @PathVariable Long id,
            @RequestBody @Validated AftersaleVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        Aftersale bo = new Aftersale(vo);
        bo.setCustomerId(userIdAudit);
        bo.setOrderItemId(id);
        bo.setState(Aftersale.State.WAITING_SHOP_REVIEW);
        bo.setGmtCreate(LocalDateTime.now());
        bo.setGmtModified(LocalDateTime.now());
        ReturnObject returnObject = aftersaleService.insertAftersale(bo);
        log.info(returnObject.getCode().toString());
        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.getRetObject(returnObject);
    }

    /**
     * 买家查询所有售后单信息
     *
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    @ApiOperation(value = "买家查询所有的售后单信息（可根据售后类型和状态选择）", notes = "默认所有，选择按类型或者状态（买家通过这个API只能查询到自己的售后单）", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
//            @ApiImplicitParam(paramType = "query", dataType = "int", name = "skuId", value = "商品SPUid", required = false),
//            @ApiImplicitParam(paramType = "query", dataType = "int", name = "skuId", value = "商品SKUid", required = false),
//            @ApiImplicitParam(paramType = "query", dataType = "int", name = "orderItemId", value = "商品详情id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "byte", name = "type", value = "售后类型", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "byte", name = "state", value = "售后状态", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/aftersales")
    @AutoLog(title = "买家查询所有的售后单信息", action = "GET")
    @Audit
    public Object getAftersales(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
//            @RequestParam(required = false) Long spuId,
//            @RequestParam(required = false) Long skuId,
//            @RequestParam(required = false) Long orderItemId,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Byte type,
            @RequestParam(required = false) Byte state) {
//        if (beginTime != null && endTime != null && beginTime.isAfter(endTime)) {
//            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.Log_Bigger), httpServletResponse);
//        }
//        if(!(AftersaleCommon.judgeTimeValid(beginTime)&&AftersaleCommon.judgeTimeValid(endTime))){
//            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
//            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
//        }
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime beginTimeLDT = null;
//        LocalDateTime endTimeLDT = null;
//        if(beginTime != null){
//            beginTimeLDT = LocalDateTime.parse(beginTime,df);
//        }
//        if(endTime != null){
//            endTimeLDT = LocalDateTime.parse(endTime,df);
//        }
        Aftersale bo = new Aftersale();
        bo.setCustomerId(userIdAudit);
//        bo.setSpuId(spuId);
//        bo.setSkuId(skuId);
//        bo.setOrderItemId(orderItemId);
        if (type != null) {
            bo.setType(Aftersale.Type.getTypeByCode(type.intValue()));
        }
        bo.setState(state != null ? Aftersale.State.getTypeByCode(state.intValue()) : null);
        ReturnObject returnObject = aftersaleService.listUserSelectAftersale(bo, beginTime, endTime, page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 买家根据售后单id查询售后单信息
     *
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    @ApiOperation(value = "买家根据售后单id查询售后单信息", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "售后单id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/aftersales/{id}")
    @AutoLog(title = "买家根据售后单id查询售后单信息", action = "GET")
    @Audit
    public Object getAfterSalesId(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @PathVariable("id") Long id) {
        ReturnObject returnObject = aftersaleService.getUserSelectAftersale(userIdAudit, id);
        return Common.getRetObject(returnObject);
    }

    /**
     * 买家取消售后，或逻辑删除售后
     *
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    @ApiOperation(value = "买家取消售后单和逻辑删除售后单", notes = "售后单完成之前，买家取消售后单；售后单完成之后，买家逻辑删除售后单", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "售后单id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/aftersales/{id}")
    @AutoLog(title = "买家取消售后单和逻辑删除售后单", action = "DELETE")
    @Audit
    public Object deleteAfterSalesId(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @PathVariable("id") Long id) {
        ReturnObject returnObject = aftersaleService.deleteAftersale(userIdAudit, id);
        return Common.getRetObject(returnObject);
    }

    /**
     * 买家修改售后单信息（店家生成售后单前）
     *
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    @ApiOperation(value = "买家修改售后单信息（店家生成售后单前）", notes = "在店家生成售后单之前买家可以修改信息，生成之后买家只能进行删除操作", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AftersaleUpdateVo", name = "vo", value = "买家可修改的信息：地址，售后商品的数量，申请售后的原因，联系人以及联系电话", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/aftersales/{id}")
    @AutoLog(title = "买家修改售后单信息", action = "PUT")
    @Audit
    public Object putAfterSalesId(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @PathVariable("id") Long id,
            @RequestBody @Validated AftersaleUpdateVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        Aftersale bo = new Aftersale(vo);
        bo.setId(id);
        bo.setCustomerId(userIdAudit);
        bo.setGmtModified(LocalDateTime.now());
        bo.setState(Aftersale.State.WAITING_SHOP_REVIEW);
        ReturnObject returnObject = aftersaleService.updateAftersaleInfo(bo);
        return Common.getRetObject(returnObject);
    }

    /**
     * 买家确认售后单结束
     *
     * @author wwc
     * @date 2020/11/23 19:57
     * @version 1.0
     */
    @ApiOperation(value = "买家确认售后单结束", notes = "修改售后单状态", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "售后单id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/aftersales/{id}/confirm")
    @AutoLog(title = "买家确认售后单结束", action = "PUT")
    @Audit
    public Object putAfterSalesIdConfirm(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @PathVariable("id") Long id) {
        Aftersale bo = new Aftersale();
        bo.setId(id);
        bo.setCustomerId(userIdAudit);
        bo.setGmtModified(LocalDateTime.now());
        bo.setState(Aftersale.State.FINISH);
        ReturnObject returnObject = aftersaleService.updateAftersaleToFinish(bo);
        return Common.getRetObject(returnObject);
    }

    /**
     * 买家填写售后的运单信息
     *
     * @author wwc
     * @date 2020/11/23 20:39
     * @version 1.0
     */
    @ApiOperation(value = "买家填写售后的运单信息", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "LogSnVo", name = "vo", value = "运单号", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/aftersales/{id}/sendback")
    @AutoLog(title = "买家填写售后的运单信息", action = "PUT")
    @Audit
    public Object putAfterSalesIdSendBack(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @PathVariable("id") Long id,
            @RequestBody @Validated LogSnVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        Aftersale bo = new Aftersale();
        bo.setId(id);
        bo.setCustomerId(userIdAudit);
        bo.setCustomerLogSn(vo.getLogSn());
        bo.setGmtModified(LocalDateTime.now());
        bo.setState(Aftersale.State.USER_DELIVE);
        ReturnObject returnObject = aftersaleService.updateAftersaleToSendback(bo);
        return Common.getRetObject(returnObject);
    }

    /**
     * 管理员查看所有售后单
     *
     * @author wwc
     * @date 2020/11/21 19:57
     * @version 1.0
     */
    @ApiOperation(value = "管理员查看所有售后单（可根据售后类型和状态选择）", notes = "管理员可通过售后单ID查看所有售后单", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "店铺id", required = true),
//            @ApiImplicitParam(paramType = "query", dataType = "int", name = "skuId", value = "商品SPUid", required = false),
//            @ApiImplicitParam(paramType = "query", dataType = "int", name = "skuId", value = "商品SKUid", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "byte", name = "type", value = "售后类型", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "byte", name = "state", value = "售后状态", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/shops/{id}/aftersales")
    @AutoLog(title = "管理员查看所有售后单", action = "GET")
    @Audit
    public Object getShopsIdAftersales(
            @Depart @ApiIgnore @RequestParam(required = false) Long shopIdAudit,
            @PathVariable("id") Long shopId,
//            @RequestParam(required = false) Long spuId,
//            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Byte type,
            @RequestParam(required = false) Byte state) {
//        if (beginTime != null && endTime != null && beginTime.isAfter(endTime)) {
//            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.Log_Bigger), httpServletResponse);
//        }
//        if(!(AftersaleCommon.judgeTimeValid(beginTime)&&AftersaleCommon.judgeTimeValid(endTime))){
//            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
//            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
//        }
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime beginTimeLDT = null;
//        LocalDateTime endTimeLDT = null;
//        if(beginTime != null){
//            beginTimeLDT = LocalDateTime.parse(beginTime,df);
//        }
//        if(endTime != null){
//            endTimeLDT = LocalDateTime.parse(endTime,df);
//        }
        Aftersale bo = new Aftersale();
        if (!shopIdAudit.equals (0L)) {
            bo.setShopId(shopIdAudit);
        } else {
            bo.setShopId(shopId);
        }
//        bo.setShopId(shopId);
//        bo.setSpuId(spuId);
//        bo.setSkuId(skuId);
        if (type != null) {
            bo.setType(Aftersale.Type.getTypeByCode(type.intValue()));
        }
        bo.setState(state != null ? Aftersale.State.getTypeByCode(state.intValue()) : null);
        ReturnObject returnObject = aftersaleService.listAdminSelectAftersale(bo, beginTime, endTime, page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 管理员同意/不同意（退款，换货，维修）
     *
     * @author wwc
     * @date 2020/11/23 22:05
     * @version 1.0
     */
    @ApiOperation(value = "管理员同意/不同意（退款，换货，维修）", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "ReviewVo", name = "vo", value = "处理意见", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{shopId}/aftersales/{id}/confirm")
    @AutoLog(title = "管理员同意/不同意（退款，换货，维修）", action = "PUT")
    @Audit
    public Object putShopsShopIdAftersalesIdConfirm(
            @Depart @ApiIgnore @RequestParam(required = false) Long shopIdAudit,
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @RequestBody @Validated ConfirmVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        Aftersale bo = new Aftersale();
        bo.setId(id);
        // 若不为平台管理员则只能审核本店的售后单
        if (!shopIdAudit.equals(0L)) {
            bo.setShopId(shopIdAudit);
        } else {
            bo.setShopId(shopId);
        }
        if (vo.getConfirm()) {
            bo.setState(Aftersale.State.WAITING_USER_DELIVE);
        } else {
            bo.setState(Aftersale.State.SHOP_REFUSE);
        }
        bo.setConclusion(vo.getConclusion());
        bo.setRefund(vo.getPrice());
        bo.setType(Aftersale.Type.getTypeByCode(vo.getType()));
        bo.setGmtModified(LocalDateTime.now());
        ReturnObject returnObject = aftersaleService.updateAftersaleToConfirm(bo);
        return Common.getRetObject(returnObject);
    }

    /**
     * 店家寄出维修好（调换）的货物
     *
     * @author wwc
     * @date 2020/11/23 22:45
     * @version 1.0
     */
    @ApiOperation(value = "店家寄出维修好（调换）的货物", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "ShopLogSn", name = "vo", value = "运单号", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{shopId}/aftersales/{id}/deliver")
    @AutoLog(title = "店家寄出维修好（调换）的货物", action = "PUT")
    @Audit
    public Object putShopsShopIdAftersalesIdDeliver(
            @Depart @ApiIgnore @RequestParam(required = false) Long shopIdAudit,
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @RequestBody(required = false) @Validated ShopLogSnVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        Aftersale bo = new Aftersale();
        bo.setId(id);
        // 若不为平台管理员则只能寄出本店的售后单
        if (!shopIdAudit.equals(0L)){
            bo.setShopId(shopIdAudit);
        } else {
            bo.setShopId(shopId);
        }
        if(vo != null && vo.getShopLogSn() != null) {
            bo.setShopLogSn(vo.getShopLogSn());
        }
        bo.setGmtModified(LocalDateTime.now());
        bo.setState(Aftersale.State.SHOP_DELIVE);
        ReturnObject returnObject = aftersaleService.updateAftersaleToDelive(bo);
        return Common.getRetObject(returnObject);
    }

    /**
     * 卖家根据售后单id查询售后单信息
     *
     * @author wwc
     * @date 2020/11/23 22:58
     * @version 1.0
     */
    @ApiOperation(value = "卖家根据售后单id查询售后单信息", notes = "卖家通过这个API只能查询到自己的售后单", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "售后单id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/shops/{shopId}/aftersales/{id}")
    @AutoLog(title = "卖家根据售后单id查询售后单信息", action = "PUT")
    @Audit
    public Object getShopsShopIdAftersalesId(
            @Depart @ApiIgnore @RequestParam(required = false) Long shopIdAudit,
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id) {
        Aftersale bo = new Aftersale();
        bo.setId(id);
        // 若不为平台管理员则只能查询本店的售后单
        if (!shopIdAudit.equals(0L)) {
            bo.setShopId(shopIdAudit);
        } else {
            bo.setShopId(shopId);
        }
        ReturnObject returnObject = aftersaleService.getAdminSelectAftersale(bo);
        return Common.getRetObject(returnObject);
    }

    /**
     * 店家确认收到买家的退（换）货
     *
     * @author wwc
     * @date 2020/11/23 23:23
     * @version 1.0
     */
    @ApiOperation(value = "店家确认收到买家的退（换）货", notes = "如果是退款，则退款给用户，如果换货则产生一个新订单，如果是维修则等待下一个动作", tags = {"aftersale"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "ReviewVo", name = "vo", value = "处理意见", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{shopId}/aftersales/{id}/receive")
    @AutoLog(title = "店家确认收到买家的退（换）货", action = "PUT")
    @Audit
    public Object putShopsShopIdAftersalesIdReceive(
            @Depart @ApiIgnore @RequestParam(required = false) Long shopIdAudit,
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @RequestBody @Validated ReviewVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        Aftersale bo = new Aftersale();
        bo.setId(id);
        // 若不为平台管理员则只能确认本店的售后单
        if (!shopIdAudit.equals(0L)) {
            bo.setShopId(shopIdAudit);
        } else {
            bo.setShopId(shopId);
        }
        // 设置修改时间
        bo.setConclusion(vo.getConclusion());
        bo.setGmtModified(LocalDateTime.now());
        if (vo.getConfirm()) {
            // 若验收成功则进行后续处理
            ReturnObject returnObject = aftersaleService.handleAftersale(bo);
            return Common.getRetObject(returnObject);
        } else {
            // 否则回到等待买家寄出状态
            bo.setState(Aftersale.State.WAITING_USER_DELIVE);
            ReturnObject returnObject = aftersaleService.updateAftersaleBackToSendback(bo);
            return Common.getRetObject(returnObject);
        }
    }

}

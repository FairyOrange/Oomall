package cn.edu.xmu.oomall.share.controller;

import cn.edu.xmu.oomall.annotation.Audit;
import cn.edu.xmu.oomall.annotation.LoginUser;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.share.model.bo.BeShare;
import cn.edu.xmu.oomall.share.model.bo.Share;
import cn.edu.xmu.oomall.share.model.bo.ShareActivity;
import cn.edu.xmu.oomall.share.model.vo.ShareActivityVo;
import cn.edu.xmu.oomall.share.model.vo.BeSharedCreateVo;
import cn.edu.xmu.oomall.share.model.vo.ShareActivityVo;
import cn.edu.xmu.oomall.share.model.vo.ShareVo;
import cn.edu.xmu.oomall.share.service.BeShareService;
import cn.edu.xmu.oomall.share.service.ShareActivityService;
import cn.edu.xmu.oomall.share.service.ShareService;
import cn.edu.xmu.oomall.util.Common;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ResponseUtil;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Api(value = "分享服务", tags = { "share" })
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @Autowired
    private BeShareService beShareService;

    @Autowired
    private ShareActivityService shareActivityService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @ApiOperation(value = "分享者生成分享链接", notes = "根据买家id和商品id生成唯一的分享链接", tags={ "share" })
    @GetMapping("/shareactitivities/states")
    public Object getShareActivitiesState() {
        return ResponseUtil.ok(ShareActivity.State.getAllState());
    }

    @ApiOperation(value = "分享者生成分享链接", notes = "根据买家id和商品id生成唯一的分享链接", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "skuId", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @Audit
    @PostMapping("/skus/{id}/shares")
    public Object postShares(@LoginUser Long userId, @PathVariable Long id) {
        Share share = new Share();
        share.setGoodsSkuId(id);
        share.setSharerId(userId);

        ReturnObject<VoObject> retObj = shareService.addShare(share);
        if (retObj.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.getRetObject(retObj);
    };


    /**
     * 买家查询所有分享记录
     * @author Qiuyan Qian
     * @param userId
     * @param skuId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return shareList
     * @date Created in 2020/11/24 21:35
     */
    @ApiOperation(value = "买家查询所有分享记录", notes = "买家查询所有分享记录", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "spuId", value = "goodsSpuId", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @Audit
    @GetMapping("/shares")
    public Object getShares(@LoginUser @ApiIgnore @RequestParam(required = false)Long userId,
                            @RequestParam(required = false) Long skuId,
                            @RequestParam(required = false) String beginTime,
                            @RequestParam(required = false) String endTime,
                            @RequestParam(required = false, defaultValue = "1") Integer page,
                            @RequestParam(required = false, defaultValue = "10") Integer pageSize){

        Share share = new Share();
        share.setGoodsSkuId(skuId);
        share.setSharerId(userId);
        ReturnObject retObj = shareService.getUserShares(share, beginTime, endTime, page, pageSize);
        return Common.getPageRetObject(retObj);
    };

    /**
     * 分享者查询所有的分享成功记录
     * @param userId 用户id
     * @param skuId  商品skuId
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param page
     * @param pageSize
     * @return beSharedPageInfo
     * @author Qiuyan Qian
     * @date Created in 2020/11/25 19:00
     */
    @ApiOperation(value = "买家查询所有分享成功记录", notes = "买家查询所有分享成功记录", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "skuId", value = "goodsSpuId", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @Audit
    @GetMapping("/beshared")
    public Object getBeshared(@LoginUser @ApiIgnore @RequestParam(required = false)Long userId,
                              @RequestParam(required = false) Long skuId,
                              @RequestParam(required = false) String beginTime,
                              @RequestParam(required = false) String endTime,
                              @RequestParam(required = false, defaultValue = "1") Integer page,
                              @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        BeShare beShare = new BeShare();
        beShare.setGoodsSkuId(skuId);
        beShare.setSharerId(userId);
        ReturnObject retObj = beShareService.getUserBeShares(beShare,beginTime, endTime, page, pageSize);
        return Common.getPageRetObject(retObj);
    }

    /**
     * 添加分享成功记录
     * @param userId
     * @param spuId
     * @param vo
     * @param bindingResult
     * @return java.lang.Object
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/1 下午12:46
    */
//    @ApiOperation(value = "生成分享成功状态信息", notes = "记录用户打开分享链接", tags={ "share" })
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
//            @ApiImplicitParam(paramType = "path", dataType = "int", name = "spuId", value = "goodsSpuId", required = true),
//            @ApiImplicitParam(paramType = "body", dataType = "BeSharedCreateVo", name = "vo", value = "分享记录id", required = true),
//    })
//    @ApiResponses({
//            @ApiResponse(code = 200, message = ""),
//    })
//    @Audit
//    @PostMapping("/goods/{spuId}/beshared")
//    public Object postGoodsSpuIdBeshared(@LoginUser @ApiIgnore @RequestParam(required = false)Long userId,
//                                         @PathVariable Long spuId,
//                                         @RequestBody @Validated BeSharedCreateVo vo,
//                                         BindingResult bindingResult){
//        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
//        if (object != null){
//            return object;
//        }
//        Long shareId = vo.getShareId();
//        ReturnObject retObj = beShareService.postBeShare(userId, spuId, shareId);
//        return Common.getRetObject(retObj);
//    };

    /**
     * 根据spuId shopId查看分享活动（所有用户均可）
     * @param shopId 店铺id
     * @param skuId 商品spuId
     * @param page
     * @param pageSize
     * @return shareActivityPageInfo
     * @author  Qiuyan Qian
     * @date  Created in 2020/11/25 下午9:33
    */
    @ApiOperation(value = "查看特定商品分享活动（所有用户均可）", notes = "根据商品id分享活动", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "shopId", value = "店铺Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "skuId", value = "SPU Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @Audit
    @GetMapping("/shareactivities")
    public Object getShareactivities(@RequestParam(required = false) Long shopId,
                                     @RequestParam(required = false) Long skuId,
                                     @RequestParam(required = false, defaultValue = "1") Integer page,
                                     @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        ShareActivity shareActivity = new ShareActivity();
        shareActivity.setShopId(shopId);
        shareActivity.setGoodsSkuId(skuId);
        ReturnObject retobj = shareActivityService.getShareActivities(shareActivity, page, pageSize);
        return Common.getPageRetObject(retobj);
    };

    @ApiOperation(value = "管理员修改平台分享活动的内容", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "分享活动id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "可修改的分享活动信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 605, message = "分享活动时段冲突"),
            @ApiResponse(code = 200, message = ""),
    })
    @Audit
    @PutMapping("/shops/{shopId}/shareactivities/{id}")
    public Object putShopsShopIdGoodsSpuIdShareactivitiesId(@PathVariable Long shopId, @PathVariable Long id,
                                                            @RequestBody @Validated ShareActivityVo vo, BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null){
            return object;
        }
        ShareActivity shareActivity = new ShareActivity(vo, shopId, null);
        shareActivity.setId(id);
        ReturnObject<VoObject> returnObject = shareActivityService.updateShareActivity(shareActivity);

        if (returnObject.getCode() == ResponseCode.Log_Bigger || returnObject.getCode() == ResponseCode.SHAREACT_CONFLICT) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return Common.getRetObject(returnObject);
    };

    /**
     * 平台或店家创建新的分享活动
     * @param shopId 店铺id
     * @param skuId 商品id
     * @param vo vo
     * @param bindingResult
     * @return java.lang.Object
     * @author Fiber W.
     * created at 12/4/20 11:01 AM
     */
    @ApiOperation(value = "平台或店家创建新的分享活动", notes = "两个管理员的身份用shop_id区分，0表示平台", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "skuId", value = "商品SKU id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "商品id和活动的起止时间与活动规则", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @Audit
    @PostMapping("/shops/{shopId}/skus/{skuId}/shareactivities")
    public Object postShopsShopIdGoodsSpuIdShareactivities(@PathVariable Long shopId, @PathVariable Long skuId,
                                                           @RequestBody @Validated ShareActivityVo vo, BindingResult bindingResult) {

        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null){
            return object;
        }

        ShareActivity shareActivity = new ShareActivity(vo, shopId, skuId);
        ReturnObject<VoObject> returnObject = shareActivityService.addShareActivity(shareActivity);
        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        } else if (returnObject.getCode() == ResponseCode.Log_Bigger || returnObject.getCode() == ResponseCode.SHAREACT_CONFLICT) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return Common.getRetObject(returnObject);

    }

    /**
     * 管理员终止指定商品的分享活动 如果是商家 则只能终止自己店铺内的商品 否则可以终止全平台的分享活动
     * @param shopId 商铺id
     * @param id 分享活动id
     * @author  Qiuyan Qian
     * @date  Created in 2020/11/25 下午10:14
    */
    @ApiOperation(value = "管理员终止指定商品的分享活动", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "分享活动id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/shareactivities/{id}")
    public Object deleteShopsShopIdGoodsSpuIdShareactivitiesId(
            @PathVariable(required = true) Long shopId,
            @PathVariable(required = true) Long id){
        ReturnObject retObj = shareActivityService.deleteShareActivity(shopId, id);
        return Common.getRetObject(retObj);
    };

    /**
     * 管理员查询所有分享成功记录
     * @param did
     * @param id
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return java.lang.Object
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/1 下午6:45
    */
    @ApiOperation(value = "管理员查询所有分享成功记录", notes = "管理查询所有分享成功记录", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "店铺Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "intr", name = "skuId", value = "SPU Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @Audit
    @GetMapping("/shops/{did}/skus/{id}/beshared")
    public Object getShopsIdBeshared(@PathVariable(required = true) Long did,
                                     @PathVariable(required = true) Long id,
                                     @RequestParam(required = false) String beginTime,
                                     @RequestParam(required = false) String endTime,
                                     @RequestParam(required = false, defaultValue = "1") Integer page,
                                     @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        ReturnObject retObj = beShareService.getAdminBeShares(did, id, beginTime, endTime, page, pageSize);
        return Common.getPageRetObject(retObj);
    };

    /**
     * 管理员查询商品分享记录 商铺管理员只查询本店商品分享记录
     * @param did
     * @param id
     * @param page
     * @param pageSize
     * @return java.lang.Object
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/1 上午10:30
    */
    @ApiOperation(value = "管理员查询商品分享记录", notes = "商铺管理员只能查询本店商铺的商品", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "店铺Id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "skuId", value = "SPU Id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @Audit
    @GetMapping("/shops/{did}/skus/{id}/shares")
    public Object getShopsIdShares(
            @PathVariable(required = true) Long did,
            @PathVariable(required = true) Long id,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        ReturnObject retObj = shareService.getAdminShares(did, id, page, pageSize);
        return Common.getPageRetObject(retObj);
    }


    /**
     * 管理员上架分享活动
     * @param shopId 店铺id
     * @param id 分享活动id
     * @return java.lang.Object
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/1 下午10:05
    */
    @ApiOperation(value = "管理员上架分享活动", tags={ "share" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "shopId", value = "店铺Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "分享活动id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @Audit
    @PutMapping("/shops/{shopId}/shareactivities/{id}/online")
    public Object putShareActivitiesOnline(
            @PathVariable(required = true) Long shopId,
            @PathVariable(required = true) Long id){
        ReturnObject retObj = shareActivityService.recoverShareActivity(shopId, id);
        return Common.getRetObject(retObj);
    };
}

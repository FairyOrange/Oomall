package cn.edu.xmu.oomall.address.controller;

import cn.edu.xmu.oomall.address.model.bo.Address;
import cn.edu.xmu.oomall.address.model.bo.Region;
import cn.edu.xmu.oomall.address.model.vo.AddressVo;
import cn.edu.xmu.oomall.address.model.vo.RegionVo;
import cn.edu.xmu.oomall.address.service.AddressService;
import cn.edu.xmu.oomall.annotation.*;
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
import java.time.LocalDateTime;

/**
 * 地址Controller
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/25 00:00
 */
@Slf4j
@Api(value = "地址服务", tags = {"address"})
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class AddressController {

    @Autowired
    AddressService addressService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 管理员在地区下新增子地区
     *
     * @author wwc
     * @date 2020/11/24 23:32
     * @version 1.0
     */
    @ApiOperation(value = "管理员在地区下新增子地区", tags = {"address"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "地区id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "RegionVo", name = "vo", value = "地区信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 602, message = "地区已废弃"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/shops/{shopId}/regions/{id}/subregions")
    @AutoLog(title = "管理员在地区下新增子地区", action = "POST")
    @Audit
    public Object postRegionsIdSubregions(
            @Depart @ApiIgnore @RequestParam(required = false) Long shopIdAudit,
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @RequestBody @Validated RegionVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        Region bo = new Region(vo.getName(), vo.getPostalCode());
        bo.setPid(id);
        bo.setState((byte) 0);
        bo.setGmtCreate(LocalDateTime.now());
        ReturnObject returnObject = addressService.insertRegion(bo);
        if (returnObject.getCode().equals(ResponseCode.OK)) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        if (returnObject.getCode().equals(ResponseCode.REGION_OBSOLETE)) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        return Common.getRetObject(returnObject);
    }

    @ApiOperation(value = "管理员修改某个地区", tags = {"address"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "地区id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "RegionVo", name = "vo", value = "地区信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{shopId}/regions/{id}")
    @AutoLog(title = "管理员修改某个地区", action = "PUT")
    @Audit
    public Object putRegionsId(
            @Depart @ApiIgnore @RequestParam(required = false) Long shopIdAudit,
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @RequestBody @Validated RegionVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        Region bo = new Region(vo.getName(), vo.getPostalCode());
        bo.setId(id);
        bo.setGmtModified(LocalDateTime.now());
        ReturnObject returnObject = addressService.updateRegion(bo);

        if (returnObject.getCode().equals(ResponseCode.REGION_OBSOLETE)) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }


        return Common.getRetObject(returnObject);
    }

    /**
     * 管理员让某个地区无效
     *
     * @author wwc
     * @date 2020/11/25 10:25
     * @version 1.0
     */
    @ApiOperation(value = "管理员让某个地区无效", tags = {"address"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "地区id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/shops/{shopId}/regions/{id}")
    @AutoLog(title = "管理员让某个地区无效", action = "DELETE")
    @Audit
    public Object deleteRegionsId(
            @Depart @ApiIgnore @RequestParam(required = false) Long shopIdAudit,
            @PathVariable("shopId") Long shopId,
            @PathVariable Long id) {
        ReturnObject returnObject = addressService.deleteRegion(id);

        if (returnObject.getCode().equals(ResponseCode.REGION_OBSOLETE)) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        return Common.getRetObject(returnObject);
    }

    /**
     * 查询某个地区的所有上级地区
     *
     * @author wwc
     * @date 2020/11/25 10:41
     * @version 1.0
     */
    @ApiOperation(value = "查询某个地区的所有上级地区", notes = "返回其所有父级地址", tags = {"address"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "该地区id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/region/{id}/ancestor")
    @AutoLog(title = "查询某个地区的所有上级地区", action = "GET")
    @Audit
    public Object getRegionIdAncestor(
            @PathVariable("id") Long id) {
        ReturnObject returnObject = addressService.listSelectAllParentRegion(id);

        if (returnObject.getCode().equals(ResponseCode.REGION_OBSOLETE)) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)){
            return Common.getRetObject(returnObject);
        }
        return Common.getListRetObject(returnObject);
    }

    /**
     * 买家新增地址
     *
     * @author wwc
     * @date 2020/11/25 14:44
     * @version 1.0
     */
    @ApiOperation(value = "买家新增地址", notes = "限制每个买家最多有20个地址", tags = {"address"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AddressVo", name = "vo", value = "地区信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 601, message = "达到地址簿上限"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/addresses")
    @AutoLog(title = "买家新增地址", action = "POST")
    @Audit
    public Object postAddresses(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @RequestBody @Validated AddressVo vo,
            BindingResult bindingResult) {

        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }

        Address bo = new Address(vo);
        bo.setCustomerId(userIdAudit);
        bo.setGmtCreate(LocalDateTime.now());
        bo.setBeDefault((byte) 0);
        ReturnObject returnObject = addressService.insertAddress(bo);

        if (returnObject.getCode().equals(ResponseCode.OK)) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }


        if (returnObject.getCode().equals(ResponseCode.REGION_OBSOLETE)) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }


        return Common.getRetObject(returnObject);
    }

    /**
     * 买家修改自己的地址信息
     *
     * @author wwc
     * @date 2020/11/25 15:23
     * @version 1.0
     */
    @ApiOperation(value = "买家修改自己的地址信息", tags = {"address"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "地址id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AddressVo", name = "vo", value = "可修改的地址信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/addresses/{id}")
    @AutoLog(title = "买家修改自己的地址信息", action = "PUT")
    @Audit
    public Object putAddressesId(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @PathVariable("id") Long id,
            @RequestBody @Validated AddressVo vo,
            BindingResult bindingResult) {

        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }

        Address bo = new Address(vo);
        bo.setId(id);
        bo.setCustomerId(userIdAudit);
        bo.setGmtModified(LocalDateTime.now());
        ReturnObject returnObject = addressService.updateAddress(bo);

        if (returnObject.getCode().equals(ResponseCode.REGION_OBSOLETE)) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        return Common.getRetObject(returnObject);
    }

    @ApiOperation(value = "买家删除地址", notes = "删除自己的地址", tags = {"address"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "地址id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/addresses/{id}")
    @AutoLog(title = "买家删除地址", action = "DELETE")
    @Audit
    public Object deleteAddressesId(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @PathVariable Long id) {
        ReturnObject returnObject = addressService.deleteAddress(userIdAudit, id);
        return Common.getRetObject(returnObject);
    }

    /**
     * 买家查询所有已有的地址信息
     *
     * @author wwc
     * @date 2020/11/25 15:52
     * @version 1.0
     */
    @ApiOperation(value = "买家查询所有已有的地址信息", tags = {"address"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/addresses")
    @AutoLog(title = "买家查询所有已有的地址信息", action = "GET")
    @JsonFieldFilter(type = Object.class, include = "", exclude = "gmtModified")
    @Audit
    public Object getAddresses(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        ReturnObject returnObject = addressService.listUserSelectAddress(userIdAudit, page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 买家设置默认地址
     *
     * @author wwc
     * @date 2020/11/25 16:01
     * @version 1.0
     */
    @ApiOperation(value = "买家设置默认地址", notes = "需将原有的默认地址改为普通地址", tags = {"address"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "地址id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/addresses/{id}/default")
    @AutoLog(title = "买家设置默认地址", action = "PUT")
    @Audit
    public Object putAddressesIdDefault(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @PathVariable("id") Long id) {
        ReturnObject returnObject = addressService.updateAddressToDefault(userIdAudit, id);
        return Common.getRetObject(returnObject);
    }

}

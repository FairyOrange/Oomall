package cn.edu.xmu.oomall.advertise.controller;

import cn.edu.xmu.oomall.annotation.*;
import cn.edu.xmu.oomall.util.ResponseUtil;
import cn.edu.xmu.oomall.advertise.model.bo.AdvertiseBo;
import cn.edu.xmu.oomall.advertise.model.vo.AdvertiseVo;
import cn.edu.xmu.oomall.advertise.service.AdvertiseService;
import cn.edu.xmu.oomall.util.*;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Api(value = "广告服务", tags = {"advertise"})
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class AdvertiseController {

    @Autowired
    private AdvertiseService advertiseService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 获得广告的所有状态
     *
     * @return 广告状态信息
     * @author cxr
     */
    @ApiOperation(value = "获得广告的所有状态", tags = {"advertise"})
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/advertisement/states")
    @AutoLog(title = "获得广告的所有状态", action = "GET")
    @JsonFieldFilter(type = Object.class, include = "", exclude = "")
    public Object getAdvertisementStates() {
        return ResponseUtil.ok(AdvertiseBo.State.getAllType());
    }

    ;

    /**
     * 管理员设置默认广告
     *
     * @param id 广告id
     * @return ReturnObject
     * @author cxr
     */
    @ApiOperation(value = "管理员设置默认广告", tags = {"advertise"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{did}/advertisement/{id}/default")
    @AutoLog(title = "管理员设置默认广告", action = "PUT")
    @Audit
    public Object putAdvertisementIdDefault(@PathVariable Long did, @PathVariable Integer id) {
        ReturnObject returnObject = advertiseService.putAdvertisementIdDefault(id.longValue());
        return Common.getRetObject(returnObject);
    }

    /**
     * 管理员修改广告内容
     *
     * @param did 管理员id
     * @param id 广告id
     * @param vo 修改内容
     * @return Object
     * @author cxr
     */
    @ApiOperation(value = "管理员修改广告内容", tags = {"advertise"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AdvertiseVo", name = "vo", value = "可修改的广告信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{did}/advertisement/{id}")
    @AutoLog(title = "管理员修改广告内容", action = "PUT")
    @Audit
    public Object putAdvertisementId(@PathVariable Integer did, @PathVariable Integer id,
                                     @RequestBody @Validated AdvertiseVo vo, BindingResult bindingResult) {
        try {
            Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
            if (object != null) {
                return object;
            }
            AdvertiseBo bo = new AdvertiseBo(vo);
            //如果用户填写了修改日期
            if(bo.getBeginDate().isAfter(bo.getEndDate())){
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return Common.getRetObject(new ReturnObject<>(ResponseCode.Log_Bigger));
            }
            bo.setId(id.longValue());
            bo.setGmtModified(LocalDateTime.now());
            ReturnObject returnObject = advertiseService.putAdvertisementId(bo);
            return Common.getRetObject(returnObject);
        } catch (Exception e) {
            return Common.getRetObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        }
    }

    /**
     * 管理员删除某一个广告
     *
     * @param id 广告id
     * @return Object
     * @author cxr
     */
    @ApiOperation(value = "管理员删除某一个广告", tags = {"advertise"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/shops/{did}/advertisement/{id}")
    @AutoLog(title = "管理员删除某一个广告", action = "DELETE")
    @Audit
    public Object deleteAdvertisementId(@PathVariable Integer did, @PathVariable Integer id) {
        ReturnObject returnObject = advertiseService.deleteAdvertisementId(id.longValue());
        return Common.getRetObject(returnObject);
    }

    /**
     * 获得当前时段广告列表
     *
     * @return 当前时段广告列表
     * @author cxr
     */
    @ApiOperation(value = "获取当前时段广告列表", notes = "无需登录", tags = {"advertise"})
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/advertisement/current")
    @AutoLog(title = "获取当前时段广告列表", action = "GET")
    public Object getAdvertisementCurrent() {
        ReturnObject returnObject = advertiseService.getAdvertisementCurrent();
        return Common.getListRetObject(returnObject);
    }

    ;

    /**
     * 管理员上传广告图片
     *
     * @param id  广告id
     * @param img 图片资源
     * @return ReturnObject
     * @author cxr
     */
    @ApiOperation(value = "管理员上传广告图片", notes = "如果该广告有图片，需修改该广告的图片，并删除图片文件", tags = {"advertise"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
            @ApiImplicitParam(paramType = "formData", dataType = "file", name = "img", value = "文件", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 506, message = "目录文件夹没有写入权限"),
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 508, message = "图片格式不正确"),
            @ApiResponse(code = 509, message = "图片大小超限")

    })
    @PostMapping("/shops/{did}/advertisement/{id}/uploadImg")
    @AutoLog(title = "管理员上传广告图片", action = "POST")
    @Audit
    public Object postAdvertisementIdUploadImg(@PathVariable Integer did, @PathVariable Integer id,
                                               @RequestPart(value = "img") MultipartFile img) {
        ReturnObject returnObject = advertiseService.uploadImg(id.longValue(), img);
        if(returnObject.getCode().equals(ResponseCode.OK)) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.getRetObject(returnObject);
    }

    ;

    /**
     * 管理员上架广告
     *
     * @param id 广告id
     * @return ReturnObject
     * @author cxr
     */
    @ApiOperation(value = "管理员上架广告", tags = {"advertise"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 608, message = "广告状态禁止"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{did}/advertisement/{id}/onshelves")
    @AutoLog(title = "管理员上架广告", action = "PUT")
    @Audit
    public Object putAdvertisementIdOnshelvess(@PathVariable Integer did, @PathVariable Integer id) {
        AdvertiseBo bo = new AdvertiseBo();
        bo.setId(id.longValue());
        bo.setGmtModified(LocalDateTime.now());
        //修改的前提是广告状态为下架态
        Byte needPreState = AdvertiseBo.State.OFFSHELF.getCode().byteValue();
        //上架成功，修改该广告的状态（下架->上架）
        bo.setState(AdvertiseBo.State.ONSHELF);
        ReturnObject returnObject = advertiseService.putAdvertisementIdState(bo, needPreState);
        return Common.getRetObject(returnObject);
    }

    ;

    /**
     * 管理员下架广告
     *
     * @param id 广告id
     * @return ReturnObject
     * @author cxr
     */
    @ApiOperation(value = "管理员下架广告", tags = {"advertise"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 608, message = "广告状态禁止"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{did}/advertisement/{id}/offshelves")
    @AutoLog(title = "管理员下架广告", action = "PUT")
    @Audit
    public Object putAdvertisementIdOffshelves(@PathVariable Integer did, @PathVariable Integer id) {
        AdvertiseBo bo = new AdvertiseBo();
        bo.setId(id.longValue());
        bo.setGmtModified(LocalDateTime.now());
        //修改的前提是广告状态为上架态
        Byte needPreState = AdvertiseBo.State.ONSHELF.getCode().byteValue();
        //上架成功，修改该广告的状态（上架->下架）
        bo.setState(AdvertiseBo.State.OFFSHELF);
        ReturnObject returnObject = advertiseService.putAdvertisementIdState(bo, needPreState);
        return Common.getRetObject(returnObject);
    }

    ;

    /**
     * 管理员审核广告
     *
     * @param id 广告id
     * @param vo 审核信息
     * @return Object
     * @author cxr
     */
    @ApiOperation(value = "管理员审核广告", tags = {"advertise"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "String", name = "vo", value = "可填写的广告信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 608, message = "广告状态禁止"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{did}/advertisement/{id}/audit")
    @AutoLog(title = "管理员审核广告", action = "PUT")
    @Audit
    public Object putAdvertisementIdAudit(@PathVariable Integer did, @PathVariable Integer id, @RequestBody String vo) {
        //把vo中的信息提取出来
        JSONObject jsonObject = JSONObject.parseObject(vo);
        String conclusion = jsonObject.getString("conclusion");
        String message = jsonObject.getString("message");

        AdvertiseBo bo = new AdvertiseBo();
        bo.setId(id.longValue());
        bo.setMessage(message);
        bo.setGmtModified(LocalDateTime.now());
        //修改的前提是广告状态为审核态
        Byte needPreState = AdvertiseBo.State.AUDIT.getCode().byteValue();
        //根据结论判断是否需要从审核态变为下架态
        if (conclusion == null || !(conclusion.equals("true") || conclusion.equals("false"))) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        } else {
            if (conclusion.equals("true")) {
                bo.setState(AdvertiseBo.State.OFFSHELF);
            } else {
                bo.setState(AdvertiseBo.State.AUDIT);
            }
        }
        ReturnObject returnObject = advertiseService.putAdvertisementIdState(bo, needPreState);
        return Common.getRetObject(returnObject);
    }

    ;


    /**
     * 管理员查看某一个广告时间段的广告
     *
     * @return 该时段下的广告列表
     * @author cxr
     */
    @ApiOperation(value = "管理员查看某一个广告时间段的广告", notes = "查询时可以选择按照时间段来查看,当id=0时为未定义时段的广告", tags = {"advertise"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "beginDate", value = "广告开始日期"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endDate", value = "广告结束日期"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/shops/{did}/timesegments/{id}/advertisement")
    @AutoLog(title = "管理员查看某一个广告时间段的广告", action = "GET")
    @JsonFieldFilter(type = Object.class, include = "", exclude = "")
    @Audit
    public Object getTimesegmentsIdAdvertisement(@PathVariable Long did, @PathVariable Integer id,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String beginDate,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String endDate,
                                                 @RequestParam(required = false, defaultValue = "1") Integer page,
                                                 @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        int a = 0;

        if (beginDate != null && endDate != null) {
            if (!dateLegality(beginDate) || !dateLegality(endDate) || LocalDate.parse(beginDate).isAfter(LocalDate.parse(endDate))) {
                ReturnObject returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return Common.getRetObject(returnObject);
            }
        } else if (beginDate != null) {
            if (!dateLegality(beginDate)) {
                ReturnObject returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return Common.getNullRetObj(returnObject,httpServletResponse);
            }
        } else if (endDate != null) {
            if (!dateLegality(endDate)) {
                ReturnObject returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return Common.getNullRetObj(returnObject,httpServletResponse);
            }
        }
        AdvertiseBo bo = new AdvertiseBo();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (beginDate != null) {
            bo.setBeginDate(LocalDate.parse(beginDate, df));
        }
        if (endDate != null) {
            bo.setEndDate(LocalDate.parse(endDate, df));
        }
        bo.setSegId(id.longValue());
        ReturnObject returnObject = advertiseService.getAdvertisementBySegId(bo, page, pageSize);
        if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)){
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.getNullRetObj(returnObject,httpServletResponse);
        }
        return Common.getPageRetObject(returnObject);
    }

    ;

    /**
     * 管理员在广告时间段下新建广告
     *
     * @param id 时段id
     * @param vo 可填写的广告信息
     * @return Object
     * @author cxr
     */
    @ApiOperation(value = "管理员在广告时段下新建广告", tags = {"advertise"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告时间段id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AdvertiseVo", name = "vo", value = "可填写的广告信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 603, message = "达到时段广告上限"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/shops/{did}/timesegments/{id}/advertisement")
    @AutoLog(title = "管理员在广告时段下新建广告", action = "Post")
    @Audit
    public Object postTimesegmentsIdAdvertisement(@PathVariable Integer did, @PathVariable Integer id,
                                                  @RequestBody @Validated AdvertiseVo vo, BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null){
            return object;
        }
        try {
            AdvertiseBo bo = new AdvertiseBo(vo);

            //判断广告日期格式以及内容是否合法
            String begin = bo.getBeginDate().toString();
            String end = bo.getEndDate().toString();
            if (Strings.isNullOrEmpty(begin) || Strings.isNullOrEmpty(end)
                    || !dateLegality(begin) || !dateLegality(end) || bo.getBeginDate().isAfter(bo.getEndDate())) {
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return Common.getRetObject(new ReturnObject<>(ResponseCode.Log_Bigger));
            }

            bo.setSegId(id.longValue());
            bo.setState(AdvertiseBo.State.AUDIT);
            bo.setGmtCreate(LocalDateTime.now());
            bo.setBeDefault((byte)0);
            ReturnObject returnObject = advertiseService.postTimesegmentsIdAdvertisement(bo);
            if(returnObject.getCode().equals(ResponseCode.OK)) {
                httpServletResponse.setStatus(HttpStatus.CREATED.value());
            }
            return Common.getRetObject(returnObject);
        } catch (Exception e) {
            return Common.getRetObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        }
    }

    ;

    /**
     * 管理员在广告时段下增加广告
     *
     * @param tid 广告时段id
     * @param id  广告id
     * @return ReturnObject
     * @author cxr
     */
    @ApiOperation(value = "管理员在广告时段下增加广告", notes = "若广告有时段则覆盖原时段", tags = {"advertise"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "did", value = "店id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "tid", value = "广告时间段id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "广告id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 603, message = "达到时段广告上限"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/shops/{did}/timesegments/{tid}/advertisement/{id}")
    @AutoLog(title = "管理员在广告时段下增加广告", action = "Post")
    @Audit
    public Object postTimesegmentsIdAdvertisementId(@PathVariable Integer did,
                                                    @PathVariable Integer tid, @PathVariable Integer id) {
        AdvertiseBo bo = new AdvertiseBo();
        bo.setId(id.longValue());
        bo.setGmtModified(LocalDateTime.now());
        bo.setSegId(tid.longValue());
        ReturnObject returnObject = advertiseService.postTimesegmentsIdAdvertisementId(bo);
        if(returnObject.getCode().equals(ResponseCode.OK)) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.getRetObject(returnObject);
    }

    public boolean dateLegality(String time) {
        String regex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(" +
                "0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})" +
                "(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))$";//正则表达式
        Pattern p = Pattern.compile(regex);
        Matcher m1 = p.matcher(time);
        return m1.matches();
    }
}


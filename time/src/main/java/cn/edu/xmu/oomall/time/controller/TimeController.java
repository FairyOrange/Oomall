package cn.edu.xmu.oomall.time.controller;

import cn.edu.xmu.oomall.annotation.Audit;
import cn.edu.xmu.oomall.annotation.AutoLog;
import cn.edu.xmu.oomall.annotation.Depart;
import cn.edu.xmu.oomall.goods.service.IFlashsaleService;
import cn.edu.xmu.oomall.other.service.IAdvertiseService;
import cn.edu.xmu.oomall.time.model.bo.Time;
import cn.edu.xmu.oomall.time.model.vo.TimeRetVo;
import cn.edu.xmu.oomall.time.service.TimeService;
import cn.edu.xmu.oomall.util.Common;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间段Controller
 *
 * @author wwc
 * @date 2020/11/25 00:00
 * @version 1.0
 */
@Slf4j
@Api(value = "时间服务", tags = { "time" })
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class TimeController {

    @Autowired
    private TimeService timeService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @DubboReference(check = false)
    private IAdvertiseService advertiseService;

    @DubboReference(check = false)
    private IFlashsaleService flashsaleService;

    /**
     * 管理员获取广告时间段列表
     *
     * @author cxr
     * @param page
     * @param pageSize
     * @return 广告时段列表
     * @date 2020/12/4 22:00
     * @version 2.0
     */
    @ApiOperation(value = "管理员获取广告时间段列表", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @AutoLog(title = "管理员获取广告时间段列表", action = "GET")
    //@Audit
    @GetMapping("/shops/{did}/advertisement/timesegments")
    public Object getAdvertisementTimesegments(
            @Depart @ApiIgnore @PathVariable("did") Long did,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Time bo = new Time();
        bo.setType(Time.Type.ADVERTISE);
        ReturnObject returnObject = timeService.listSelectTimesegment(bo, page, pageSize);
        return Common.getPageRetObject(returnObject);
    };

    /**
     * 平台管理员新增广告时间段
     *
     * @author cxr
     * @date 2020/12/4 21:40
     * @version 3.0
     */
    @ApiOperation(value = "平台管理员新增广告时间段", notes = "系统内部调用，或者平台管理员", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "string", name = "vo", value = "起止时间", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 604, message = "时段冲突"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/shops/{did}/advertisement/timesegments")
    @AutoLog(title = "平台管理员新增广告时间段", action = "POST")
    @Audit
    public Object postAdvertisementTimesegments(@PathVariable Integer did, @RequestBody String vo) {
        try {
            //解析json，获得beginTime和endTime的实际取值
            JSONObject jsonObject = JSONObject.parseObject(vo);
            String begin = jsonObject.getString("beginTime");
            String end = jsonObject.getString("endTime");

            //利用正则表达式判断时间格式是否符合要求
            String regex="^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(" +
                    "0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})" +
                    "(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+" +
                    "([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";//正则表达式
            Pattern p=Pattern.compile(regex);
            Matcher m1=p.matcher(begin);
            Matcher m2=p.matcher(end);
            if (Strings.isNullOrEmpty(begin) || Strings.isNullOrEmpty(end) || !m1.matches() || !m2.matches()) {
                return Common.getRetObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
            }

            //构建时段对象
            Time bo = new Time(Timestamp.valueOf(begin).toLocalDateTime(), Timestamp.valueOf(end).toLocalDateTime());
            //开始时间必须早于结束时间，所以这里进行了先判断开始时间是否早于结束时间，若false，需要报错
            if(!bo.getBeginTime().isBefore(bo.getEndTime())){
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return Common.getRetObject(new ReturnObject<>(ResponseCode.Log_Bigger));
            }
            //设置插入时段类型为广告时间段
            bo.setType(Time.Type.ADVERTISE);
            bo.setGmtCreate(LocalDateTime.now());

            ReturnObject returnObject = timeService.insertTimesegment(bo);
            if(returnObject.getCode().equals(ResponseCode.OK)) {
                httpServletResponse.setStatus(HttpStatus.CREATED.value());
            }
            return Common.getRetObject(returnObject);
        } catch (Exception e) {
            return Common.getRetObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("body格式错误")));
        }
    };

    /**
     * 平台管理员新增秒杀时间段
     *
     * @author cxr
     * @date 2020/12/4 21:40
     * @version 3.0
     */
    @ApiOperation(value = "平台管理员新增秒杀时间段", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "Object", name = "vo", value = "起止时间", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 604, message = "时段冲突"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/shops/{did}/flashsale/timesegments")
    @AutoLog(title = "平台管理员新增秒杀时间段", action = "POST")
    @Audit
    public Object postFlashsaleTimesegments(@PathVariable Integer did, @RequestBody String vo) {
        try {
            //解析json，获得beginTime和endTime的实际取值
            JSONObject jsonObject = JSONObject.parseObject(vo);
            String begin = jsonObject.getString("beginTime");
            String end = jsonObject.getString("endTime");

            //利用正则表达式判断时间格式是否符合要求
            String regex="^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(" +
                    "0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})" +
                    "(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+" +
                    "([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";//正则表达式
            Pattern p=Pattern.compile(regex);
            Matcher m1=p.matcher(begin);
            Matcher m2=p.matcher(end);
            if (Strings.isNullOrEmpty(begin) || Strings.isNullOrEmpty(end) || !m1.matches() || !m2.matches()) {
                return Common.getRetObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
            }

            //构建时段对象
            Time bo = new Time(Timestamp.valueOf(begin).toLocalDateTime(), Timestamp.valueOf(end).toLocalDateTime());
            //开始时间必须早于结束时间，所以这里进行了先判断开始时间是否早于结束时间，若false，需要报错
            if(!bo.getBeginTime().isBefore(bo.getEndTime())){
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return Common.getRetObject(new ReturnObject<>(ResponseCode.Log_Bigger));
            }
            //设置插入时段类型为秒杀时间段
            bo.setType(Time.Type.FLASHSALE);
            bo.setGmtCreate(LocalDateTime.now());

            ReturnObject returnObject = timeService.insertTimesegment(bo);
            if(returnObject.getCode().equals(ResponseCode.OK)) {
                httpServletResponse.setStatus(HttpStatus.CREATED.value());
            }
            return Common.getRetObject(new ReturnObject<>(bo));
        } catch (Exception e) {
            return Common.getRetObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        }
    };

    /**
     * 管理员获取秒杀时间段列表
     * @author cxr
     * @param page 页
     * @param pageSize 页大小
     * @return 秒杀时段列表
     */
    @ApiOperation(value = "管理员获取秒杀时间段列表", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/shops/{did}/flashsale/timesegments")
    @AutoLog(title = "管理员获取秒杀时间段列表", action = "GET")
    @Audit
    public Object getFlashsaleTimesegments(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Time bo = new Time();
        bo.setType(Time.Type.FLASHSALE);
        ReturnObject returnObject = timeService.listSelectTimesegment(bo, page, pageSize);
        return Common.getPageRetObject(returnObject);
    };

    /**
     * 平台管理员删除秒杀时间段
     *
     * @author wwc
     * @date 2020/11/25 22:12
     * @version 1.0
     */
    @ApiOperation(value = "平台管理员删除秒杀时间段", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "时段id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/shops/{did}/flashsale/timesegments/{id}")
    @AutoLog(title = "平台管理员删除秒杀时间段", action = "DELETE")
    @Audit
    public Object deleteFlashsaleTimesegmentsId(@PathVariable Integer did, @PathVariable("id") Long id) {
        Time bo = new Time();
        bo.setId(id);
        bo.setType(Time.Type.FLASHSALE);
        ReturnObject returnObject = timeService.deleteTimesegment(bo);
        //删除时段下的秒杀
        if(returnObject.getCode().equals(ResponseCode.OK)){
            flashsaleService.deleteSegmentFlashsale(id);
        }
        return Common.getRetObject(returnObject);
    };

    /**
     * 平台管理员删除广告时间段
     *
     * @author wwc
     * @date 2020/11/25 22:12
     * @version 1.0
     */
    @ApiOperation(value = "平台管理员删除广告时间段", tags={ "time" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "时段id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @DeleteMapping("/shops/{did}/advertisement/timesegments/{id}")
    @AutoLog(title = "平台管理员删除广告时间段", action = "DELETE")
    @Audit
    public Object deleteAdvertisementTimesegmentsId(@PathVariable Integer did, @PathVariable("id") Long id) {
        try {
            Time bo = new Time();
            bo.setId(id);
            bo.setType(Time.Type.ADVERTISE);
            ReturnObject returnObject = timeService.deleteTimesegment(bo);
            //删除时段下的广告
            if (returnObject.getCode().equals(ResponseCode.OK)) {
                advertiseService.deleteTimeSegmentAdvertisements(id);
            }
            return Common.getRetObject(returnObject);
        }catch (Exception e){
            log.info(e.getMessage());
            return Common.getRetObject(new ReturnObject<>());
        }
    };

}
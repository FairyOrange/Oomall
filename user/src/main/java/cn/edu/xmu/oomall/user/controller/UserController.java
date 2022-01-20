package cn.edu.xmu.oomall.user.controller;

import cn.edu.xmu.oomall.annotation.Audit;
import cn.edu.xmu.oomall.annotation.AutoLog;
import cn.edu.xmu.oomall.annotation.Depart;
import cn.edu.xmu.oomall.annotation.LoginUser;
import cn.edu.xmu.oomall.user.model.bo.User;
import cn.edu.xmu.oomall.user.model.vo.*;
import cn.edu.xmu.oomall.user.service.UserService;
import cn.edu.xmu.oomall.util.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Api(value = "用户服务", tags = { "user" })
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * @return java.lang.Object
     * @title getUsersState
     * @description 获得买家的所有状态
     * @author wwc
     * @date 2020/12/02 20:43
     */
    @ApiOperation(value = "获得买家的所有状态", tags = {"user"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/users/states")
    @AutoLog(title = "获得买家的所有状态", action = "GET")
    @Audit
    public Object getUsersState() {
        return ResponseUtil.ok(User.State.getAllType());
    }


    /**
     * @title postUsers
     * @description 注册用户
     * @author wwc
     * @param vo
     * @return java.lang.Object
     * @date 2020/12/01 20:43
     */
    @ApiOperation(value = "注册用户", notes = "无需登录", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "UserVo", name = "vo", value = "可填写的用户信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 731, message = "用户名已被注册"),
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 733, message = "电话已被注册"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/users")
    @AutoLog(title = "注册用户", action = "POST")
    public Object postUsers(
            @RequestBody @Validated UserVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null){
            return object;
        }

        User bo = new User(vo);
        bo.setBeDeleted((byte) 0);
        bo.setState(User.State.NORMAL);
        bo.setGmtCreate(LocalDateTime.now());
        ReturnObject returnObject = userService.insertUser(bo);
        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.getRetObject(returnObject);
    }


    /**
     * @return java.lang.Object
     * @title getUsers
     * @description 买家查看自己信息
     * @author wwc
     * @date 2020/12/02 20:43
     */
    @ApiOperation(value = "买家查看自己信息", tags = {"user"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/users")
    @AutoLog(title = "买家查看自己信息", action = "GET")
    @Audit
    public Object getUsers(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit) {
        ReturnObject returnObject = userService.getUserSelectInfo(userIdAudit);
        return Common.getRetObject(returnObject);
    }


    /**
     * @return java.lang.Object
     * @title putUsers
     * @description 买家修改自己的信息
     * @author wwc
     * @date 2020/12/02 20:43
     */
    @ApiOperation(value = "买家修改自己的信息", tags = {"user"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "UserModifyInfoVo", name = "vo", value = "可修改的用户信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/users")
    @AutoLog(title = "买家修改自己的信息", action = "PUT")
    @Audit
    public Object putUsers(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long userIdAudit,
            @RequestBody @Validated UserModifyInfoVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        User bo = new User(vo);
        bo.setId(userIdAudit);
        bo.setGmtModified(LocalDateTime.now());
        ReturnObject returnObject = userService.updateUserInfo(bo);
        return Common.getRetObject(returnObject);
    }

    /**
     * @title putUsersPassword
     * @description 用户修改密码
     * @author wwc
     * @param vo
     * @return java.lang.Object
     * @date 2020/12/01 20:43
     */
    @ApiOperation(value = "用户修改密码", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "ModifyPasswordVo", name = "vo", value = "新旧密码", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 741, message = "不能与旧密码相同"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/users/password")
    @AutoLog(title = "用户修改密码", action = "PUT")
    public Object putUsersPassword(
            @RequestBody @Validated ModifyPasswordVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        ReturnObject returnObject = userService.updateUserPwd(vo.getNewPassword(), vo.getCaptcha());
        return Common.getRetObject(returnObject);
    }

    /**
     * @title putUsersPasswordReset
     * @description 用户重置密码
     * @author wwc
     * @param vo
     * @return java.lang.Object
     * @date 2020/12/01 20:43
     */
    @ApiOperation(value = "用户重置密码", notes = "向用户邮箱发送随机密码", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "ResetPasswordVo", name = "vo", value = "邮箱和电话", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 745, message = "与系统预留的邮箱不一致"),
            @ApiResponse(code = 746, message = "与系统预留的电话不一致"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/users/password/reset")
    @AutoLog(title = "用户重置密码", action = "PUT")
    public Object putUsersPasswordReset(
            @RequestBody @Validated ResetPasswordVo vo,
            BindingResult bindingResult,
            HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null){
            return object;
        }
        String ip = IpUtil.getIpAddr(httpServletRequest);
        ReturnObject returnObject = userService.updateUserResetPwd(vo.getUserName(), vo.getEmail(), ip);
        return Common.getRetObject(returnObject);
    }

    /**
     * @return java.lang.Object
     * @title putUsersPassword
     * @description 平台管理员获取所有用户列表
     * @author wwc
     * @date 2020/12/01 20:43
     */
    @ApiOperation(value = "平台管理员获取所有用户列表", tags = {"user"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "userName", value = "用户名", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "email", value = "邮箱", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "mobile", value = "电话号码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/users/all")
    @AutoLog(title = "平台管理员获取所有用户列表", action = "GET")
    public Object getUsersAll(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        User bo = new User();
        bo.setUserName(userName);
        bo.setEmail(email);
        bo.setMobile(mobile);
        ReturnObject returnObject = userService.listAdminSelectAllUser(bo, page, pageSize);
        return Common.getPageRetObject(returnObject);
    }


    /**
     * @title postUsersLogin
     * @description 用户名密码登录
     * @author wwc
     * @param vo
     * @return java.lang.Object
     * @date 2020/12/01 20:43
     */
    @ApiOperation(value = "用户名密码登录", notes = "其中740错误是因为重置密码后，密码不符合规定造成的。前端应该让用户修改密码。", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "LoginVo", name = "vo", value = "用户名和密码", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 702, message = "用户被禁止登录"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/users/login")
    @AutoLog(title = "用户名密码登录", action = "POST")
    public Object postUsersLogin(
            @RequestBody @Validated LoginVo vo,
            BindingResult bindingResult) {
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null){
            return object;
        }
        ReturnObject returnObject = userService.userLogin(vo.getUserName(), vo.getPassword());

        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return ResponseUtil.ok(returnObject.getData());
        } else {
            return Common.getRetObject(returnObject);
        }
    }

    /**
     * @title getUserLogout
     * @description 用户登出
     * @author wwc
     * @param userIdAudit
     * @return java.lang.Object
     * @date 2020/12/01 20:43
     */
    @ApiOperation(value = "用户登出", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/users/logout")
    @AutoLog(title = "用户登出", action = "GET")
    @Audit
    public Object getUserLogout(
            @LoginUser @ApiIgnore Long userIdAudit,
            HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("authorization");
        return Common.getRetObject(userService.userLogout(token));
    }


    /**
     * @return java.lang.Object
     * @title getUsersId
     * @description 管理员查看任意买家信息
     * @author wwc
     * @date 2020/12/02 20:43
     */
    @ApiOperation(value = "管理员查看任意买家信息", tags = {"user"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "用户id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/users/{id}")
    @AutoLog(title = "管理员查看任意买家信息", action = "GET")
    @Audit
    public Object getUsersId(
            @PathVariable("id") Long id) {
        ReturnObject returnObject = userService.getAdminSelectInfo(id);
        return Common.getRetObject(returnObject);
    }

    /**
     * @return java.lang.Object
     * @title putUsersIdBan
     * @description 平台管理员封禁买家
     * @author wwc
     * @date 2020/12/02 20:43
     */
    @ApiOperation(value = "平台管理员封禁买家", tags = {"user"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "用户id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{did}/users/{id}/ban")
    @AutoLog(title = "平台管理员封禁买家", action = "PUT")
    @Audit
    public Object putUsersIdBan(
            @PathVariable("did") Long did,
            @PathVariable("id") Long id) {
        if (did.equals(0)) {
            ReturnObject returnObject = userService.updateUserToBan(id);
            return Common.getRetObject(returnObject);
        } else {
            return Common.getRetObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
    }

    /**
     * @param id
     * @return java.lang.Object
     * @title postUsers
     * @description 平台管理员解禁买家
     * @author wwc
     * @date 2020/12/01 20:43
     */
    @ApiOperation(value = "平台管理员解禁买家", tags = {"user"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "string", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "用户id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/shops/{did}/users/{id}/release")
    @AutoLog(title = "平台管理员解禁买家", action = "PUT")
    @Audit
    public Object putUsersIdRelease(
            @PathVariable("did") Long did,
            @PathVariable("id") Long id) {
        if (did.equals(0)) {
            ReturnObject returnObject = userService.updateUserToNormal(id);
            return Common.getRetObject(returnObject);
        } else {
            return Common.getRetObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW));
        }
    }

}

package cn.edu.xmu.oomall.other.controller;

import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/other", produces = "application/json;charset=UTF-8")
public class UserController {

    @ApiOperation(value = "获得买家的所有状态", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/users/states")
    public Object getUsersState(){
        return null;
    };

    @ApiOperation(value = "平台管理员封禁买家", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "用户id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/users/{id}/ban")
    public Object putUsersIdBan(@PathVariable Integer id){
      return null;
    };

    @ApiOperation(value = "买家修改自己的信息", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "可修改的用户信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/users")
    public Object putUsers(@RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "用户修改密码", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "新旧密码", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 741, message = "不能与旧密码相同"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/users/password")
    public Object putUsersPassword(@RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "平台管理员获取所有用户列表", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "userName", value = "用户名", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "email", value = "邮箱", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "mobile", value = "电话号码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/users/all")
    public Object getUsersAll(@RequestParam String userName, @RequestParam String email, @RequestParam String mobile, @RequestParam Integer page, @RequestParam Integer pageSize){
        return null;
    };

    @ApiOperation(value = "买家查看自己信息", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/users")
    public Object getUsers(){
        return null;
    };

    @ApiOperation(value = "管理员查看任意买家信息", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "用户id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/users/{id}")
    public Object getUsersId(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "用户名密码登录", notes = "其中740错误是因为重置密码后，密码不符合规定造成的。前端应该让用户修改密码。", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "用户名和密码", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 702, message = "用户被禁止登录"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/users/login")
    public Object postUsersLogin(@RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "用户登出", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @GetMapping("/users/logout")
    public Object getUserLogout(){
        return null;
    };

    @ApiOperation(value = "注册用户", notes = "无需登录", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "可填写的用户信息", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 731, message = "用户名已被注册"),
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 733, message = "电话已被注册"),
            @ApiResponse(code = 200, message = ""),
    })
    @PostMapping("/users")
    public Object postUsers(@RequestBody @Validated Object vo){
        return null;
    };

    @ApiOperation(value = "平台管理员解禁买家", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "用户id", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/users/{id}/release")
    public Object putUsersIdRelease(@PathVariable Integer id){
        return null;
    };

    @ApiOperation(value = "用户重置密码", notes = "向用户邮箱发送随机密码", tags={ "user" })
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "VoObject", name = "vo", value = "邮箱和电话", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 745, message = "与系统预留的邮箱不一致"),
            @ApiResponse(code = 746, message = "与系统预留的电话不一致"),
            @ApiResponse(code = 200, message = ""),
    })
    @PutMapping("/users/password/reset")
    public Object putUsersPasswordReset(@RequestBody @Validated Object vo){
        return null;
    };

}

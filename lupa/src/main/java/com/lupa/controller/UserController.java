package com.lupa.controller;


import com.lupa.entity.User;
import com.lupa.mapper.UserMapper;
import com.lupa.service.UserService;
import com.lupa.utils.response.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    /*查询用户列表*/
    @PostMapping("userList")
    public CommonResult userList(User user){
        List<User> userList = userService.userList(user);
        return CommonResult.success(userList,"查询用户列表成功");
    }

    /*添加用户*/
    @PostMapping("add")
    public CommonResult registerUser(User user){
        userService.registerUser(user);
        return CommonResult.success("注册成功");
    }

    /*用户登录*/
    @PostMapping("login")
    public CommonResult login(User user) {
        //将接口转到服务器里面
        userService.login(user);
        Map userMap = userService.login(user);
        return CommonResult.success(userMap, "登录成功");
    }

    /*修改密码*/
    @PostMapping("updatauser")
    public CommonResult modifyUser(User user) {
        userService.modifyUser(user);
        return CommonResult.success("修改成功");
    }

    /*删除用户*/
    @PostMapping("deluser")
    public CommonResult deluser(User user){
        userService.delUser(user);
        return CommonResult.success("注销成功");
    }

    /*查看用户信息*/
    @PostMapping("setUserinfo")
    public CommonResult setUserinfo(User user){
        List<User> setUserinfo = userService.setUserinfo(user);
        return CommonResult.success(setUserinfo,"查询成功");
    }

}


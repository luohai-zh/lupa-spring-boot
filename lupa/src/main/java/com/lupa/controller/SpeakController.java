package com.lupa.controller;


import com.lupa.entity.Speak;
import com.lupa.entity.User;
import com.lupa.mapper.SpeakMapper;
import com.lupa.mapper.UserMapper;
import com.lupa.service.SpeakService;
import com.lupa.service.UserService;
import com.lupa.utils.response.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户消息表 前端控制器
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/speak")
public class SpeakController {

    @Autowired
    private SpeakMapper speakMapper;

    @Autowired
    private SpeakService speakService;
    @Autowired
    private UserService userService;

    /*查询用户消息列表*/
    @PostMapping("speakList")
    public CommonResult speakList(Speak speak) {
        List<Speak> speakList = speakService.speakList(speak);
        return CommonResult.success(speakList, "查询用户消息列表成功");
    }

    /*添加用户消息*/
    @PostMapping("addUserinfo")
    public CommonResult addUserinfo(Speak speak, User user) {
        userService.userList(user);
        speakService.addUserinfo(speak, user);
        return CommonResult.success("添加成功");
    }

    /*删除用户消息*/
    @PostMapping("delUserinfo")
    public CommonResult delUserinfo(Speak speak) {
        speakService.delUserinfo(speak);
        return CommonResult.success("删除操作成功");
    }
}


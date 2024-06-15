package com.lupa.controller;


import com.lupa.entity.Device;
import com.lupa.entity.User;
import com.lupa.service.DeviceService;
import com.lupa.service.UserService;
import com.lupa.utils.response.CommonResult;
import com.my.netty.core.service.AbsDataService;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 设备信息表 前端控制器
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private UserService userService;

    /*查看设备列表*/
    @PostMapping("deviceList")
    public CommonResult deviceList(Device device) {
        List<Device> deviceList = deviceService.deviceList(device);  //定义一个List接收服务器返回的查询列表
        return CommonResult.success(deviceList, "查询列表成功");
    }

    /*添加设备*/
    @PostMapping("adddevice")
    public CommonResult adddevice(Device device, User user) {
        userService.userList(user);
        deviceService.adddevice(device,user);
        return CommonResult.success("添加成功");
    }

    /*删除设备*/
    @PostMapping("deldevice")
    public CommonResult deldevice(Device device) {
        deviceService.deldevice(device);
        return CommonResult.success("删除成功");
    }

    /*修改设备*/
    @PostMapping("updatedevice")
    public CommonResult updatedevice(Device device) {
        deviceService.updatedevice(device);
        return CommonResult.success("修改成功");
    }

    /*查看设备*/
    @PostMapping("seldevice")
    public CommonResult seldevice(Device device) {
        List<Device> seldevice = deviceService.seldevice(device);
        return CommonResult.success(seldevice, "查询成功");
    }

    /*删除所有设备*/
    @PostMapping("delalldevice")
    public CommonResult delalldevice(Device device) {
        deviceService.delalldevice(device);
        return CommonResult.success("删除成功");
    }

    /*下发设备*/
    @PostMapping("devicecontrol")
    @CrossOrigin
    public  CommonResult devicecontrol(String deviceid,String message){
        AbsDataService absDataService = new AbsDataService() {
            @Override //从父类或接口中继承或实现
            public String inputListening(Channel channel, String topic, String inputMessage) {
                return null;
            }
        } ;
        absDataService.pushTopicAll(message,"sys/"+deviceid+"/control");
        return CommonResult.success("发送成功");
    }



}


package com.lupa.service;

import com.lupa.entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lupa.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 设备信息表 服务类
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
public interface DeviceService extends IService<Device> {

    /*查询设备列表*/
    @Transactional(rollbackFor = Exception.class)
    List<Device> deviceList(Device device);

    /*添加设备*/
    @Transactional(rollbackFor = Exception.class)
    void adddevice(Device device, User user);

    /*删除设备*/
    @Transactional(rollbackFor = Exception.class)
    void deldevice(Device device);

    /*修改设备*/
    @Transactional(rollbackFor = Exception.class)
    void updatedevice(Device device);

    /*查看设备信息*/
    @Transactional(rollbackFor = Exception.class)
    List<Device> seldevice(Device device);

    /*删除所有设备*/
    @Transactional(rollbackFor = Exception.class)
    void delalldevice(Device device);
}

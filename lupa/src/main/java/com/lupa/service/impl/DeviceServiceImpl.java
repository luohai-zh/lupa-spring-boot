package com.lupa.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lupa.entity.Device;
import com.lupa.entity.User;
import com.lupa.mapper.DeviceMapper;
import com.lupa.mapper.UserMapper;
import com.lupa.service.DeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lupa.utils.exception.BusinessException;
import com.lupa.utils.exception.ExceptionCodeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 设备信息表 服务实现类
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {
    @Autowired
    public DeviceMapper deviceMapper;
    @Autowired
    public UserMapper userMapper;

    /*查询设备列表*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Device> deviceList(Device device) {
        List<Device> delalldevice = deviceMapper.selectList(new QueryWrapper<Device>().eq("userid", device.getUserid()));//查询数据库中用没用数据
        if (delalldevice.isEmpty()) { //当返回的list是空值
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        } else {
            return deviceMapper.selectList(new QueryWrapper<Device>().eq("userid", device.getUserid()));  //定义一个查询条件,查询userid相同的所有设备信息
        }

    }

    /*添加设备*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adddevice(Device device, User user) {

//        Device seldevice = deviceMapper.selectOne(new QueryWrapper<Device>().eq("userid",device.getUserid()).eq("devicename",device.getDevicename()));
//        if (seldevice  == null) {
//            throw new BusinessException(ExceptionCodeEnum.USER_IS_EXIST);
//        }  避免重复添加
        List<User> seluserid = userMapper.selectList(new QueryWrapper<User>().eq("userid", user.getUserid()));//查询数据库中有没有对应用户ID的数据
        User username = userMapper.selectOne(new QueryWrapper<User>().eq("userid", user.getUserid()));
        if (seluserid.isEmpty()) { //当返回的list是空值
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        } else {
            Device mydevice = new Device();
            mydevice.setUserid(username.getUserid());
            mydevice.setDeviceid(RandomUtil.randomString(6));
            mydevice.setDeviceidsecretkey(SecureUtil.md5(RandomUtil.randomString(10)));
            mydevice.setDevicename(device.getDevicename());
            mydevice.setDeviceremarks(device.getDeviceremarks());
            if (device.getDevicename() == null || device.getDevicename().isEmpty()) {
                throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_DEVICENAME);
            } else {
                deviceMapper.insert(mydevice);
            }
        }
    }

    /*删除设备*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deldevice(Device device) {
        int deleteres = deviceMapper.delete(new QueryWrapper<Device>().eq("deviceid", device.getDeviceid()));//指定设备ID
        if (deleteres == 0) {
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MY);
        }
    }

    /*修改设备*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatedevice(Device device) {
        String deviceName = device.getDevicename();
        if (deviceName == null || deviceName.isEmpty()) {
            throw new BusinessException(ExceptionCodeEnum.PARAM_IS_BLANK);
        } else {
            Device mydevice = new Device();
            BeanUtils.copyProperties(device, mydevice);
            int updateres = deviceMapper.update(mydevice, new UpdateWrapper<Device>().eq("deviceid", mydevice.getDeviceid()));//添加查询条件,返回一条数据
            if (updateres == 0) {//如果查询到的结果为0,则提示以下报错信息
                throw new BusinessException((ExceptionCodeEnum.WEIXINLOGIN_ERROR_MY));
            }
        }
    }

    /*查看设备信息*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Device> seldevice(Device device) {
        List<Device> deviceIDlist = deviceMapper.selectList(new QueryWrapper<Device>().eq("deviceid", device.getDeviceid()));
        if (deviceIDlist.isEmpty()) {
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_DEVICE);
        }
        return deviceMapper.selectList(new QueryWrapper<Device>().eq("deviceid", device.getDeviceid()));  //定义一个查询条件,只查询deviceid设备信息
    }

    /*删除所有设备*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delalldevice(Device device) {
        List<Device> delalldevice = deviceMapper.selectList(null);//查询数据库中用没用数据
        if (delalldevice.isEmpty()) { //当返回的list是空值
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        }else {
            deviceMapper.delete(new QueryWrapper<Device>());
        }
    }

}

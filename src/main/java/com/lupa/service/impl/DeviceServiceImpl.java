package com.lupa.service.impl;

import com.lupa.entity.Device;
import com.lupa.mapper.DeviceMapper;
import com.lupa.service.DeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备信息表 服务实现类
 * </p>
 *
 * @author lupa
 * @since 2024-03-29
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

}

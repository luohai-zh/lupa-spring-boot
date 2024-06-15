package com.lupa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.lupa.entity.Newdata;
import com.lupa.mapper.NewdataMapper;
import com.lupa.service.NewdataService;
import com.lupa.utils.exception.BusinessException;
import com.lupa.utils.exception.ExceptionCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 实时数据表 服务实现类
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@Service
public class NewdataServiceImpl extends MppServiceImpl<NewdataMapper, Newdata> implements NewdataService {
    @Autowired
    public NewdataMapper newdataMapper;

    /*查询设备的实时数据列表*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Newdata> newList(Newdata newdata) {
        List<Newdata> delalldevicenew = newdataMapper.selectList(new QueryWrapper<Newdata>().eq("deviceid", newdata.getDeviceid()));//查询数据库中用没用数据
        if (delalldevicenew.isEmpty()) { //当返回的list是空值
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        } else {
            return newdataMapper.selectList(new QueryWrapper<Newdata>().eq("deviceid", newdata.getDeviceid()));
        }
    }

    /*删除实时数据*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delnewdata(Newdata newdata) {
        int selnewdata = newdataMapper.delete(new QueryWrapper<Newdata>().eq("deviceid", newdata.getDeviceid()));
        if (selnewdata == 0) {
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        }
    }
}

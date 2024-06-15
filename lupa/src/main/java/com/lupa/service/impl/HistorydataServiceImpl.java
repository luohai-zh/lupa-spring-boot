package com.lupa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.lupa.entity.Historydata;
import com.lupa.mapper.HistorydataMapper;
import com.lupa.service.HistorydataService;
import com.lupa.utils.exception.BusinessException;
import com.lupa.utils.exception.ExceptionCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 历史数据表 服务实现类
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@Service
public class HistorydataServiceImpl extends MppServiceImpl<HistorydataMapper, Historydata> implements HistorydataService {

    @Autowired
    public HistorydataMapper historydataMapper;

    /*查看历史数据*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Historydata> selhistorylist(Historydata historydata) {
        List<Historydata> delalldevice = historydataMapper.selectList(new QueryWrapper<Historydata>().eq("deviceid", historydata.getDeviceid()));//查询数据库中用没用数据
        if (delalldevice.isEmpty()) { //当返回的list是空值
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        } else {
            return historydataMapper.selectList(new QueryWrapper<Historydata>().eq("deviceid", historydata.getDeviceid()));
        }
    }


    /*删除该设备的所有历史数据*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delhiatory(Historydata historydata) {
        List<Historydata> historyDataList = historydataMapper.selectList(null);//查询数据库中用没用数据
        if (historyDataList.isEmpty()) { //当返回的list是空值
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        }
        historydataMapper.delete(new QueryWrapper<Historydata>().eq("deviceid", historydata.getDeviceid()));
    }

   /*根据ID删除历史数据*/
   @Override
   @Transactional(rollbackFor = Exception.class)
   public void delhiatoryID(Historydata historydata){
       List<Historydata> historyDataList = historydataMapper.selectList(new QueryWrapper<Historydata>().eq("id",historydata.getId()));//查询数据库中用没用数据
       if (historyDataList.isEmpty()) { //当返回的list是空值
           throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
       }
       historydataMapper.delete(new QueryWrapper<Historydata>().eq("id", historydata.getId()));
   }

    /*删除所有历史数据*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delallhiatory(Historydata historydata) {
        List<Historydata> historyDataList = historydataMapper.selectList(null);//查询数据库中用没用数据
        if (historyDataList.isEmpty()) { //当返回的list是空值
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        }
        historydataMapper.delete(new QueryWrapper<Historydata>());
    }
}

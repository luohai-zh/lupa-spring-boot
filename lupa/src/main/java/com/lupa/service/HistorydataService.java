package com.lupa.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.lupa.entity.Historydata;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 历史数据表 服务类
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
public interface HistorydataService extends IMppService<Historydata> {

    /*查看历史数据*/
    @Transactional(rollbackFor = Exception.class)
    List<Historydata> selhistorylist(Historydata historydata);

    /*删除该设备的所有历史数据*/
    @Transactional(rollbackFor = Exception.class)
    void delhiatory(Historydata historyData);

    /*根据ID删除历史数据*/
    @Transactional(rollbackFor = Exception.class)
    void delhiatoryID(Historydata historydata);

    /*删除所有历史数据*/
    @Transactional(rollbackFor = Exception.class)
    void delallhiatory(Historydata historyData);
}

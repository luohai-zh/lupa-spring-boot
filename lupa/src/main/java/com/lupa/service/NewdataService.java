package com.lupa.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.lupa.entity.Newdata;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 实时数据表 服务类
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
public interface NewdataService extends IMppService<Newdata> {

    /*查询设备的实时数据列表*/
    @Transactional(rollbackFor = Exception.class)
    List<Newdata> newList(Newdata newdata);

    /*删除实时数据*/
    @Transactional(rollbackFor = Exception.class)
    void delnewdata(Newdata newdata);
}

package com.lupa.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lupa.entity.Historydata;
import com.lupa.mapper.HistorydataMapper;
import com.lupa.service.HistorydataService;
import com.lupa.utils.exception.BusinessException;
import com.lupa.utils.exception.ExceptionCodeEnum;
import com.lupa.utils.response.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 历史数据表 前端控制器
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/historydata")
public class HistorydataController {
    @Autowired
    private HistorydataMapper historydataMapper;
    @Autowired
    private HistorydataService historydataService;

    /*查看历史数据*/
    @PostMapping("historylist")
    public CommonResult historylist(Historydata historydata) {
        List<Historydata> selhistorylist = historydataService.selhistorylist(historydata);
        return CommonResult.success(selhistorylist, "查询成功");
    }

    /*删除该设备的所有历史数据*/
    @PostMapping("delhiatory")
    public CommonResult delhiatory(Historydata historydata) {
        historydataService.delhiatory(historydata);
        return CommonResult.success("当前设备的历史数据全部删除成功");
    }

    /*删除该设备的所有历史数据*/
    @PostMapping("delhiatoryID")
    public CommonResult delhiatoryID(Historydata historydata) {
        historydataService.delhiatoryID(historydata);
        return CommonResult.success("当条数据已经被删除");
    }

    /*删除所有历史数据*/
    @PostMapping("delallhiatory")
    public CommonResult delallhiatory(Historydata historyData){
        historydataService.delallhiatory(historyData);
        return CommonResult.success("删除成功");
    }

}


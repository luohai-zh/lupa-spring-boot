package com.lupa.controller;


import com.lupa.entity.Newdata;
import com.lupa.mapper.NewdataMapper;
import com.lupa.service.NewdataService;
import com.lupa.utils.response.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 实时数据表 前端控制器
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/newdata")
public class NewdataController {

    @Autowired
    private NewdataService newdataService;
    @Autowired
    private NewdataMapper newdataMapper;

    /*查询设备的实时数据列表*/
    @PostMapping("newlist")
    public CommonResult newlist(Newdata newdata) {
        List<Newdata> newlist = newdataService.newList(newdata);
        return CommonResult.success(newlist, "查询成功");
    }
    /*删除实时数据*/
    @PostMapping("delnewdata")
    public CommonResult delnewdata(Newdata newdata){
        newdataService.delnewdata(newdata);
        return CommonResult.success("删除成功");
    }

}


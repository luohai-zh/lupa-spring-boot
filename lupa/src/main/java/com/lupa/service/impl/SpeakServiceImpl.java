package com.lupa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lupa.entity.Speak;
import com.lupa.entity.User;
import com.lupa.mapper.SpeakMapper;
import com.lupa.mapper.UserMapper;
import com.lupa.service.SpeakService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lupa.utils.exception.BusinessException;
import com.lupa.utils.exception.ExceptionCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Watchable;
import java.util.List;

/**
 * <p>
 * 用户消息表 服务实现类
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@Service
public class SpeakServiceImpl extends ServiceImpl<SpeakMapper, Speak> implements SpeakService {
    @Autowired
    public SpeakMapper speakMapper;
    @Autowired
    public UserMapper userMapper;

    /*查询用户消息列表*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Speak> speakList(Speak speak) {
        List<Speak> speakList = speakMapper.selectList(null);//不需要传递参数
        if (speakList.isEmpty()) {
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        } else {
            return speakMapper.selectList(null);
        }
    }

    /*用户添加消息*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserinfo(Speak speak, User user) {
        List<User> seluserid = userMapper.selectList(new QueryWrapper<User>().eq("userid", user.getUserid()));//查询数据库中有没有对应用户ID的数据
        User username = userMapper.selectOne(new QueryWrapper<User>().eq("userid", user.getUserid()));
        if (seluserid.isEmpty()) { //当返回的list是空值
            throw new BusinessException(ExceptionCodeEnum.PARAM_NOT_VALID);
        } else {
            Speak speakInfo = new Speak();
            speakInfo.setUserid(speak.getUserid());
            speakInfo.setUsername(username.getUsername());
            speakInfo.setUserspeak(speak.getUserspeak());
            if (speak.getUserspeak() == null || speak.getUserspeak().isEmpty()) {
                throw new BusinessException(ExceptionCodeEnum.PARAM_NOT_COMPLETE);
            } else {
                speakMapper.insert(speakInfo);
            }
        }
    }

    /*删除用户消息*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delUserinfo(Speak speak) {
        List<Speak> seluserid = speakMapper.selectList(new QueryWrapper<Speak>().eq("userid", speak.getUserid()));//查询数据库中有没有对应用户ID的数据
        if (seluserid.isEmpty()) {
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        } else {
            QueryWrapper<Speak> queryWrapper = Wrappers.query();
            queryWrapper.eq("userid", speak.getUserid());
            speakMapper.delete(queryWrapper);
        }
    }
}

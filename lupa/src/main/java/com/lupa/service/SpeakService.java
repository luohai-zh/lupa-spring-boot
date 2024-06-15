package com.lupa.service;

import com.lupa.entity.Speak;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lupa.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户消息表 服务类
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
public interface SpeakService extends IService<Speak> {

    /*查询用户列表*/
    @Transactional(rollbackFor = Exception.class)
    List<Speak> speakList(Speak speak);

    /*用户添加消息*/
    @Transactional(rollbackFor = Exception.class)
    void addUserinfo(Speak speak, User user);

    /*删除用户消息*/
    @Transactional(rollbackFor = Exception.class)
    void delUserinfo(Speak speak);
}

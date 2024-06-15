package com.lupa.service;

import com.lupa.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
public interface UserService extends IService<User> {

    /*查询用户列表*/
      @Transactional(rollbackFor = Exception.class)
      List<User> userList(User user);

    /*添加用户*/
    @Transactional(rollbackFor = Exception.class)
    void registerUser(User user);

    /*用户登录*/
    @Transactional(rollbackFor = Exception.class)
    Map login(User user);

    /*修改用户信息*/
    @Transactional(rollbackFor = Exception.class)
    void modifyUser(User user);

    /*删除用户*/
    @Transactional(rollbackFor = Exception.class)
    void delUser(User user);

    /*查看用户信息*/
    @Transactional(rollbackFor = Exception.class)
    List<User> setUserinfo(User user);
}

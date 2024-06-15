package com.lupa.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lupa.entity.User;
import com.lupa.mapper.UserMapper;
import com.lupa.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lupa.utils.exception.BusinessException;
import com.lupa.utils.exception.ExceptionCodeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    public UserMapper userMapper;


    /*查询用户列表*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<User> userList(User user) {
        List<User> userList = userMapper.selectList(null);//不需要传递参数
        if (userList.isEmpty()) {
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        } else {
            return userMapper.selectList(null);
        }
    }

    /*添加用户*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registerUser(User user) {
        // 检查用户名是否已存在
        List<User> usersWithSameUsername = userMapper.selectList(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (!usersWithSameUsername.isEmpty()) {
            throw new BusinessException(ExceptionCodeEnum.USERNAME_IS_EXIST);
        }
        // 检查电话号码是否已存在
        List<User> usersWithSamePhone = userMapper.selectList(new QueryWrapper<User>().eq("userphonenumber", user.getUserphonenumber()));
        if (!usersWithSamePhone.isEmpty()) {
            throw new BusinessException(ExceptionCodeEnum.USER_IS_EXIST);
        }
        User myuser = new User();
        myuser.setUserid(IdUtil.simpleUUID());
        myuser.setUsername(user.getUsername());
        myuser.setUserpassword(SecureUtil.md5(user.getUserpassword()));
        myuser.setUserphonenumber(user.getUserphonenumber());
        userMapper.insert(myuser);

    }

    /*用户登录*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map login(User user) {
        User seluser = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (seluser == null) {
            throw new BusinessException(ExceptionCodeEnum.USER_NOTFOUND);
        }
        if (!seluser.getUserpassword().equals(SecureUtil.md5(user.getUserpassword()))) {
            throw new BusinessException(ExceptionCodeEnum.PASSWORD_ERROR);
        }
        Map resultmap = new HashMap();  //引用HashMap存储自定义类型
        resultmap.put("userid", seluser.getUserid());
        resultmap.put("username",seluser.getUsername());
        return resultmap;
    }


    /*修改用户信息*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUser(User user) {
        // 假设user.getId()是用户的唯一标识，用于定位要修改的用户
        User seluser = userMapper.selectOne(new QueryWrapper<User>().eq("userphonenumber", user.getUserphonenumber()));//查看用户名是否存在
        if (seluser == null) {
            throw new BusinessException(ExceptionCodeEnum.PHEONE_IS_REGISTER);
        }
        User myuser = new User();
        BeanUtils.copyProperties(user, myuser);// 复制属性，除了密码（假设密码字段不应该被直接覆盖）
        myuser.setUserpassword(SecureUtil.md5(user.getUserpassword()));//只更改密码
        // 更新数据库中的用户信息
        int updateCount = userMapper.update(myuser, new UpdateWrapper<User>().eq("userphonenumber", myuser.getUserphonenumber()));  //校验手机号并获取到唯一数据
        if (updateCount == 0) {
            throw new BusinessException(ExceptionCodeEnum.UPDATE_ERROR);
        }
    }

    /*删除用户*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delUser(User user) {
        User seluser = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        User seluserPhone = userMapper.selectOne(new QueryWrapper<User>().eq("userphonenumber", user.getUserphonenumber()));
        if (seluser == null && seluserPhone == null) {
            throw new BusinessException(ExceptionCodeEnum.DEVICE_IS_EXISTID);
        }
        QueryWrapper<User> query = Wrappers.query();
        QueryWrapper<User> queryPhone = Wrappers.query();
        query.eq("username", user.getUsername());
        queryPhone.eq("userphonenumber", user.getUserphonenumber());
        userMapper.delete(query);
        userMapper.delete(queryPhone);
    }

    /*查看用户信息*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<User> setUserinfo(User user) {
        List<User> seluser = userMapper.selectList(new QueryWrapper<User>().eq("userid", user.getUserid()));//查询数据库中用没用数据
        if (seluser.isEmpty()) { //当返回的list是空值
            throw new BusinessException(ExceptionCodeEnum.WEIXINLOGIN_ERROR_MYSQL);
        } else {
            return userMapper.selectList(new QueryWrapper<User>().eq("userid", user.getUserid()));  //定义一个查询条件,只查询userid用户信息
        }
    }
}

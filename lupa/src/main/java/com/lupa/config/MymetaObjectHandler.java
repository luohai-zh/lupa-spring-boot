package com.lupa.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class MymetaObjectHandler implements MetaObjectHandler {

    //插入时生成时间戳
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createtime",System.currentTimeMillis()/1000,metaObject);
        this.setFieldValByName("acceptTime",System.currentTimeMillis()/1000,metaObject);
    }

    //更新时生成时间戳
    @Override
    public void updateFill(MetaObject metaObject) {
//        this.setFieldValByName("paymentTime",new Date(),metaObject);
        this.setFieldValByName("acceptTime",System.currentTimeMillis()/1000,metaObject);
    }
}

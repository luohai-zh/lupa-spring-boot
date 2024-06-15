package com.lupa.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("lupa_user")
@ApiModel(value="User对象", description="用户信息表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "序号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private String userid;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户密码")
    private String userpassword;

    @ApiModelProperty(value = "用户手机号")
    private String userphonenumber;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "createtime",fill = FieldFill.INSERT)
    private Long createtime;


}

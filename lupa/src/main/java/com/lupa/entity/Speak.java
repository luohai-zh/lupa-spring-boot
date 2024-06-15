package com.lupa.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户消息表
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("lupa_speak")
@ApiModel(value="Speak对象", description="用户消息表")
public class Speak implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "序号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private String userid;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户消息")
    private String userspeak;

    @ApiModelProperty(value = "发布时间")
    @TableField(value = "createtime",fill = FieldFill.INSERT)
    private Long createtime;


}

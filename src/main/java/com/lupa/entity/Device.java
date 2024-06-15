package com.lupa.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 设备信息表
 * </p>
 *
 * @author lupa
 * @since 2024-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("lupa_device")
@ApiModel(value="Device对象", description="设备信息表")
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "序号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private String userid;

    @ApiModelProperty(value = "设备ID")
    private String deviceid;

    @ApiModelProperty(value = "设备秘钥")
    private String deviceidsecretkey;

    @ApiModelProperty(value = "设备名称")
    private String devicename;

    @ApiModelProperty(value = "备注信息")
    private String deviceremarks;

    @ApiModelProperty(value = "创建时间")
    private Integer createtime;


}

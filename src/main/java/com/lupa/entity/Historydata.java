package com.lupa.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 历史数据表
 * </p>
 *
 * @author lupa
 * @since 2024-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("lupa_historydata")
@ApiModel(value="Historydata对象", description="历史数据表")
public class Historydata implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "序号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "设备ID")
    private String deviceid;

    @ApiModelProperty(value = "参数名")
    @TableField("paramsName")
    private String paramsname;

    @ApiModelProperty(value = "参数值")
    @TableField("paramsValues")
    private String paramsvalues;

    @ApiModelProperty(value = "接收时间")
    @TableField("acceptTime")
    private Integer accepttime;


}

package com.lupa.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 实时数据表
 * </p>
 *
 * @author lupa
 * @since 2024-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("lupa_newdata")
@ApiModel(value="Newdata对象", description="实时数据表")
public class Newdata extends Model<Newdata> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID",type = IdType.AUTO)
    private Integer id;

    /**
     * 设备ID
     */
    @MppMultiId
    @TableField("deviceID")
    private String deviceid;

    /**
     * 参数名
     */
    @MppMultiId
    @TableField("paramsName")
    private String paramsname;

    /**
     * 参数值
     */
    @TableField("paramsValues")
    private String paramsvalues;


    /**
     * 接收时间
     */
    @TableField("acceptTime")
    private Integer accepttime;




}

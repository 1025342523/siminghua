package com.yscoco.net.dto.base;

import com.ys.module.utils.DateUtils;
import com.ys.module.utils.StringUtils;

import java.io.Serializable;

/**
 * 作者：karl.wei
 * 创建日期： 2017/7/31 0031 19:23
 * 邮箱：karl.wei@yscoco.com
 * QQ:2736764338
 * 类介绍：DTO基类
 */
public class BaseBean implements Serializable {

    private static final long serialVersionUID = 5103083567079158561L;
    
    //	@ApiModelProperty("创建时间")
    private String createTime;

    private String id;

    //	@ApiModelProperty("创建人")
    private String createBy;

    public String getCreateTime() {
        if (StringUtils.isEmpty(createTime)) {
            return "";
        } else {
            if (!createTime.contains(DateUtils.getDate())) {
                return createTime.split(" ")[0];
            } else {
                return createTime.split(" ")[1];
            }
        }
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "createTime='" + createTime + '\'' +
                ", id='" + id + '\'' +
                ", createBy='" + createBy + '\'' +
                '}';
    }
}

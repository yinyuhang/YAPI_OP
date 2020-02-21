package com.shark.ctrip.yapi;

import com.shark.ctrip.common.Utils;
import lombok.Data;

@Data
public class Pojo extends YapiVo {
    /**
     * 当前字段是否必填
     */
    private Boolean required;
    /**
     * 是否为数组
     */
    private Boolean isArray;

    private Class<?> clz;

    public void setClz(Class<?> clz) {
        this.clz = clz;
        setType(clz.getSimpleName());
        setName(Utils.toLowCamelCase(getType()));
    }
}

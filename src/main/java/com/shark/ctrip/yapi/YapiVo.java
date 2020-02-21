package com.shark.ctrip.yapi;

import lombok.Data;

import java.util.List;

@Data
public class YapiVo {
    /**
     * 字段名
     */
    private String name;
    /**
     * 字段注释
     */
    private String description;
    /**
     * 字段类型
     */
    private String type;
    /**
     * YAPI 上设置的必填子字段
     */
    private List<String> requireFields;
    /**
     * 子字段列表
     */
    private List<? extends YapiVo> properties;
}

package com.frog.generator.mybatis.plugin.model;

import lombok.Data;

/**
 * Description 实体类，用于储存从数据库获取的数据
 *
 * @author yxy
 * @date 2019/07/09
 */
@Data
public class BaseData {

    private String columnComment;

    private String columnType;

    private String columnName;
}

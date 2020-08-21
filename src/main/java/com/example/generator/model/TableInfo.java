package com.example.generator.model;

import lombok.Data;

import java.util.List;

/**
 * @author administrator
 * @date 2020/08/20
 * @description: 类描述: 表信息及对应类信息
 **/
@Data
public class TableInfo {

    /**
     * 数据库类型: mysql | sql server | oracle
     */
    private DatabaseSchemaEnum databaseSchema;

    /**
     * 列信息列表
     */
    private List<ColumnInfo> columnInfoList;


    /**
     * 表信息 表名(下划线形式) | 表名(首字母大写驼峰,例如UserDO) | 表名(首字母小写驼峰,例如userDao)
     */
    private String tableName;
    private String upperCamelCaseName;
    private String lowerCamelCaseName;


}

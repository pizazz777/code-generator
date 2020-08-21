package com.example.generator.properties;

import com.example.generator.model.TableInfo;
import lombok.Data;

/**
 * @author administrator
 * @date 2020/08/20
 * @description: 类描述: 属性对象
 **/
@Data
public class GeneratorProperties {

    /**
     * 表信息 主键是否是自增ID
     */
    private Boolean autoIncrease;
    /**
     * 类信息 作者+版本+名称+描述
     */
    private String author;
    private String version;
    private String name;
    private String description;
    /**
     * 项目所在具体目录 例如: windows D:/project/ Linux: /usr/project/
     */
    private String projectDir;

    /**
     * 包路径 公共路径
     */
    private String projectPackage;
    private String moduleName;

    /**
     * 模块路径 controller+service+dao+entity
     */
    private String controllerPackage;
    private String servicePackage;
    private String daoPackage;
    private String entityPackage;
    /**
     * 数据库名+表名
     */
    private String database;
    private String tableName;

    /**
     * 数据库 url+username+password
     */
    private String url;
    private String username;
    private String password;
    /**
     * 单独控制每个文件是否需要生成
     */
    private Boolean generateController;
    private Boolean generateService;
    private Boolean generateDao;
    private Boolean generateEntity;
    private Boolean generateMapper;

    /**
     * 表信息 生成模板的时候从这儿取值
     */
    private TableInfo tableInfo;
}

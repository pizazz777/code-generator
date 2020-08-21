package com.example.generator.loader;

import com.example.generator.properties.GeneratorProperties;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * @author administrator
 * @date 2020/08/20
 * @description: 类描述: 因为不是Spring项目,需要手动获取properties值
 **/
@Getter
public class PropertiesLoader {

    /**
     * 配置文件名
     */
    private final static String DEFAULT_PROPERTIES_FILE_NAME = "generator.properties";
    /**
     * 占位符 &{ }
     */
    private static final String REFER_LEFT = "${";
    private static final String REFER_RIGHT = "}";
    /**
     * 读取的属性
     */
    private Properties properties;
    /**
     * 读取后的属性对象
     */
    private GeneratorProperties generatorProperties;

    /**
     * 私有化构造方法
     */
    private PropertiesLoader() {
        init();
    }

    /**
     * 使用内部类维护单例
     */
    private static class ClassHolder {
        private static final PropertiesLoader INSTANCE = new PropertiesLoader();
    }

    /**
     * 给一个获取实例的方法
     */
    public static PropertiesLoader getInstance() {
        return ClassHolder.INSTANCE;
    }

    /**
     * 初始化
     */
    private void init() {
        // 从 resource 目录的配置文件中读取需要的字段
        try (InputStream inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE_NAME)) {
            if (Objects.nonNull(inputStream)) {
                initObject();
                // 防止中文乱码,包装成 Reader
                try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                    // 读取属性文件数据
                    properties.load(reader);

                    // 获取 properties里面数据
                    // 类信息 作者+版本+名称+描述
                    generatorProperties.setAuthor(getPropertyWithRefer(properties, "class.author"));
                    generatorProperties.setVersion(getPropertyWithRefer(properties, "class.version"));
                    generatorProperties.setName(getPropertyWithRefer(properties, "class.name"));
                    generatorProperties.setDescription(getPropertyWithRefer(properties, "class.description"));
                    // 表主键信息
                    generatorProperties.setAutoIncrease(Boolean.parseBoolean(getPropertyWithRefer(properties, "auto-increase")));
                    // 项目目录
                    generatorProperties.setProjectDir(getPropertyWithRefer(properties, "project-dir"));
                    // 项目包路径
                    generatorProperties.setProjectPackage(getPropertyWithRefer(properties, "package.project"));
                    generatorProperties.setModuleName(getPropertyWithRefer(properties, "package.module-name"));
                    // 模块路径 controller+service+dao+entity
                    generatorProperties.setControllerPackage(getPropertyWithRefer(properties, "package.controller"));
                    generatorProperties.setServicePackage(getPropertyWithRefer(properties, "package.service"));
                    generatorProperties.setDaoPackage(getPropertyWithRefer(properties, "package.dao"));
                    generatorProperties.setEntityPackage(getPropertyWithRefer(properties, "package.entity"));
                    // 数据库名+表名
                    generatorProperties.setDatabase(getPropertyWithRefer(properties, "database.database"));
                    generatorProperties.setTableName(getPropertyWithRefer(properties, "database.table-name"));
                    // 数据库 url+username+password
                    generatorProperties.setUrl(getPropertyWithRefer(properties, "database.url"));
                    generatorProperties.setUsername(getPropertyWithRefer(properties, "database.username"));
                    generatorProperties.setPassword(getPropertyWithRefer(properties, "database.password"));
                    // 单独控制每个文件是否需要生成
                    generatorProperties.setGenerateController(Boolean.parseBoolean(getPropertyWithRefer(properties, "generate.controller")));
                    generatorProperties.setGenerateService(Boolean.parseBoolean(getPropertyWithRefer(properties, "generate.service")));
                    generatorProperties.setGenerateDao(Boolean.parseBoolean(getPropertyWithRefer(properties, "generate.dao")));
                    generatorProperties.setGenerateEntity(Boolean.parseBoolean(getPropertyWithRefer(properties, "generate.entity")));
                    generatorProperties.setGenerateMapper(Boolean.parseBoolean(getPropertyWithRefer(properties, "generate.mapper")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initObject() {
        if (Objects.isNull(properties)) {
            properties = new Properties();
        }
        if (Objects.isNull(generatorProperties)) {
            generatorProperties = new GeneratorProperties();
        }
    }

    /**
     * 获取引用变量参数
     */
    private String getPropertyWithRefer(Properties properties, String key) {
        String result = properties.getProperty(key);
        if (checkText(result)) {
            // 获取标签中的内容: 例如 ${project-package}.controller.${module-name} -> [project-package, module-name]
            String[] markContents = StringUtils.substringsBetween(result, REFER_LEFT, REFER_RIGHT);
            for (String content : markContents) {
                result = result.replace(REFER_LEFT + content + REFER_RIGHT, properties.getProperty(content));
            }
        }
        return result;
    }


    private boolean checkText(String result) {
        return StringUtils.isNotBlank(result) && result.contains(REFER_LEFT) && result.contains(REFER_RIGHT);
    }
}

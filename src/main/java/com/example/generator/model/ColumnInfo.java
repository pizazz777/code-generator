package com.example.generator.model;

import com.example.generator.util.StrUtil;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.example.generator.model.MySqlDataTypeEnum.*;

/**
 * @author administrator
 * @date 2020/08/20
 * @description: 类描述: 表字段信息及对应类字段信息
 **/
@Data
@Builder
public class ColumnInfo {

    /**
     * 下划线接一个字母或数字
     */
    private static final Pattern UNDERLINE_WITH_CHAR = Pattern.compile("(_[A-Za-z0-9])");

    /**
     * 是否允许为null 1.允许,0:不允许
     */
    public static final String NULLABLE_YES = "1";
    public static final String NULLABLE_NO = "0";

    /**
     * 表字段信息 Ordinal(序号) | Name(字段名) | Comment(注释) | Data Type(数据类型) | Length(长度) | Mandatory(是否允许为null) | Default Value(默认值)
     */
    private Integer ordinal;
    private String name;
    private String comment;
    private String dataType;
    private Integer length;
    private Integer mandatory;
    private String defaultValue;

    /**
     * 类字段信息 表字段类型->类字段类型 | 表字段名->类字段名(驼峰形式首字母小写)
     */
    private String fieldDataType;
    private String firstLowerCamelCaseName;

    /**
     * 数据库字段类型转为类字段类型 数据库字段名转为类字段名(驼峰形式首字母小写)
     */
    public void transform(DatabaseSchemaEnum schemaEnum) {
        // 表字段类型->类字段类型
        transformFieldDataType(schemaEnum);
        // 表字段名->类字段名(
        this.firstLowerCamelCaseName = StrUtil.convertUnderLineToFirstLowerCamelCase(this.name);
    }

    private void transformFieldDataType(DatabaseSchemaEnum schemaEnum) {
        if (Objects.equals(schemaEnum, DatabaseSchemaEnum.MYSQL)) {
            this.fieldDataType = getFieldTypeByDataTypeOfMySql();
        }
        if (Objects.equals(schemaEnum, DatabaseSchemaEnum.ORACLE)) {
            this.fieldDataType = getFieldTypeByDataTypeOfOracle();
        }
    }


    /**
     * 通过数据类型字符串获取 java 数据类型字符串 MySql
     */
    private String getFieldTypeByDataTypeOfMySql() {
        // 默认为String类型
        String fieldType = "String";
        String upperCase = this.dataType.toUpperCase();
        if (Objects.equals(upperCase, VARCHAR.name())) {
            fieldType = "String";
        } else if (Objects.equals(upperCase, MySqlDataTypeEnum.NVACHAR.name())) {
            fieldType = "String";
        } else if (Objects.equals(upperCase, MySqlDataTypeEnum.TEXT.name())) {
            fieldType = "String";
        } else if (Objects.equals(upperCase, MySqlDataTypeEnum.INT.name())) {
            fieldType = "Integer";
        } else if (Objects.equals(upperCase, MySqlDataTypeEnum.SMALLINT.name())) {
            fieldType = "Short";
        } else if (Objects.equals(upperCase, MySqlDataTypeEnum.BIGINT.name())) {
            fieldType = "Long";
        } else if (Objects.equals(upperCase, MySqlDataTypeEnum.TINYINT.name())) {
            fieldType = "Boolean";
        } else if (Objects.equals(upperCase, MySqlDataTypeEnum.DATETIME.name()) || Objects.equals(dataType, MySqlDataTypeEnum.DATETIME2.name()) || Objects.equals(dataType, MySqlDataTypeEnum.TIMESTAMP.name())) {
            fieldType = "LocalDateTime";
        } else if (Objects.equals(upperCase, MySqlDataTypeEnum.DATE.name())) {
            fieldType = "LocalDate";
        } else if (Objects.equals(upperCase, MySqlDataTypeEnum.DOUBLE.name())) {
            fieldType = "Double";
        } else if (Objects.equals(upperCase, MySqlDataTypeEnum.FLOAT.name())) {
            fieldType = "Float";
        } else if (Objects.equals(upperCase, MySqlDataTypeEnum.DECIMAL.name())) {
            fieldType = "BigDecimal";
        }
        return fieldType;
    }


    /**
     * 通过数据类型字符串获取 java 数据类型字符串 ORACLE
     */
    private String getFieldTypeByDataTypeOfOracle() {
        // 默认为String类型
        String fieldType = "String";
        String upperCase = this.dataType.toUpperCase();
        if (Objects.equals(upperCase, OracleDataTypeEnum.VARCHAR.name()) || Objects.equals(upperCase, OracleDataTypeEnum.VARCHAR2.name()) || Objects.equals(upperCase, OracleDataTypeEnum.CHAR.name())) {
            fieldType = "String";
        } else if (Objects.equals(upperCase, OracleDataTypeEnum.NUMBER.name())) {
            fieldType = "Integer";
        } else if (Objects.equals(upperCase, OracleDataTypeEnum.LONG.name())) {
            fieldType = "Long";
        } else if (Objects.equals(upperCase, OracleDataTypeEnum.DATE.name())) {
            fieldType = "LocalDateTime";
        }
        return fieldType;
    }

}

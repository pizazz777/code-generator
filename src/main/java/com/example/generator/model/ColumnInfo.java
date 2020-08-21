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
        if (Objects.equals(schemaEnum, DatabaseSchemaEnum.MYSQL)) {
            fieldDataType = getFieldTypeByDataTypeOfMySql();
        }
        if (Objects.equals(schemaEnum, DatabaseSchemaEnum.ORACLE)) {
            fieldDataType = getFieldTypeByDataTypeOfOracle();
        }
        this.firstLowerCamelCaseName = StrUtil.convertUnderLineToFirstLowerCamelCase(this.name);
    }


    /**
     * 通过数据类型字符串获取 java 数据类型字符串 MySql
     */
    private String getFieldTypeByDataTypeOfMySql() {
        // 默认为String类型
        String fieldType = "String";
        if (Objects.equals(this.dataType, VARCHAR.name())) {
            fieldType = "String";
        } else if (Objects.equals(this.dataType, MySqlDataTypeEnum.NVACHAR.name())) {
            fieldType = "String";
        } else if (Objects.equals(this.dataType, MySqlDataTypeEnum.TEXT.name())) {
            fieldType = "String";
        } else if (Objects.equals(this.dataType, MySqlDataTypeEnum.INT.name())) {
            fieldType = "Integer";
        } else if (Objects.equals(this.dataType, MySqlDataTypeEnum.SMALLINT.name())) {
            fieldType = "Short";
        } else if (Objects.equals(this.dataType, MySqlDataTypeEnum.BIGINT.name())) {
            fieldType = "Long";
        } else if (Objects.equals(this.dataType, MySqlDataTypeEnum.TINYINT.name())) {
            fieldType = "Boolean";
        } else if (Objects.equals(this.dataType, MySqlDataTypeEnum.DATETIME.name()) || Objects.equals(dataType, MySqlDataTypeEnum.DATETIME2.name()) || Objects.equals(dataType, MySqlDataTypeEnum.TIMESTAMP.name())) {
            fieldType = "LocalDateTime";
        } else if (Objects.equals(this.dataType, MySqlDataTypeEnum.DATE.name())) {
            fieldType = "LocalDate";
        } else if (Objects.equals(this.dataType, MySqlDataTypeEnum.DOUBLE.name())) {
            fieldType = "Double";
        } else if (Objects.equals(this.dataType, MySqlDataTypeEnum.FLOAT.name())) {
            fieldType = "Float";
        } else if (Objects.equals(this.dataType, MySqlDataTypeEnum.DECIMAL.name())) {
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
        if (Objects.equals(this.dataType, OracleDataTypeEnum.VARCHAR.name()) || Objects.equals(this.dataType, OracleDataTypeEnum.VARCHAR2.name()) || Objects.equals(this.dataType, OracleDataTypeEnum.CHAR.name())) {
            fieldType = "String";
        } else if (Objects.equals(this.dataType, OracleDataTypeEnum.NUMBER.name())) {
            fieldType = "Integer";
        } else if (Objects.equals(this.dataType, OracleDataTypeEnum.LONG.name())) {
            fieldType = "Long";
        } else if (Objects.equals(this.dataType, OracleDataTypeEnum.DATE.name())) {
            fieldType = "LocalDateTime";
        }
        return fieldType;
    }

}
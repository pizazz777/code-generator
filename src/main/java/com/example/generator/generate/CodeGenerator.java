package com.example.generator.generate;

import com.example.generator.handler.TemplateHandler;
import com.example.generator.loader.PropertiesLoader;
import com.example.generator.model.ColumnInfo;
import com.example.generator.model.DatabaseSchemaEnum;
import com.example.generator.model.TableInfo;
import com.example.generator.properties.GeneratorProperties;
import com.example.generator.util.StrUtil;
import com.google.common.collect.Lists;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Objects;

/**
 * @author administrator
 * @date 2020/08/20
 * @description: 类描述: 代码生成器
 **/
@Slf4j
public class CodeGenerator {

    private Connection connection;
    private GeneratorProperties generatorProperties;

    public CodeGenerator(GeneratorProperties generatorProperties) {
        this.generatorProperties = generatorProperties;
    }

    /**
     * 输出Controller Service ServiceImpl Dao Mapper DO 到指定文件夹
     */
    public void write() throws SQLException, IOException, TemplateException {
        // 获取属性
        if (Objects.isNull(generatorProperties)) {
            generatorProperties = PropertiesLoader.getInstance().getGeneratorProperties();
        }
        // 获取表结构对象
        TableInfo tableInfo = getTableStructure();
        // 输出模板数据到文件
        writeTemplateToFile(tableInfo, generatorProperties.getProjectDir());
    }

    /**
     * 输出模板数据到文件
     */
    private void writeTemplateToFile(TableInfo tableInfo, String outputFileDirPath) throws IOException, TemplateException {
        // 模板处理器
        TemplateHandler templateHandler = new TemplateHandler();
        // 表名(首字母大写驼峰,例如SysRole)
        String tableInfoUpperCamelCaseName = tableInfo.getUpperCamelCaseName();
        outputFileDirPath = getOutputPath(outputFileDirPath);
        String templateFilePathPrefix = outputFileDirPath + File.separator + "#{package}" + File.separator + tableInfoUpperCamelCaseName;
        generatorProperties.setTableInfo(tableInfo);

        // 生成模板文件
        if (generatorProperties.getGenerateController()) {
            // controller
            templateHandler.writeTemplateToFile(generatorProperties, "controllerTemplate.ftl", templateFilePathPrefix.replace("#{package}", "java/" + generatorProperties.getControllerPackage().replace(".", File.separator)) + "Controller.java");
        }
        if (generatorProperties.getGenerateService()) {
            // service
            templateHandler.writeTemplateToFile(generatorProperties, "serviceTemplate.ftl", templateFilePathPrefix.replace("#{package}", "java/" + generatorProperties.getServicePackage().replace(".", File.separator)) + "Service.java");
            // serviceImpl
            templateHandler.writeTemplateToFile(generatorProperties, "serviceImplTemplate.ftl", templateFilePathPrefix.replace("#{package}", "java/" + generatorProperties.getServicePackage().replace(".", File.separator) + "/impl") + "ServiceImpl.java");
        }
        if (generatorProperties.getGenerateDao()) {
            // dao
            templateHandler.writeTemplateToFile(generatorProperties, "daoTemplate.ftl", templateFilePathPrefix.replace("#{package}", "java/" + generatorProperties.getDaoPackage().replace(".", File.separator)) + "Dao.java");
        }
        if (generatorProperties.getGenerateEntity()) {
            // entity
            templateHandler.writeTemplateToFile(generatorProperties, "entityTemplate.ftl", templateFilePathPrefix.replace("#{package}", "java/" + generatorProperties.getEntityPackage().replace(".", File.separator)) + "DO.java");
        }
        if (generatorProperties.getGenerateMapper()) {
            // mapper
            templateHandler.writeTemplateToFile(generatorProperties, "mapperTemplate.ftl", templateFilePathPrefix.replace("#{package}", "resources/mapper" + File.separator + generatorProperties.getModuleName()) + ".xml");
        }
    }

    private String getOutputPath(String outputFileDirPath) {
        outputFileDirPath = StringUtils.isNotBlank(outputFileDirPath) ? outputFileDirPath : generatorProperties.getTableName();
        File outputFileDir = new File(outputFileDirPath);
        if (!outputFileDir.exists()) {
            boolean success = outputFileDir.mkdirs();
            if (success) {
                outputFileDirPath = outputFileDir.getAbsolutePath();
            }
        }
        return outputFileDirPath;
    }

    /**
     * 获取表结构信息
     */
    private TableInfo getTableStructure() throws SQLException {
        String query = null;
        DatabaseSchemaEnum databaseSchema = getDatabaseTypeByUrl(generatorProperties.getUrl());
        if (Objects.equals(DatabaseSchemaEnum.MYSQL, databaseSchema)) {
            query = selectTableInfoOfMySql(generatorProperties.getDatabase());
        }
        if (Objects.equals(DatabaseSchemaEnum.SQLSERVER, databaseSchema)) {
            query = selectTableInfoOfSqlServer();
        }
        if (Objects.equals(DatabaseSchemaEnum.ORACLE, databaseSchema)) {
            query = selectTableInfoOfOracle();
        }
        Objects.requireNonNull(query, "获取表信息为空");

        List<ColumnInfo> list = Lists.newArrayList();
        PreparedStatement preparedStatement = getConnection().prepareStatement(query);
        // 设置查询参数
        preparedStatement.setString(1, generatorProperties.getTableName());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            ColumnInfo columnInfo = ColumnInfo.builder()
                    // .tableName(resultSet.getString("table_name"))
                    // .name(resultSet.getString("column_name"))
                    .name(resultSet.getString(2))
                    // .ordinal(resultSet.getInt("column_ordinal"))
                    .ordinal(resultSet.getInt(3))
                    // .defaultValue(resultSet.getString("column_default"))
                    .defaultValue(resultSet.getString(4))
                    // .mandatory(resultSet.getInt("is_nullable"))
                    .mandatory(resultSet.getInt(5))
                    // .dataType(resultSet.getString("data_type"))
                    .dataType(resultSet.getString(6))
                    // .length(resultSet.getInt("data_max_length"))
                    .length(resultSet.getInt(7))
                    // .comment(resultSet.getString("column_comment"))
                    .comment(resultSet.getString(8))
                    .build();
            // 数据库字段类型转为类字段类型 数据库字段名转为类字段名(驼峰形式首字母小写)
            columnInfo.transform(databaseSchema);
            list.add(columnInfo);
        }

        // 表信息
        TableInfo tableInfo = new TableInfo();
        tableInfo.setDatabaseSchema(databaseSchema);
        tableInfo.setTableName(generatorProperties.getTableName());
        tableInfo.setUpperCamelCaseName(StrUtil.convertUnderLineToFirstUpperCamelCase(generatorProperties.getTableName()));
        tableInfo.setLowerCamelCaseName(StrUtil.convertUnderLineToFirstLowerCamelCase(generatorProperties.getTableName()));
        tableInfo.setColumnInfoList(list);

        return tableInfo;
    }

    /**
     * MySql 表结构信息
     */
    private String selectTableInfoOfMySql(String tableSchema) {
        return "   SELECT \n" +
                "      TABLE_NAME                                                 AS table_name,\n" +
                "      COLUMN_NAME                                                AS column_name,\n" +
                "      ORDINAL_POSITION                                           AS column_ordinal,\n" +
                "      `IFNULL`(COLUMN_DEFAULT, '')                               AS column_default,\n" +
                "      CASE WHEN IS_NULLABLE = 'YES' then '1' else '' end         AS is_nullable,\n" +
                "      DATA_TYPE                                                  AS data_type,\n" +
                "      CHARACTER_MAXIMUM_LENGTH                                   AS data_max_length,\n" +
                "      COLUMN_COMMENT                                             AS column_comment\n" +
                "  FROM INFORMATION_SCHEMA.COLUMNS \n" +
                "  WHERE\n" +
                "      TABLE_SCHEMA= '" + tableSchema + "'\n" +
                "      AND TABLE_NAME = ?\n" +
                "  ORDER BY\n" +
                "      column_ordinal";
    }


    private String selectTableInfoOfOracle() {
        return " SELECT                              \n" +
                "   T2.TABLE_NAME                                               AS table_name,\n" +
                "   T1.COLUMN_NAME                                              AS column_name,\n" +
                "   T1.COLUMN_ID                                                AS column_ordinal,\n" +
                "   NVL( t1.DATA_DEFAULT, '' )                                  AS column_default,\n" +
                "   CASE WHEN T1.NULLABLE = 'N' THEN 1 ELSE 0 END               AS is_nullable,\n" +
                "   T1.DATA_TYPE                                                AS data_type,\n" +
                "   T1.DATA_LENGTH                                              AS date_max_length,\n" +
                "   T2.COMMENTS                                                 AS column_comment\n" +
                "FROM\n" +
                "   USER_TAB_COLS T1,\n" +
                "   USER_COL_COMMENTS T2,\n" +
                "   USER_TAB_COMMENTS T3\n" +
                "WHERE\n" +
                "   T1.TABLE_NAME = T2.TABLE_NAME ( + )\n" +
                "   AND T1.COLUMN_NAME = T2.COLUMN_NAME ( + )\n" +
                "   AND T1.TABLE_NAME = T3.TABLE_NAME ( + )\n" +
                "   AND T1.TABLE_NAME = ?\n" +
                "ORDER BY\n" +
                "   T1.COLUMN_ID";
    }


    /**
     * SqlServer 表结构信息
     */
    private String selectTableInfoOfSqlServer() {
        return "  SELECT \n" +
                "     table_name = case when a.colorder = 1 then d.name else '' end,\n" +
                "     column_name = a.name,\n" +
                "     column_ordinal = a.colorder,\n" +
                "     column_default = isnull(e.text, ''),\n" +
                "     is_nullable = case when a.isnullable = 1 then 1 else 0 end,\n" +
                "     data_type = b.name,\n" +
                "     data_max_length = a.length,\n" +
                "     column_comment =isnull(g.[value], ''),\n" +
                "     data_max_value = COLUMNPROPERTY(a.id, a.name, 'PRECISION'),\n" +
                "     data_decimal_digit = isnull(COLUMNPROPERTY(a.id, a.name, 'Scale'), 0),\n" +
                "     primary_key = case\n" +
                "                      when exists(SELECT 1\n" +
                "                                  FROM sysobjects\n" +
                "                                  where xtype = 'PK'\n" +
                "                                    and name in (\n" +
                "                                      SELECT name\n" +
                "                                      FROM sysindexes\n" +
                "                                      WHERE indid in (\n" +
                "                                          SELECT indid\n" +
                "                                          FROM sysindexkeys\n" +
                "                                          WHERE id = a.id\n" +
                "                                            AND colid = a.colid\n" +
                "                                      ))) then 1\n" +
                "                      else 0 end,\n" +
                "     is_identify = case when COLUMNPROPERTY(a.id, a.name, 'IsIdentity') = 1 then 1 else 0 end\n" +
                " FROM syscolumns a\n" +
                "     left join systypes b on a.xusertype = b.xusertype\n" +
                "     inner join sysobjects d on a.id = d.id and d.xtype = 'U' and d.name <> 'dtproperties'\n" +
                "     left join syscomments e on a.cdefault = e.id\n" +
                "     left join sys.extended_properties g on a.id = g.major_id and a.colid = g.minor_id\n" +
                "     left join sys.extended_properties f on d.id = f.major_id and f.minor_id = 0\n" +
                " WHERE \n" +
                "     d.name = ?\n" +
                " ORDER BY \n" +
                "     a.id, a.colorder\n";
    }


    /**
     * 通过 url 获取数据库类型
     */
    private DatabaseSchemaEnum getDatabaseTypeByUrl(String url) throws SQLException {
        if (url.contains(DatabaseSchemaEnum.MYSQL.name().toLowerCase())) {
            return DatabaseSchemaEnum.MYSQL;
        }
        if (url.contains(DatabaseSchemaEnum.SQLSERVER.name().toLowerCase())) {
            return DatabaseSchemaEnum.SQLSERVER;
        }
        if (url.contains(DatabaseSchemaEnum.ORACLE.name().toLowerCase())) {
            return DatabaseSchemaEnum.ORACLE;
        }
        throw new SQLException("未知JDBC连接地址");
    }

    /**
     * 获取连接
     */
    private Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(generatorProperties.getUrl(), generatorProperties.getUsername(), generatorProperties.getPassword());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }


}

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${daoPackage!""}.${tableInfo.upperCamelCaseName!""}Dao">
<#assign modelPath = "${entityPackage!''}.${tableInfo.upperCamelCaseName!''}DO">
    <resultMap type="${modelPath}" id="commonMap">
<#if tableInfo.columnInfoList?? && (tableInfo.columnInfoList?size > 0)>
    <#list tableInfo.columnInfoList as item>
        <result column="${item.name!""}" property="${item.firstLowerCamelCaseName!""}"/>
    </#list>
<#else>
</#if>
    </resultMap>

    <sql id="tableName">
        ${tableInfo.tableName}
    </sql>

    <sql id="columns">
<#-- 是否有 ID 字段-->
<#assign hasId = 0>
<#if tableInfo.columnInfoList?? && (tableInfo.columnInfoList?size > 0)>
    <#list tableInfo.columnInfoList as item>
        <#if item.name?lower_case == "id">
            <#assign hasId = 1>
            <#continue>
        </#if>
        ${item.name!""}<#if item_has_next>,</#if>
    </#list>
<#else>
</#if>
    </sql>

<#if hasId == 1>
    <sql id="columnsWithId">
        id,
        <include refid="columns"/>
    </sql>
<#else>
</#if>

    <sql id="entities">
<#if tableInfo.columnInfoList?? && (tableInfo.columnInfoList?size > 0)>
    <#list tableInfo.columnInfoList as item>
        <#if item.name?lower_case == "id">
            <#continue>
        </#if>
        <#noparse>#{</#noparse>${item.firstLowerCamelCaseName}<#noparse>}</#noparse><#if item_has_next>,</#if>
    </#list>
<#else>
</#if>
    </sql>

<#if hasId == 1>
    <sql id="entitiesWithId">
        <#noparse>#{id},</#noparse>
        <include refid="entities"/>
    </sql>
<#else>
</#if>

    <insert id="save" <#if hasId == 1>keyColumn="id" keyProperty="id" <#if autoIncrease>useGeneratedKeys="true"</#if></#if>>
        INSERT INTO
        <include refid="tableName"/>
        (
<#if !autoIncrease && hasId == 1>
        <include refid="columnsWithId"/>
    <#else>
        <include refid="columns"/>
</#if>
        )
        VALUES
        (
<#if !autoIncrease && hasId == 1>
        <include refid="entitiesWithId"/>
        <#else>
        <include refid="entities"/>
</#if>
        )
    </insert>
<#if hasId == 1>

    <update id="update" parameterType="${modelPath}">
        UPDATE
        <include refid="tableName"/>
        <trim prefix="SET" suffixOverrides=",">
<#if tableInfo.columnInfoList?? && (tableInfo.columnInfoList?size > 0)>
    <#list tableInfo.columnInfoList as item>
        <#if item.firstLowerCamelCaseName != "id" && item.firstLowerCamelCaseName != "createUserId" && item.firstLowerCamelCaseName != "createTime">
            <if test="${item.firstLowerCamelCaseName} != null<#if item.fieldDataType?? && item.fieldDataType == "String"> and ${item.firstLowerCamelCaseName} != ''</#if>">
                ${item.columnName!""} = <#noparse>#{</#noparse>${item.firstLowerCamelCaseName}<#noparse>}</#noparse><#if item_has_next>,</#if>
            </if>
        </#if>
    </#list>
<#else>
</#if>
        </trim>
        WHERE
        id = <#noparse>#{id}</#noparse>
    </update>

    <delete id="deleteById">
        DELETE FROM
        <include refid="tableName"/>
        WHERE
        id = <#noparse>#{id}</#noparse>
    </delete>

    <select id="getById" resultMap="commonMap">
        SELECT
        <include refid="columnsWithId"/>
        FROM
        <include refid="tableName"/>
        WHERE
        id = <#noparse>#{id}</#noparse>
    </select>

<#if tableInfo.databaseSchema == "DatabaseSchemaEnum.MYSQL">
    <select id="isExistById" resultType="java.lang.Boolean">
        SELECT EXISTS
        (
        SELECT 1 FROM
        <include refid="tableName"/>
        WHERE
        id = <#noparse>#{id}</#noparse>
        )
    </select>
</#if>
<#if tableInfo.databaseSchema == "DatabaseSchemaEnum.SQLSERVER">
    <select id="isExistById" resultType="java.lang.Boolean">
        SELECT ISNULL(
        (
        SELECT TOP(1) 1 from
        <include refid="tableName"/>
        WHERE
        id = <#noparse>#{id}</#noparse>
        ),0)
    </select>
</#if>
</#if>

    <select id="list" resultMap="commonMap">
        SELECT
<#if hasId == 1>
        <include refid="columnsWithId"/>
<#else>
        <include refid="columns"/>
</#if>
        FROM
        <include refid="tableName"/>
        <include refid="queryCondition"/>
    </select>

    <sql id="queryCondition">
        <trim prefix="where" prefixOverrides="and">
            <if test="query != null">
<#if tableInfo.columnInfoList?? && (tableInfo.columnInfoList?size > 0)>
    <#list tableInfo.columnInfoList as item>
                <if test="query.${item.firstLowerCamelCaseName} != null<#if item.dataType?? && (item.dataType == "Integer" || item.dataType == "Short" || item.dataType == "Long")> and query.${item.firstLowerCamelCaseName} > 0 </#if>">
                    <#if item.dataType == "String">
                    AND
                    ${item.name!""} LIKE CONCAT('%', <#noparse>#{</#noparse>query.${item.firstLowerCamelCaseName}<#noparse>}</#noparse>, '%')
                    <#else>
                    AND
                    ${item.name!""} = <#noparse>#{</#noparse>query.${item.firstLowerCamelCaseName}<#noparse>}</#noparse>
                    </#if>
                </if>
    </#list>
<#else>
</#if>
            </if>
        </trim>
    </sql>

</mapper>

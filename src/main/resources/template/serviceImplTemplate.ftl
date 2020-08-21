<#assign modelClassName = "${tableInfo.upperCamelCaseName!''}DO">
<#assign modelFieldName = "${tableInfo.lowerCamelCaseName!''}DO">
<#assign daoClassName = "${tableInfo.upperCamelCaseName!''}Dao">
<#assign daoFieldName = "${tableInfo.lowerCamelCaseName!''}Dao">
<#-- 是否有 ID 字段-->
<#assign hasId = 0>
<#assign hasUpdateTime = 0>
<#if tableInfo.columnInfoList?? && (tableInfo.columnInfoList?size > 0)>
    <#list tableInfo.columnInfoList as item>
        <#if item.name?lower_case == "id">
            <#assign hasId = 1>
        </#if>
        <#if item.name?lower_case == "update_time">
            <#assign hasUpdateTime = 1>
        </#if>
    </#list>
<#else>
</#if>
package ${servicePackage!""}.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import ${projectPackage!""}.component.exception.ServiceException;
import ${projectPackage!""}.component.response.DelResInfo;
import ${projectPackage!""}.component.response.ResCode;
import ${projectPackage!""}.component.response.ResList;
import ${projectPackage!""}.component.response.ResResult;
import ${entityPackage!""}.${modelClassName};
import ${daoPackage!""}.${daoClassName};
import ${servicePackage!""}.${tableInfo.upperCamelCaseName!''}Service;
import ${projectPackage!""}.util.container.ContainerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author ${author}
 * @version ${version}
 * @date ${.now?string("yyyy/MM/dd")}
 * @description ${description}
 **/
@Service
public class ${tableInfo.upperCamelCaseName!''}ServiceImpl implements ${tableInfo.upperCamelCaseName!''}Service {

    private ${daoClassName} ${daoFieldName};

    @Autowired
    public ${tableInfo.upperCamelCaseName!''}ServiceImpl(${daoClassName} ${daoFieldName}) {
        this.${daoFieldName} = ${daoFieldName};
    }

    @Override
    public ResResult list(${modelClassName} query) throws ServiceException {
        PageHelper.startPage(query);
        List<${modelClassName}> list = ${daoFieldName}.list(query);
        if (ContainerUtil.isNotEmpty(list)) {
            ResList<${modelClassName}> resList = ResList.page(list, ((Page) list).getTotal());
            return ResResult.success(resList);
        }
        return ResResult.fail(ResCode.NOT_FOUND);
    }

<#if hasId == 1>
    @Override
    public ResResult getById(Serializable id) {
        ${modelClassName} object = ${daoFieldName}.getById(id);
        if (Objects.nonNull(object)) {
            return ResResult.success(object);
        }
        return ResResult.fail(ResCode.NOT_FOUND);
    }
<#else>
</#if>

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResResult save(${modelClassName} object) throws ServiceException {
        LocalDateTime now = LocalDateTime.now();
        object.setCreateTime(now);
<#if hasUpdateTime == 1>
        object.setUpdateTime(now);
</#if>
        int save = ${daoFieldName}.save(object);
        if (save > 0) {
            return ResResult.success();
        }
        return ResResult.fail();
    }

<#if hasUpdateTime == 1>
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResResult update(${modelClassName} object) throws ServiceException {
        object.setUpdateTime(LocalDateTime.now());
        int update = ${daoFieldName}.update(object);
        if (update > 0) {
            return ResResult.success();
        }
        return ResResult.fail();
    }
</#if>

<#if hasId == 1>
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResResult deleteByIds(Serializable[] ids) throws ServiceException {
        DelResInfo delResInfo = new DelResInfo();
        for (Serializable id : ids) {
            if (${daoFieldName}.deleteById(id) > 0) {
                // 不存在且已删除则将 id 添加到已删除列表
                delResInfo.addDeleted(id);
            } else {
                delResInfo.addNotDelete(id);
            }
        }
        if (ContainerUtil.isNotEmpty(delResInfo.getNotDelete())) {
            return ResResult.response(ResCode.OK, "有${name}对象删除失败", delResInfo);
        }
        return ResResult.success(delResInfo);
    }
<#else>
</#if>
}

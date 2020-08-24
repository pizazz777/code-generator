<#assign modelClassName = "${tableInfo.upperCamelCaseName!''}DO">
<#assign modelFieldName = "${tableInfo.lowerCamelCaseName!''}DO">
<#assign serviceClassName = "${tableInfo.upperCamelCaseName!''}Service">
<#assign serviceFieldName = "${tableInfo.lowerCamelCaseName!''}Service">
<#assign permissionPrefix = "${tableInfo.tableName?replace('_','-')}">
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
package ${controllerPackage!""};

import ${projectPackage!""}.component.exception.ServiceException;
import ${projectPackage!""}.component.response.ResCode;
import ${projectPackage!""}.component.response.ResResult;
import java.util.Objects;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ${entityPackage!""}.${modelClassName};
import ${servicePackage!""}.${serviceClassName};

/**
 * @author ${author}
 * @version ${version}
 * @date ${.now?string("yyyy/MM/dd")}
 * @description: ${description}
 **/
@Api(tags = "${moduleName}模块-${name}")
@RestController
@RequestMapping("/${tableInfo.tableName}")
public class ${tableInfo.upperCamelCaseName}Controller {

    private ${serviceClassName} ${serviceFieldName};

    @Autowired
    public ${tableInfo.upperCamelCaseName}Controller(${serviceClassName} ${serviceFieldName}) {
        this.${serviceFieldName} = ${serviceFieldName};
    }

    private static final String LIST_DESC = "获取${name}列表";

    @ApiOperation(value = LIST_DESC)
    @ApiImplicitParam(name = "${modelFieldName}", value = "${name}对象", required = true, dataTypeClass = ${modelClassName}.class)
    @GetMapping(value = "/list")
    public String list(@ModelAttribute ${modelClassName} query) throws ServiceException {
        ResResult result = ${serviceFieldName}.list(query);
        return result.getStr(LIST_DESC);
    }

<#if hasId == 1>
    private static final String GET_DESC = "获取${name}对象";

    @ApiOperation(value = GET_DESC)
    @ApiImplicitParam(name = "id", value = " ${name}对象ID", required = true, dataType = "Long")
    @GetMapping(value = "/get_by_id")
    public String getById(@RequestParam(value = "id") Long id) throws ServiceException {
        ResResult result = ${serviceFieldName}.getById(id);
        return result.getStr(GET_DESC);
    }

</#if>

    private static final String SAVE_DESC = "新增${name}对象";

    @ApiOperation(value = SAVE_DESC)
    @ApiImplicitParam(name = "${modelFieldName}", value = "${name}对象", required = true, dataTypeClass = ${modelClassName}.class)
    @PostMapping(value = "/save")
    public String save(@ModelAttribute ${modelClassName} object) throws ServiceException {
        // 接口校验
        String verifyResult = legalParam(object);
        if (Objects.nonNull(verifyResult)) {
            return ResResult.fail(ResCode.ILLEGAL_PARAM, verifyResult).getStr(SAVE_DESC);
        }
        ResResult result = ${serviceFieldName}.save(object);
        return result.getStr(SAVE_DESC);
    }

<#if hasUpdateTime == 1>
    private static final String UPDATE_DESC = "修改${name}对象";

    @ApiOperation(value = UPDATE_DESC)
    @ApiImplicitParam(name = "${modelFieldName}", value = " ${name}对象", required = true, dataTypeClass = ${modelClassName}.class)
    @PostMapping(value = "/update")
    public String update(@ModelAttribute ${modelClassName} object) throws ServiceException {
        // 接口校验
        String verifyResult = legalParam(object);
        if (Objects.nonNull(verifyResult)) {
            return ResResult.fail(ResCode.ILLEGAL_PARAM, verifyResult).getStr(UPDATE_DESC);
        }
        ResResult result = ${serviceFieldName}.update(object);
        return result.getStr(UPDATE_DESC);
    }
</#if>

<#if hasId == 1>
    private static final String DELETE_DESC = "删除${name}对象";

    @ApiOperation(value = DELETE_DESC)
    @ApiImplicitParam(name = "ids", value = " ${name}对象ID列表", required = true, dataType = "Long")
    @PostMapping(value = "/delete_by_ids")
    public String deleteByIds(@RequestParam(value = "ids") Long[] ids) throws ServiceException {
        ResResult result = ${serviceFieldName}.deleteByIds(ids);
        return result.getStr(DELETE_DESC);
    }
</#if>

    /**
    * 参数校验
    */
    private String legalParam(${modelClassName} object) {

        return null;
    }

}

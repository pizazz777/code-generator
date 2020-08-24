package ${entityPackage!""};

import lombok.*;
import ${projectPackage!""}.entity.PageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ${author}
 * @version ${version}
 * @date ${.now?string("yyyy/MM/dd")}
 * @description ${description}
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(${name})
public class ${tableInfo.upperCamelCaseName!""}DO extends PageBean implements Serializable {

    private static final long serialVersionUID = 1L;

<#if tableInfo.columnInfoList?? && (tableInfo.columnInfoList?size > 0)>
<#list tableInfo.columnInfoList as item>

    @ApiModelProperty("${item.comment!""}")
    private ${item.fieldDataType!""} ${item.firstLowerCamelCaseName!""};
</#list>
<#else>
</#if>

    /* ------------------ 非数据库数据分割线 ------------------ */

}

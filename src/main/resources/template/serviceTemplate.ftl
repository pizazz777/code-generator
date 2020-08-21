package ${servicePackage!""};

import ${entityPackage!''}.${tableInfo.upperCamelCaseName!''}DO;
import ${projectPackage!""}.service.BaseService;

/**
 * @author ${author}
 * @version ${version}
 * @date ${.now?string("yyyy/MM/dd")}
 * @description ${description}
 **/
public interface ${tableInfo.upperCamelCaseName!''}Service extends BaseService<${tableInfo.upperCamelCaseName!''}DO> {

}

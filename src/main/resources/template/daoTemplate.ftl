package ${daoPackage!""};

import ${entityPackage!''}.${tableInfo.upperCamelCaseName!''}DO;
import ${projectPackage!""}.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * @author ${author}
 * @version ${version}
 * @date ${.now?string("yyyy/MM/dd")}
 * @description ${description}
 **/
@Mapper
@Repository
public interface ${tableInfo.upperCamelCaseName!''}Dao extends BaseDao<${tableInfo.upperCamelCaseName!''}DO> {

}

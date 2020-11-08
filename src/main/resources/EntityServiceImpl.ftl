package ${topPackage}.service${serviceSubPackage}.impl;

import com.frog.backend.components.mysql.component.mapper.BaseMapper;
import com.frog.backend.components.mysql.component.service.impl.BaseServiceImpl;
import ${topPackage}.pojo.domain.${EntityName};
import ${topPackage}.service${serviceSubPackage}.${EntityName}Service;
import ${topPackage}.mapper.${EntityName}Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * Description ${EntityName}ServiceImpl
 *
 * @author yxy
 * @date ${currentDate}
 */
@Slf4j
@Service
public class ${EntityName}ServiceImpl extends BaseServiceImpl<${EntityName},Long> implements ${EntityName}Service {

	@Autowired
	private ${EntityName}Mapper ${entityName}Mapper;

    @Override
    protected BaseMapper<${EntityName}> getMapper() {
        return ${entityName}Mapper;
    }

}
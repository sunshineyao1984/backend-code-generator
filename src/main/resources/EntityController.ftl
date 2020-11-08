package ${topPackage}.controller${controllerSubPackage};

import ${topPackage}.pojo.domain.${EntityName};
import ${topPackage}.service${serviceSubPackage}.${EntityName}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

/**
 * Description ${EntityName}Controller
 *
 * @author yxy
 * @date ${currentDate}
 */
@Slf4j
@RestController
public class ${EntityName}Controller {

	@Autowired
	private ${EntityName}Service ${entityName}Service;

}
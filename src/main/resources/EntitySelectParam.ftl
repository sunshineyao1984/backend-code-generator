package ${topPackage}.api.pojo.param.${paramSubPackage};

import com.frog.backend.components.mysql.component.pojo.param.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Description ${EntityName}Param
 *
 * @author yxy
 * @date ${currentDate}
 */
@Data
public class ${EntityName}${camelParamSubPackage}Param extends PageParam {
    <#list baseDataList as data>

    @ApiModelProperty(value = "${data.columnComment}")
    private ${data.columnType} ${data.columnName};
    </#list>

}
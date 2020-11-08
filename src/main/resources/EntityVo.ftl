package ${topPackage}.api.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Description ${EntityName}Vo
 *
 * @author yxy
 * @date ${currentDate}
 */
@Data
public class ${EntityName}Vo {
    <#list baseDataList as data>

    @ApiModelProperty(value = "${data.columnComment}")
    private ${data.columnType} ${data.columnName};
    </#list>

}
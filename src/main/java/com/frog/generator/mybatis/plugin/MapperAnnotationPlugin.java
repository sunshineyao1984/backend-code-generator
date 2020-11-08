package com.frog.generator.mybatis.plugin;

import com.frog.backend.components.commons.util.TimeUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * Description
 *
 * @author yxy
 * @date 2019/03/27
 */
public class MapperAnnotationPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Repository"));
        interfaze.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * Description ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append("表Mapper");
        interfaze.addJavaDocLine(sb.toString());
        interfaze.addJavaDocLine(" * ");
        interfaze.addJavaDocLine(" * @author yxy");
        StringBuilder sbData = new StringBuilder();
        sbData.append(" * @date ");
        sbData.append(TimeUtils.millisToString(System.currentTimeMillis(), TimeUtils.TimeFormat.SHORT_DATE_PATTERN_SLASH));
        interfaze.addJavaDocLine(sbData.toString());
        interfaze.addJavaDocLine(" */");
        interfaze.addAnnotation("@Repository");
        return true;
    }
}

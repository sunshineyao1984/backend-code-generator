package com.frog.generator.mybatis.plugin.comment;

import com.frog.backend.components.commons.util.TimeUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.Properties;

/**
 * Description 自定义注释生成器
 *
 * @author yxy
 * @date 2019/03/27
 */
public class MyCommentGenerator extends DefaultCommentGenerator {

    private boolean suppressAllComments;

    private boolean addRemarkComments;

    /**
     * 设置用户配置的参数
     * @param properties
     */
    @Override
    public void addConfigurationProperties(Properties properties) {
        //先调用父类方法保证父类方法可以正常使用
        super.addConfigurationProperties(properties);
        //获取suppressAllComments参数值
        suppressAllComments = StringUtility.isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
        //获取addRemarkComments参数值
        addRemarkComments = true;
        //StringUtility.isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS));
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        if (!this.suppressAllComments) {
            innerClass.addJavaDocLine("/**");
            innerClass.addJavaDocLine(" * Description ");
            innerClass.addJavaDocLine(" * ");
            innerClass.addJavaDocLine(" * @author yxy");
            StringBuilder sb = new StringBuilder();
            sb.append(" * @date ");
            sb.append(TimeUtils.millisToString(System.currentTimeMillis(), TimeUtils.TimeFormat.SHORT_DATE_PATTERN_SLASH));
            innerClass.addJavaDocLine(sb.toString());
            innerClass.addJavaDocLine(" */");
        }
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        addClassComment(innerClass,introspectedTable);
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (!this.suppressAllComments && this.addRemarkComments) {
            //添加import


            topLevelClass.addJavaDocLine("/**");
            StringBuilder sb = new StringBuilder();
            sb.append(" * Description ");
            sb.append(introspectedTable.getFullyQualifiedTable());
            sb.append("表DO");
            topLevelClass.addJavaDocLine(sb.toString());
            topLevelClass.addJavaDocLine(" * ");
            topLevelClass.addJavaDocLine(" * @author yxy");
            StringBuilder sbData = new StringBuilder();
            sbData.append(" * @date ");
            sbData.append(TimeUtils.millisToString(System.currentTimeMillis(), TimeUtils.TimeFormat.SHORT_DATE_PATTERN_SLASH));
            topLevelClass.addJavaDocLine(sbData.toString());
            topLevelClass.addJavaDocLine(" */");
        }
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        //如果阻止生成所有注释，直接返回
        if(suppressAllComments){
            return;
        }
        //文档注释开始
        field.addJavaDocLine("/**");
        //获取数据库字段的备注信息
        String remarks = introspectedColumn.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for(String remarkLine : remarkLines) {
                field.addJavaDocLine(" * " + remarkLine);
            }
        }
        field.addJavaDocLine(" */");
        if(introspectedTable.getPrimaryKeyColumns().contains(introspectedColumn)){
            field.addAnnotation("@Id");
            field.addAnnotation("@KeySql(useGeneratedKeys = true)");
            field.addAnnotation("@JsonSerialize(using = ToStringSerializer.class)");
        }
        if("version".equals(introspectedColumn.getActualColumnName())){
            field.addAnnotation("@Version");
        }
    }
}

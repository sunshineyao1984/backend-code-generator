package com.frog.generator.mybatis.plugin.resolver;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Description 自定义类型转换
 *
 * @author yxy
 * @date 2019/03/27
 */
public class MyJavaTypeResolver extends JavaTypeResolverDefaultImpl {

    public MyJavaTypeResolver() {
        super();
        //将tinyint转换为Integer
        super.typeMap.put(-6, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TINYINT", new FullyQualifiedJavaType(Integer.class.getName())));
        super.typeMap.put(91, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("DATE", new FullyQualifiedJavaType(LocalDate.class.getName())));
        super.typeMap.put(92, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TIME", new FullyQualifiedJavaType(LocalTime.class.getName())));
        super.typeMap.put(93, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TIMESTAMP", new FullyQualifiedJavaType(LocalDateTime.class.getName())));
    }

}

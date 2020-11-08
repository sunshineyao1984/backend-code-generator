package com.frog.generator.mybatis.plugin;

import com.frog.backend.components.commons.util.TimeUtils;
import com.frog.generator.mybatis.plugin.model.BaseData;
import freemarker.core.ParseException;
import freemarker.template.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.*;
import java.util.*;

/**
 * Description Service和Controller生成插件
 *
 * @author yxy
 * @date 2019/03/25
 */
@Slf4j
public class ServiceControllerPlugin extends PluginAdapter {

    private String topPackage = "";
    private String serviceSubPackage = "";
    private String controllerSubPackage = "";
    private String insertParamSubPackage = "insert";
    private String updateParamSubPackage = "update";
    private String selectParamSubPackage = "select";
    private String voSubPackage = "vo";
    private String apiPackage = "/api/";
    private String apiProject = "";

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String topPackage = this.properties.getProperty("topPackage");
        if(StringUtility.stringHasValue(topPackage)){
            this.topPackage = topPackage;
        }
        String serviceSubPackage = this.properties.getProperty("serviceSubPackage");
        if(StringUtility.stringHasValue(serviceSubPackage)){
            this.serviceSubPackage = serviceSubPackage;
        }
        String controllerSubPackage = this.properties.getProperty("controllerSubPackage");
        if(StringUtility.stringHasValue(controllerSubPackage)){
            this.controllerSubPackage = controllerSubPackage;
        }
        String apiProject = this.properties.getProperty("apiProject");
        if(StringUtility.stringHasValue(apiProject)){
            this.apiProject = apiProject;
        }
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {


        String javaRepositoryPackage = this.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String javaMapperType = introspectedTable.getMyBatis3JavaMapperType();
//        String topPackage = javaRepositoryPackage.substring(0, javaRepositoryPackage.lastIndexOf('.'));
        String javaClassName = javaMapperType.substring(javaMapperType.lastIndexOf('.') + 1, javaMapperType.length()).replace("Mapper", "");
        String targetProject = this.getContext().getJavaClientGeneratorConfiguration().getTargetProject();


        Map<String, Object> root = new HashMap<String, Object>();
        root.put("topPackage", topPackage);
        root.put("EntityName", javaClassName);
        root.put("entityName", new StringBuilder().append(Character.toLowerCase(javaClassName.charAt(0)))
                .append(javaClassName.substring(1)).toString());
        root.put("serviceSubPackage",StringUtility.stringHasValue(serviceSubPackage)?"."+serviceSubPackage:"");
        root.put("controllerSubPackage",StringUtility.stringHasValue(controllerSubPackage)?"."+controllerSubPackage:"");
        root.put("currentDate", TimeUtils.millisToString(System.currentTimeMillis(), TimeUtils.TimeFormat.SHORT_DATE_PATTERN_SLASH));

        genParam(introspectedTable,apiProject, topPackage+apiPackage,insertParamSubPackage, javaClassName, root);
        genParam(introspectedTable,apiProject, topPackage+apiPackage,updateParamSubPackage, javaClassName, root);
        genSelectParam(introspectedTable,apiProject, topPackage+apiPackage,selectParamSubPackage, javaClassName, root);
        genVo(introspectedTable,apiProject, topPackage+apiPackage, javaClassName, root);
        genService(targetProject, topPackage, javaClassName, root);
        genServiceImpl(targetProject, topPackage, javaClassName, root);
        genController(targetProject, topPackage, javaClassName, root);

        return null;
    }

    private void genParam(IntrospectedTable introspectedTable,String targetProject, String topPackage,String paramSubPackage, String javaClassName, Map<String, Object> root) {
        List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
        List<BaseData> baseDataList = new ArrayList<BaseData>();
        for(IntrospectedColumn column : columnList){
            BaseData baseData = new BaseData();
            baseData.setColumnName(column.getJavaProperty());
            baseData.setColumnType(column.getFullyQualifiedJavaType().getShortName());
            baseData.setColumnComment(column.getRemarks());
            baseDataList.add(baseData);
        }
        root.put("baseDataList", baseDataList);
        root.put("paramSubPackage",paramSubPackage);


        StringBuilder sbDomainVoPath = new StringBuilder();
        sbDomainVoPath.append("/pojo/param/");
        if(StringUtility.stringHasValue(paramSubPackage)){
            sbDomainVoPath.append(paramSubPackage);
            sbDomainVoPath.append("/");
        }
        String camelParamSubPackage = paramSubPackage.substring(0,1).toUpperCase()+paramSubPackage.substring(1);
        root.put("camelParamSubPackage", camelParamSubPackage);
        String dirPath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbDomainVoPath.toString();
        String filePath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbDomainVoPath.toString() + javaClassName
                + camelParamSubPackage +"Param.java";
        File dir = new File(dirPath);
        File file = new File(filePath);

        try {
            dir.mkdirs();
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            log.error("",e);
        }

        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());


        try {
            Template temp = cfg.getTemplate("EntityParam.ftl");
            Writer out = new OutputStreamWriter(new FileOutputStream(file));
            temp.process(root, out);
            out.flush();
        } catch (TemplateNotFoundException e) {
            log.error("",e);
        } catch (MalformedTemplateNameException e) {
            log.error("",e);
        } catch (ParseException e) {
            log.error("",e);
        } catch (IOException e) {
            log.error("",e);
        } catch (TemplateException e) {
            log.error("",e);
        }
    }

    private void genSelectParam(IntrospectedTable introspectedTable,String targetProject, String topPackage,String paramSubPackage, String javaClassName, Map<String, Object> root) {
        List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
        List<BaseData> baseDataList = new ArrayList<BaseData>();
        for(IntrospectedColumn column : columnList){
            BaseData baseData = new BaseData();
            baseData.setColumnName(column.getJavaProperty());
            baseData.setColumnType(column.getFullyQualifiedJavaType().getShortName());
            baseData.setColumnComment(column.getRemarks());
            baseDataList.add(baseData);
        }
        root.put("baseDataList", baseDataList);
        root.put("paramSubPackage",paramSubPackage);


        StringBuilder sbDomainVoPath = new StringBuilder();
        sbDomainVoPath.append("/pojo/param/");
        if(StringUtility.stringHasValue(paramSubPackage)){
            sbDomainVoPath.append(paramSubPackage);
            sbDomainVoPath.append("/");
        }
        String camelParamSubPackage = paramSubPackage.substring(0,1).toUpperCase()+paramSubPackage.substring(1);
        root.put("camelParamSubPackage", camelParamSubPackage);
        String dirPath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbDomainVoPath.toString();
        String filePath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbDomainVoPath.toString() + javaClassName
                + camelParamSubPackage +"Param.java";
        File dir = new File(dirPath);
        File file = new File(filePath);

        try {
            dir.mkdirs();
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            log.error("",e);
        }

        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());


        try {
            Template temp = cfg.getTemplate("EntitySelectParam.ftl");
            Writer out = new OutputStreamWriter(new FileOutputStream(file));
            temp.process(root, out);
            out.flush();
        } catch (TemplateNotFoundException e) {
            log.error("",e);
        } catch (MalformedTemplateNameException e) {
            log.error("",e);
        } catch (ParseException e) {
            log.error("",e);
        } catch (IOException e) {
            log.error("",e);
        } catch (TemplateException e) {
            log.error("",e);
        }
    }

    private void genVo(IntrospectedTable introspectedTable,String targetProject, String topPackage, String javaClassName, Map<String, Object> root) {
        List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
        List<BaseData> baseDataList = new ArrayList<BaseData>();
        for(IntrospectedColumn column : columnList){
            BaseData baseData = new BaseData();
            baseData.setColumnName(column.getJavaProperty());
            baseData.setColumnType(column.getFullyQualifiedJavaType().getShortName());
            baseData.setColumnComment(column.getRemarks());
            baseDataList.add(baseData);
        }
        root.put("baseDataList", baseDataList);



        StringBuilder sbDomainVoPath = new StringBuilder();
        sbDomainVoPath.append("/pojo/");
        if(StringUtility.stringHasValue(voSubPackage)){
            sbDomainVoPath.append(voSubPackage);
            sbDomainVoPath.append("/");
        }
        String dirPath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbDomainVoPath.toString();
        String filePath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbDomainVoPath.toString() + javaClassName
                + "Vo.java";
        File dir = new File(dirPath);
        File file = new File(filePath);

        try {
            dir.mkdirs();
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            log.error("",e);
        }

        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());


        try {
            Template temp = cfg.getTemplate("EntityVo.ftl");
            Writer out = new OutputStreamWriter(new FileOutputStream(file));
            temp.process(root, out);
            out.flush();
        } catch (TemplateNotFoundException e) {
            log.error("",e);
        } catch (MalformedTemplateNameException e) {
            log.error("",e);
        } catch (ParseException e) {
            log.error("",e);
        } catch (IOException e) {
            log.error("",e);
        } catch (TemplateException e) {
            log.error("",e);
        }
    }



    @SuppressWarnings("deprecation")
    private void genService(String targetProject, String topPackage, String javaClassName, Map<String, Object> root) {
        StringBuilder sbServicePath = new StringBuilder();
        sbServicePath.append("/service/");
        if(StringUtility.stringHasValue(serviceSubPackage)){
            sbServicePath.append(serviceSubPackage);
            sbServicePath.append("/");
        }
        String dirPath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbServicePath.toString();
        String filePath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbServicePath.toString() + javaClassName
                + "Service.java";
        File dir = new File(dirPath);
        File file = new File(filePath);

        try {
            dir.mkdirs();
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            log.error("",e);
        }

        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());


        try {
            Template temp = cfg.getTemplate("EntityService.ftl");
            Writer out = new OutputStreamWriter(new FileOutputStream(file));
            temp.process(root, out);
            out.flush();
        } catch (TemplateNotFoundException e) {
            log.error("",e);
        } catch (MalformedTemplateNameException e) {
            log.error("",e);
        } catch (ParseException e) {
            log.error("",e);
        } catch (IOException e) {
            log.error("",e);
        } catch (TemplateException e) {
            log.error("",e);
        }


    }

    @SuppressWarnings("deprecation")
    private void genServiceImpl(String targetProject, String topPackage, String javaClassName, Map<String, Object> root) {
        StringBuilder sbServiceImplPath = new StringBuilder();
        sbServiceImplPath.append("/service/");
        if(StringUtility.stringHasValue(serviceSubPackage)){
            sbServiceImplPath.append(serviceSubPackage);
            sbServiceImplPath.append("/");
        }
        sbServiceImplPath.append("impl/");
        String dirPath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbServiceImplPath.toString();
        String filePath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbServiceImplPath.toString() + javaClassName
                + "ServiceImpl.java";
        File dir = new File(dirPath);
        File file = new File(filePath);

        try {
            dir.mkdirs();
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            log.error("",e);
        }

        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());


        try {
            Template temp = cfg.getTemplate("EntityServiceImpl.ftl");
            Writer out = new OutputStreamWriter(new FileOutputStream(file));
            temp.process(root, out);
            out.flush();
        } catch (TemplateNotFoundException e) {
            log.error("",e);
        } catch (MalformedTemplateNameException e) {
            log.error("",e);
        } catch (ParseException e) {
            log.error("",e);
        } catch (IOException e) {
            log.error("",e);
        } catch (TemplateException e) {
            log.error("",e);
        }


    }


    @SuppressWarnings("deprecation")
    private void genController(String targetProject, String topPackage, String javaClassName,
                               Map<String, Object> root) {
        StringBuilder sbControllerPath = new StringBuilder();
        sbControllerPath.append("/controller/");
        if(StringUtility.stringHasValue(controllerSubPackage)){
            sbControllerPath.append(controllerSubPackage);
            sbControllerPath.append("/");
        }
        String dirPath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbControllerPath.toString();
        String filePath = targetProject + "/" + topPackage.replaceAll("\\.", "/") + sbControllerPath.toString() + javaClassName
                + "Controller.java";
        File dir = new File(dirPath);
        File file = new File(filePath);

        try {
            dir.mkdirs();
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            log.error("",e);
        }

        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());


        try {
            Template temp = cfg.getTemplate("EntityController.ftl");
            Writer out = new OutputStreamWriter(new FileOutputStream(file));
            temp.process(root, out);
            out.flush();
        } catch (TemplateNotFoundException e) {
            log.error("",e);
        } catch (MalformedTemplateNameException e) {
            log.error("",e);
        } catch (ParseException e) {
            log.error("",e);
        } catch (IOException e) {
            log.error("",e);
        } catch (TemplateException e) {
            log.error("",e);
        }


    }

    private static String firstUpperCamelCase(String str) {
        if (StringUtils.isNotBlank(str)) {
            str = str.toLowerCase();
            String[] strs = str.split("_");
            if (strs.length == 1) {
                return str.substring(0, 1).toLowerCase()+ str.substring(1);
            } else {
                String convertedStr = "";
                for (int i = 0; i < strs.length; i++) {
                    convertedStr += firstLetterUpper(strs[i]);
                }
                return convertedStr.substring(0, 1).toLowerCase()+ convertedStr.substring(1);
            }
        }
        return str;
    }

    private static String firstLetterUpper(String str) {
        if (StringUtils.isNotBlank(str)) {
            str = str.toLowerCase();
            return str.substring(0, 1).toUpperCase()
                    + str.substring(1, str.length());
        }
        return str;
    }


}

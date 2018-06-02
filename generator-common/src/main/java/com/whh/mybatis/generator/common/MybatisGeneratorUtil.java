package com.whh.mybatis.generator.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 代码生成类
 * Created by huahui.wu on 2017/1/10.
 */
public class MybatisGeneratorUtil {

    // generatorConfig模板路径
    private static String generatorConfig_vm = "/template/generatorConfig.vm";

    private static final String paginationOralce = "com.whh.common.generator.PaginationOraclePlugin";
    private static final String paginationMysql = "com.whh.common.generator.MysqlPaginationPlugin";

    private static final String datePluginOralce = "com.whh.common.generator.DatePlugin";
    private static final String datePluginMysql = "com.whh.common.generator.DatePluginMysql";

    // Service模板路径
//	private static String service_vm = "/template/Service.vm";
    // ServiceMock模板路径
//	private static String serviceMock_vm = "/template/ServiceMock.vm";
    // ServiceImpl模板路径
//	private static String serviceImpl_vm = "/template/ServiceImpl.vm";

    // Service模板路径
    private static String manager_vm = "/template/Manager.vm";
    // ServiceMock模板路径
    private static String managerMock_vm = "/template/ManagerMock.vm";
    // ServiceImpl模板路径
    private static String managerImpl_vm = "/template/ManagerImpl.vm";

    /**
     * 根据模板生成generatorConfig.xml文件
     *
     * @param jdbc_driver   驱动路径
     * @param jdbc_url      链接
     * @param jdbc_username 帐号
     * @param jdbc_password 密码
     * @param module        项目模块
     * @param database      数据库
     * @param table_names   表前缀
     * @param package_name  包名
     */
    public static void generator(
            String jdbc_driver,
            String jdbc_url,
            String jdbc_username,
            String jdbc_password,
            String targetProject,
            String module,
            String database,
            String table_names,
            String package_name,
            Map<String, String> last_insert_id_tables) throws Exception {
        generator(targetProject, module, table_names, package_name, last_insert_id_tables, "mysql");
    }

    /**
     * 根据模板生成generatorConfig.xml文件
     *
     * @param module       项目模块
     * @param table_names  表前缀
     * @param package_name 包名
     */
    public static void generator(
            String targetProject,
            String module,
            String table_names,
            String package_name,
            Map<String, String> last_insert_id_tables, String dbType) throws Exception {

        String module_path = targetProject + module + "-dao/src/main/resources/generatorConfig.xml";
        String DaoModule = targetProject + module + "-dao";
        String ManagerModule = targetProject + module + "-biz";
        String CommonModule = targetProject + module + "-common";

        //String sql = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = '" + database + "' AND table_name LIKE '" + table_prefix + "_%';";

        System.out.println("========== 开始生成generatorConfig.xml文件 ==========");
        List<Map<String, Object>> tables = new ArrayList<>();
        try {
            VelocityContext context = new VelocityContext();
            Map<String, Object> table;

            // 查询定制前缀项目的所有表
//			JdbcUtil jdbcUtil = new JdbcUtil(jdbc_driver, jdbc_url, jdbc_username, AESUtil.AESDecode(jdbc_password));
//			List<Map> result = jdbcUtil.selectByParams(sql, null);
            String[] result = table_names.split(",");
            for (String tn : result) {
                System.out.println(tn);
                table = new HashMap<>();
                table.put("table_name", tn);
                table.put("model_name", lineToHump(ObjectUtils.toString(tn)));
                tables.add(table);
            }
//			jdbcUtil.release();


            context.put("tables", tables);
            context.put("generator_javaModelGenerator_targetPackage", package_name + ".dao.model");
            context.put("generator_sqlMapGenerator_targetPackage", package_name + ".dao.mapper");
            context.put("generator_javaClientGenerator_targetPackage", package_name + ".dao.mapper");
            context.put("generator_javaClientGenerator_paginationPlugin", "mysql".equals(dbType) ? paginationMysql : paginationOralce);
            context.put("generator_javaClientGenerator_datePlugin", "mysql".equals(dbType) ? datePluginMysql : datePluginOralce);
            context.put("targetProject", DaoModule);
            context.put("targetProject_sqlMap", DaoModule);
            //context.put("generator_jdbc_password", jdbc_password);
            context.put("last_insert_id_tables", last_insert_id_tables);
            VelocityUtil.generate(generatorConfig_vm, module_path, context);
            // 删除旧代码
//			deleteDir(new File(targetProject + "/src/main/java/" + package_name.replaceAll("\\.", "/") + "/dao/model"));
//			deleteDir(new File(targetProject + "/src/main/java/" + package_name.replaceAll("\\.", "/") + "/dao/mapper"));
//			deleteDir(new File(targetProject_sqlMap + "/src/main/java/" + package_name.replaceAll("\\.", "/") + "/dao/mapper"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(module_path);
        System.out.println("========== 结束生成generatorConfig.xml文件 ==========");

        System.out.println("========== 开始运行MybatisGenerator ==========");
        List<String> warnings = new ArrayList<>();
        File configFile = new File(module_path);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        for (String warning : warnings) {
            System.out.println(warning);
        }
        System.out.println("========== 结束运行MybatisGenerator ==========");

        System.out.println();
        System.out.println("请检查生成的 Mapper.xml 文件,删除重复生成的代码!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println();

        System.out.println("========== 开始复制DO到DTO ==========");
        // 复制DO到DTO
        for (Map<String, Object> table : tables) {
            File doFile = new File(DaoModule + "/src/main/java/" + package_name.replaceAll("\\.", "/") + "/dao/model/" + table.get("model_name") + "DO.java");
            File dtoFile = new File(CommonModule + "/src/main/java/" + package_name.replaceAll("\\.", "/") + "/model/" + table.get("model_name") + ".java");
            if (dtoFile.exists()) {
                System.out.println("已经存在DTO对象,请手动修改DTO对象字段! " + table.get("model_name") + ".java");
                continue;
            }
            //FileUtils.copyFile(doFile, dtoFile);
            String s = FileUtils.readFileToString(doFile);
            s = s.replace(".dao.model", ".model");
            s = s.replace(table.get("model_name").toString() + "DO", table.get("model_name").toString());
            FileUtils.writeStringToFile(dtoFile, s);
        }
        System.out.println("========== 结束复制DO到DTO ==========");

        System.out.println("========== 开始生成Manager ==========");
        String ctime = new SimpleDateFormat("yyyy/M/d").format(new Date());
        String servicePath = ManagerModule + "/src/main/java/" + package_name.replaceAll("\\.", "/") + "/manager";
        String serviceImplPath = servicePath + "/impl";
        String serviceMockPath = servicePath + "/mock";
        for (int i = 0; i < tables.size(); i++) {
            String model = lineToHump(ObjectUtils.toString(tables.get(i).get("table_name")));
            String service = servicePath + "/" + model + "Manager.java";
            String serviceMock = serviceMockPath + "/" + model + "ManagerMock.java";
            String serviceImpl = serviceImplPath + "/" + model + "ManagerImpl.java";
            // 生成service
            File serviceFile = new File(service);
            if (!serviceFile.exists()) {
                VelocityContext context = new VelocityContext();
                context.put("package_name", package_name);
                context.put("model", model);
                context.put("ctime", ctime);
                VelocityUtil.generate(manager_vm, service, context);
                System.out.println(service);
            } else {
                System.out.println("已经存在 " + service);
            }
            // 生成serviceMock
            File serviceMockFile = new File(serviceMock);
            if (!serviceMockFile.exists()) {
                VelocityContext context = new VelocityContext();
                context.put("package_name", package_name);
                context.put("model", model);
                context.put("ctime", ctime);
                VelocityUtil.generate(managerMock_vm, serviceMock, context);
                System.out.println(serviceMock);
            } else {
                System.out.println("已经存在 " + serviceMock);
            }
            // 生成serviceImpl
            File serviceImplFile = new File(serviceImpl);
            if (!serviceImplFile.exists()) {
                VelocityContext context = new VelocityContext();
                context.put("package_name", package_name);
                context.put("model", model);
                context.put("mapper", toLowerCaseFirstOne(model));
                context.put("ctime", ctime);
                VelocityUtil.generate(managerImpl_vm, serviceImpl, context);
                System.out.println(serviceImpl);
            } else {
                System.out.println("已经存在 " + serviceImpl);
            }
        }
        System.out.println("========== 结束生成Manager ==========");

        System.out.println("========== 开始生成Controller ==========");
        System.out.println("========== 结束生成Controller ==========");

        System.out.println("========== 删除generatorConfig.xml ==========");
        File genConfigFile = new File(module_path);
        genConfigFile.delete();
        System.out.println();
        System.out.println("请检查生成的 Mapper.xml 文件,删除重复生成的代码!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println();
    }

    // 递归删除非空文件夹
    public static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDir(files[i]);
            }
        }
        dir.delete();
    }

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 下划线转驼峰
     *
     * @param str
     * @return
     */
    public static String lineToHump(String str) {
        if (null == str || "".equals(str)) {
            return str;
        }
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);

        str = sb.toString();
        str = str.substring(0, 1).toUpperCase() + str.substring(1);

        return str;
    }


    /**
     * 首字母转小写
     *
     * @param s
     * @return
     */
    public static String toLowerCaseFirstOne(String s) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }
}

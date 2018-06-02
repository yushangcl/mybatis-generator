package com.whh.mybatis.generator.common;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.FileWriter;

/**
 * Velocity工具类
 * Created by ZhangShuzheng on 2017/1/10.
 */
public class VelocityUtil {

    /**
     * 根据模板生成文件
     *
     * @param inputVmFilePath 模板路径
     * @param outputFilePath  输出文件路径
     * @param context
     * @throws Exception
     */
    public static void generate(String inputVmFilePath, String outputFilePath, VelocityContext context) throws Exception {
        try {
            Velocity.init();
            File outputFile = new File(outputFilePath);
            FileWriter writer = new FileWriter(outputFile);
            Velocity.evaluate(context, writer, "error", VelocityUtil.class.getResourceAsStream(inputVmFilePath));
            writer.close();
        } catch (Exception ex) {
            throw ex;
        }
    }

}

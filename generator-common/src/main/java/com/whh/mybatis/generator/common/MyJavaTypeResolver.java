package com.whh.mybatis.generator.common;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

/**
 * 定义数据库与java类型转换规则
 * Created by huahui.wu on 2017/6/29.
 */
public class MyJavaTypeResolver extends JavaTypeResolverDefaultImpl {

    /**
     * 从写calculateJavaType方法，自定义数据库与java类型转换规则
     *
     * @param introspectedColumn
     * @return
     */
    @Override
    public FullyQualifiedJavaType calculateJavaType(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType answer = null;
        JdbcTypeInformation jdbcTypeInformation = typeMap
                .get(introspectedColumn.getJdbcType());

        if (jdbcTypeInformation != null) {
            switch (introspectedColumn.getJdbcType()) {
                //自定义number类型的
                case Types.DECIMAL:
                case Types.NUMERIC:
                    if (introspectedColumn.getScale() <= 0 && introspectedColumn.getLength() <= 18 && !this.forceBigDecimals) {
                        //number长度>9或为定义长度的均生成Long类型
                        if (introspectedColumn.getLength() > 9 || introspectedColumn.getLength() == 0) {
                            answer = new FullyQualifiedJavaType(Long.class.getName());
                        } else {
                            answer = new FullyQualifiedJavaType(Integer.class.getName());
                        }
                    } else {
                        answer = jdbcTypeInformation.getFullyQualifiedJavaType();
                        answer = this.overrideDefaultType(introspectedColumn, answer);
                    }
                    break;
                default:
                    answer = jdbcTypeInformation.getFullyQualifiedJavaType();
                    answer = this.overrideDefaultType(introspectedColumn, answer);
            }
        }
        return answer;
    }
}

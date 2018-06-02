package com.whh.mybatis.generator.common;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 修改createTime,updateTime
 * Created by huahui.wuu on 2017/6/15 .
 */
public class DatePlugin extends PluginAdapter {

    private static final String CREATE_TIME_FIELD = "CREATE_TIME";
    private static final String UPDATE_TIME_FIELD = "UPDATE_TIME";
    private static final String CREATE_USER_ID_FIELD = "CREATE_USER_ID";
    private static final String CREATE_TIME_VALUE = "#{createTime,jdbcType=TIMESTAMP}";
    private static final String UPDATE_TIME_VALUE = "#{updateTime,jdbcType=TIMESTAMP}";
    private static final String CREATE_USER_ID_VALUE = "#{createUserId,jdbcType=DECIMAL}";
    private static final String CREATE_TIME_VALUE_RECORD = "#{record.createTime,jdbcType=TIMESTAMP}";
    private static final String UPDATE_TIME_VALUE_RECORD = "#{record.updateTime,jdbcType=TIMESTAMP}";
    private static final String CREATE_USER_ID_VALUE_RECORD = "#{record.createUserId,jdbcType=DECIMAL}";
    private static final String EQUAL = " = ";
    private static final String SYSDATE = "sysdate";
    private static final String CREATE_TIME_COLUMN_VALUE = CREATE_TIME_FIELD + EQUAL + CREATE_TIME_VALUE;
    private static final String CREATE_USER_ID_COLUMN_VALUE = CREATE_USER_ID_FIELD + EQUAL + CREATE_USER_ID_VALUE;
    private static final String UPDATE_TIME_COLUMN_VALUE = UPDATE_TIME_FIELD + EQUAL + UPDATE_TIME_VALUE;
    private static final String CREATE_TIME_RECORD_COLUMN_VALUE = CREATE_TIME_FIELD + EQUAL + CREATE_TIME_VALUE_RECORD;
    private static final String UPDATE_TIME_RECORD_COLUMN_VALUE = UPDATE_TIME_FIELD + EQUAL + UPDATE_TIME_VALUE_RECORD;
    private static final String CREATE_USER_ID_RECORD_COLUMN_VALUE = CREATE_USER_ID_FIELD + EQUAL + CREATE_USER_ID_VALUE_RECORD;

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /**
     * 修改Mapper的insert方法的createTime,updateTime为系统默认时间
     */
    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Element> elementList = element.getElements();

        replaceTimeFieldForInsert(elementList, CREATE_TIME_FIELD);
        replaceTimeFieldForInsert(elementList, UPDATE_TIME_FIELD);

        replaceTimeValueForInsert(elementList, CREATE_TIME_VALUE);
        replaceTimeValueForInsert(elementList, UPDATE_TIME_VALUE);
        return true;
    }

    /**
     * 修改Mapper的insertSelective方法的createTime,updateTime为系统默认时间
     */
    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Element> elementList = element.getElements();
        for (int i = 0; i < elementList.size(); i++) {
            if (!(elementList.get(i) instanceof XmlElement)) {
                continue;
            }
            XmlElement trim = (XmlElement) elementList.get(i);
            List<Element> trimList = trim.getElements();
            replaceTimeFieldForInsert(trimList, CREATE_TIME_FIELD);
            replaceTimeFieldForInsert(trimList, UPDATE_TIME_FIELD);

            replaceTimeValueForInsert(trimList, CREATE_TIME_VALUE);
            replaceTimeValueForInsert(trimList, UPDATE_TIME_VALUE);
        }
        return true;
    }

    /**
     * 修改Mapper的updateByExampleSelective方法的updateTime为系统默认时间
     */
    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Element> elementList = (List) element.getElements();
        //直接获取trim
        XmlElement trim = (XmlElement) elementList.get(elementList.size() - 2);
        List trimList = trim.getElements();

        removeCreateTimeForUpdate(trimList, CREATE_TIME_RECORD_COLUMN_VALUE);
        removeCreateTimeForUpdate(trimList, CREATE_USER_ID_RECORD_COLUMN_VALUE);
        replaceUpdateTimeForUpdate(trimList, UPDATE_TIME_RECORD_COLUMN_VALUE);

        return true;
    }

    /**
     * 修改Mapper的updateByExample方法的updateTime为系统默认时间
     */
    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Element> elementList = element.getElements();

        removeCreateTimeForUpdate(elementList, CREATE_TIME_RECORD_COLUMN_VALUE);
        removeCreateTimeForUpdate(elementList, CREATE_USER_ID_RECORD_COLUMN_VALUE);
        replaceUpdateTimeForUpdate(elementList, UPDATE_TIME_RECORD_COLUMN_VALUE);
        return true;
    }

    /**
     * 修改Mapper的updateByPrimaryKeySelective方法的updateTime为系统默认时间
     */
    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        List<Element> elementList = element.getElements();
        List<Element> trimList = null;
        //直接获取trim
        for (int i = elementList.size() - 1; i > 0; i--){
            if (elementList.get(i) instanceof XmlElement) {
                XmlElement trim = (XmlElement) elementList.get(i);
                if ("set".equals(trim.getName())) {
                    trimList = trim.getElements();
                    break;
                }
            }
        }
        if (trimList != null) {
            removeCreateTimeForUpdate(trimList, CREATE_TIME_COLUMN_VALUE);
            removeCreateTimeForUpdate(trimList, CREATE_USER_ID_COLUMN_VALUE);
            replaceUpdateTimeForUpdate(trimList, UPDATE_TIME_COLUMN_VALUE);
        }

        return true;
    }

    /**
     * 修改Mapper的updateByPrimaryKey方法的updateTime为系统默认时间
     */
    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Element> elementList = element.getElements();

        removeCreateTimeForUpdate(elementList, CREATE_TIME_COLUMN_VALUE);
        removeCreateTimeForUpdate(elementList, CREATE_USER_ID_COLUMN_VALUE);
        replaceUpdateTimeForUpdate(elementList, UPDATE_TIME_COLUMN_VALUE);
        return true;
    }

    /**
     * 验证并替换为sysdate
     *
     * @param target
     * @param elementList
     */
    private void removeCreateTimeForUpdate(List<Element> elementList, String target) {
        for (int i = 0; i < elementList.size(); i++) {
            String content = elementList.get(i).getFormattedContent(0);
            if (content.contains(target)) {
                elementList.remove(i);
                break;
            }
        }
    }

    private void replaceUpdateTimeForUpdate(List<Element> elementList, String updateTimeSection) {
        String[] keyValue = updateTimeSection.split(EQUAL);
        for (int i = 0; i < elementList.size(); i++) {
            String content = elementList.get(i).getFormattedContent(0);
            if (content.contains(updateTimeSection)) {
                if (content.contains("<if")) {
                    String sqlSection = keyValue[0] + EQUAL + SYSDATE;
                    if (content.contains("},")) {
                        sqlSection += ",";
                    }
                    elementList.set(i, new TextElement(sqlSection));
                } else {
                    content = content.replace(keyValue[1], SYSDATE);
                    elementList.set(i, new TextElement(content));
                }
                break;
            }
        }
    }

    private void replaceTimeFieldForInsert(List<Element> elementList, String timeFeild) {
        for (int i = 0; i < elementList.size(); i++) {
            String content = elementList.get(i).getFormattedContent(0);
            if (content.contains(timeFeild) && content.contains("<if")) {
                if (content.contains(",")) {
                    timeFeild += ",";
                }
                elementList.set(i, new TextElement(timeFeild));
                break;
            }
        }
    }

    private void replaceTimeValueForInsert(List<Element> elementList, String timeSection) {
        for (int i = 0; i < elementList.size(); i++) {
            String content = elementList.get(i).getFormattedContent(0);
            if (content.contains(timeSection)) {
                String sqlSection;
                if (content.contains("<if")) {
                    sqlSection = SYSDATE;
                    if (content.contains("},")) {
                        sqlSection += ",";
                    }
                } else {
                    sqlSection = content.replace(timeSection, SYSDATE);
                }

                elementList.set(i, new TextElement(sqlSection));
                break;
            }
        }
    }

    //TODO
    private String getColumnName(String section) {
        return section.split(EQUAL)[0];
    }

    //TODO
    private String replaceValue(String section, String replaceStr) {
        return section.replace(replaceStr, SYSDATE);
    }

}

package com.whh.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 修改createTime,updateTime
 * Created by huahui.wu on 2017/6/15 .
 */
public class MysqlDatePlugin extends PluginAdapter {

    private static final String CREATE_TIME_FIELD = "create_time";
    private static final String UPDATE_TIME_FIELD = "update_time";
    private static final String CREATE_USER_ID_FIELD = "create_user_id";
    private static final String CREATE_TIME_VALUE = "#{createTime,jdbcType=TIMESTAMP}";
    private static final String UPDATE_TIME_VALUE = "#{updateTime,jdbcType=TIMESTAMP}";
    private static final String CREATE_USER_ID_VALUE = "#{createUserId,jdbcType=BIGINT}";
    private static final String CREATE_TIME_VALUE_RECORD = "#{record.createTime,jdbcType=TIMESTAMP}";
    private static final String UPDATE_TIME_VALUE_RECORD = "#{record.updateTime,jdbcType=TIMESTAMP}";
    private static final String CREATE_USER_ID_VALUE_RECORD = "#{record.createUserId,jdbcType=BIGINT}";
    private static final String EQUAL = " = ";
    private static final String SYSDATE = "now()";
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
        int i = 0;
        for (i = 0; i < elementList.size(); i++) {
            if ((elementList.get(i) instanceof XmlElement)) {
                break;
            }
        }
        XmlElement trim = (XmlElement) elementList.get(i);
        List<Element> trimList = trim.getElements();
        replaceTimeFieldForInsert(trimList, CREATE_TIME_FIELD);
        replaceTimeFieldForInsert(trimList, UPDATE_TIME_FIELD);

        XmlElement trim1 = (XmlElement) elementList.get(i + 1);
        List<Element> trimList1 = trim1.getElements();
        replaceTimeValueForInsert(trimList1, CREATE_TIME_VALUE);
        replaceTimeValueForInsert(trimList1, UPDATE_TIME_VALUE);

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
            if (content.toLowerCase().contains(target.toLowerCase())) {
                elementList.remove(i);
                break;
            }
        }
    }

    private void replaceUpdateTimeForUpdate(List<Element> elementList, String updateTimeSection) {
        String[] keyValue = updateTimeSection.split(EQUAL);
        for (int i = 0; i < elementList.size(); i++) {
            String content = elementList.get(i).getFormattedContent(0);
            TextElement te = null;
            if (content.toLowerCase().contains(updateTimeSection.toLowerCase())) {
                if (content.contains("<if")) {
                    String sqlSection = keyValue[0] + EQUAL + SYSDATE;
                    if (content.contains("},")) {
                        sqlSection += ",";
                    }
                    te = new TextElement(sqlSection);
                } else {
                    content = content.replace(keyValue[1], SYSDATE);
                    te = new TextElement(content);
                }
                elementList.set(i, te);
//                if (elementList.get(i) instanceof XmlElement) {
//                    XmlElement xmlElement = (XmlElement)elementList.get(i);
//                    xmlElement.getElements().set(0, te);
//                } else {
//                    elementList.set(i, te);
//                }
                break;
            }
        }
    }

    private void replaceTimeFieldForInsert(List<Element> elementList, String timeFeild) {
        for (int i = 0; i < elementList.size(); i++) {
            String content = elementList.get(i).getFormattedContent(0);
            if (content.toLowerCase().contains(timeFeild) && content.contains("<if")) {
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
                    if (content.contains(",")) {
                        sqlSection += ",";
                    }
                } else {
                    sqlSection = content.replace(timeSection, SYSDATE);
                }

                //                if (elementList.get(i) instanceof XmlElement) {
//                    XmlElement xmlElement = (XmlElement)elementList.get(i);
//                    xmlElement.getElements().set(0, new TextElement(sqlSection));
//                } else {
                elementList.set(i, new TextElement(sqlSection));
//                }
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

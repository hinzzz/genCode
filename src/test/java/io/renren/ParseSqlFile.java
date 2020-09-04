package io.renren;
 
import org.apache.commons.lang.StringUtils;
 
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/**
 * @description 解析建表sql文件，生成对应的封装对象TableInfo
 * @date 2019/12/13
 * @address shanghai
 *
 */
public class ParseSqlFile {
 
 
    /**
     * Mysql建表语句关键字
     */
    private final static Map<String, String> MYSQL_KEY_WORD_MAP = new HashMap();
    static {
        MYSQL_KEY_WORD_MAP.put("ENGINE", "ENGINE");
        MYSQL_KEY_WORD_MAP.put("CHARACTER", "CHARACTER");
        MYSQL_KEY_WORD_MAP.put("COLLATE", "COLLATE");
    }
 
    /**
     * 解析sql建表语句文件，得到FieldInfo集合
     * @param fileObj
     * @return
     */
    public TableInfo getFieldInfo(File fileObj) {
 
        BufferedReader reader = null;
        InputStream in = null;
 
        TableInfo tableInfo = new TableInfo();
        List<FieldInfo> list = new ArrayList<>();
        List<FieldInfo> listParsed = null;
 
        try {
            reader = new BufferedReader(new FileReader(fileObj));
            in = new FileInputStream(fileObj);
            String line = null;
            int count = 0;
            while((line  = reader.readLine()) != null) {
 
                // 第一行
                if (line.toUpperCase().contains("CREATE TABLE")) {
 
                    tableInfo.setTableName(parseTableName(line));
                    continue;
                }
 
                // PRIMARY KEY 行
                if (line.toUpperCase().contains("PRIMARY KEY")) {
                    tableInfo.setPrimaryKeyDB(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
                    continue;
                }
 
                // 建表语句结束的右括号 行
                if (line.trim().startsWith(")")) {
                    continue;
                }
 
                // 表备注信息
                if (line.trim().toUpperCase().startsWith("COMMENT")) {
                    continue;
                }
 
                boolean keyWordFlag = false;
                for (Map.Entry<String, String> entry : MYSQL_KEY_WORD_MAP.entrySet()) {
                    if (line.toUpperCase().contains(entry.getKey())) {
                        keyWordFlag = true;
                        break;
                    }
                }
 
                if (keyWordFlag){
                    continue;
                }
 
                // 递归处理，拿到字段名/字段类型/字段备注信息，然后存入ArrayList
                parseFiledInfo(line, list);
 
            }
 
            listParsed = convertMysql2Java(list, tableInfo);
            tableInfo.setFieldList(listParsed);
 
        } catch (IOException e) {
            System.out.println(" catch 分支 IOException " + e);
        } finally {
            try {
                in.close();
            } catch (IOException e){
                System.out.println(" finally 分支 IOException " + e);
            }
 
        }
 
        return tableInfo;
    }
 
    private String parseTableName(String line) {
        return line.replaceFirst("CREATE", "").replaceFirst("TABLE", "").replaceFirst("\\(", "").trim().toLowerCase();
    }
 
 
    private List<FieldInfo> parseFiledInfo(String line, List<FieldInfo> list) {
 
        // 去掉每行前面的空格
        String lineWithoutFrontBlank = parLineFrontBlank(line);
        // 字段名
        String fieldNameDB = lineWithoutFrontBlank.substring(0, lineWithoutFrontBlank.indexOf(" "));
        // 字段备注信息
        String comment = lineWithoutFrontBlank.substring(lineWithoutFrontBlank.indexOf("'") + 1, lineWithoutFrontBlank.lastIndexOf("'"));
        String lineNotStartsWithBlankAndFieldName = parLineFrontBlank(lineWithoutFrontBlank.substring(lineWithoutFrontBlank.indexOf(" ")));
        // 字段数据类型
        String dataTypeMysql = lineNotStartsWithBlankAndFieldName.substring(0, lineNotStartsWithBlankAndFieldName.indexOf(" "));
 
        // 去掉数据类型后面的括号内容
        if (dataTypeMysql.trim().endsWith(")")) {
            dataTypeMysql = dataTypeMysql.substring(0, dataTypeMysql.indexOf("("));
        }
 
        FieldInfo fieldInfo = new FieldInfo(fieldNameDB, null, dataTypeMysql, null, comment);
        list.add(fieldInfo);
        return list;
    }
 
    /**
     * 递归去掉每行前面的空格
     * @param tempStr
     * @return
     */
    private String parLineFrontBlank(String tempStr) {
 
        if (tempStr.startsWith(" ")) {
            String newTempStr = tempStr.substring(1);
            return parLineFrontBlank(newTempStr);
        }
 
        return tempStr;
    }
 
    private List<FieldInfo> convertMysql2Java(List<FieldInfo> list, TableInfo tableInfo) {
 
        List<FieldInfo> listParsed = new ArrayList<>();
 
        for (FieldInfo fieldInfo : list) {
 
            // 如果该字段是主键
            if (StringUtils.equals(fieldInfo.getFieldNameJava(), tableInfo.getPrimaryKeyJava())) {
                tableInfo.setPrimaryKeyJava(mappingMysqlFieldName2Java(tableInfo.getPrimaryKeyDB()));
                tableInfo.setPrimaryKeyDataType(fieldInfo.getDataTypeMysql());
            }
            // mysql字段转java字段
            fieldInfo.setFieldNameJava(mappingMysqlFieldName2Java(fieldInfo.getFieldNameDB()));
            // mysql字段类型 转 java 数据类型
            fieldInfo.setDataTypeJava(mappingMysqlDataType2Java(fieldInfo.getDataTypeMysql()));
 
            listParsed.add(fieldInfo);
        }
 
        return listParsed;
    }
 
    private String mappingMysqlDataType2Java(String dataTypeMysql) {
 
        // 字符串
        if (MysqlDataTypeConstants.MYSQL_DATA_TYPE_VARCHAR.containsValue(dataTypeMysql.toLowerCase())) {
            return "String";
        }
 
        // 整形
        if (MysqlDataTypeConstants.MYSQL_DATA_TYPE_INT.containsValue(dataTypeMysql.toLowerCase())) {
            return "Integer";
        }
 
        // 长整形
        if (MysqlDataTypeConstants.MYSQL_DATA_TYPE_LONG.containsValue(dataTypeMysql.toLowerCase())) {
            return "Long";
        }
 
        // date
        if (MysqlDataTypeConstants.MYSQL_DATA_TYPE_DATE.containsValue(dataTypeMysql.toLowerCase())) {
            return "Date";
        }
 
        return dataTypeMysql;
    }
 
    /**
     * Mysql字段名称转java字段名称
     * @param filedName
     * @return
     */
    private String mappingMysqlFieldName2Java(String filedName) {
 
        // 去掉数据类型后面的括号内容
        if (!filedName.contains("_")) {
            return filedName;
        }
 
        return parseUnderLine(filedName);
    }
 
    /**
     * 递归处理Mysql字段名称中的下划线
     */
    private String parseUnderLine(String filedName) {
 
        int underLineIndex = filedName.indexOf("_");
        String parsedFieldName = filedName.replaceFirst(filedName.substring(underLineIndex, underLineIndex + 2), filedName.substring(underLineIndex + 1, underLineIndex + 2).toUpperCase());
 
        if (!parsedFieldName.contains("_")) {
            return parsedFieldName;
        }
 
        return parseUnderLine(parsedFieldName);
    }
 
 
}
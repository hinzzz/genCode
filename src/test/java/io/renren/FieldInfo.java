package io.renren;
 
/**
 * @description 字段数据接口
 * @date 2019/12/13
 * @address shanghai
 *
 */
public class FieldInfo {
 
    /**
     * 字段名（数据库字段格式）
     */
    private String fieldNameDB;
 
    /**
     * 字段名（java驼峰格式）
     */
    private String fieldNameJava;
 
    /**
     * mysql数据类型
     */
    private String dataTypeMysql;
 
    /**
     * java数据类型
     */
    private String dataTypeJava;
 
    /**
     * 字段conmment备注信息
     */
    private String comment;
 
    public FieldInfo(String fieldNameDB, String fieldNameJava, String dataTypeMysql, String dataTypeJava, String comment) {
        this.fieldNameDB = fieldNameDB;
        this.fieldNameJava = fieldNameJava;
        this.dataTypeMysql = dataTypeMysql;
        this.dataTypeJava = dataTypeJava;
        this.comment = comment;
    }
 
    public String getFieldNameDB() {
        return fieldNameDB;
    }
 
    public void setFieldNameDB(String fieldNameDB) {
        this.fieldNameDB = fieldNameDB;
    }
 
    public String getFieldNameJava() {
        return fieldNameJava;
    }
 
    public void setFieldNameJava(String fieldNameJava) {
        this.fieldNameJava = fieldNameJava;
    }
 
    public String getDataTypeMysql() {
        return dataTypeMysql;
    }
 
    public void setDataTypeMysql(String dataTypeMysql) {
        this.dataTypeMysql = dataTypeMysql;
    }
 
    public String getDataTypeJava() {
        return dataTypeJava;
    }
 
    public void setDataTypeJava(String dataTypeJava) {
        this.dataTypeJava = dataTypeJava;
    }
 
    public String getComment() {
        return comment;
    }
 
    public void setComment(String comment) {
        this.comment = comment;
    }
}
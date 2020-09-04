package io.renren;
 
import java.util.List;
 
/**
 * @description 解析建表sql生成的对象
 * @date 2019/12/13
 * @address shanghai
 *
 */
public class TableInfo {
 
    /**
     * 表名
     */
    private String tableName;
 
    /**
     * 主键（数据库字段名格式）
     */
    private String primaryKeyDB;
 
    /**
     * 主键（java驼峰命名格式）
     */
    private String primaryKeyJava;
 
    /**
     * 主键的数据类型
     */
    private String primaryKeyDataType;
 
    /**
     * 表字段集合
     */
    private List<FieldInfo> fieldList;
 
    public String getTableName() {
        return tableName;
    }
 
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
 
    public String getPrimaryKeyDB() {
        return primaryKeyDB;
    }
 
    public void setPrimaryKeyDB(String primaryKeyDB) {
        this.primaryKeyDB = primaryKeyDB;
    }
 
    public String getPrimaryKeyJava() {
        return primaryKeyJava;
    }
 
    public void setPrimaryKeyJava(String primaryKeyJava) {
        this.primaryKeyJava = primaryKeyJava;
    }
 
    public String getPrimaryKeyDataType() {
        return primaryKeyDataType;
    }
 
    public void setPrimaryKeyDataType(String primaryKeyDataType) {
        this.primaryKeyDataType = primaryKeyDataType;
    }
 
    public List<FieldInfo> getFieldList() {
        return fieldList;
    }
 
    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }
}
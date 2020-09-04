package io.renren;
 
import java.util.HashMap;
import java.util.Map;
 
/**
 * @description 常量定义
 * @date 2019/12/13
 * @address shanghai
 *
 */
public class MysqlDataTypeConstants {
 
    /**
     * Mysql数据类型之字符串
     */
    public final static Map<String, String> MYSQL_DATA_TYPE_VARCHAR = new HashMap();
    static {
        MYSQL_DATA_TYPE_VARCHAR.put("varchar", "varchar");
        MYSQL_DATA_TYPE_VARCHAR.put("json", "json");
        MYSQL_DATA_TYPE_VARCHAR.put("char", "char");
    }
 
    /**
     * Mysql数据类型之整形
     */
    public final static Map<String, String> MYSQL_DATA_TYPE_INT = new HashMap();
    static {
        MYSQL_DATA_TYPE_INT.put("int", "int");
        MYSQL_DATA_TYPE_INT.put("bit", "bit");
    }
 
    /**
     * Mysql数据类型之长整形
     */
    public final static Map<String, String> MYSQL_DATA_TYPE_LONG = new HashMap();
    static {
        MYSQL_DATA_TYPE_LONG.put("bigint", "bigint");
        MYSQL_DATA_TYPE_LONG.put("long", "long");
    }
 
    /**
     * Mysql数据类型之整形
     */
    public final static Map<String, String> MYSQL_DATA_TYPE_DATE = new HashMap();
    static {
        MYSQL_DATA_TYPE_DATE.put("datetime", "datetime");
        MYSQL_DATA_TYPE_DATE.put("timestamp", "timestamp");
    }
 
}
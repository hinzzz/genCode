package io.renren;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 功能：根据mysql建表语句文件（XXX.sql），自动生成mapper文件（XXXMapper.xml）
 *
 * @date 2019/12/13
 * @address shanghai
 */
public class GenSqlXml {

    /**
     * 源文件路径
     */
    private static final String SOURCE_FILE_PATH = "D:\\sourcefile\\";

    /**
     * 源文件名称
     */
    private static final String SOURCE_FILE_NAME = "table_create";

    /**
     * 源文件后缀
     */
    private static final String SOURCE_FILE_SUFFIX = "sql";

    /**
     * 目标文件路径
     */
    private static final String TARGET_FILE_PATH = "D:\\targetfile\\";

    /**
     * 目标文件名称
     */
    private static final String TARGET_FILE_NAME = "SqlMapper";

    /**
     * 目标文件后缀
     */
    private static final String TARGET_FILE_SUFFIX = "java";

    /**
     * 换行符
     */
    private static final String LINE_BREAK = "\r\n";

    /**
     * sqlxml文件的namespace
     */
    private static final String NAMESPACE = "cn.xxx.xxx.dao.StoreActivityDao";


    public static void main(String[] args) {

        //File fileObj = new File(SOURCE_FILE_PATH + SOURCE_FILE_NAME + "." + SOURCE_FILE_SUFFIX);
        File fileObj = new File("D:\\space\\renren-generator\\src\\test\\java\\io\\renren\\test.sql");

        ParseSqlFile parseSqlFile = new ParseSqlFile();
        TableInfo tableInfo = parseSqlFile.getFieldInfo(fileObj);
        GenSqlXml genSqlXml = new GenSqlXml();
        genSqlXml.genSqlXmlFile(tableInfo);
    }


    public void genSqlXmlFile(TableInfo tableInfo) {
        genTargetSqlXmlFile(tableInfo);
    }

    private void genTargetSqlXmlFile(TableInfo tableInfo) {


        FileOutputStream fos = null;

        try {

            File f1 = new File("D:\\space\\renren-generator\\src\\main\\resources\\template\\Dao.java.vm");
            if (!f1.exists()) {
                f1.getParentFile().mkdirs();
            }

            fos = new FileOutputStream(f1);

            // 生成文件头

            fos.write(("package ${package}.${moduleName}.dao;" + LINE_BREAK).getBytes());
            fos.write(LINE_BREAK.getBytes());
            fos.write(LINE_BREAK.getBytes());
            fos.write(("import ${package}.${moduleName}.entity.${className}Entity;" + LINE_BREAK).getBytes());
            fos.write(("import org.apache.ibatis.jdbc.SQL;" + LINE_BREAK).getBytes());
            fos.write(("import cn.eeepay.framework.util.StringUtil;" + LINE_BREAK).getBytes());
            fos.write(("import org.apache.ibatis.annotations.*;" + LINE_BREAK).getBytes());
            fos.write(LINE_BREAK.getBytes());
            fos.write( LINE_BREAK.getBytes());
            fos.write( LINE_BREAK.getBytes());
            fos.write(("public interface ${className}Dao  {" + LINE_BREAK).getBytes());
            fos.write(LINE_BREAK.getBytes());


            // 生成insert语句
            genInsert(fos, tableInfo);
            // 生成delete语句
            genDelete(fos, tableInfo);


            fos.write(("class SqlProvider {"+LINE_BREAK).getBytes());
            // 生成select语句
            genSelet(fos, tableInfo);
            // 生成update语句
            genUpdate(fos, tableInfo);
            fos.write(("}"+LINE_BREAK).getBytes());




            // 生成文件尾部
            fos.write(LINE_BREAK.getBytes());
            fos.write( LINE_BREAK.getBytes());
            fos.write( LINE_BREAK.getBytes());
            fos.write(("}" + LINE_BREAK).getBytes());
        } catch (IOException e) {
            System.out.println("catch " + e);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                System.out.println("finally " + e);
            }
        }
    }



    private String upStr(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 生成insert语句
     *
     * @param fos
     * @param tableInfo
     */
    private void genInsert(FileOutputStream fos, TableInfo tableInfo) {

        String tableName = tableInfo.getTableName();
        String primaryKeyDB = tableInfo.getPrimaryKeyDB();
        List<FieldInfo> list = tableInfo.getFieldList();

        StringBuffer fieldListDBStr = new StringBuffer();
        StringBuffer fieldListJavaStr = new StringBuffer();

        for (FieldInfo fieldInfo : list) {
            fieldListDBStr.append(fieldInfo.getFieldNameDB()).append(",");
            fieldListJavaStr.append("#{").append(fieldInfo.getFieldNameJava()).append("},");
        }
        String fieldListDBStrFinal = fieldListDBStr.toString().substring(0, fieldListDBStr.toString().length() - 1);
        String fieldListJavaStrFinal = fieldListJavaStr.toString().substring(0, fieldListJavaStr.toString().length() - 1);
        try {

            //生成javasql
            StringBuilder sb = new StringBuilder();
            sb.append("@Insert( \"insert into " + tableName + "(" );

            for (FieldInfo field : list) {
                sb.append("`").append(field.getFieldNameDB()).append("`,");
            }
            sb.setLength(sb.length() - 1);
            sb.append(") values ( " );

            for (FieldInfo field : list) {
                sb.append("#{info.").append(field.getFieldNameJava()).append("},");
            }
            sb.setLength(sb.length() - 1);
            sb.append(")\"");
            sb.append(") " + LINE_BREAK);
            sb.append("int save(@Param(\"info\") ${className}Entity info);");
            sb.append( LINE_BREAK);
            fos.write((sb.toString() + LINE_BREAK).getBytes());

        } catch (IOException e) {
            System.out.println("genInsert IOException " + e);
        }

    }

    /**
     * 生成delete语句
     *
     * @param fos
     * @param tableInfo
     */
    private void genDelete(FileOutputStream fos, TableInfo tableInfo) {

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("@Delete(\"");
            sb.append(" delete from ");
            sb.append(tableInfo.getTableName());
            sb.append("where id = #{info.id}");
            sb.append(")\")");
            sb.append(LINE_BREAK);
            sb.append("int delete(@Param(\"id\") Long id);");
            sb.append(LINE_BREAK);
            sb.append(LINE_BREAK);
            fos.write(sb.toString().getBytes());

        } catch (IOException e) {
            System.out.println("genDelete IOException " + e);
        }
    }

    /**
     * 生成update语句
     * 注意：update语句，需要执行jdbcType
     *
     * @param fos
     * @param tableInfo
     */
    private void genUpdate(FileOutputStream fos, TableInfo tableInfo) {

        String tableName = tableInfo.getTableName();
        String primaryKeyDB = tableInfo.getPrimaryKeyDB();
        List<FieldInfo> list = tableInfo.getFieldList();

        try {

            StringBuilder sb = new StringBuilder();
            sb.append("     public String update(Map<String, Object> param) {");
            sb.append(LINE_BREAK);
            sb.append("         final ").append("${className}Entity ").append(" info = ").append("(${className}Entity) param.get(\"info\");");
            sb.append(LINE_BREAK);
            sb.append("         return new SQL() {{");
            sb.append(LINE_BREAK);
            sb.append("             UPDATE(\" ");
            sb.append(tableName).append("\");").append(LINE_BREAK);
            for (FieldInfo filed :list){
                sb.append("             if(StringUtil.isNotBlank(").append("info.get").append(upStr(filed.getFieldNameJava())).append("()){").append(LINE_BREAK);
                sb.append("                 SET( ").append("\"").append(filed.getFieldNameDB()).append(" = ").append("#{info.").append(filed.getFieldNameJava()).append("}\");").append(LINE_BREAK);
                sb.append("             }").append(LINE_BREAK);
            }

            sb.setLength(sb.length() - 1);
            sb.append(LINE_BREAK);
            sb.append("             WHERE(\" id = #{info.id} \")").append(LINE_BREAK);


            sb.append("         }}.toString();");
            sb.append(LINE_BREAK);
            sb.append("     }");
            sb.append(LINE_BREAK);
            fos.write(sb.toString().getBytes());


        } catch (IOException e) {
            System.out.println("genUpdate IOException " + e);
        }
    }
    /**
     * 生成select语句
     *
     * @param fos
     * @param tableInfo
     */
    private void genSelet(FileOutputStream fos, TableInfo tableInfo) {
        List<FieldInfo> list = tableInfo.getFieldList();
        String resultMapId = "BaseResultMap";
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("     public String queryPage(Map<String, Object> param) {");
            sb.append(LINE_BREAK);
            sb.append("         final ").append("${className}Entity ").append(" info = ").append("(${className}Entity) param.get(\"info\");");
            sb.append(LINE_BREAK);
            sb.append("         return new SQL() {{");
            sb.append(LINE_BREAK);
            sb.append("             SELECT(\" ");
            for (FieldInfo filed : list) {
                sb.append(filed.getFieldNameDB()).append(",");
            }
            sb.setLength(sb.length() - 1);
            sb.append("\"");
            sb.append(" where 1 = 1 ").append(LINE_BREAK);
            for (FieldInfo filed :list){
                sb.append("             if(StringUtil.isNotBlank(").append("info.get").append(upStr(filed.getFieldNameJava())).append("()){").append(LINE_BREAK);
                sb.append("                 WHERE( ").append("\"").append(filed.getFieldNameDB()).append(" = ").append("#{info.").append(filed.getFieldNameJava()).append("}\");").append(LINE_BREAK);
                sb.append("             }").append(LINE_BREAK);
            }
            sb.append("         }}.toString();");
            sb.append(LINE_BREAK);
            sb.append("     }");
            sb.append(LINE_BREAK);
            fos.write(sb.toString().getBytes());
        } catch (IOException e) {
            System.out.println("genSelect IOException " + e);
        }
    }



}
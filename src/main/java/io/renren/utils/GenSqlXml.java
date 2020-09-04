package io.renren.utils;

import io.renren.entity.ColumnEntity;
import io.renren.entity.TableEntity;
import org.apache.ibatis.annotations.ResultType;

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
     * 换行符
     */
    private static final String LINE_BREAK = "\r\n";



    public void genSqlXmlFile(TableEntity tableEntity) {
        genTargetSqlXmlFile(tableEntity);
    }

    private void genTargetSqlXmlFile(TableEntity tableInfo) {


        FileOutputStream fos = null;

        try {

            File f1 = new File(this.getClass().getClassLoader().getResource("template/Dao.java.vm").getPath());
            if (!f1.exists()) {
                f1.getParentFile().mkdirs();
            }

            fos = new FileOutputStream(f1);

            // 生成文件头

            fos.write(("package ${package}.${moduleName}.dao;" + LINE_BREAK).getBytes());
            fos.write(LINE_BREAK.getBytes());
            fos.write(LINE_BREAK.getBytes());
            fos.write(("import ${package}.model.${moduleName}.${className}Entity;" + LINE_BREAK).getBytes());
            fos.write(("import org.apache.ibatis.jdbc.SQL;" + LINE_BREAK).getBytes());
            fos.write(("import cn.eeepay.framework.util.StringUtil;" + LINE_BREAK).getBytes());
            fos.write(("import org.apache.ibatis.annotations.*;" + LINE_BREAK).getBytes());

            fos.write(("import java.util.Map;" + LINE_BREAK).getBytes());
            fos.write(("import java.util.List;" + LINE_BREAK).getBytes());
            fos.write(("import cn.eeepay.framework.db.pagination.Page;" + LINE_BREAK).getBytes());
            fos.write(LINE_BREAK.getBytes());
            fos.write( LINE_BREAK.getBytes());
            fos.write( LINE_BREAK.getBytes());
            fos.write(("public interface ${className}Dao  {" + LINE_BREAK).getBytes());
            fos.write(LINE_BREAK.getBytes());


            // 生成 简单 insert语句
            genInsert(fos, tableInfo);
            // 生成 简单 delete语句
            genDelete(fos, tableInfo);
            //生成 简单 query
            genQueryPageMethod(fos);
            //生成 简单 update
            genUpdateMethod(fos);
            //生成 简单 详情
            genInfoMethod(fos,tableInfo);

            fos.write(("class SqlProvider {"+LINE_BREAK).getBytes());
            // 生成 复杂 select语句
            genSelet(fos, tableInfo);
            // 生成 复杂 update语句
            genUpdate(fos, tableInfo);
            fos.write(("}"+LINE_BREAK).getBytes());

            // 生成文件尾部
            fos.write(LINE_BREAK.getBytes());
            fos.write( LINE_BREAK.getBytes());
            fos.write( LINE_BREAK.getBytes());
            fos.write(("}" + LINE_BREAK).getBytes());
            fos.flush();
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


    private void genInfoMethod(FileOutputStream fos,TableEntity tableEntity) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE_BREAK);
        sb.append("@Select(\" select * from ").append(tableEntity.getTableName()).append(" where id = #{id} \")");
        sb.append(LINE_BREAK);
        sb.append("@ResultType(${className}Entity.class)");
        sb.append(LINE_BREAK);
        sb.append("${className}Entity info(@Param(\"id\") Long id);");
        sb.append(LINE_BREAK);
        sb.append(LINE_BREAK);
        sb.append(LINE_BREAK);
        try {
            fos.write(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void genUpdateMethod(FileOutputStream fos) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE_BREAK);
        sb.append("@SelectProvider(type= ${className}Dao.SqlProvider.class,method=\"update\")");
        sb.append(LINE_BREAK);
        sb.append("int update(@Param(\"info\") ${className}Entity info);");
        sb.append(LINE_BREAK);
        try {
            fos.write(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void genQueryPageMethod(FileOutputStream fos) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE_BREAK);
        sb.append("@SelectProvider(type= ${className}Dao.SqlProvider.class,method=\"queryPage\")");
        sb.append(LINE_BREAK);
        sb.append("@ResultType(${className}Entity.class)");
        sb.append(LINE_BREAK);
        sb.append("List<${className}Entity> queryPage(@Param(\"page\") Page page,@Param(\"info\") ${className}Entity info);");
        sb.append(LINE_BREAK);
        try {
            fos.write(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 生成insert语句
     *
     * @param fos
     * @param tableInfo
     */
    private void genInsert(FileOutputStream fos, TableEntity tableInfo) {

        String tableName = tableInfo.getTableName();
        List<ColumnEntity> list = tableInfo.getColumns();

        try {

            //生成javasql
            StringBuilder sb = new StringBuilder();
            sb.append("@Insert( \"insert into " + tableName + "(" );

            for (ColumnEntity field : list) {
                sb.append("`").append(field.getColumnName()).append("`,");
            }
            sb.setLength(sb.length() - 1);
            sb.append(") values ( " );

            for (ColumnEntity field : list) {
                sb.append("#{info.").append(field.getAttrname()).append("},");
            }
            sb.setLength(sb.length() - 1);
            sb.append(")\"");
            sb.append(") " + LINE_BREAK);
            sb.append("@SelectKey(statement = \"select LAST_INSERT_ID()\", keyProperty = \"info.id\", before = false, resultType = Long.class)");
            sb.append( LINE_BREAK);
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
    private void genDelete(FileOutputStream fos, TableEntity tableInfo) {

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("@Delete(\"");
            sb.append(" delete from ");
            sb.append(tableInfo.getTableName());
            sb.append("where id = #{info.id}");
            sb.append(")\")");
            sb.append(LINE_BREAK);
            sb.append("int removeById(@Param(\"id\") Long id);");
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
    private void genUpdate(FileOutputStream fos, TableEntity tableInfo) {

        String tableName = tableInfo.getTableName();
        List<ColumnEntity> list = tableInfo.getColumns();

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
            for (ColumnEntity filed :list){
                sb.append("             if(StringUtil.isNotBlank(").append("info.get").append(filed.getAttrName()).append("())){").append(LINE_BREAK);
                sb.append("                 SET( ").append("\"").append(filed.getColumnName()).append(" = ").append("#{info.").append(filed.getAttrname()).append("}\");").append(LINE_BREAK);
                sb.append("             }").append(LINE_BREAK);
            }

            sb.setLength(sb.length() - 1);
            sb.append(LINE_BREAK);
            sb.append("             WHERE(\" id = #{info.id} \");").append(LINE_BREAK);


            sb.append("         }}.toString();");
            sb.append(LINE_BREAK);
            sb.append("     }");
            sb.append(LINE_BREAK);
            fos.write(sb.toString().getBytes());


        } catch (IOException e) {
            System.out.println("genUpdate IOException " + e);
        }
    }
    private boolean checkDateName(String attrType) {
        String[] s = {"begin","end","start"};
        for (String s1 : s) {
            if(attrType.toLowerCase().contains(s1.toLowerCase())){
                return false;
            }
        }
        return true;
    }
    /**
     * 生成select语句
     *
     * @param fos
     * @param tableInfo
     */
    private void genSelet(FileOutputStream fos, TableEntity tableInfo) {
        List<ColumnEntity> list = tableInfo.getColumns();
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
            for (ColumnEntity filed : list) {
                sb.append(filed.getColumnName()).append(",");
            }
            sb.setLength(sb.length() - 1);
            sb.append("\");");
            sb.append(LINE_BREAK);
            for (ColumnEntity filed :list){
                String javaType = filed.getAttrType();
                if(javaType.equals("String")){
                    sb.append("             if(StringUtil.isNotBlank(").append("info.get").append(filed.getAttrName()).append("())){").append(LINE_BREAK);
                    sb.append("                 WHERE( ").append("\"").append(filed.getColumnName()).append(" = ").append("#{info.").append(filed.getAttrname()).append("}\");").append(LINE_BREAK);
                    sb.append("             }").append(LINE_BREAK);
                }else if(javaType.equals("Date") && !checkDateName(filed.getAttrName())){
                    sb.append("             if(").append("info.get").append(filed.getAttrName()).append("() != null ){").append(LINE_BREAK);
                    String oper = "";
                    if(filed.getAttrName().contains("Begin")){
                        oper = ">=";
                    }else if(filed.getAttrName().contains("End")){
                        oper = "<=";
                    }
                    sb.append("                 WHERE( ").append("\"").append(filed.getColumnName()).append(oper).append("  #{info.").append(filed.getAttrname()).append("}\");").append(LINE_BREAK);
                }else if(javaType.equals("Long") || javaType.equals("Integer")){
                    sb.append("             if(").append("info.get").append(filed.getAttrName()).append("() != null ){").append(LINE_BREAK);
                    sb.append("                 WHERE( ").append("\"").append(filed.getColumnName()).append(" = ").append("#{info.").append(filed.getAttrname()).append("}\");").append(LINE_BREAK);
                    sb.append("             }").append(LINE_BREAK);
                }



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
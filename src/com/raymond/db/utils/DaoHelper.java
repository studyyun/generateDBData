package com.raymond.db.utils;


import com.raymond.db.annotation.TableScan;
import com.raymond.utils.ClassUtils;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.util.*;

/**
 * 动态bean转换对象
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-09 14:28
 */
@TableScan(basePackages = "com.raymond.entity")
public class DaoHelper {
    private final static Logger logger = Logger.getLogger(DaoHelper.class);

    private static final Map<String, ReflectCache> TABLE_MAP = new HashMap<String, ReflectCache>();

    static {
        Class<?> aClass = DaoHelper.class;
        TableScan annotation = aClass.getAnnotation(TableScan.class);
        setPackageNameList(annotation.basePackages());
    }

    /**
     * 设置包扫描路径,多个包名
     * @param packageNameList 包名
     */
    private static void setPackageNameList(String[] packageNameList) {
        for (String packageName : packageNameList) {
            setPackageName(packageName);
        }

    }

    /**
     * 设置包扫描路径,单个包名
     * @param packageName 包名
     */
    private static void setPackageName(String packageName) {
        try {
            Set<Class<?>> classes = ClassUtils.getClasses(packageName);
            loadClass(classes);
        } catch (Exception e) {
            logger.error("启动失败", e);
        }
    }

    private static void loadClass(Set<Class<?>> classes) throws NoSuchMethodException {
        ReflectCache reflectCache;
        boolean attribute;
        for (Class<?> aClass : classes) {
            reflectCache = new ReflectCache(aClass);
            attribute = reflectCache.getAttribute();
            if (!attribute) {
                continue;
            }
            TABLE_MAP.put(aClass.getCanonicalName(), reflectCache);
        }
    }

    /**
     * 获取单表新增sql
     * @param object 对象
     * @return 查询sql
     */
    public static String getInsertSql(Object object, Map<Integer, Object> indexValueBind) {
        ReflectCache reflectCache = TABLE_MAP.get(object.getClass().getCanonicalName());
        return reflectCache.getInsertSql(object, indexValueBind);
    }
    /**
     * 获取单表新增sql
     * @param object 对象
     * @return 查询sql
     */
    public static String getBatInsertSql(List<Object> object, List<Map<Integer, Object>> indexValueBind, Class clazz) {
        ReflectCache reflectCache = TABLE_MAP.get(clazz.getCanonicalName());
        return reflectCache.getInsertSql(object, indexValueBind);
    }

    /**
     * 获取单表修改sql
     * @param object 对象
     * @return 查询sql
     */
    public static String getUpdateSql(Object object, Map<Integer, Object> indexValueBind, boolean flag) {
        ReflectCache reflectCache = TABLE_MAP.get(object.getClass().getCanonicalName());
        return reflectCache.getUpdateSql(object, indexValueBind, flag);
    }

    /**
     * 获取删除单表sql
     * @param object 对象
     * @return 查询sql
     */
    public static String getDeleteSql(Object object, Map<Integer, Object> indexValueBind) {
        ReflectCache reflectCache = TABLE_MAP.get(object.getClass().getCanonicalName());
        return reflectCache.getDeleteSql(object, indexValueBind);
    }

    /**
     * 获取删除sql
     * @param clazz 需要删除的对象
     * @param isId 是否通过ID
     * @return sql
     */
    public static String getDeleteByConditionSql(Class clazz, boolean isId) {
        ReflectCache reflectCache = TABLE_MAP.get(clazz.getCanonicalName());
        return reflectCache.getDeleteByIdSql(isId);
    }

    /**
     * 获取单表查询sql
     * @param clazz 类属性
     * @return 查询sql
     */
    public static String getSql(Class clazz) {
        ReflectCache reflectCache = TABLE_MAP.get(clazz.getCanonicalName());
        return reflectCache.getTableSql();
    }
    /**
     * 获取单表查询sql
     * @param object 类属性
     * @return 查询sql
     */
    public static String getSqlByCondition(Object object, Map<Integer, Object> indexValueBind) {
        ReflectCache reflectCache = TABLE_MAP.get(object.getClass().getCanonicalName());
        return reflectCache.getSqlAndCondition(object, indexValueBind);
    }

    /**
     * 获取结果集数据
     * @param rs 结果集
     * @param clazz 需要转换的对象
     * @return 数据
     */
    public static <T> List<T> getDataByResultSet(ResultSet rs, Class<?> clazz) throws Exception {
        List<T> list = new ArrayList<T>();
        ReflectCache reflectCache = TABLE_MAP.get(clazz.getCanonicalName());
        while (rs.next()) {
            list.add(reflectCache.<T>resultSetToObj(rs));
        }
        return list;
    }
    /**
     * 获取分页 sql
     *
     * @param pageInfo  分页数据
     * @param selectSql sql
     * @return 分页 sql
     */
    public static String getPageSql(PageInfo pageInfo, String selectSql) {
        switch (StaticValues.DB_TYPE) {
            case StaticValues.DB2_DB_TYPE:
                return PageHelper.db2PageSql(pageInfo, selectSql);
            case StaticValues.ORACLE_DB_TYPE:
                return PageHelper.oraclePageSql(pageInfo, selectSql);
            case StaticValues.SQL_SERVER_DB_TYPE:
                return PageHelper.sqlServerPageSql(pageInfo, selectSql);
            default:
                return PageHelper.mysqlPageSql(pageInfo, selectSql);
        }
    }

}

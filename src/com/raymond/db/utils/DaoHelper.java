package com.raymond.db.utils;


import com.raymond.db.annotation.TableScan;
import com.raymond.utils.ClassUtils;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.util.*;

/**
 * ��̬beanת������
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
     * ���ð�ɨ��·��,�������
     * @param packageNameList ����
     */
    private static void setPackageNameList(String[] packageNameList) {
        for (String packageName : packageNameList) {
            setPackageName(packageName);
        }

    }

    /**
     * ���ð�ɨ��·��,��������
     * @param packageName ����
     */
    private static void setPackageName(String packageName) {
        try {
            Set<Class<?>> classes = ClassUtils.getClasses(packageName);
            loadClass(classes);
        } catch (Exception e) {
            logger.error("����ʧ��", e);
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
     * ��ȡ��������sql
     * @param object ����
     * @return ��ѯsql
     */
    public static String getInsertSql(Object object, Map<Integer, Object> indexValueBind) {
        ReflectCache reflectCache = TABLE_MAP.get(object.getClass().getCanonicalName());
        return reflectCache.getInsertSql(object, indexValueBind);
    }
    /**
     * ��ȡ��������sql
     * @param object ����
     * @return ��ѯsql
     */
    public static String getBatInsertSql(List<Object> object, List<Map<Integer, Object>> indexValueBind, Class clazz) {
        ReflectCache reflectCache = TABLE_MAP.get(clazz.getCanonicalName());
        return reflectCache.getInsertSql(object, indexValueBind);
    }

    /**
     * ��ȡ�����޸�sql
     * @param object ����
     * @return ��ѯsql
     */
    public static String getUpdateSql(Object object, Map<Integer, Object> indexValueBind, boolean flag) {
        ReflectCache reflectCache = TABLE_MAP.get(object.getClass().getCanonicalName());
        return reflectCache.getUpdateSql(object, indexValueBind, flag);
    }

    /**
     * ��ȡɾ������sql
     * @param object ����
     * @return ��ѯsql
     */
    public static String getDeleteSql(Object object, Map<Integer, Object> indexValueBind) {
        ReflectCache reflectCache = TABLE_MAP.get(object.getClass().getCanonicalName());
        return reflectCache.getDeleteSql(object, indexValueBind);
    }

    /**
     * ��ȡɾ��sql
     * @param clazz ��Ҫɾ���Ķ���
     * @param isId �Ƿ�ͨ��ID
     * @return sql
     */
    public static String getDeleteByConditionSql(Class clazz, boolean isId) {
        ReflectCache reflectCache = TABLE_MAP.get(clazz.getCanonicalName());
        return reflectCache.getDeleteByIdSql(isId);
    }

    /**
     * ��ȡ�����ѯsql
     * @param clazz ������
     * @return ��ѯsql
     */
    public static String getSql(Class clazz) {
        ReflectCache reflectCache = TABLE_MAP.get(clazz.getCanonicalName());
        return reflectCache.getTableSql();
    }
    /**
     * ��ȡ�����ѯsql
     * @param object ������
     * @return ��ѯsql
     */
    public static String getSqlByCondition(Object object, Map<Integer, Object> indexValueBind) {
        ReflectCache reflectCache = TABLE_MAP.get(object.getClass().getCanonicalName());
        return reflectCache.getSqlAndCondition(object, indexValueBind);
    }

    /**
     * ��ȡ���������
     * @param rs �����
     * @param clazz ��Ҫת���Ķ���
     * @return ����
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
     * ��ȡ��ҳ sql
     *
     * @param pageInfo  ��ҳ����
     * @param selectSql sql
     * @return ��ҳ sql
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

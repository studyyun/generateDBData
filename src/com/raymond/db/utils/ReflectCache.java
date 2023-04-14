package com.raymond.db.utils;


import com.raymond.db.annotation.Columns;
import com.raymond.db.annotation.Id;
import com.raymond.db.annotation.Table;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类的反射信息
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-10 08:55
 */
class ReflectCache {
    private Logger logger = Logger.getLogger(ReflectCache.class);
    /**
     * class
     */
    private Class clazz;
    /**
     * 字段对应的属性
     */
    private Map<String, MyMethod> attribute;
    /**
     * 表名
     */
    private String tableName;



    <T> ReflectCache(Class<T> clazz) {
        this.clazz = clazz;
        attribute = new HashMap<String, MyMethod>(16);
    }

    boolean getAttribute() throws NoSuchMethodException {
        Table table = (Table) this.clazz.getAnnotation(Table.class);
        if (table == null) {
            return false;
        }
        tableName = table.name();
        Field[] declaredFields = this.clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            getField(field);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    <T> T resultSetToObj(ResultSet rs) throws Exception {
        try {
            T object = (T) clazz.newInstance();
            MyMethod myMethod;
            for (String columns : attribute.keySet()) {
                myMethod = attribute.get(columns);
                getData(object, columns, myMethod, rs);
            }
            return object;
        } catch (Exception e) {
            logger.error("动态bean转换对象异常,表名" + tableName, e);
            throw e;
        }
    }

    /**
     * 获取新增sql
     * @param object 需要新增的对象
     * @param indexValueBind 参数
     * @return insert sql
     */
    String getInsertSql(Object object, Map<Integer, Object> indexValueBind) {
        StringBuilder sql = new StringBuilder("insert into ").append(tableName).append(" (");
        StringBuilder valuesSql = new StringBuilder(" values (");
        boolean flag = false;
        Object invoke;
        for (String column : attribute.keySet()) {
            invoke = getObject(column, object);
            if (invoke == null) {
                continue;
            }
            if (flag) {
                sql.append(",");
                valuesSql.append(",");
            }
            sql.append(column);
            valuesSql.append("?");
            flag = true;
        }
        valuesSql.append(")");
        sql.append(")");
        if (indexValueBind != null) {
            getIndexValueBind(object,indexValueBind);
        }
        return sql.toString() + valuesSql.toString();
    }

    /**
     * 获取当前属性的值
     * @param column 列名
     * @param object object对象
     * @return 获取object对象中对应列的值
     */
    private Object getObject(String column, Object object) {
        MyMethod myMethod = attribute.get(column);
        if (myMethod.isId) {
            return null;
        }
        try {
            return myMethod.getMethod.invoke(object);
        } catch (Exception e) {
            logger.error("获取条件错误", e);
            return null;
        }

    }

    /**
     * 获取批量新增sql
     * @param objects 对象的集合
     * @param indexValueBinds 参数集合
     * @return insert sql
     */
    String getInsertSql(List<Object> objects, List<Map<Integer, Object>> indexValueBinds) {
        Map<Integer, Object> indexValueBind;
        for (Object object : objects) {
            indexValueBind = new HashMap<Integer, Object>(16);
            getIndexValueBind(object, indexValueBind);
            indexValueBinds.add(indexValueBind);
        }
        return getInsertSql(objects.get(0), null);
    }

    /**
     * 获取参数
     * @param object 对象
     * @param indexValueBind 参数
     */
    private void getIndexValueBind(Object object, Map<Integer, Object> indexValueBind) {
        Object invoke;
        int i = 1;
        for (String column : attribute.keySet()) {
            invoke = getObject(column, object);
            if (invoke == null) {
                continue;
            }
            indexValueBind.put(i, invoke);
            i ++;
        }
    }

    /**
     * 获取修改sql
     * @param object 需要修改的对象
     * @param indexValueBind 参数
     * @param isId 是否通过ID修改
     * @return update sql
     */
    String getUpdateSql(Object object, Map<Integer, Object> indexValueBind, boolean isId) {
        StringBuilder sql = new StringBuilder("update ").append(tableName).append(" set ");
        StringBuilder whereSql = new StringBuilder(" where ");
        boolean flag = false;
        MyMethod myMethod;
        Object invoke;
        Object id = null;
        int i = 1;
        for (String column : attribute.keySet()) {
            myMethod = attribute.get(column);
            if (myMethod.isId && !isId) {
                continue;
            }
            try {
                invoke =  myMethod.getMethod.invoke(object);
            } catch (Exception e) {
                logger.error("获取条件错误", e);
                continue;
            }
            if (flag) {
                sql.append(",");
            }
            if (myMethod.isId) {
                whereSql.append(column).append(" = ?");
                id = invoke;
            } else {
                sql.append(column).append(" = ?");
                indexValueBind.put(i, invoke);
                flag = true;
                i ++;
            }

        }
        if (isId) {
            indexValueBind.put(indexValueBind.size() + 1, id);
            sql.append(whereSql);
        }
        return sql.toString();
    }

    /**
     * 获取删除sql
     * @param object 删除条件对象
     * @param indexValueBind 参数
     * @return delete sql
     */
    String getDeleteSql(Object object, Map<Integer, Object> indexValueBind) {
        StringBuilder sql = new StringBuilder("delete from ").append(tableName).append(" where ");
        getCondition(sql, object, indexValueBind);
        return sql.toString();
    }

    /**
     * 获取删除sql
     * @param isId 是否按ID
     * @return delete sql
     */
    String getDeleteByIdSql(boolean isId) {
        StringBuilder sql = new StringBuilder("delete from ").append(tableName);
        if (!isId) {
            return sql.toString();
        }
        sql.append(" where ");
        MyMethod myMethod;
        for (String column : attribute.keySet()) {
            myMethod = attribute.get(column);
            if (myMethod.isId) {
                sql.append(column).append(" = ?");
                break;
            }
        }
        return sql.toString();
    }

    /**
     * 获取单表查询sql
     * @return 无条件查询sql
     */
    String getTableSql() {
        StringBuilder sql = new StringBuilder("select ");
        boolean flag = false;
        for (String column : attribute.keySet()) {
            if (flag) {
                sql.append(",");
            }
            sql.append(column);
            flag = true;
        }
        return sql.append(" from ").append(tableName).toString();
    }

    /**
     * 获取查询sql并加条件
     * @param object 条件对象
     * @param indexValueBind 参数
     * @return 查询带条件sql
     */
    String getSqlAndCondition(Object object, Map<Integer, Object> indexValueBind) {
        String tableSql = getTableSql();
        StringBuilder whereSql = new StringBuilder(" where ");
        boolean flag = getCondition(whereSql, object, indexValueBind);
        return tableSql + (flag ? whereSql.toString() : "");
    }

    /**
     * 获取条件
     * @param sql sql
     * @param object 条件对象
     * @param indexValueBind 参数
     * @return 是否有条件
     */
    private boolean getCondition(StringBuilder sql, Object object, Map<Integer, Object> indexValueBind) {
        boolean flag = false;
        int i =1;
        Object invoke;
        for (String column : attribute.keySet()) {
            invoke = getObject(column, object);
            if (invoke == null) {
                continue;
            }
            if (flag) {
                sql.append(" and ");
            }
            sql.append(" ").append(column).append(" = ? ");
            indexValueBind.put(i, invoke);
            i ++;
            flag = true;
        }
        return flag;
    }

    /**
     * 将数据存放对象中
     * @param object 对象
     * @param column 列名
     * @param myMethod 属性值
     */
    private <T> void getData(T object, String column, MyMethod myMethod, ResultSet rs) throws InvocationTargetException, IllegalAccessException, SQLException {
        Method method = myMethod.setMethod;
        if (myMethod.type.isAssignableFrom(String.class)) {
            method.invoke(object, rs.getString(column));
        } else if (myMethod.type.isAssignableFrom(Integer.class) || myMethod.type.isAssignableFrom(int.class)) {
            method.invoke(object, rs.getInt(column));
        } else if (myMethod.type.isAssignableFrom(Float.class) || myMethod.type.isAssignableFrom(float.class)) {
            method.invoke(object, rs.getFloat(column));
        } else if (myMethod.type.isAssignableFrom(Long.class) || myMethod.type.isAssignableFrom(long.class)) {
            method.invoke(object, rs.getLong(column));
        } else if (myMethod.type.isAssignableFrom(Double.class) || myMethod.type.isAssignableFrom(Double.class)) {
            method.invoke(object, rs.getDouble(column));
        } else if (myMethod.type.isAssignableFrom(Timestamp.class)) {
            method.invoke(object, rs.getTimestamp(column));
        } else if (myMethod.type.isAssignableFrom(Boolean.class) || myMethod.type.isAssignableFrom(boolean.class)) {
            method.invoke(object, rs.getBoolean(column));
        } else if (myMethod.type.isAssignableFrom(Byte.class) || myMethod.type.isAssignableFrom(byte.class)) {
            method.invoke(object, rs.getByte(column));
        }
    }

    /**
     * 获取每个的字段属性值
     * @param field 字段
     * @throws NoSuchMethodException 异常
     */
    @SuppressWarnings("unchecked")
    private void getField(Field field) throws NoSuchMethodException {
        Columns columns = field.getAnnotation(Columns.class);
        if (columns == null) {
            return;
        }
        Id id = field.getAnnotation(Id.class);
        String nodeName = columns.name();
        Class<?> type = field.getType();
        String fieldName = field.getName();
        Method setFieldName = this.clazz.getMethod("set" + Character.toUpperCase(fieldName.charAt(0))
                + fieldName.substring(1), type);
        Method getFieldName = this.clazz.getMethod("get" + Character.toUpperCase(fieldName.charAt(0))
                + fieldName.substring(1));
        attribute.put(nodeName, new MyMethod(type, setFieldName, getFieldName, id != null));
    }

    static class MyMethod {
        Class<?> type;
        Method setMethod;
        Method getMethod;
        boolean isId;

        MyMethod(Class<?> type, Method setMethod, Method getMethod, boolean isId) {
            this.type = type;
            this.setMethod = setMethod;
            this.getMethod = getMethod;
            this.isId = isId;
        }
    }
}

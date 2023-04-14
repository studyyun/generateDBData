package com.raymond.db.utils;

import com.raymond.db.enums.AndOr;
import com.raymond.db.enums.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 条件查询
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-14 15:14
 */
public class SqlHelper {
    private StringBuffer sql;

    private Map<Integer, Object> map;

    private int i;
    public SqlHelper() {
        this.sql = new StringBuffer(" where 1 = 1");
        this.map = new HashMap<Integer, Object>();
        i = 1;
    }

    private String getAndOr(AndOr andOr) {
        return andOr.equals(AndOr.AND) ? " and " : "or";
    }
    /**
     * 相等
     * @param name 列名
     * @param object 数据
     */
    public void eq(AndOr andOr, String name, Object object) {
        sql.append(getAndOr(andOr)).append(name).append(" = ?");
        map.put(i, object);
        i ++;
    }
    /**
     * 相等
     * @param name 列名
     * @param object 数据
     */
    public void eq(String name, Object object) {
       eq(AndOr.AND, name, object);
    }
    /**
     * 大于等于
     * @param name 列名
     * @param object 数据
     */
    public void gte(String name, Object object) {
        gte(AndOr.AND, name, object);
    }
    /**
     * 大于等于
     * @param andOr 并且还是或者
     * @param name 列名
     * @param object 数据
     */
    public void gte(AndOr andOr, String name, Object object) {
        sql.append(getAndOr(andOr)).append(name).append(" >= ?");
        map.put(i, object);
        i ++;
    }
    /**
     * 小于等于
     * @param name 列名
     * @param object 数据
     */
    public void lte(String name, Object object) {
        lte(AndOr.AND, name, object);
    }
    /**
     * 小于等于
     * @param andOr 并且还是或者
     * @param name 列名
     * @param object 数据
     */
    public void lte(AndOr andOr, String name, Object object) {
        sql.append(getAndOr(andOr)).append(name).append(" <= ?");
        map.put(i, object);
        i ++;
    }

    /**
     * 中间模糊查询
     * @param name 列名
     * @param object 数据
     */
    public void innerLike(String name, String object) {
        innerLike(AndOr.AND, name, object);
    }
    /**
     * 中间模糊查询
     * @param andOr 并且还是或者
     * @param name 列名
     * @param object 数据
     */
    public void innerLike(AndOr andOr, String name, String object) {
        sql.append(getAndOr(andOr)).append(name).append(" like \"%?%\"");
        map.put(i, object);
        i ++;
    }
    /**
     * 左模糊查询
     * @param name 列名
     * @param object 数据
     */
    public void leftLike(String name, String object) {
        leftLike(AndOr.AND, name, object);
    }
    /**
     * 左模糊查询
     * @param andOr 并且还是或者
     * @param name 列名
     * @param object 数据
     */
    public void leftLike(AndOr andOr, String name, String object) {
        sql.append(getAndOr(andOr)).append(name).append(" like \"%?\"");
        map.put(i, object);
        i ++;
    }
    /**
     * 左模糊查询
     * @param name 列名
     * @param object 数据
     */
    public void rightLike(String name, String object) {
        leftLike(AndOr.AND, name, object);
    }
    /**
     * 左模糊查询
     * @param andOr 并且还是或者
     * @param name 列名
     * @param object 数据
     */
    public void rightLike(AndOr andOr, String name, String object) {
        sql.append(getAndOr(andOr)).append(name).append(" like \"%?\"");
        map.put(i, object);
        i ++;
    }
    /**
     * 大于
     * @param name 列名
     * @param object 数据
     */
    public void gt(String name, Object object) {
        gt(AndOr.AND, name, object);
    }
    /**
     * 大于
     * @param andOr 并且还是或者
     * @param name 列名
     * @param object 数据
     */
    public void gt(AndOr andOr, String name, Object object) {
        sql.append(getAndOr(andOr)).append(name).append(" > ?");
        map.put(i, object);
        i ++;
    }
    /**
     * 小于
     * @param name 列名
     * @param object 数据
     */
    public void lt(String name, Object object) {
        lt(AndOr.AND, name, object);
    }
    /**
     * 小于
     * @param andOr 并且还是或者
     * @param name 列名
     * @param object 数据
     */
    public void lt(AndOr andOr, String name, Object object) {
        sql.append(getAndOr(andOr)).append(name).append(" < ?");
        map.put(i, object);
        i ++;
    }
    /**
     * in
     * @param name 列名
     * @param object 数据
     */
    public <T> void in(String name, List<T> object) {
        in(AndOr.AND, name, object);
    }
    /**
     * in
     * @param andOr 并且还是或者
     * @param name 列名
     * @param object 数据
     */
    public <T> void in(AndOr andOr, String name, List<T> object) {
        if (object == null || object.isEmpty()) {
            return;
        }
        sql.append(getAndOr(andOr)).append(name).append(" in (");
        boolean flag = false;
        for (Object o : object) {
            if (flag) {
                sql.append(",");
            }
            sql.append("?");
            map.put(i, o);
            i ++;
            flag = true;
        }
        sql.append(")");

    }

    public void condition(Query query, String name, Object object) {
        condition(AndOr.AND, query, name, object);
    }
    @SuppressWarnings("unchecked")
    public void condition(AndOr andOr, Query query, String name, Object object) {
        switch (query) {
            case GT:
                gt(andOr, name, object);
                break;
            case IN:
                in(andOr, name, (List<Object>) object);
                break;
            case EQUAL:
                eq(andOr, name, object);
                break;
            case GTE:
                gte(andOr, name, object);
                break;
            case LTE:
                lte(andOr, name, object);
                break;
            case INNER_LIKE:
                innerLike(andOr, name, (String) object);
                break;
            case LEFT_LIKE:
                leftLike(andOr, name, (String) object);
                break;
            case RIGHT_LIKE:
                rightLike(andOr, name, (String) object);
                break;
            case LT:
                lt(andOr, name, object);
                break;
            default:
        }
    }

    public StringBuffer getSql() {
        return sql;
    }

    public Map<Integer, Object> getMap() {
        return map;
    }


}

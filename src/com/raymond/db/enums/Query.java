package com.raymond.db.enums;

/**
 * 查询
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-14 15:18
 */
public enum Query {
    /**
     * 相等
     */
    EQUAL,
    /**
     * 大于等于
     */
    GTE,
    /**
     * 小于等于
     */
    LTE,
    /**
     * 中模糊查询
     */
    INNER_LIKE,
    /**
     * 左模糊查询
     */
    LEFT_LIKE,
    /**
     * 右模糊查询
     */
    RIGHT_LIKE,
    /**
     * 大于
     */
    GT,
    /**
     * 小于
     */
    LT,
    /**
     * 包含, 属性必须是 Collection<Long> 类型, 因为 EMP 中 ID 为 Long 型, 为了简便, 我只写了 Long
     */
    IN;

}

package com.raymond.db.utils;

/**
 * 分页sql
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-11 14:35
 */
public class PageHelper {

    private static final Integer MAX_LENGTH = 500000;

    private static final String DISTINCT = "distinct";

    /**
     * 获取 Db2 分页 SQL
     * @param pageInfo 分页对象
     * @param selectSql 要分页的 SQL
     * @return 分页 SQL
     */
    public static String db2PageSql(PageInfo pageInfo, String selectSql) {
        // 拼接分页数据
        // 开始行数
        int beginCount = pageInfo.getPageSize() * pageInfo.getPageIndex()
                - (pageInfo.getPageSize() - 1);
        // 结束行数
        int endCount = pageInfo.getPageSize() * pageInfo.getPageIndex();
        return "select * from(select row_number() over() as rownum ,t.* from (" +
                selectSql + ")t )temp_t where rownum between " +
                beginCount + " and " + endCount;
    }

    /**
     * 获取 Oracle 分页 SQL
     * @param pageInfo 分页对象
     * @param selectSql 要分页的 SQL
     * @return 分页 SQL
     */
    public static String oraclePageSql(PageInfo pageInfo, String selectSql) {
        // 开始行数
        int beginCount = pageInfo.getPageSize() * pageInfo.getPageIndex() - (pageInfo.getPageSize() - 1);
        // 结束行数
        int endCount = pageInfo.getPageSize() * pageInfo.getPageIndex();
        StringBuilder sqlSb = new StringBuilder();
        if (pageInfo.getPageIndex() <= MAX_LENGTH) {
            sqlSb.append("select * from (select t.*, rownum rn from (")
                    .append(selectSql).append(") t where rownum<=").append(endCount).append(") where rn>=").append(beginCount);
        } else {
            sqlSb.append("select * from (select t.*, rownum rn from (")
                    .append(selectSql).append(") t) where  rn between ").append(
                    beginCount).append(" and ").append(endCount);
        }
        return sqlSb.toString();
    }

    /**
     * 获取 Mysql 分页 SQL
     * @param pageInfo 分页对象
     * @param selectSql 要分页的 SQL
     * @return 分页 SQL
     */
    public static String mysqlPageSql(PageInfo pageInfo, String selectSql) {
        // 开始行数
        int beginCount = pageInfo.getPageSize() * (pageInfo.getPageIndex() - 1);
        // 结束行数
        int endCount = pageInfo.getPageSize();
        return selectSql + " limit " + beginCount +
                "," + endCount;
    }

    /**
     * 获取 SqlServer 分页 SQL
     * @param pageInfo 分页对象
     * @param selectSql 要分页的 SQL
     * @return 分页 SQL
     */
    public static String sqlServerPageSql(PageInfo pageInfo, String selectSql) {
        // 开始行数
        int beginCount = pageInfo.getPageSize() * pageInfo.getPageIndex()
                - (pageInfo.getPageSize() - 1);
        // 结束行数
        int endCount = pageInfo.getPageSize() * pageInfo.getPageIndex();
        StringBuilder sqlSb = new StringBuilder();
        int offset = 20;
        if (!selectSql.contains(DISTINCT) || selectSql.indexOf(DISTINCT) > offset) {
            selectSql = selectSql.substring(selectSql.indexOf("select") + 7);
            selectSql = "select top " + endCount +
                    " 0 as tempColumn," + selectSql;
        } else {
            selectSql = selectSql.substring(selectSql.indexOf(DISTINCT) + 9);
            selectSql = "select distinct top " + endCount +
                    " 0 as tempColumn," + selectSql;
        }
        sqlSb.append("select * from (select row_number() over(order by tempColumn) tempRowNumber,* from (")
                .append(selectSql).append(") t) tt where tempRowNumber>=").append(beginCount);
        return sqlSb.toString();
    }


}

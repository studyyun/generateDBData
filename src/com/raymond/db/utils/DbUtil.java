package com.raymond.db.utils;



import java.sql.*;

/**
 * DBUtil
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-11 10:56
 */
public class DbUtil {
    /**
     * 释放结果集，句柄，和数据库连接
     * @param rs ResultSet
     * @param ps PreparedStatement
     * @param conn Connection
     */
    public static void free(ResultSet rs, PreparedStatement ps, Connection conn) throws SQLException {
        free(rs);
        free(ps, conn);
    }

    /**
     * 释放句柄和数据库连接
     * @param ps PreparedStatement
     * @param conn Connection
     */
    public static void free(PreparedStatement ps, Connection conn) throws SQLException {
       free(ps);
       free(conn);
    }

    /**
     * 释放句柄和数据库连接
     * @param ps Statement
     * @param conn Connection
     */
    public static void free(Statement ps, Connection conn) throws SQLException {
        free(ps);
        free(conn);
    }
    /**
     * 释放句柄和数据库连接
     * @param rs ResultSet
     * @param ps Statement
     * @param conn Connection
     */
    public static void free(ResultSet rs, Statement ps, Connection conn) throws SQLException {
        free(rs);
        free(ps, conn);

    }

    /**
     * 释放句柄
     * @param ps PreparedStatement
     */
    public static void free(PreparedStatement ps) throws SQLException {
        if(ps != null){
            ps.close();
        }

    }

    /**
     * 释放句柄
     * @param ps Statement
     */
    public static void free(Statement ps) throws SQLException {
        if(ps != null){
            ps.close();
        }

    }

    /**
     * 释放句柄
     * @param rs ResultSet
     */
    public static void free(ResultSet rs) throws SQLException {
        if(rs != null){
            rs.close();
        }

    }

    /**
     * 释放数据库连接
     * @param conn Connection
     */
    public static void free(Connection conn) throws SQLException {
        if(conn != null){
            conn.close();
        }

    }

    /**
     * 提交事务
     * @param conn Connection
     */
    public static void commit(Connection conn) throws SQLException {
        if (conn != null) {
            conn.commit();
        }
    }

    /**
     * 货滚事务
     * @param conn Connection
     */
    public static void rollback(Connection conn) throws SQLException {
        if (conn != null) {
            conn.rollback();
        }
    }

    /**判断数据库连接是否正常
     * @param conn 数据库连接
     * @return false为正常
     */
    public static boolean isClosed(Connection conn, String sql) throws SQLException {
        try {
            boolean flag =conn.isClosed();
            Statement st=null;
            ResultSet rs=null;
            if(!flag){
                try {
                    st = conn.createStatement();
                    rs = st.executeQuery(sql);
                } catch (Exception e) {
                    free(null,st,null);
                    return true;
                }finally{
                    free(rs,st,null);
                }
            }
            return flag;
        } catch (SQLException e) {
            free(null, conn);
        }
        return true;
    }

}

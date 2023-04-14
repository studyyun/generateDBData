package com.raymond.db;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 数据库连接池
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-11 10:36
 */
public class DbPool {
    private static final Logger logger = Logger.getLogger(DbPool.class);

    private static DataSource dataSource;
    /**
     * 数据库连接池
     */
    private static DbPool pool;

    private DbPool(){
        intiDataSource();
    }
    private void intiDataSource(){
        try {
            Properties prop = new Properties();
            // 1.创建一个字节输入流的对象
            InputStream in = new FileInputStream("jdbc.properties");
            prop.load(in);
            // 2.初始化数据源
            dataSource = DruidDataSourceFactory.createDataSource(prop);
            logger.info("初始化数据源信息成功....");
        } catch (Exception e) {
            logger.error("初始化数据源信息异常....", e);
        }
    }

    public static DbPool getInstance(){
        if(null == pool) {
            synchronized (DbPool.class) {
                if(null == pool) {
                    pool = new DbPool();
                }
            }
        }
        return pool;
    }
    /**
     * 获取数据库连接池
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 获取数据库连接
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("获取数据库连接信息异常....", e);
        }
        return conn;
    }

    /**
     * 释放数据库连接
     */
    public void close(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("关闭数据库连接信息异常....", e);
            }
        }
        close(conn, st);
    }

    public void close(Connection conn, Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                logger.error("关闭数据库连接信息异常....", e);
            }
        }
        close(conn);
    }

    public void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("关闭数据库连接信息异常....", e);
            }
        }
    }



}

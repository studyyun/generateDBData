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
 * ���ݿ����ӳ�
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-11 10:36
 */
public class DbPool {
    private static final Logger logger = Logger.getLogger(DbPool.class);

    private static DataSource dataSource;
    /**
     * ���ݿ����ӳ�
     */
    private static DbPool pool;

    private DbPool(){
        intiDataSource();
    }
    private void intiDataSource(){
        try {
            Properties prop = new Properties();
            // 1.����һ���ֽ��������Ķ���
            InputStream in = new FileInputStream("jdbc.properties");
            prop.load(in);
            // 2.��ʼ������Դ
            dataSource = DruidDataSourceFactory.createDataSource(prop);
            logger.info("��ʼ������Դ��Ϣ�ɹ�....");
        } catch (Exception e) {
            logger.error("��ʼ������Դ��Ϣ�쳣....", e);
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
     * ��ȡ���ݿ����ӳ�
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * ��ȡ���ݿ�����
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("��ȡ���ݿ�������Ϣ�쳣....", e);
        }
        return conn;
    }

    /**
     * �ͷ����ݿ�����
     */
    public void close(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("�ر����ݿ�������Ϣ�쳣....", e);
            }
        }
        close(conn, st);
    }

    public void close(Connection conn, Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                logger.error("�ر����ݿ�������Ϣ�쳣....", e);
            }
        }
        close(conn);
    }

    public void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("�ر����ݿ�������Ϣ�쳣....", e);
            }
        }
    }



}

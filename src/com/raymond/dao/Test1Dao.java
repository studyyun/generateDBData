package com.raymond.dao;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.raymond.db.BaseDao;
import com.raymond.db.utils.DbUtil;
import com.raymond.db.utils.PageInfo;
import com.raymond.db.utils.SqlHelper;
import com.raymond.entity.Test;
import com.raymond.entity.Test1;
import com.raymond.httpclient.HttpClientFactory;
import com.raymond.httpclient.MyHttpClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * test1��dao��
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-17 11:08
 */
public class Test1Dao extends BaseDao<Test1> {


    public static void main(String[] args) throws Exception {
//        insert();
//        for (int i = 0; i < 1; i++) {
//            insert2(i, 15);
//        }
//        del();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            try {
                insert(i, 1);
            } catch (Exception e) {
                System.out.println("报错拉");
//                e.printStackTrace();
            }
//            insert(i,1);   2360045
        }
        /*Test1Dao testDao = new Test1Dao();
        Connection conn = testDao.getConnection();
        String sql = "SELECT * FROM TM_CUST_RISK_INFO";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        DbUtil.free(ps, conn);*/
//        batchinsert();
//        insert(1,10);
//        update();
//        query();
//        Runtime.getRuntime().addShutdownHook();
        System.out.println("总共耗时" + (System.currentTimeMillis() - start));
//        PageInfo<Test1> pageInfo = new PageInfo<Test1>();
//        Test test1 = new Test();
//        test1.setName("as1");
//        testDao.findPage(pageInfo);
//        System.out.println(JSONObject.toJSONString(testDao.delete(test1)));
//        System.out.println(JSONObject.toJSONString(pageInfo));

//        MyHttpClient myHttpClient = HttpClientFactory.getMyHttpClient(HttpClientFactory.Type.XML);

    }

    private static void batchinsert() throws SQLException {
        Test1Dao testDao = new Test1Dao();
        Connection conn = testDao.getConnection();
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        List<String> sqls = new ArrayList<>();
        sqls.add("INSERT INTO TB_CONTROL(STIME,MOTIME,PHONE,SPNUMBER,CPNO,MSG) VALUES(SYSTIMESTAMP,SYSTIMESTAMP,' ',' ',' ',' ')");
        sqls.add("INSERT INTO TB_CONTROL(STIME,MOTIME,PHONE,SPNUMBER,CPNO,MSG) VALUES(SYSTIMESTAMP,SYSTIMESTAMP,' ',' ',' ',' ')");
        sqls.add("INSERT INTO TB_CONTROL(STIME,MOTIME,PHONE,SPNUMBER,CPNO,MSG) VALUES(SYSTIMESTAMP,SYSTIMESTAMP,' ',' ',' ',' ')");
        sqls.add("INSERT INTO TB_CONTROL(STIME,MOTIME,PHONE,SPNUMBER,CPNO,MSG) VALUES(SYSTIMESTAMP,SYSTIMESTAMP,' ',' ',' ',' ')");
        sqls.add("INSERT INTO TB_CONTROL(STIME,MOTIME,PHONE,SPNUMBER,CPNO,MSG) VALUES(SYSTIMESTAMP,SYSTIMESTAMP,' ',' ',' ',' ')");
        for (String sql : sqls) {
            stmt.addBatch(sql);
        }
        int[] re = stmt.executeBatch();
        conn.commit();
        
    }
    
    private static void query() throws SQLException {
        Test1Dao testDao = new Test1Dao();
        Connection conn = testDao.getConnection();
        String sql = "SELECT * FROM sms_mt_report2021 ORDER BY sms_mt_report2021.IMONTH";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet resultSet = ps.executeQuery();
        DbUtil.free(ps, conn);
    }
    
    private static void update() throws SQLException {
        Test1Dao testDao = new Test1Dao();
        Connection conn = testDao.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement("UPDATE lf_server_file SET USER_NAME = 'admin' WHERE FILE_NAME = ? AND USER_NAME != 'admin'");

        ps.setString(1, "111.txt");
        int i = ps.executeUpdate();
        conn.commit();
        conn.setAutoCommit(true);
        DbUtil.free(ps, conn);
    }

    private static void del() throws SQLException {
        Test1Dao testDao = new Test1Dao();
        Connection conn = testDao.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_queue WHERE id = ?");

        ps.setInt(1, 1);
        int i = ps.executeUpdate();
        ps = conn.prepareStatement("DELETE FROM TB_READ WHERE SRC_ID = ?");
        ps.setInt(1, 1);
        i = ps.executeUpdate();
        conn.commit();
        conn.setAutoCommit(true);
        DbUtil.free(ps, conn);
    }

    private static void insert(int count, int onceCount) throws SQLException {
        Test1Dao testDao = new Test1Dao();
        Connection conn = testDao.getConnection();
        conn.setAutoCommit(false);
//        PreparedStatement ps = conn.prepareStatement("insert into PB_WHITE_INFO_SERVICE (ID, PHONE, CUST_ID, TYPE) values (?,?,?,?)");
//        PreparedStatement ps = conn.prepareStatement("insert into PB_BLACK_INFO_SERVICE (ID, PHONE, CUST_ID) values (?,?,?)");
        PreparedStatement ps = conn.prepareStatement("insert into tb_queue (phone, msg, svrtype) values (?,?,?)");
        //PreparedStatement ps = conn.prepareStatement("insert into tb_queue_batch (phone, msg, svrtype) values (?,?,?)");
//        PreparedStatement ps = conn.prepareStatement("insert into LF_CLIENT_ZSZQ (GROUP_ID, PHONE, ACOUNT,GROUP_TYPE) values (?,?,?,?)");
        /*PreparedStatement ps = conn.prepareStatement("insert into TM_CUST_RISK_INFO (UC_NO,CAPITAL_ACCOUNT, INVEST_DURATION, DISLIKE_TYPE, EVAL_LEVEL, MOBILE)" +
                " values (?,?,?,?,?,?)");*/
//        PreparedStatement ps = conn.prepareStatement("insert into lf_server_file (file_path, file_name, FILE_CREATE_TIME,FILE_UPDATE_TIME) values (?,?,?,?)");
        long start = System.currentTimeMillis();
        long phone = 13500000004L + count * 10000;
        Date date = DateUtil.offsetDay(new Date(), 2);
        Timestamp timestamp = new Timestamp(date.getTime());
        for (int i=0;i < onceCount;i++){
            ps.setString( 1, phone + i + "");
            ps.setString( 2, phone + i + "雷浩啊");
            //ps.setString( 3, "96512");
            ps.setString( 3, "96512");
//            ps.setLong( 4,2);
//            ps.setLong( 4,1);
//            ps.setTimestamp( 3, timestamp);
//            ps.setDate( 3, new java.sql.Date(System.currentTimeMillis()));
//            ps.setTimestamp( 3, timestamp);
//            ps.setTimestamp( 4, timestamp);
            ps.addBatch();
            // ÿ1000����¼����һ��
//            if (i % 1000 == 0){
//                ps.executeBatch();
//                conn.commit();
//                ps.clearBatch();
//            }
            // ÿ1000����¼����һ��
        }
        // ʣ����������1000
        ps.executeBatch();
        conn.commit();
        conn.setAutoCommit(true);
        DbUtil.free(ps, conn);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    private static void insert2(int count, int onceCount) throws SQLException {
        Test1Dao testDao = new Test1Dao();
        Connection conn = testDao.getConnection();
        conn.setAutoCommit(false);
        StringBuilder sql = new StringBuilder("insert into tb_read values");
//        for (int i = 0; i < 10000; i++) {
        for (int i = 0; i < onceCount; i++) {
            if (i == 0) {
                sql.append("(?)");
                continue;
            }
            sql.append(",(?)");
        }
        PreparedStatement ps = conn.prepareStatement(sql.toString());
        for (int i=1;i <= onceCount;i++){
            ps.setInt(i, count * 10000 + i);
        }
        // ʣ����������1000
        ps.execute();
        conn.commit();
        conn.setAutoCommit(true);
        DbUtil.free(ps, conn);
    }
    private static void insert1(int count, int onceCount) throws SQLException {
        Test1Dao testDao = new Test1Dao();
        Connection conn = testDao.getConnection();
        conn.setAutoCommit(false);
        StringBuilder sql = new StringBuilder("insert into TM_CUST_RISK_INFO (UC_NO, CAPITAL_ACCOUNT) values");
//        for (int i = 0; i < 10000; i++) {
        for (int i = 0; i < onceCount; i++) {
            if (i == 0) {
                sql.append("(?,?)");
                continue;
            }
            sql.append(",(?,?)");
        }
        PreparedStatement ps = conn.prepareStatement(sql.toString());
        long start = System.currentTimeMillis();
        long phone = 13800000000L + count * 10000;
        Date date = DateUtil.offsetDay(new Date(), 2);
        Timestamp timestamp = new Timestamp(date.getTime());
        for (int i=0;i < onceCount;i++){
            ps.setLong( 1, phone + i);
            ps.setString( 2,phone+"");
        }
        // ʣ����������1000
        ps.execute();
        conn.commit();
        conn.setAutoCommit(true);
        DbUtil.free(ps, conn);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}

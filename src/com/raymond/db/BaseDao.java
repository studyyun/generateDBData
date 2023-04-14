package com.raymond.db;

import com.alibaba.druid.util.StringUtils;
import com.raymond.db.utils.DaoHelper;
import com.raymond.db.utils.DbUtil;
import com.raymond.db.utils.PageInfo;
import com.raymond.db.utils.SqlHelper;
import com.raymond.enums.DateStyle;
import com.raymond.utils.DateUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.Date;
import java.util.*;
/**
 * ���������ݿ����
 * @author raymond
 *
 */
public class BaseDao<T> {

    /**���ݿ����ӳ�**/
    private DbPool pool;

    private Type clazz;

    public BaseDao(){
        pool = DbPool.getInstance();
        clazz = getClazz();
    }

    private Type getClazz() {
        return ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }


    /**
     * ��ȡ���ݿ�����
     * @return ���ݿ�����
     */
    protected Connection getConnection(){
        return pool.getConnection();
    }
    /**
     * �ͷ����ݿ�����
     * @param stmt Statementʵ��
     * @param conn ���ݿ�����
     */
    void free(Statement stmt, Connection conn) throws SQLException {
        DbUtil.free(null, stmt, conn);
    }

    void free(ResultSet rs, PreparedStatement ps) throws SQLException {
        DbUtil.free(rs, ps, null);
    }
    /**
     * �ͷ����ݿ�����
     * @param ps PreparedStatementʵ��
     * @param conn ���ݿ�����
     */
    void free(PreparedStatement ps, Connection conn) throws SQLException {
        DbUtil.free(ps, conn);
    }
    /**
     * �ر����ݿ�����
     * @param rs ���ؽ����ʵ��
     * @param stmt Statementʵ��
     * @param conn ���ݿ�����
     */
    void free(ResultSet rs, Statement stmt, Connection conn) throws SQLException {
        DbUtil.free(rs, stmt, conn);
    }
    /**
     * �ر����ݿ�����
     * @param rs ���صĽ����
     * @param pstmt PreparedStatementʵ��
     * @param conn ���ݿ�����
     */
    void free(ResultSet rs, PreparedStatement pstmt, Connection conn) throws SQLException {
        DbUtil.free(rs, pstmt, conn);
    }
    /**
     * �ر����ݿ�����
     * @param cs CallableStatementʵ��
     * @param conn ���ݿ�����
     */
    void free(CallableStatement cs, Connection conn) throws SQLException {
        DbUtil.free(null, cs, conn);
    }
    /**
     * �ر����ݿ�����
     * @param conn ���ݿ�����
     */
    void free(Connection conn) throws SQLException {
        DbUtil.free(conn);
    }

    /**
     * ƴ��unionAll
     * @param fieldSql �ֶ�
     * @param conditionSql ����
     * @param tableNames ����
     * @return ƴ�Ӻõ�sql
     */
    String getUnionAllSql(String fieldSql, String conditionSql, Set<String> tableNames) {
        if (tableNames.size() == 0) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        int i = 0;
        for (String tableName : tableNames) {
            if (i > 0) {
                sql.append(" union all ");
            }
            sql.append(fieldSql).append(" from ").append(tableName).append(conditionSql);
            i++;
        }
        return sql.toString();
    }

    /**
     * ִ�и��²���,UPDATE�Լ�DELETE����
     * @param sql ��Ҫִ�еĲ������
     * @return ִ�и���Ӱ��������� -1 ��ʾ������
     * @throws SQLException ���������ݿ����Ȩ�޲���������ݿ������ѶϿ��ȴ��󡣻��׳��쳣
     */
    public int executeUpdate(String sql) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = getConnection();
            stmt = conn.createStatement();
            int count = stmt.executeUpdate(sql);
            conn.commit();
            return count;
        }catch(SQLException e){
            conn.rollback();
            throw e;
        }finally{

            free(stmt,conn);
        }
    }

    /**
     * ִ�д洢����
     * @param procedure �洢���̵���sql���
     * @return �����Ƿ�ɹ�
     * @throws SQLException ִ�д洢�����г��ֵ��쳣
     */
    public boolean executeProcedure(String procedure) throws SQLException {
        Connection conn = null;
        CallableStatement cs = null;
        try{
            conn = getConnection();
            conn.setAutoCommit(false);
            cs = conn.prepareCall(procedure);
            cs.execute();
            conn.commit();
            return true;
        }catch(SQLException e){
            conn.rollback();
            throw e;
        }finally{
            if(null != conn){
                conn.setAutoCommit(true);
            }
            free(cs,conn);
        }
    }

    public int save(Object object, Connection conn) throws SQLException {
        return save(object, conn, true);
    }
    /**
     * ����
     * @param object ����
     * @return ����
     */
    private int save(Object object, Connection conn, boolean isCommit) throws SQLException {
        PreparedStatement ps = null;
        try {
            if (isCommit && conn == null) {
                throw new SQLException("���������봫������");
            }
            if(!isCommit && conn == null) {
                conn = getConnection();
            }
            Map<Integer, Object> indexValueBind = new HashMap<Integer, Object>(10);
            String sql = DaoHelper.getInsertSql(object, indexValueBind);
            ps = conn.prepareStatement(sql);
            setDynamicParams(ps, indexValueBind);
            return ps.executeUpdate();
        } finally {
            free(ps, isCommit ? null : conn);
        }
    }

    /**
     * ����
     * @param object ����
     * @return ����
     */
    public int save(Object object) throws SQLException {
       return save(object, null, false);
    }
    /**
     * ����
     * @param object ����
     * @return ����
     */
    public int batSave(List<T> object) throws SQLException {
        return batSave(object, (Class) clazz);
    }

    /**
     * ����
     * @param object ����
     * @return ����
     */
    public <E> int batSave(List<E> object, Class clazz) throws SQLException {
        return batSave(object, clazz, null, false);
    }

    /**
     * ����
     * @param object ����
     * @return ����
     */
    public <E> int batSave(List<E> object, Class clazz, Connection conn) throws SQLException {
        return batSave(object, clazz, conn, true);
    }
    /**
     * ����
     * @param object ����
     * @return ����
     */
    @SuppressWarnings("unchecked")
    private <E> int batSave(List<E> object, Class clazz, Connection conn, boolean isCommit) throws SQLException {
        if (object == null || object.size() < 1) {
            return 0;
        }
        PreparedStatement ps = null;
        try {
            if (isCommit && conn == null) {
                throw new SQLException("��������������������");
            }
            if(!isCommit && conn == null) {
                conn = getConnection();
                conn.setAutoCommit(false);
            }
            List<Map<Integer, Object>> indexValueBinds = new ArrayList<Map<Integer, Object>>();
            String sql = DaoHelper.getBatInsertSql((List<Object>) object, indexValueBinds, clazz);
            ps = conn.prepareStatement(sql);
            for (Map<Integer, Object> indexValueBind : indexValueBinds) {
                setDynamicParams(ps, indexValueBind);
                ps.addBatch();
            }
            ps.executeBatch();
            if (!isCommit) {
                conn.commit();
            }
            return object.size();
        } catch (SQLException e) {
            if (!isCommit) {
                DbUtil.rollback(conn);
            }
            throw e;
        } finally {
            free(ps, isCommit ? null : conn);
        }
    }
    /**
     * �޸�ȫ������
     * @param object �޸ĵ�����
     * @return �޸ĵ�����
     */
    public int update(Object object) throws SQLException {
        return update(object, false);
    }

    /**
     * ��ID�޸�
     * @param object ��Ҫ�޸ĵ�����
     * @return �޸ĵ�����
     */
    public int updateById(Object object) throws SQLException {
        return update(object, true);
    }

    /**
     * �Ƿ�ID�޸�
     * @param object ��Ҫ�޸ĵ�����
     * @param flag true��ID�޸�
     * @return �޸�����
     */
    private int update(Object object, boolean flag) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            Map<Integer, Object> indexValueBind = new HashMap<Integer, Object>();
            String sql = DaoHelper.getUpdateSql(object, indexValueBind, flag);
            ps = conn.prepareStatement(sql);
            setDynamicParams(ps, indexValueBind);
            return ps.executeUpdate();
        } finally {
            free(ps, conn);
        }
    }

    /**
     * �������޸�
     * @param object ��Ҫ�޸ĵ�����
     * @param sqlHelper �޸ĵ�����
     * @return �޸�����
     */
    public int updateByCondition(Object object, SqlHelper sqlHelper) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            Map<Integer, Object> indexValueBind = new HashMap<Integer, Object>(16);
            String sql = DaoHelper.getUpdateSql(object, indexValueBind, false);
            sql += sqlHelper.getSql();
            ps = conn.prepareStatement(sql);
            merge(indexValueBind, sqlHelper.getMap());
            setDynamicParams(ps, indexValueBind);
            return ps.executeUpdate();
        } finally {
            free(ps, conn);
        }
    }

    /**
     * ��IDɾ��
     * @param id id
     * @return ɾ������
     */
    public int deleteById(String id) throws SQLException {
        return deleteById((Object) id);
    }

    /**
     * ��IDɾ��
     * @param id id
     * @return ɾ������
     */
    public int deleteById(int id) throws SQLException {
        return deleteById((Object) id);
    }

    /**
     * ��IDɾ��
     * @param id id
     * @return ɾ������
     */
    public int deleteById(long id) throws SQLException {
        return deleteById((Object) id);
    }

    /**
     * ��IDɾ��
     * @param object ID
     * @return ɾ������
     */
    private int deleteById(Object object) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = DaoHelper.getDeleteByConditionSql((Class) clazz, true);
            ps = conn.prepareStatement(sql);
            ps.setObject(1, object);
            return ps.executeUpdate();
        } finally {
            free(ps, conn);
        }
    }

    /**
     * ɾ��
     * @param object ����
     * @return ɾ������
     */
    public int delete(Object object) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            Map<Integer, Object> indexValueBind = new HashMap<Integer, Object>(16);
            String sql = DaoHelper.getDeleteSql(object, indexValueBind);
            ps = conn.prepareStatement(sql);
            setDynamicParams(ps, indexValueBind);
            return ps.executeUpdate();
        } finally {
            free(ps, conn);
        }
    }

    /**
     * ������ɾ��  ��ǰ��
     * @param sqlHelper SqlHelper
     * @return ɾ������
     */
    public int deleteByCondition(SqlHelper sqlHelper) throws SQLException {
        return deleteByCondition(sqlHelper, (Class) clazz);
    }

    /**
     * ������ɾ��
     * @param sqlHelper SqlHelper
     * @param clazz ��Ҫɾ������
     * @return ɾ������
     */
    public int deleteByCondition(SqlHelper sqlHelper, Class clazz) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = DaoHelper.getDeleteByConditionSql(clazz, false);
            ps = conn.prepareStatement(sql);
            setDynamicParams(ps, sqlHelper.getMap());
            return ps.executeUpdate();
        } finally {
            free(ps, conn);
        }
    }
    /**
     * �����ѯ����
     * @return ��ȡ����
     */
    public List<T> find() throws Exception {
        String sql = DaoHelper.getSql((Class) clazz);
        return getData(sql, null, (Class) clazz);
    }


    /**
     * �����ҳ��ѯ
     * @param pageInfo pageInfo
     * @return ��ҳ����
     */
    public PageInfo<T> findPage(PageInfo<T> pageInfo) throws Exception {
        return findPageEntity(pageInfo, (Class) clazz);

    }

    /**
     * ����������ѯ
     * @param obj ��Ҫ��ѯ����
     * @return ��ȡ����
     */
    public List<T> findByCondition(Object obj) throws Exception {
        return findEntityByCondition(obj);
    }

    /**
     * ��ҳ����������ѯ
     * @param object ��Ҫ��ѯ����
     * @param pageInfo ��ҳ��Ϣ
     * @return ��ȡ����
     */
    public PageInfo<T> findPageByCondition(Object object, PageInfo<T> pageInfo) throws Exception {
        Map<Integer, Object> indexValueBind = new HashMap<Integer, Object>(10);
        String sql = DaoHelper.getSqlByCondition(object, indexValueBind);
        return getPageData(sql, pageInfo, indexValueBind, object.getClass());
    }

    /**
     * ����̬������ѯ
     * @param sqlHelper ����
     * @return ��ȡ����
     */
    public List<T> findByDynaCondition(SqlHelper sqlHelper) throws Exception {
        Map<Integer, Object> indexValueBind = sqlHelper.getMap();
        String sql = DaoHelper.getSql((Class) clazz) + sqlHelper.getSql();
        return getData(sql, indexValueBind, (Class) clazz);
    }

    /**
     * ��ҳ����̬������ѯ
     * @param sqlHelper ����
     * @param pageInfo ��ҳ��Ϣ
     * @return ��ȡ����
     */
    public PageInfo<T> findPageByDynaCondition(SqlHelper sqlHelper, PageInfo<T> pageInfo) throws Exception {
        Map<Integer, Object> indexValueBind = sqlHelper.getMap();
        String sql = DaoHelper.getSql((Class) clazz) + sqlHelper.getSql();
        return getPageData(sql, pageInfo, indexValueBind, (Class) clazz);

    }

    /**
     * ��ȡ��ҳ����
     * @param pageInfo ��ҳ����
     * @param clazz ��Ҫ��ѯ�Ķ���
     * @return ��ҳ��Ϣ
     */
    public <E> PageInfo<E> findPageEntity(PageInfo<E> pageInfo, Class clazz) throws Exception {
        return findPageEntityBySql(null, pageInfo, clazz);
    }

    /**
     * ��ҳ����������ѯ
     * @param object ��Ҫ��ѯ����
     * @return ��ȡ����
     */
    public <E> PageInfo<E> findPageEntityByCondition(Object object, PageInfo<E> pageInfo) throws Exception {
        Map<Integer, Object> indexValueBind = new HashMap<Integer, Object>(10);
        String sql = DaoHelper.getSqlByCondition(object, indexValueBind);
        return getPageData(sql, pageInfo, indexValueBind, object.getClass());
    }

    /**
     * ����������ѯ
     * @param object ��Ҫ��ѯ����
     * @return ��ȡ����
     */
    public <E> List<E> findEntityByCondition(Object object) throws Exception {
        Map<Integer, Object> indexValueBind = new HashMap<Integer, Object>(10);
        String sql = DaoHelper.getSqlByCondition(object, indexValueBind);
        return getData(sql, indexValueBind, object.getClass());
    }

    /**
     * ͨ��sql��ѯ��ҳ����
     * @param sql sql
     * @param pageInfo ��ҳ����
     * @return ��ҳ����
     */
    public PageInfo<T> findPageEntityBySql(String sql, PageInfo<T> pageInfo) throws Exception {
        return findPageEntityBySql(sql, pageInfo, (Class) clazz);
    }
    /**
     * ͨ��sql��ѯ��ҳ����
     * @param sql sql
     * @param pageInfo ��ҳ����
     * @param clazz ָ����ѯ�Ķ���
     * @return ��ҳ����
     */
    public <E> PageInfo<E> findPageEntityBySql(String sql, PageInfo<E> pageInfo, Class clazz) throws Exception {
        if (StringUtils.isEmpty(sql)) {
            sql = DaoHelper.getSql(clazz);
        }
        return getPageData(sql, pageInfo, null, clazz);
    }

    /**
     * ͨ��sql��ѯ����
     * @param sql sql
     * @return ��ѯ�����ݼ���
     */
    public List<T> findEntityBySql(String sql) throws Exception {
        return findEntityBySql(sql, (Class) clazz);
    }

    /**
     * ͨ��sql��ѯ����
     * @param sql sql
     * @param clazz ��Ҫ��ѯ�Ķ���
     * @return ��ѯ�����ݼ���
     */
    public <E> List<E> findEntityBySql(String sql, Class clazz) throws Exception {
        return getData(sql, null, clazz);
    }

    private <E> List<E> getData(String sql, Map<Integer, Object> indexValueBind, Class clazz) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            setDynamicParams(ps, indexValueBind);
            rs = ps.executeQuery();
            return DaoHelper.getDataByResultSet(rs, clazz);
        } finally {
            free(rs, ps, conn);
        }

    }

    private <E> PageInfo<E> getPageData(String sql, PageInfo<E> pageInfo, Map<Integer, Object> indexValueBind, Class clazz) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            if (pageInfo != null) {
                setPageInfo(sql, pageInfo, conn, indexValueBind);
            } else {
                pageInfo = new PageInfo<E>();
            }
            sql = DaoHelper.getPageSql(pageInfo, sql);
            List<E> data = getData(sql, indexValueBind, clazz);
            pageInfo.setRecords(data);
            return pageInfo;
        } finally {
            free(conn);
        }

    }


    /**
     * ���÷�ҳ����
     * @param sql sql
     * @param pageInfo   ��ҳ����
     * @param conn    conn
     * @param indexValueBind ����
     * @throws SQLException �쳣
     */
    private void setPageInfo(String sql, PageInfo pageInfo, Connection conn, Map<Integer, Object> indexValueBind) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String countSql = "select count(*) totalCount from (" + sql + ") countSql";
            ps = conn.prepareStatement(countSql);
            setDynamicParams(ps, indexValueBind);
            rs = ps.executeQuery();
            // �ܼ�¼��
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt("totalCount");
            }
            // ��ǰҳ��
            int pageSize = pageInfo.getPageSize();
            if (pageSize == 0) {
                return;
            }
            // ��ҳ��
            int totalPage = totalCount % pageSize == 0 ? totalCount
                    / pageSize : totalCount / pageSize + 1;
            pageInfo.setTotal(totalCount);
            pageInfo.setPageTotal(totalPage);
            // ��ǰҳ������ҳ������ת����һҳ
            if (pageInfo.getPageIndex() > totalPage) {
                pageInfo.setPageIndex(1);
            }
        } finally {
            free(rs, ps);
        }


    }

    /**
     * �ϲ���������ֵ,������Ĳ�����ӵ�ǰ��ȥ
     * @param indexValueBind ��ʼֵ
     * @param whereValue ��Ҫ��ӵ�ֵ
     */
    private void merge(Map<Integer, Object> indexValueBind, Map<Integer, Object> whereValue) {
        if (whereValue == null || indexValueBind.isEmpty()) {
            return;
        }
        int size = indexValueBind.size();
        for (int i = 1; i <= whereValue.size(); i++) {
            indexValueBind.put(size + i, whereValue.get(i));
        }
    }

    /**
     * ���ò���
     * @param ps PreparedStatement
     * @param indexValueBind ����
     * @throws SQLException sql�쳣
     */
    private void setDynamicParams(PreparedStatement ps, Map<Integer, Object> indexValueBind) throws SQLException {
        if (indexValueBind == null || indexValueBind.isEmpty()) {
            return;
        }
        Object val;
        for (int i = 1; i <= indexValueBind.size(); i++) {
            // �����ж��ֶε�����, ������Ҫ���������ͽ�������Ĵ���
            val = indexValueBind.get(i);
            // �ж��Ƿ�����������
            if (val instanceof java.util.Date) {
                java.util.Date time = (Date) val;
                String format = DateUtil.dateToStr(time, DateStyle.YYYY_MM_DD_HH_MM_SS);
                ps.setTimestamp(i, Timestamp.valueOf(format));
            } else if (null == val) {
                // ����ǿ�ֵ
                ps.setNull(i, Types.VARCHAR);
            } else {
                ps.setObject(i, val);
            }
        }
    }



}

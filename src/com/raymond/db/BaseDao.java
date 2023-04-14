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
 * 基本的数据库操作
 * @author raymond
 *
 */
public class BaseDao<T> {

    /**数据库连接池**/
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
     * 获取数据库链接
     * @return 数据库链接
     */
    protected Connection getConnection(){
        return pool.getConnection();
    }
    /**
     * 释放数据库链接
     * @param stmt Statement实例
     * @param conn 数据库链接
     */
    void free(Statement stmt, Connection conn) throws SQLException {
        DbUtil.free(null, stmt, conn);
    }

    void free(ResultSet rs, PreparedStatement ps) throws SQLException {
        DbUtil.free(rs, ps, null);
    }
    /**
     * 释放数据库链接
     * @param ps PreparedStatement实例
     * @param conn 数据库链接
     */
    void free(PreparedStatement ps, Connection conn) throws SQLException {
        DbUtil.free(ps, conn);
    }
    /**
     * 关闭数据库链接
     * @param rs 返回结果集实例
     * @param stmt Statement实例
     * @param conn 数据库链接
     */
    void free(ResultSet rs, Statement stmt, Connection conn) throws SQLException {
        DbUtil.free(rs, stmt, conn);
    }
    /**
     * 关闭数据库连接
     * @param rs 返回的结果集
     * @param pstmt PreparedStatement实例
     * @param conn 数据库连接
     */
    void free(ResultSet rs, PreparedStatement pstmt, Connection conn) throws SQLException {
        DbUtil.free(rs, pstmt, conn);
    }
    /**
     * 关闭数据库连接
     * @param cs CallableStatement实例
     * @param conn 数据库链接
     */
    void free(CallableStatement cs, Connection conn) throws SQLException {
        DbUtil.free(null, cs, conn);
    }
    /**
     * 关闭数据库连接
     * @param conn 数据库链接
     */
    void free(Connection conn) throws SQLException {
        DbUtil.free(conn);
    }

    /**
     * 拼接unionAll
     * @param fieldSql 字段
     * @param conditionSql 条件
     * @param tableNames 表名
     * @return 拼接好的sql
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
     * 执行更新操作,UPDATE以及DELETE操作
     * @param sql 需要执行的操作语句
     * @return 执行更新影响的行数。 -1 表示出错了
     * @throws SQLException 若出现数据库访问权限不足或者数据库连接已断开等错误。会抛出异常
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
     * 执行存储过程
     * @param procedure 存储过程调用sql语句
     * @return 操作是否成功
     * @throws SQLException 执行存储过程中出现的异常
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
     * 新增
     * @param object 数据
     * @return 条数
     */
    private int save(Object object, Connection conn, boolean isCommit) throws SQLException {
        PreparedStatement ps = null;
        try {
            if (isCommit && conn == null) {
                throw new SQLException("开启事务请传入连接");
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
     * 新增
     * @param object 数据
     * @return 条数
     */
    public int save(Object object) throws SQLException {
       return save(object, null, false);
    }
    /**
     * 新增
     * @param object 数据
     * @return 条数
     */
    public int batSave(List<T> object) throws SQLException {
        return batSave(object, (Class) clazz);
    }

    /**
     * 新增
     * @param object 数据
     * @return 条数
     */
    public <E> int batSave(List<E> object, Class clazz) throws SQLException {
        return batSave(object, clazz, null, false);
    }

    /**
     * 新增
     * @param object 数据
     * @return 条数
     */
    public <E> int batSave(List<E> object, Class clazz, Connection conn) throws SQLException {
        return batSave(object, clazz, conn, true);
    }
    /**
     * 新增
     * @param object 数据
     * @return 条数
     */
    @SuppressWarnings("unchecked")
    private <E> int batSave(List<E> object, Class clazz, Connection conn, boolean isCommit) throws SQLException {
        if (object == null || object.size() < 1) {
            return 0;
        }
        PreparedStatement ps = null;
        try {
            if (isCommit && conn == null) {
                throw new SQLException("控制事务请存放数据连接");
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
     * 修改全部数据
     * @param object 修改的数据
     * @return 修改的条数
     */
    public int update(Object object) throws SQLException {
        return update(object, false);
    }

    /**
     * 按ID修改
     * @param object 需要修改的数据
     * @return 修改的条数
     */
    public int updateById(Object object) throws SQLException {
        return update(object, true);
    }

    /**
     * 是否按ID修改
     * @param object 需要修改的数据
     * @param flag true按ID修改
     * @return 修改条数
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
     * 按条件修改
     * @param object 需要修改的数据
     * @param sqlHelper 修改的条件
     * @return 修改条数
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
     * 按ID删除
     * @param id id
     * @return 删除条数
     */
    public int deleteById(String id) throws SQLException {
        return deleteById((Object) id);
    }

    /**
     * 按ID删除
     * @param id id
     * @return 删除条数
     */
    public int deleteById(int id) throws SQLException {
        return deleteById((Object) id);
    }

    /**
     * 按ID删除
     * @param id id
     * @return 删除条数
     */
    public int deleteById(long id) throws SQLException {
        return deleteById((Object) id);
    }

    /**
     * 按ID删除
     * @param object ID
     * @return 删除条数
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
     * 删除
     * @param object 条件
     * @return 删除条数
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
     * 按条件删除  当前表
     * @param sqlHelper SqlHelper
     * @return 删除数量
     */
    public int deleteByCondition(SqlHelper sqlHelper) throws SQLException {
        return deleteByCondition(sqlHelper, (Class) clazz);
    }

    /**
     * 按条件删除
     * @param sqlHelper SqlHelper
     * @param clazz 需要删除的类
     * @return 删除数量
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
     * 单表查询所有
     * @return 获取数据
     */
    public List<T> find() throws Exception {
        String sql = DaoHelper.getSql((Class) clazz);
        return getData(sql, null, (Class) clazz);
    }


    /**
     * 单表分页查询
     * @param pageInfo pageInfo
     * @return 分页对象
     */
    public PageInfo<T> findPage(PageInfo<T> pageInfo) throws Exception {
        return findPageEntity(pageInfo, (Class) clazz);

    }

    /**
     * 单表条件查询
     * @param obj 需要查询的类
     * @return 获取数据
     */
    public List<T> findByCondition(Object obj) throws Exception {
        return findEntityByCondition(obj);
    }

    /**
     * 分页单表条件查询
     * @param object 需要查询的类
     * @param pageInfo 分页信息
     * @return 获取数据
     */
    public PageInfo<T> findPageByCondition(Object object, PageInfo<T> pageInfo) throws Exception {
        Map<Integer, Object> indexValueBind = new HashMap<Integer, Object>(10);
        String sql = DaoHelper.getSqlByCondition(object, indexValueBind);
        return getPageData(sql, pageInfo, indexValueBind, object.getClass());
    }

    /**
     * 单表动态条件查询
     * @param sqlHelper 条件
     * @return 获取数据
     */
    public List<T> findByDynaCondition(SqlHelper sqlHelper) throws Exception {
        Map<Integer, Object> indexValueBind = sqlHelper.getMap();
        String sql = DaoHelper.getSql((Class) clazz) + sqlHelper.getSql();
        return getData(sql, indexValueBind, (Class) clazz);
    }

    /**
     * 分页单表动态条件查询
     * @param sqlHelper 条件
     * @param pageInfo 分页信息
     * @return 获取数据
     */
    public PageInfo<T> findPageByDynaCondition(SqlHelper sqlHelper, PageInfo<T> pageInfo) throws Exception {
        Map<Integer, Object> indexValueBind = sqlHelper.getMap();
        String sql = DaoHelper.getSql((Class) clazz) + sqlHelper.getSql();
        return getPageData(sql, pageInfo, indexValueBind, (Class) clazz);

    }

    /**
     * 获取分页数据
     * @param pageInfo 分页对象
     * @param clazz 需要查询的对象
     * @return 分页信息
     */
    public <E> PageInfo<E> findPageEntity(PageInfo<E> pageInfo, Class clazz) throws Exception {
        return findPageEntityBySql(null, pageInfo, clazz);
    }

    /**
     * 分页单表条件查询
     * @param object 需要查询的类
     * @return 获取数据
     */
    public <E> PageInfo<E> findPageEntityByCondition(Object object, PageInfo<E> pageInfo) throws Exception {
        Map<Integer, Object> indexValueBind = new HashMap<Integer, Object>(10);
        String sql = DaoHelper.getSqlByCondition(object, indexValueBind);
        return getPageData(sql, pageInfo, indexValueBind, object.getClass());
    }

    /**
     * 单表条件查询
     * @param object 需要查询的类
     * @return 获取数据
     */
    public <E> List<E> findEntityByCondition(Object object) throws Exception {
        Map<Integer, Object> indexValueBind = new HashMap<Integer, Object>(10);
        String sql = DaoHelper.getSqlByCondition(object, indexValueBind);
        return getData(sql, indexValueBind, object.getClass());
    }

    /**
     * 通过sql查询分页数据
     * @param sql sql
     * @param pageInfo 分页对象
     * @return 分页数据
     */
    public PageInfo<T> findPageEntityBySql(String sql, PageInfo<T> pageInfo) throws Exception {
        return findPageEntityBySql(sql, pageInfo, (Class) clazz);
    }
    /**
     * 通过sql查询分页数据
     * @param sql sql
     * @param pageInfo 分页对象
     * @param clazz 指定查询的对象
     * @return 分页数据
     */
    public <E> PageInfo<E> findPageEntityBySql(String sql, PageInfo<E> pageInfo, Class clazz) throws Exception {
        if (StringUtils.isEmpty(sql)) {
            sql = DaoHelper.getSql(clazz);
        }
        return getPageData(sql, pageInfo, null, clazz);
    }

    /**
     * 通过sql查询数据
     * @param sql sql
     * @return 查询的数据集合
     */
    public List<T> findEntityBySql(String sql) throws Exception {
        return findEntityBySql(sql, (Class) clazz);
    }

    /**
     * 通过sql查询数据
     * @param sql sql
     * @param clazz 需要查询的对象
     * @return 查询的数据集合
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
     * 设置分页参数
     * @param sql sql
     * @param pageInfo   分页对象
     * @param conn    conn
     * @param indexValueBind 参数
     * @throws SQLException 异常
     */
    private void setPageInfo(String sql, PageInfo pageInfo, Connection conn, Map<Integer, Object> indexValueBind) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String countSql = "select count(*) totalCount from (" + sql + ") countSql";
            ps = conn.prepareStatement(countSql);
            setDynamicParams(ps, indexValueBind);
            rs = ps.executeQuery();
            // 总记录数
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt("totalCount");
            }
            // 当前页数
            int pageSize = pageInfo.getPageSize();
            if (pageSize == 0) {
                return;
            }
            // 总页数
            int totalPage = totalCount % pageSize == 0 ? totalCount
                    / pageSize : totalCount / pageSize + 1;
            pageInfo.setTotal(totalCount);
            pageInfo.setPageTotal(totalPage);
            // 当前页大于总页数则跳转到第一页
            if (pageInfo.getPageIndex() > totalPage) {
                pageInfo.setPageIndex(1);
            }
        } finally {
            free(rs, ps);
        }


    }

    /**
     * 合并两个参数值,将后面的参数添加到前面去
     * @param indexValueBind 初始值
     * @param whereValue 需要添加的值
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
     * 设置参数
     * @param ps PreparedStatement
     * @param indexValueBind 参数
     * @throws SQLException sql异常
     */
    private void setDynamicParams(PreparedStatement ps, Map<Integer, Object> indexValueBind) throws SQLException {
        if (indexValueBind == null || indexValueBind.isEmpty()) {
            return;
        }
        Object val;
        for (int i = 1; i <= indexValueBind.size(); i++) {
            // 首先判断字段的类型, 这里需要对日期类型进行特殊的处理
            val = indexValueBind.get(i);
            // 判断是否是日期类型
            if (val instanceof java.util.Date) {
                java.util.Date time = (Date) val;
                String format = DateUtil.dateToStr(time, DateStyle.YYYY_MM_DD_HH_MM_SS);
                ps.setTimestamp(i, Timestamp.valueOf(format));
            } else if (null == val) {
                // 如果是空值
                ps.setNull(i, Types.VARCHAR);
            } else {
                ps.setObject(i, val);
            }
        }
    }



}

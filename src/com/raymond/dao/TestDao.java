package com.raymond.dao;

import com.raymond.db.BaseDao;
import com.raymond.entity.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试表Dao
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-16 17:24
 */
public class TestDao extends BaseDao<Test> {

    public static void main(String[] args) throws Exception {
        TestDao testDao = new TestDao();
        List<Test> tests = new ArrayList<Test>();
        Test test;
        for (int i = 0; i < 100000; i++) {
            test = new Test("");
            test.setName("as" + i);
            tests.add(test);
        }
        int save = testDao.batSave(tests);
        System.out.println(save);
        System.out.println(testDao.find().size());
    }
}

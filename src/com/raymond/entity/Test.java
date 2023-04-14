package com.raymond.entity;

import com.raymond.db.annotation.Columns;
import com.raymond.db.annotation.Table;

/**
 * 测试表
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-16 17:23
 */
@Table(name = "test")
public class Test {
    @Columns(name = "name")
    private String name;


    public Test() {
    }

    public Test(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

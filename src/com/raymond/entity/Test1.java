package com.raymond.entity;

import com.raymond.db.BaseDao;
import com.raymond.db.annotation.Columns;
import com.raymond.db.annotation.Id;
import com.raymond.db.annotation.Table;

/**
 * Test1表
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-17 11:04
 */
@Table(name = "test1")
public class Test1{
    @Id
    @Columns(name = "id")
    private long id;
    @Columns(name = "name")
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

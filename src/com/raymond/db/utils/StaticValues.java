package com.raymond.db.utils;

import com.raymond.utils.PropUtils;

/**
 * 静态数据
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-19 17:23
 */
public class StaticValues {
    public static final int				DB_TYPE								= Integer.parseInt(PropUtils.getProp("DB_TYPE", "3"));

    public static final int				ORACLE_DB_TYPE						= 1;

    public static final int				SQL_SERVER_DB_TYPE					= 2;

    public static final int				MYSQL_DB_TYPE						= 3;

    public static final int				DB2_DB_TYPE							= 4;
}

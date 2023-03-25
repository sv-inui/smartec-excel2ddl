package jp.co.softventure.smartec.excel2ddl;

import java.util.HashSet;
import java.util.Set;

public class Constants {

    /**
     * PostgreSQLのデータをシングルクォートで囲む必要のある型
     */
    public static final Set<String> COLUMNTYPE_NEEDSQ = new HashSet<String>(){{
        add("char");
        add("varchar");
        add("nchar");
        add("nvarchar");
        add("text");
        add("date");
        add("time");
        add("timestamp");
        add("character varying");
    }};

    /**
     * PostgreSQLのデータをシングルクォートで囲む必要のない文字列
     */
    public static final Set<String> NEEDSQ_EXPECT = new HashSet<String>(){{
        add("current_timestamp");
    }};
}

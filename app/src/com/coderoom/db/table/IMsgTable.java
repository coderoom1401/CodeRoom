package com.coderoom.db.table;

import android.database.sqlite.SQLiteDatabase;

import com.coderoom.common.GlobaleParms;

import z.db.BaseDBHelper;

// db:[id] [type] [title]  [status] [createTime]

public interface IMsgTable {

    /** 表名 */
    String TNAME = GlobaleParms.TABLE_MSG;
    /** 消息id */
    String MSG_ID = "id";
    /** 消息类型 */
    String MSG_TYPE = "msg_type";
    String MSG_TITLE = "title";

    /** 是否已读 默认y=未读，n=已读 */
    String MSG_STATUS = "status";

    String MSG_CREATE_TIME = "create_time";
    String MSG_ICON_URL = "icon_url";
    String MSG_URL = "url";//链接地址
    String MSG_HOME_WORK_ID = "home_work_id";//作业id


    //信息内容
    String MSG_CONTENT = "content";

    class Table implements BaseDBHelper.ILTable {
        @Override
        public void onCreate(SQLiteDatabase db) {
            StringBuffer sb = new StringBuffer(128);
            sb.append("create table ").append(TNAME).append('(');
            sb.append(MSG_ID).append(" text default \"\", ");
            sb.append(MSG_TYPE).append(" int default 0, ");
            sb.append(MSG_TITLE).append(" text default \"\", ");
            sb.append(MSG_STATUS).append(" int default 1, ");
            sb.append(MSG_CREATE_TIME).append(" int default 0, ");
            sb.append(MSG_CONTENT).append(" text default \"\", ");
            sb.append(MSG_HOME_WORK_ID).append(" text default \"\", ");
            sb.append(MSG_URL).append(" text default \"\", ");
            sb.append(MSG_ICON_URL).append(" text default \"\")");
            db.execSQL(sb.toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

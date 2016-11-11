package com.coderoom.db.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.coderoom.bean.MsgBean;
import com.coderoom.db.table.IMsgTable;
import com.talk51.afast.log.Logger;

import z.db.BaseDBHelper;
import z.db.DBQuery;
import z.db.DU;
import z.frame.JsonTree;

/**
 * 说明：消息的表的操作
 */
public class MsgDao implements IMsgTable {
    public static final String TAG = MsgDao.class.getSimpleName();
    public static final int MSG_STATUS_READ = 1;//信息已读
    public static final int MSG_STATUS_UNREAD = 0;//信息未读


    /**
     * 向数据库增加一条msg
     *
     * @param msg 消息的Bean
     */
    public static void addMsg(MsgBean msg) {

        if (msg == null) {
            return;
        }

        SQLiteDatabase db = BaseDBHelper.getWDatabase();
        ContentValues cv = new ContentValues();

        try {
            MsgBean old = queryMsg(msg.id);
            bean2Db(msg, cv);
            if (old != null) {
                db.update(TNAME, cv, MSG_ID + "=?", new String[]{msg.id});
            } else {
                db.insert(TNAME, null, cv);
            }
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        }
    }

    private static void bean2Db(MsgBean msg, ContentValues cv) {
        cv.put(MSG_ID, msg.id);
        cv.put(MSG_TYPE, msg.type);
        cv.put(MSG_TITLE, msg.title);
        cv.put(MSG_STATUS, msg.msgStatus);
        cv.put(MSG_CREATE_TIME, msg.createTime);
        cv.put(MSG_ICON_URL, msg.iconUrl);

//        MsgBean.Content content = new MsgBean.Content();
//        content.msgBean2Content(msg);
        cv.put(MSG_CONTENT, JsonTree.toJSONString(msg.content));

    }

    // 根据msgId查询
    public static MsgBean queryMsg(String msgId) {
        MsgBean msg = null;
        DBQuery dbq = new DBQuery();
        if (dbq.query(TNAME, null, MSG_ID + "=?", new String[]{msgId}, null, null, null, null)) {
            prepareDb2Bean(dbq);
            msg = db2Bean(dbq);
        }
        dbq.close();
        return msg;
    }


    private static void prepareDb2Bean(DBQuery dbq) {
        dbq.prepareCols(7).addCol(MSG_ID).addCol(MSG_TYPE).addCol(MSG_TITLE).
                addCol(MSG_STATUS).addCol(MSG_CREATE_TIME).addCol(MSG_ICON_URL).addCol(MSG_CONTENT);
    }

    private static MsgBean db2Bean(DBQuery dbq) {
        MsgBean mb = new MsgBean();
        mb.id = dbq.getString(0);
        mb.type = dbq.getInt(1);
        mb.title = dbq.getString(2);
        mb.msgStatus = dbq.getInt(3);
        mb.createTime = dbq.getLong(4);
        mb.iconUrl = dbq.getString(5);

        String tmp = dbq.getString(6);
        if (!TextUtils.isEmpty(tmp)) {
            MsgBean.Content content = JsonTree.getObject(tmp, MsgBean.Content.class);
//            content.content2MsgBean(mb);
            mb.content = content;
        }

        return mb;
    }

    /**
     * 向数据库增加多条msg
     */
    public static void addMsg(ArrayList<MsgBean> list) {

        if (list == null) {
            return;
        }

        for (MsgBean mb : list) {
            mb.msgStatus = 0;
            addMsg(mb);
        }
    }

    /**
     * 更新对应消息是否已读 msg_status字段 默认0=未读，1=已读
     */
    public static void updateIsRead(String msgID) {
        DU.Once.updateAttr(true, TNAME, MSG_ID, msgID, MSG_STATUS, MSG_STATUS_READ);
    }


    /**
     * 查询数据库所有消息
     */
    public static ArrayList<MsgBean> queryAllMsg() {
        ArrayList<MsgBean> list = new ArrayList<MsgBean>();
        DBQuery dbq = new DBQuery();
        if (dbq.query(TNAME, null, null, null, null, null, MSG_CREATE_TIME + " DESC",
                null)) {
            prepareDb2Bean(dbq);
            do {
                list.add(db2Bean(dbq));
            } while (dbq.next());
        }
        dbq.close();
        return list;
    }


    // 查询最后更新时间
    public static String queryLastTime() {
        String lastTime = "-1";
        DBQuery dbq = new DBQuery();
        if (dbq.query(TNAME, null, null, null, null, null, MSG_CREATE_TIME + " DESC", "0,1")) {
            lastTime = dbq.getString(MSG_CREATE_TIME);
        }
        dbq.close();
        return lastTime;
    }

    // 查询unread数量
    public static int queryUnreadCount() {
        int count = 0;
        DBQuery dbq = new DBQuery();
        if (dbq.query(TNAME, new String[]{"count(*)"}, MSG_STATUS + "=0", null, null, null, null, null)) {
            count = dbq.getInt("count(*)");
        }
        dbq.close();
        return count;
    }


}

package com.coderoom.bean;


import java.io.Serializable;

import android.text.TextUtils;

import com.coderoom.db.dao.IUser;

import z.db.ShareDB;

/**
 * 说明：用户信息的Bean
 * @author Duguang
 */
public class UserBean implements IUser {

    //用户ID
    public String uid;
    //用户登录标识
    public String token;
    //用户昵称
    public String nickName;
    //用户联系方式
    public String mobile;
    //用户真实姓名
    public String realName;
    //是否是付费元用户 1 付费 0 免费
    public int isBuy = -1;
    //性别
    public String sex;
    //头像
    public String avatar;
    // 本地登录信息
    public int login = 0;
    //个人中心支付item 显示内容
    public PayTagBean payTag;

    public static class PayTagBean implements Serializable {
        public String tag;
        public String title;
    }

    // 防止空id在其他模块出错
    public static String getId() {
        ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
        String uid = sec.getString(UID);
        return TextUtils.isEmpty(uid) ? "null" : uid;
    }

    public static String getToken() {
        ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
        return sec.getString(TOKEN);
    }

    public boolean isBuy() {
        //1 付费 0 免费
        return isBuy == 1;
    }

    public static int getSex() {
        //1 男 0 女
        ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
        String sex = sec.getString(SEX);
        return "man".equals(sex) ? 1 : 0;
    }

    public static String getAvatar() {
        ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
        String avatar = sec.getString(AVATAR);
        return avatar;
    }

    public void toSec(ShareDB.Sec s) {
        if (!TextUtils.isEmpty(uid)) s.put(UID, uid);
        if (nickName != null) s.put(NICKNAME, nickName);
        if (!TextUtils.isEmpty(token)) s.put(TOKEN, token);
        if (mobile != null) s.put(MOBILE, mobile);
        if (realName != null) s.put(REALNAME, realName);
        if (isBuy != -1) s.put(ISBUY, isBuy);
        if (sex != null) s.put(SEX, sex);
        if (avatar != null) s.put(AVATAR, avatar);
    }

    //兼容空指针
    private String com(String s) {
        return s == null ? "" : s;
    }

    public void fromSec(ShareDB.Sec s) {
        uid = s.getString(UID);
        token = s.getString(TOKEN);
        mobile = com(s.getString(MOBILE));
        nickName = com(s.getString(NICKNAME));
        realName = com(s.getString(REALNAME));
        isBuy = s.getInt(ISBUY);
        sex = com(s.getString(SEX));
        avatar = com(s.getString(AVATAR));
    }

}

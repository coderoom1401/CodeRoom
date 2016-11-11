package com.coderoom.db.dao;

import android.text.TextUtils;

import com.coderoom.bean.UserBean;

import z.db.ShareDB;

/**
 * 说明：本地对用户信息操作的类
 * @author Duguang
 */
public interface IUser {
    String SEC_USERBEAN = "userbean";
    String SEC_AUTOLOGIN = "userinfo";

    //用户id
    String UID = "uid";
    // 昵称
    String NICKNAME = "nickName";
    //用户登录标识
    String TOKEN = "token";
    //手机号
    String MOBILE = "mobile";
    //真实姓名
    String REALNAME = "realName";
    //是否付费
    String ISBUY = "isBuy";
    //年纪名
    String OLD = "old";
    //性别
    String SEX = "sex";
    //头像
    String AVATAR = "avatar";


    class Dao {
        private static UserBean mUser = null;

        // 获取用户
        public static UserBean getUser() {
            if (mUser == null) {
                mUser = new UserBean();
                ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
                mUser.fromSec(sec);
            }
            return mUser;
        }

        public static String getUserId() {
            return UserBean.getId();
        }

        public static String getToken() {
            return UserBean.getToken();
        }

        public static boolean isBuy() {
            return getUser().isBuy();
        }

        public static int getSex() {
            return UserBean.getSex();
        }

        public static String getAvatar() {
            return UserBean.getAvatar();
        }


        // 检查用户是否登录
        public static boolean checkAutoLogin() {
            ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
            return sec.getBoolean("autologin");
        }

        public static void clearUser() {
            ShareDB.Sec.clearSec(SEC_USERBEAN);
        }

        public static void saveUser() {
            if (mUser == null) return;
            saveUser(mUser);
        }

        // 保存用户
        public static void saveUser(UserBean user) {
            ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
            if (!TextUtils.isEmpty(user.uid)) {
                String oldId = sec.getString("id");
                if (oldId == null
                        || !oldId.equals(user.uid)) {
                    clearUser();
                    sec.clearAttrs();
                }
            } else {
                // 防止id为空
                user.uid = sec.getString(UID);
            }
            user.toSec(sec);
            sec.save(false);
            if (mUser != null && user != mUser) {
                mUser.fromSec(sec);
            }
        }

        public static void exitUser() {
            clearUser();
            if (mUser != null) {
                // 保留id值不清除
                ShareDB.Key.update(SEC_USERBEAN, UID, mUser.uid);
                mUser = null;
            }
        }

        // 更新用户头像
        public static void updateAvatar(String avatar) {
            UserBean user = getUser();
            if (user.avatar != null && user.avatar.equals(avatar)) return;
            user.avatar = avatar;
            ShareDB.Key.update(SEC_USERBEAN, AVATAR, avatar);
        }

        // 更新用户性别
        public static void updateSex(String sex) {
            UserBean user = getUser();
            if (user.sex != null && user.sex.equals(sex)) return;
            user.sex = sex;
            ShareDB.Key.update(SEC_USERBEAN, SEX, sex);
        }
        // 更新用户英文名
        public static void updateEngName(String name) {
            UserBean user = getUser();
            if (user.nickName != null && user.nickName.equals(name)) return;
            user.nickName = name;
            ShareDB.Key.update(SEC_USERBEAN, NICKNAME, name);
        }

        public static void updateLogin(int status) {
            UserBean user = getUser();
            user.login = status;
        }

        public static boolean isLogin() {
            ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
            return sec.getInt("login") > 0;
        }

    }
}

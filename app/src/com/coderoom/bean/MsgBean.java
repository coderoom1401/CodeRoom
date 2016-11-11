package com.coderoom.bean;

// db:[id] [type] [title] [iconUrl] [createTime][msgStatus][url][homeWorkId]
public class MsgBean {
//    id: "1",
//    type: "1", //消息类型：默认为1， 打开连接，其他类型则目前无操作
//    iconUrl；"http://duoshuo.51talk.com/storage/avatar/20160702/20160702220146_845797.jpg"
//    title: "test",
//    content: "http://www.baidu.com",
//    createTime: "0",
//    msgStatus: "1"
//    homeWorkId: "111"

    public String id;//消息id
    public int type;//
    public String title;
    public String iconUrl;
    public long createTime;//创建时间
    public int msgStatus = 1;//1已读 0 未读


    public String url;
    public Content content;

    public static class Content {
        public String url;
        public String homeWorkId;

    }


}

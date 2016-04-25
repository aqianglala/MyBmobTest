package cc.talkpal.mybmobtest1.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by admin on 2016/4/25.
 * 好友关系表
 */
public class Relation extends BmobObject{
    private String uid;
    private String puid;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }
}

package cc.talkpal.mybmobtest1.bean;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by admin on 2016/4/25.
 */
public class MyBmobInstallation extends BmobInstallation {

    /**
     * 用户id-这样可以将设备与用户之间进行绑定
     */
    private String uid;

    public MyBmobInstallation(Context context) {
        super(context);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
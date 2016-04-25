package cc.talkpal.mybmobtest1.global;

import android.app.Application;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * Created by admin on 2016/4/25.
 */
public class BaseApplication extends Application{

    public static String APPID = "71b6a3b36e15b329968fcde0055e14f2";

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, APPID);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this);
    }
}

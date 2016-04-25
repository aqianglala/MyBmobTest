package cc.talkpal.mybmobtest1.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cc.talkpal.mybmobtest1.R;
import cc.talkpal.mybmobtest1.bean.MyBmobInstallation;
import cc.talkpal.mybmobtest1.interfaces.Keys;
import cc.talkpal.mybmobtest1.utils.SPUtils;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText et_name;
    private EditText et_psw;
    private Button btn_register;
    private Button btn_login;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initView();
        setListener();
    }

    private void setListener() {
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    private void initView() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_psw = (EditText) findViewById(R.id.et_psw);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_login = (Button) findViewById(R.id.btn_login);
    }

    @Override
    public void onClick(View v) {
        // 获取用户名密码
        String name = et_name.getText().toString().trim();
        String psw = et_psw.getText().toString().trim();
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(psw)){
            showToast("用户名或密码为空");
            return;
        }
        final BmobUser bmobUser = new BmobUser();
        bmobUser.setUsername(name);
        bmobUser.setPassword(psw);

        switch (v.getId()){
            case R.id.btn_register:
                bmobUser.signUp(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        showToast("注册成功");
                        // 将数据保存到本地
                        SPUtils.put(context, Keys.USERNAME,bmobUser.getUsername());
                        goActivity(FriendListActivity.class);
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        showToast(s);
                        SPUtils.clear(context);
                    }
                });
                break;
            case R.id.btn_login:
                bmobUser.login(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        showToast("登录成功");
                        // 更新Installation表
                        updateInstallation(bmobUser);
                        SPUtils.put(context, Keys.USERNAME,bmobUser.getUsername());
                        goActivity(FriendListActivity.class);
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        showToast(s);
                        SPUtils.clear(context);
                    }
                });
                break;
        }
    }

    private void updateInstallation(final BmobUser bmobUser) {
        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation
                .getInstallationId(context));
        query.findObjects(context, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {
                // TODO Auto-generated method stub
                if(object.size() > 0){
                    MyBmobInstallation mbi = object.get(0);
                    mbi.setUid(bmobUser.getObjectId());
                    mbi.update(context,new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Log.i("bmob","设备信息更新成功");
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            Log.i("bmob","设备信息更新失败:"+msg);
                        }
                    });
                }else{
                }
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void goActivity(Class clazz){
        startActivity(new Intent(this,clazz));
    }
}

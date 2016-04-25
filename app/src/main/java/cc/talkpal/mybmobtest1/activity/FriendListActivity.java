package cc.talkpal.mybmobtest1.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cc.talkpal.mybmobtest1.R;
import cc.talkpal.mybmobtest1.adapter.FriendsListAdapter;
import cc.talkpal.mybmobtest1.bean.MyBmobInstallation;
import cc.talkpal.mybmobtest1.bean.Relation;
import cc.talkpal.mybmobtest1.interfaces.Keys;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class FriendListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView lv_friends;
    private Context context;
    private EditText et_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        context = this;
        initView();
        setListener();
        getData();
    }

    private void setListener() {
        lv_friends.setOnItemClickListener(this);
    }

    private void initView() {
        lv_friends = (ListView) findViewById(R.id.lv_friends);
    }

    public void getData() {
        BmobQuery<Relation> query = new BmobQuery<>("relation");
        query.addWhereEqualTo("objectId",BmobUser.getCurrentUser(this));
        query.findObjects(this, new FindListener<Relation>() {
            @Override
            public void onSuccess(List<Relation> list) {
                showToast("数据长度为"+list.size());
                FriendsListAdapter adapter = new FriendsListAdapter(context, list);
                lv_friends.setAdapter(adapter);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add){
            final EditText editText = new EditText(this);
            // 弹出对话框
            new AlertDialog.Builder(this)
                    .setTitle("添加好友")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView( new EditText(this))
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = editText.getText().toString().trim();
                            if(!TextUtils.isEmpty(name)){
                                // 从服务器中查询时候有此用户
                                BmobQuery<BmobUser> query = new BmobQuery<>("_User");
                                query.addWhereEqualTo(Keys.USERNAME,name);
                                query.findObjects(context, new FindListener<BmobUser>() {

                                    @Override
                                    public void onSuccess(final List<BmobUser> blist) {
                                       // 再查询好友列表中是否已经存在此好友了
                                        BmobQuery<Relation> relation = new BmobQuery<>("relation");
                                        relation.addWhereEqualTo("puid",blist.get(0).getObjectId());
                                        relation.findObjects(context, new FindListener<Relation>() {

                                            @Override
                                            public void onSuccess(List<Relation> list) {
                                                // list的长度为0，则向用户关系表中添加一条数据
                                                if(list.size() ==0){
                                                    Relation relation = new Relation();
                                                    relation.setUid(BmobUser.getCurrentUser(context).getObjectId());
                                                    relation.setPuid(list.get(0).getObjectId());
                                                    relation.setUsername(blist.get(0).getUsername());
                                                    relation.save(context, new SaveListener() {

                                                        @Override
                                                        public void onSuccess() {
                                                            showToast("添加成功");
                                                            // 更新好友列表

                                                        }

                                                        @Override
                                                        public void onFailure(int i, String s) {
                                                            showToast("添加失败");
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onError(int i, String s) {
                                                showToast("对方已是您的好友");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        showToast("该用户不存在");
                                    }
                                });
                            }
                        }
                    })
                    .setNegativeButton("取消", null).show();
        }
        return true;
    }

    public void  showToast(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Relation relation = (Relation) parent.getItemAtPosition(position);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_browse, null);
        et_path = (EditText) inflate.findViewById(R.id.et_path);
        Button btn_browse = (Button) inflate.findViewById(R.id.btn_browse);
        btn_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开系统文件浏览功能
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,0);
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("浏览")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(inflate)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String path = et_path.getText().toString().trim();
                        // 上传文件
                        final BmobFile bmobFile = new BmobFile(new File(path));
                        bmobFile.uploadblock(context, new UploadFileListener() {

                            @Override
                            public void onSuccess() {
                                //bmobFile.getFileUrl(context)--返回的上传文件的完整地址
                                showToast("上传文件成功:" + bmobFile.getFileUrl(context));
                                // 获取installationId
                                BmobQuery<MyBmobInstallation> query1 = new BmobQuery<>();
                                query1.addQueryKeys("installationId");
                                query1.addWhereEqualTo("uid",relation.getPuid());
                                query1.findObjects(context, new FindListener<MyBmobInstallation>() {
                                    @Override
                                    public void onSuccess(List<MyBmobInstallation> list) {
                                        // 将地址推送给该好友
                                        BmobPushManager bmobPush = new BmobPushManager(context);
                                        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
                                        query.addWhereEqualTo("installationId", list.get(0).getInstallationId());
                                        bmobPush.setQuery(query);
                                        bmobPush.pushMessage(bmobFile.getFileUrl(context));
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        showToast("找不到installationId");
                                    }
                                });

                            }

                            @Override
                            public void onProgress(Integer value) {
                                // 返回的上传进度（百分比）
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                showToast("上传文件失败：" + msg);
                            }
                        });
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode ==0 && resultCode ==RESULT_OK){
            Uri uri = data.getData();
            String path = uri.getPath();
            et_path.setText(path);
        }
    }
}

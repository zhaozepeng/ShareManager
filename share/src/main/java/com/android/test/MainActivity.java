package com.android.test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.libcore.Toast.T;
import com.android.sharemanager.IShareCallback;
import com.android.sharemanager.R;
import com.android.sharemanager.ShareManager;
import com.android.sharemanager.ShareModel;
import com.android.sharemanager.utils.CommonUtil;

/**
 * Description: #TODO
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2015-12-24
 */
public class MainActivity extends Activity implements View.OnClickListener{

    private Button btn_choose_pic;
    private TextView tv_pic_path;
    private Button btn_share;
    private ShareManager shareManager;
    private ShareModel shareModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_choose_pic = (Button) findViewById(R.id.btn_choose_pic);
        btn_choose_pic.setOnClickListener(this);
        tv_pic_path = (TextView) findViewById(R.id.tv_pic_path);
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(this);

        shareManager = new ShareManager(this, new IShareCallback() {
            @Override
            public void onShareCallback(boolean success) {
                T.getInstance().showShort("分享成功");
            }
        });

        shareModel = new ShareModel();
        shareModel.title = "测试应用分享";
        shareModel.content = "测试应用分享内容";
        shareModel.shareUrl = "https://www.baidu.com";
        shareModel.imageUrl = "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_choose_pic:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                if (android.os.Build.VERSION.SDK_INT >= 19) {
                    intent.setAction("android.intent.action.OPEN_DOCUMENT");
                } else {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                }
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "选择图片"), 0);
                break;
            case R.id.btn_share:
                if (TextUtils.isEmpty(shareModel.imagePath)){
                    T.getInstance().showLong("图片未选择");
                    return;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (shareManager.registerOnActivityCallback(requestCode, resultCode, data)) {
        }else if (requestCode==0 && resultCode==RESULT_OK){
            if (data != null && data.getData() != null) {
                // 根据返回的URI获取对应的SQLite信息
                Uri uri = data.getData();
                shareModel.imageUri = uri;
                shareModel.imagePath = CommonUtil.uriToPath(this, uri);
                tv_pic_path.setText(shareModel.imagePath);
            }
        }
    }
}

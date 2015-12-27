package com.android.test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.libcore.Toast.T;
import com.android.libcore.utils.CommonUtils;
import com.android.sharemanager.IShareCallback;
import com.android.sharemanager.R;
import com.android.sharemanager.ShareManager;
import com.android.sharemanager.ShareModel;

import java.util.ArrayList;

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
                if (success)
                    T.getInstance().showShort("分享成功");
                else
                    T.getInstance().showLong("分享失败");
            }
        });

        shareModel = new ShareModel();
        shareModel.type = 1;
        shareModel.title = "测试应用分享";
        shareModel.content = "测试应用分享内容";
        shareModel.shareUrl = "https://www.baidu.com";
        shareModel.imageUrl = new ArrayList<>();
        shareModel.imageUrl.add("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_choose_pic:
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "选择图片"), 0);
//                } else {
//                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.setType("image/*");
//                    startActivityForResult(intent, 0);
//                }
                Intent picture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(picture, 0);
                break;
            case R.id.btn_share:
                if (shareModel.imagePath==null || shareModel.imagePath.size()==0){
                    T.getInstance().showLong("图片未选择");
                    return;
                }
                shareManager.show(shareModel);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (shareManager.registerOnShareCallback(requestCode, resultCode, data)) {
        }else if (requestCode==0 && resultCode==RESULT_OK){
            if (data != null && data.getData() != null) {
                // 根据返回的URI获取对应的SQLite信息
                Uri uri = data.getData();
                shareModel.imagePath = new ArrayList<>();
                shareModel.imagePath.add(CommonUtils.uriToPath(uri));
                tv_pic_path.setText(shareModel.imagePath.get(0));
            }
        }
    }
}

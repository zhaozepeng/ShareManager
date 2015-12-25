package com.android.sharemanager.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.android.libcore.utils.FileUtils;
import com.android.libcore.utils.ImageUtils;
import com.android.sharemanager.IShare;
import com.android.sharemanager.IShareCallback;
import com.android.sharemanager.ShareModel;

import java.io.File;
import java.io.IOException;

/**
 * Description: 邮件消息等系统分享
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2015-12-21
 */
public class SystemShare implements IShare {
    public static final int REQUEST_SYSTEM_SHARE = 10100;

    Activity context;
    ShareModel model;
    IShareCallback callback;

    @Override
    public void doShare(ShareModel model, Context context, int type, IShareCallback callback) {
        this.context = (Activity) context;
        this.model = model;
        this.callback = callback;
        switch (type){
            case 0:
                email();
                break;
            case 1:
                sms();
                break;
        }
    }

    @Override
    public boolean doShareCallback(int requestCode, int resultCode, Intent data) {
        if (requestCode == SystemShare.REQUEST_SYSTEM_SHARE){
            if (callback != null){
                callback.onShareCallback(resultCode==Activity.RESULT_OK);
            }
            return true;
        }
        return false;
    }

    //使用附件的方式发送邮件
    private void email(){
        //type 1---图片分享
        if (model.type == 1) {
            Intent email = new Intent(android.content.Intent.ACTION_SEND);
            //邮件发送类型：带附件的邮件
            email.setType("application/octet-stream");
            //设置邮件标题
            email.putExtra(android.content.Intent.EXTRA_SUBJECT, model.title);
            //设置发送的内容
            email.putExtra(android.content.Intent.EXTRA_TEXT, model.content + "\n" + model.shareUrl);
            //图片附件
            if (model.imagePath != null) {
                File image = new File(model.imagePath.get(0));
                if (!ImageUtils.isFileImage(model.imagePath.get(0))) {
                    //如果分享出去的文件不是图片，则要变更文件格式为png，要不然在分享处不会显示图片
                    try {
                        File newFile = new File(FileUtils.getExternalStorageImagePath() + "share.png");
                        //如果原来文件存在，先删除，再创建
                        if (newFile.exists())
                            newFile.delete();
                        newFile.createNewFile();
                        //拷贝到目标文件中
                        FileUtils.copyFile(image, newFile);
                        email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(newFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
                }
            }
            //调用系统的邮件系统
            context.startActivityForResult(Intent.createChooser(email, "请选择分享客户端"), REQUEST_SYSTEM_SHARE);
        }
    }

    //带图片的系统短信分享
    private void sms(){
        if (model.type == 1) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            // 图片分享
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_SUBJECT, model.title);
            intent.putExtra(Intent.EXTRA_TEXT, model.content + "\n" + model.shareUrl);
            intent.putExtra("sms_body", model.content + "\n" + model.shareUrl);
            //图片附件
            if (model.imagePath != null) {
                File image = new File(model.imagePath.get(0));
                if (!ImageUtils.isFileImage(model.imagePath.get(0))) {
                    //如果分享出去的文件不是图片，则要变更文件格式为png，要不然在分享处不会显示图片
                    try {
                        File newFile = new File(FileUtils.getExternalStorageImagePath() + "share.png");
                        //如果原来文件存在，先删除，再创建
                        if (newFile.exists())
                            newFile.delete();
                        newFile.createNewFile();
                        //拷贝到目标文件中
                        FileUtils.copyFile(image, newFile);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(newFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
                }
            }
            context.startActivityForResult(Intent.createChooser(intent, "请选择分享客户端"), REQUEST_SYSTEM_SHARE);
        }
    }
}

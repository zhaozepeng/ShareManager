package com.android.sharemanager.other;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.android.sharemanager.IShare;
import com.android.sharemanager.IShareCallback;
import com.android.sharemanager.ShareModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Description: 邮件消息等系统分享
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2015-12-21
 */
public class SystemShare implements IShare {
    Context context;
    ShareModel model;
    IShareCallback callback;

    @Override
    public void doShare(ShareModel model, Context context, int type, IShareCallback callback) {
        this.context = context;
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

    //使用附件的方式发送邮件
    private void email(){
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        //邮件发送类型：带附件的邮件
        email.setType("application/octet-stream");

        //设置邮件标题
        email.putExtra(android.content.Intent.EXTRA_SUBJECT, model.title);
        //设置发送的内容
        email.putExtra(android.content.Intent.EXTRA_TEXT, model.content+"\n"+model.shareUrl);
        //图片附件
        String path = model.imagePath;
        File file = new File(path);
        path = path.substring(0, path.lastIndexOf("/"));
        File pic = new File(path + "share.png");
        if (!pic.exists()){
            try {
                int bytesum = 0;
                int byteread = 0;
                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = new FileOutputStream(pic);
                byte[] buffer = new byte[1024];
                int length;
                while ( (byteread = inputStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    outputStream.write(buffer, 0, byteread);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pic));
        //调用系统的邮件系统
        context.startActivity(Intent.createChooser(email, "请选择分享客户端"));
    }

    //带图片的系统短信分享
    private void sms(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        // 图片分享
        intent.setType("image/png");
        //图片附件
        String path = model.imagePath;
        File file = new File(path);
        path = path.substring(0, path.lastIndexOf("/"));
        File pic = new File(path + "share.png");
        if (!pic.exists()){
            try {
                int bytesum = 0;
                int byteread = 0;
                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = new FileOutputStream(pic);
                byte[] buffer = new byte[1024];
                int length;
                while ( (byteread = inputStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    outputStream.write(buffer, 0, byteread);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pic));
        intent.putExtra(Intent.EXTRA_SUBJECT, model.title);
        intent.putExtra(Intent.EXTRA_TEXT, model.content+"\n"+model.shareUrl);
        intent.putExtra("sms_body", model.content+"\n"+model.shareUrl);
        context.startActivity(Intent.createChooser(intent, "请选择分享客户端"));
    }
}

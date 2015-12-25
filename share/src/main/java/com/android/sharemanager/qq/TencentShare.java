package com.android.sharemanager.qq;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.android.libcore.log.L;
import com.android.sharemanager.IShare;
import com.android.sharemanager.IShareCallback;
import com.android.sharemanager.ShareModel;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * Description: QQ分享
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2015-12-21
 */
public class TencentShare implements IShare {
    private ShareModel model;
    private Activity context;
    private IShareCallback callback;
    private Tencent mTencent;
    private IUiListener shareListener;

    @Override
    public void doShare(ShareModel model, Context context, int type, IShareCallback callback) {
        this.model = model;
        this.context = (Activity) context;
        this.callback = callback;
        this.mTencent = Tencent.createInstance("ddddd", context.getApplicationContext());
        //0---QQ好友  1---QQ空间
        switch (type){
            case 0:
                QQShare();
                break;
            case 1:
                QQZone();
                break;
        }
    }

    private void QQShare(){
        final Bundle params = new Bundle();
        //这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, model.shareUrl);
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
        params.putString(QQShare.SHARE_TO_QQ_TITLE, model.title);
        //分享的图片本地URL
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, model.imagePath);
//        //分享图片的外部URL
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, model.imageUrl);
        //分享的消息摘要，最长50个字
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, model.content);
        //手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
//        params.putString(QQShare.PARAM_APPNAME, "??我在测试");
        //标识该消息的来源应用，值为应用名称+AppId。
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "分享测试");

        mTencent.shareToQQ(context, params, generateShareListener());
    }

    private void QQZone(){
        final Bundle params = new Bundle();
        //这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, model.shareUrl);
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
        params.putString(QQShare.SHARE_TO_QQ_TITLE, model.title);
        //分享的图片URL
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, model.imagePath);
//        //分享图片的外部URL
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, model.imageUrl);
        //分享的消息摘要，最长50个字
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, model.content);
        //标识该消息的来源应用，值为应用名称
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "分享测试");

        mTencent.shareToQzone(context, params, generateShareListener());
    }

    private IUiListener generateShareListener(){
        if (shareListener == null) {
            shareListener = new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    L.i("qq share success", o.toString());
                    if (callback != null)
                        callback.onShareCallback(true);
                }

                @Override
                public void onError(UiError uiError) {
                    L.i("qq share fail:code" + uiError.errorCode + "\tmessage:"
                            + uiError.errorMessage + "\tdetail:" + uiError.errorDetail);
                    if (callback != null)
                        callback.onShareCallback(false);
                }

                @Override
                public void onCancel() {
                    L.i("qq share cancel");
                    if (callback != null)
                        callback.onShareCallback(false);
                }
            };
        }
        return shareListener;
    }
}

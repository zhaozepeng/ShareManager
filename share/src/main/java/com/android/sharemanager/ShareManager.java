package com.android.sharemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.PopupWindow;

import com.android.libcore.log.L;
import com.android.sharemanager.system.SystemShare;
import com.android.sharemanager.qq.TencentShare;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * Description: #TODO
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2015-12-18
 */
public class ShareManager {
    private Activity activity;
    private ShareModel model;
    private IShareCallback callback;
    private PopupWindow popupWindow;
    //QQ分享回调
    private IUiListener shareListener;

    /**
     * @param callback 分享出去的回调
     */
    public ShareManager(Activity activity, IShareCallback callback){
        this.activity = activity;
        this.callback = callback;
        //初始化Share枚举类
        Share demo = Share.QQ_FRIEND;
    }

    /**
     * 用来展示分享popUpWindow
     * @param model 分享出去的实体
     */
    public void show(final ShareModel model) {
        this.model = model;
        if (popupWindow == null) {
            popupWindow = new SharePopupWindow(activity, new SharePopupWindow.IShareClickCallback() {
                @Override
                public void onShareCallback(int position) {
                    Share.values()[position].doShare(model, activity, Share.values()[position].getType(), callback);
                }
            });
        }
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 用来在activity的onActivityResult函数中注册分享回调
     * @return 是否是分享回调，如果是，返回true，表明activity不用处理相关result
     */
    public boolean registerOnActivityCallback(int requestCode, int resultCode, Intent data){
        //QQ分享回调处理
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            if (callback != null) {
                generateShareListener();
                Tencent.onActivityResultData(requestCode, resultCode, data, shareListener);
            }
            return true;
        }
        //系统分享回调处理
        else if (requestCode == SystemShare.REQUEST_SYSTEM_SHARE){
            if (callback != null){
                callback.onShareCallback(resultCode==Activity.RESULT_OK);
            }
            return true;
        }
        return false;
    }

    private void generateShareListener(){
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
    }

    /**
     * 在此处动态添加分享模块
     */
    public enum Share {
        QQ_FRIEND("QQ", R.mipmap.logo_qq, 0, TencentShare.class),
        QQ_ZONE("QQ空间", R.mipmap.logo_qzone, 1, TencentShare.class),

        MAIL("邮件", R.mipmap.logo_email, 0, SystemShare.class),
        MESSAGE("信息", R.mipmap.logo_shortmessage, 1, SystemShare.class);

        private String name;
        private int drawable;
        private Class shareClass;
        private int type;

        Share(String name, int drawable, int type, Class<? extends IShare> shareClass){
            this.name = name;
            this.drawable = drawable;
            this.type = type;
            this.shareClass = shareClass;
        }

        public String getName(){
            return name;
        }
        public int getDrawableId(){
            return drawable;
        }

        public int getType(){
            return type;
        }

        public void doShare(ShareModel model, Context context, int type, IShareCallback callback){
            try {
                ((IShare)shareClass.newInstance()).doShare(model, context, type, callback);
            } catch (Exception e) {
                L.e("ShareManager error", e);
            }
        }
    }
}

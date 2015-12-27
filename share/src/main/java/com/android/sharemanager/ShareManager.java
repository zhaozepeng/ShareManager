package com.android.sharemanager;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.widget.PopupWindow;

import com.android.sharemanager.system.SystemShare;
import com.android.sharemanager.qq.TencentShare;

/**
 * Description: #TODO
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2015-12-18
 */
public class ShareManager {
    private Activity activity;
    private IShareCallback callback;
    private PopupWindow popupWindow;
    /** 最新一次调用分享的对象 */
    private IShare shareObject;

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
        if (popupWindow == null) {
            popupWindow = new ShareGridViewPopupWindow(activity, new ShareGridViewPopupWindow.IShareClickCallback() {
                @Override
                public void onShareCallback(int position) {
                    try {
                        Class<? extends IShare> clazz = Share.values()[position].getShareClass();
                        shareObject = clazz.newInstance();
                        shareObject.doShare(model, activity, Share.values()[position].getType(), callback);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popupWindow = null;
                }
            });
        }
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 用来在制定的函数（比如onactivityResult）中注册分享回调
     * @return 是否是分享回调，如果是，返回true，表明activity不用处理相关result
     */
    public boolean registerOnShareCallback(int requestCode, int resultCode, Intent data){
            return shareObject!=null && shareObject.doShareCallback(requestCode, resultCode, data);
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
        private Class<? extends IShare> shareClass;
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

        public Class<? extends IShare> getShareClass() {
            return shareClass;
        }
    }
}

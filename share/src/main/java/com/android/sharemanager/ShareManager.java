package com.android.sharemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.PopupWindow;

import com.android.sharemanager.other.SystemShare;
import com.android.sharemanager.qq.TencentShare;

import java.io.File;

/**
 * Description: #TODO
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2015-12-18
 */
public class ShareManager {
    private int type;
    private String param;
    private Activity activity;
    private IShareCallback callback;
    private PopupWindow popupWindow;
    private File image;

    /** 分享店铺类型 */
    public static final int SHARE_SHOP_TYPE = 0;
    /** 分享产品类型 */
    public static final int SHARE_PRODUCT_TYPE = 1;
    /** 分享采购订单 */
    public static final int SHARE_PURCHASES = 10;
    /** 平台号子供应商商城分享 */
    public static final int SHARE_PLATFORM_CHILD_MARKET = 14;
    /**招商需求详情分享 param：招商需求ID*/
    public static final int SHARE_DEMAND_DETAIL = 13;
    /**分享同系列 param：同系列ID*/
    public static final int SHARE_SAME_SERIES = 15;

    /**
     * @param param 分享所需的参数
     * @param type 分享类型 {@linkplain #SHARE_SHOP_TYPE}{@linkplain #SHARE_PRODUCT_TYPE}
     * {@linkplain #SHARE_PURCHASES} {@linkplain #SHARE_PLATFORM_CHILD_MARKET}
     * {@linkplain #SHARE_DEMAND_DETAIL} {@linkplain #SHARE_SAME_SERIES}　
     * @param callback 分享之后的回调，可以为null
     */
    public ShareManager(Activity activity, String param, int type, IShareCallback callback){
        this.param = param;
        this.activity = activity;
        this.type = type;
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
            popupWindow = new SharePopupWindow(activity, new SharePopupWindow.IShareClickCallback() {
                @Override
                public void onShareCallback(int position) {
                    Share.values()[position].doShare(model, activity, Share.values()[position].getType(), callback);
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
     * 用来在activity中注册分享回调，请在activity中的onActivityResult函数中调用
     * @return 是否是分享回调，如果是，则返回true，activity不用处理相关result
     */
    public boolean registerOnActivityCallback(int requestCode, int resultCode, Intent data){
        return false;
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
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

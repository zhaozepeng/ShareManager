package com.android.sharemanager;

import android.app.Activity;
import android.content.Context;
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
        Share demo = Share.QQ_FRIEND;
    }

    public void show() {
//        Map<String, Object> params = new HashMap<>();
//        params.put("param", param);
//        params.put("tiket", AppContext.getInstance().getToken());
//        params.put("type", type);
//        LoadingDialog dialog = new LoadingDialog(activity);
//        dialog.setLoadText(activity.getResources().getString(R.string.txt_getting_share_info));
//
//        new AQuery(activity).progress(dialog).ajax(URLConfig.URL_ISHARE_MSG, params, String.class, new AjaxCallback<String>() {
//
//            @Override
//            public void callback(String url, String object, AjaxStatus status) {
//                CallResponse response = ResponseHelper.handleResponse(url, object, status);
//                if (response != null && response.getStatus() == 1) {
//                    final ShareModel model = JSON.parseObject(response.getResult(), ShareModel.class);
//                    // 将网络图片下载到本地
//                    image = AQUtility.getCacheFile(AQUtility.getCacheDir(activity), model.imageUrl);
//                    // 下面是处理下载后的操作
//                    if (null != image && !image.exists()) { // 不存在图片
//                        new AQuery(activity).id(new ImageView(activity)).image(model.imageUrl, false, true, 0, R.drawable.ic_launcher, new BitmapAjaxCallback() {
//                            protected void callback(String url, android.widget.ImageView iv, android.graphics.Bitmap bm, AjaxStatus status) {
//                                image = AQUtility.getCacheFile(AQUtility.getCacheDir(activity), model.imageUrl);
//                                try {
//                                    model.imageUri = Uri.fromFile(image);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                showShareWindow(model);
//                            }
//                        });
//                    } else { // 存在图片
//                        try {
//                            model.imageUri = Uri.fromFile(image);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        showShareWindow(model);
//                    }
//                } else {
//                    T.showShort(activity, R.string.share_get_info_from_fail);
//                }
//            }
//        });
    }

    private void showShareWindow(final ShareModel model){
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

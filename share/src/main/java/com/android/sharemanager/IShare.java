package com.android.sharemanager;

import android.content.Context;
import android.content.Intent;

/**
 * Description: 所有分享需要继承的接口
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2015-12-18
 */
public interface IShare {
    /**
     * @param model 调用分享的实体
     * @param context 上下文
     * @param type　调用该分享的类别
     * @param callback　分享回调
     */
    void doShare(ShareModel model, Context context, int type, IShareCallback callback);

    /**
     * 该函数在{@link android.app.Activity#onActivityResult(int, int, Intent)}
     * 调用，用来处理分享的回调，比如QQ和系统分享（微信这种不需要在onActivityResult中处理的直接返回false即可）
     * @return 是否为该分享的回调
      */
    boolean doShareCallback(int requestCode, int resultCode, Intent data);
}

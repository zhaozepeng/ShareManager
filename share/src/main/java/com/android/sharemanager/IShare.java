package com.android.sharemanager;

import android.content.Context;

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
}

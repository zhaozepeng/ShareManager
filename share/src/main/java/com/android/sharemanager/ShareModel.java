package com.android.sharemanager;

import android.net.Uri;

/**
 * Description: 分享的基本实体
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2015-12-18
 */
public class ShareModel {
    /** 分享内容 */
    public String content;
    /** 分享图片的网络url */
    public String imageUrl;
    /** 分享图片的本地uri */
    public Uri imageUri;
    /** 分享图片的本地path路径 */
    public String imagePath;
    /** 分享出去之后的链接 */
    public String shareUrl;
    /** 分享的标题 */
    public String title;
}

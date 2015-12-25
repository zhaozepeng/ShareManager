package com.android.sharemanager;


import java.util.ArrayList;

/**
 * Description: 分享的基本实体
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2015-12-18
 */
public class ShareModel {
    /** 分享的类型，图片，文字等 */
    public int type;
    /** 分享的标题 */
    public String title;
    /** 分享内容 */
    public String content;
    /** 分享的链接 */
    public String shareUrl;
    /** 分享图片的网络url */
    public ArrayList<String> imageUrl;
    /** 分享图片的本地path路径 */
    public ArrayList<String> imagePath;
}

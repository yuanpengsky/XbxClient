package com.xbx.client.utils;

import android.os.Environment;

/**
 * Created by EricYuan on 2016/3/30.
 * 常量定义
 */
public class Constant {
    public static final String SPLOC_NAME = "location_spname";
    public static final String SPUSER_PHONE = "user_phone";
    public static final String SPUSER_INFO = "user_info";
    public static final String SPPHONE_ID = "phone_id";
    public static final String SPUSER_LATLNG = "user_latlng";
    public static final int outsetFlag = 100;
    public static final int destFlag = 101;
    public static final String ACTION_GCANCELORD = "com.xbx.client.guide.cancel.order";
    /**
     * 存储根目录
     */
    public static final String APP_ROOT_PATH = Environment.getExternalStorageDirectory().toString();
    /**
     * 图片缓存路径
     */
    public static final String PICTURE_ALBUM_PATH = APP_ROOT_PATH + "/XbxTravel/";
}

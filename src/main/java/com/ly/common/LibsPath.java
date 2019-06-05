package com.ly.common;

import java.io.File;

/**
 * Created by luoyoujun on 2019/5/30.
 */
public class LibsPath {

    public static String getMiniCapBinPath() {
        return System.getProperty("user.dir") + File.separator +  "minicap-libs" + File.separator + "bin";
    }

    public static String getMiniCapSoPath() {
        return System.getProperty("user.dir") + File.separator +  "minicap-libs";
    }

    public static String getMiniTouchPath() {
        return System.getProperty("user.dir") + File.separator  + "minitouch-libs";
    }
}

package com.applex.verifier.modules.utilities.CommonUtils;

import android.Manifest;

public class Constants {

    public static final String PREF_NAME = "com.applex.authenticationreimagined.users";
    public static final String IS_FIRST_TIME_LAUNCH = "firstTime";
    public static final String IS_FIRST_TIME = "firsttime";
    public static final String PROPERTY_CURRENT_USER = "property_user";
    public static final String SHARECODE = "sharecode";

    public static final String[] READ_WRITE_CAMERA_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
}

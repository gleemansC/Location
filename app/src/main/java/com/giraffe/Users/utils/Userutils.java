package com.giraffe.Users.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import okhttp3.MediaType;


/**
 * Created by Mr.Giraffe on 2018/6/1.
 */

public class Userutils {

    public static void startActvity(Context context, Class targrtClass) {
        Intent intent = new Intent(context, targrtClass);
        context.startActivity(intent);
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static String userid = "";


    public static void setUserid(String userid) {
        Userutils.userid = userid;
    }

    public static String getUserid() {
        return userid;
    }
}

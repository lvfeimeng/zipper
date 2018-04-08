package com.zipper.wallet.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/27.
 */

public class RuntHandleFile {

    /**
     * 获取文件夹里的文件列表
     * @param context
     * @return
     */
    public static String[] getAssets(Context context, String path){
        String[] list = null;
        try {
            list = context.getAssets().list(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  list;
    }


    public static InputStream getAssetsInputStream(Context context,String path){
        InputStream stream = context.getClass().getResourceAsStream(path);
        return stream;

    }


    /**
     * 获取指定文件的输入流
     * @param context
     * @param filePath
     * @return
     */
    public InputStream getInputStream(Context context, String filePath){
        InputStream is = null;
        try {
            is = context.getAssets().open(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  is;
    }

    /**
     * 输出流转换成字符串
     * @param is
     * @return
     */
    public String getStr(InputStream is){
        String str = "";

        if (is != null) {
            try {
                int size = 0;
                size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                str = new String(buffer, "UTF-8");
                Log.i("getStr", String.format("is:%s,buffer:%s,str:%s",is,buffer,str));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  str;
    }

    /** 从assets 文件夹中读取图片 */
    public static Drawable loadImageFromAsserts(final Context ctx, String fileName) {
        try {
            InputStream is = ctx.getResources().getAssets().open(fileName);
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (OutOfMemoryError e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** 从assets 文件夹中读取图片 */
    public static List<Drawable> loadImageFromAsserts(final Context ctx, String[] files) {
        List<Drawable> list = new LinkedList<Drawable>();
        for(String fileName:files){
            list.add(loadImageFromAsserts(ctx,fileName));
        }
        return list;
    }
}

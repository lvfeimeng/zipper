package com.zipper.wallet.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/9.
 */

public class SqliteUtils {
    public static final String DB = "ZipperOne.db";
     static SQLiteDatabase sqlDB;
    static Context mContext;

     public static void openDataBase(Context context){
         mContext = context;
     }


    public static void test(){
        String sql = "select * from sqlite_master";
        sqlDB = mContext.openOrCreateDatabase(DB,Context.MODE_PRIVATE,null);
        Cursor cursor = sqlDB.rawQuery(sql, null);

        //打印表的所有列名
        MyLog.i("SqliteUtils", Arrays.toString(cursor.getColumnNames()));

        //打印当前数据库中的所有表
        if (cursor.moveToFirst()) {
            do {
                String str = "";

                for (String item : cursor.getColumnNames()) {
                    str += item + ": " + cursor.getString(cursor.getColumnIndex(item)) + "\n";
                }

                MyLog.i("SqliteUtils", str);

            } while (cursor.moveToNext());
        }
        sqlDB.close();
    }


    public static  void insert(String table , ContentValues cValue){
        sqlDB = mContext.openOrCreateDatabase(DB,Context.MODE_PRIVATE,null);
        sqlDB.insert(table,null,cValue);
        sqlDB.close();
    }


    public static  void delete(String table,String whereClause,String[] whereArgs){
        sqlDB = mContext.openOrCreateDatabase(DB,Context.MODE_PRIVATE,null);
        //删除条件
        //String whereClause = "id=?";
        //删除条件参数
        //String[] whereArgs = {String.valueOf(2)};
        //执行删除
        sqlDB.delete(table,whereClause,whereArgs);
        sqlDB.close();
    }

    /**
     *
     * @param table
     * @param cValue
     * @param whereClause   条件的列名 "id=?"
     * @param whereArgs 条件各个值，列入Id=1，Id=2
     */
    public  static void update(String table ,ContentValues cValue,String whereClause,String[] whereArgs){
        sqlDB = mContext.openOrCreateDatabase(DB,Context.MODE_PRIVATE,null);
        //删除条件
        //String whereClause = "id=?";
        //删除条件参数
        //String[] whereArgs = {String.valueOf(2)};
        //执行删除
        sqlDB.update(table,cValue,whereClause,whereArgs);
        sqlDB.close();
    }


    public static List<Map> selecte(String table){
        sqlDB = mContext.openOrCreateDatabase(DB,Context.MODE_PRIVATE,null);
        List<Map> list = new ArrayList<>();
        Cursor cursor = sqlDB.query(table,null,null,null,null,null,null);
        //判断游标是否为空
        if(cursor.moveToFirst()) {
            //遍历游标
            for(int i=0;i<cursor.getCount();i++){
                cursor.move(i);
                Map map = new HashMap();
                for(int p = 0 ; p < cursor.getColumnCount() ; p ++){
                    try {
                        map.put(cursor.getColumnName(p), cursor.getString(p));
                        MyLog.i("SqliteUtils", cursor.getColumnName(p) + ":" + cursor.getString(p));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                list.add(map);
            }
        }
        sqlDB.close();
        return list;
    }


    public static void execSQL(String sql){
        sqlDB = mContext.openOrCreateDatabase(DB,Context.MODE_PRIVATE,null);
        sqlDB.execSQL(sql);
        sqlDB.close();
    }

}

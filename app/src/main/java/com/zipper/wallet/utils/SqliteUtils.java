package com.zipper.wallet.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

     public static SQLiteDatabase openDataBase(Context context){
         sqlDB = context.openOrCreateDatabase(DB,Context.MODE_PRIVATE,null);
         return sqlDB;
     }


    public static void test(){
        String sql = "select * from sqlite_master";
        Cursor cursor = sqlDB.rawQuery(sql, null);

        //打印表的所有列名
        Log.i("SqliteUtils", Arrays.toString(cursor.getColumnNames()));

        //打印当前数据库中的所有表
        if (cursor.moveToFirst()) {
            do {
                String str = "";

                for (String item : cursor.getColumnNames()) {
                    str += item + ": " + cursor.getString(cursor.getColumnIndex(item)) + "\n";
                }

                Log.i("SqliteUtils", str);

            } while (cursor.moveToNext());
        }
    }


    public static  void insert(String table , ContentValues cValue){
        sqlDB.insert(table,null,cValue);

    }


    public static  void delete(String table,String whereClause,String[] whereArgs){
        //删除条件
        //String whereClause = "id=?";
        //删除条件参数
        //String[] whereArgs = {String.valueOf(2)};
        //执行删除
        sqlDB.delete(table,whereClause,whereArgs);
    }

    /**
     *
     * @param table
     * @param cValue
     * @param whereClause   条件的列名 "id=?"
     * @param whereArgs 条件各个值，列入Id=1，Id=2
     */
    public  static void update(String table ,ContentValues cValue,String whereClause,String[] whereArgs){
        //删除条件
        //String whereClause = "id=?";
        //删除条件参数
        //String[] whereArgs = {String.valueOf(2)};
        //执行删除
        sqlDB.update(table,cValue,whereClause,whereArgs);
    }


    public static List<Map> selecte(String table){
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
                        Log.i("SqliteUtils", cursor.getColumnName(p) + ":" + cursor.getString(p));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                list.add(map);
            }
        }
        return list;
    }


    public void execSql(String sql){
        sqlDB.execSQL(sql);
    }

}

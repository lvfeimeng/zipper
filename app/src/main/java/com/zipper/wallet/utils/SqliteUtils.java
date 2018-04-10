package com.zipper.wallet.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by Administrator on 2018/4/9.
 */

public class SqliteUtils {
     static SQLiteDatabase sqlDB;

    public static void openDataBase(Context context){
        if(sqlDB == null)
            sqlDB = context.openOrCreateDatabase("test.db",Context.MODE_PRIVATE,null);

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


    public void insert(String table , ContentValues cValue){
        sqlDB.insert(table,null,cValue);

    }


    public void delete(String table,String whereClause,String[] whereArgs){
        //删除条件
        //String whereClause = "id=?";
        //删除条件参数
        //String[] whereArgs = {String.valueOf(2)};
        //执行删除
        sqlDB.delete(table,whereClause,whereArgs);
    }

    public void update(String table ,ContentValues cValue,String whereClause,String[] whereArgs){
        //删除条件
        //String whereClause = "id=?";
        //删除条件参数
        //String[] whereArgs = {String.valueOf(2)};
        //执行删除
        sqlDB.update(table,cValue,whereClause,whereArgs);
    }

    public void update(){}

    public void selecte(){}


    public void execSql(String sql){
        sqlDB.execSQL(sql);
    }

}

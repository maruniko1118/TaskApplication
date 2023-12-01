package com.maru2525.taskapplication.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context, dbName: String, dbVersion: Int, dtName: String) : SQLiteOpenHelper(
  context,
  dbName,
  null,
  dbVersion
) {
  private val table = dtName
  // キー
  companion object {
    const val Id = "_id"
    const val Title = "title"
    const val Date = "date"
    const val Time = "time"
  }

  // DB作成処理
  override fun onCreate(db: SQLiteDatabase) {
    try{
      val sql = "CREATE TABLE $table ( $Id INTEGER PRIMARY KEY,$Title TEXT,$Date TEXT,$Time TEXT );"
      db.execSQL(sql)
    } catch (ex: java.lang.Exception){
      Log.e("YrcInfoDataDbHelper", ex.toString())
    }
  }

  // DBアップデート処理
  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    // アップデートの判断、旧バージョン: 削除、新バージョン: 作成
    if (db != null) {
      db.execSQL( "DROP TABLE IF EXISTS $table" )
      onCreate(db)
    }
  }
}
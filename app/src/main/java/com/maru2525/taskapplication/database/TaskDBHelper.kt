package com.maru2525.taskapplication.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context, dbName: String, dbVersion: Int) : SQLiteOpenHelper(
  context,
  dbName,
  null,
  dbVersion
) {
  private val table1 = "Task"
  private val table2 = "Archive"
  // キー
  companion object {
    const val Id = "_id"
    const val Title = "title"
    const val Date = "date"
    const val Time = "time"
    const val Details = "details"
  }

  // DB作成処理
  override fun onCreate(db: SQLiteDatabase) {
    db.execSQL("CREATE TABLE $table1 ( $Id INTEGER PRIMARY KEY,$Title TEXT,$Date TEXT,$Time TEXT, $Details TEXT );")
    db.execSQL("CREATE TABLE $table2 ( $Id INTEGER PRIMARY KEY,$Title TEXT,$Date TEXT,$Time TEXT, $Details TEXT );")
  }

  // DBアップデート処理
  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    // アップデートの判断、旧バージョン: 削除、新バージョン: 作成
    if (db != null) {
      db.execSQL( "DROP TABLE IF EXISTS $table1" )
      db.execSQL( "DROP TABLE IF EXISTS $table2" )
      onCreate(db)
    }
  }
}
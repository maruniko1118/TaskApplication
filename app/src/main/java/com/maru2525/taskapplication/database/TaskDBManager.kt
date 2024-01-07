package com.maru2525.taskapplication.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.util.Log
import com.maru2525.taskapplication.ui.Task

class DatabaseManager(context: Context, private val dtName: String) {
  private val dbName = "Task.db"

  // 生成時のDBバージョン番号
  private val dbVersion = 1

  private val _helper = DatabaseHelper(context, dbName, dbVersion)

  /**
   * クローズ処理
   */
  fun closeConnect() {
    _helper.close()
  }

  /**
   * データ追加処理
   */
  fun addData(title: String, date: String, time: String, details: String): Int{
    val id: Int
    val db = _helper.writableDatabase

    val sqlInsert = "INSERT INTO $dtName (title, date, time, details) VALUES (?, ?, ?, ?);"

    val stmt = db.compileStatement(sqlInsert)

    stmt.bindString(1, title)
    stmt.bindString(2, date)
    stmt.bindString(3, time)
    stmt.bindString(4, details)
    id = stmt.executeInsert().toInt()

    Log.d("DatabaseManager","addData")
    return id
  }

  /**
   * 指定IDデータ削除処理
   */
  fun deleteData(vararg id: Int) {
    val db = _helper.writableDatabase
    val whereClauses = BaseColumns._ID +" = ?"
    val whereArgs = id.map {it.toString()}.toTypedArray()
    db.delete(dtName, whereClauses, whereArgs)
  }

  /**
   * データ更新処理
   */
  fun updateData(task: Task){
    val db = _helper.writableDatabase
    val values = ContentValues()
    values.put(DatabaseHelper.Title, task.title)
    values.put(DatabaseHelper.Date, task.date)
    values.put(DatabaseHelper.Time, task.time)
    val whereClauses = BaseColumns._ID +" = ?"
    val whereArgs = arrayOf(task.id.toString())
    val result = db.update(dbName, values, whereClauses, whereArgs)
    result != 0
  }

  /**
   * 指定IDデータ取得処理
   */
  fun getData(id: Int): Task? {
    var task: Task? = null
    val db = _helper.writableDatabase
    // データを取得するSQL文
    val sql = "SELECT * FROM $dtName WHERE ${BaseColumns._ID} = $id"
    // SQL文(SELECT文)の実行結果表を格納するCursorオブジェクト
    val cursor = db.rawQuery(sql, null)
    if (cursor.count > 0) {
      cursor.moveToFirst()
      task = Task(
        cursor.getInt(0),
        cursor.getString(1),
        cursor.getString(2),
        cursor.getString(3),
        cursor.getString(4)
      )
    }
    cursor.close()
    return task
  }

  /**
   * 全データ取得処理
   */
  fun getAllData(): ArrayList<Task> {
    val db = _helper.writableDatabase
    val allData: ArrayList<Task> = arrayListOf()
    // データを取得するSQL文
    val sql = "SELECT * FROM $dtName"
    // SQL文(SELECT文)の実行結果表を格納するCursorオブジェクト
    val cursor = db.rawQuery(sql, null)
    if (cursor.count > 0) {
      cursor.moveToFirst()
      while (!cursor.isAfterLast) {
        val task = Task(
          cursor.getInt(0),
          cursor.getString(1),
          cursor.getString(2),
          cursor.getString(3),
          cursor.getString(4)
        )
        allData.add(task)
        cursor.moveToNext()
      }
    }
    cursor.close()
    return allData
  }
}
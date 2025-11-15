package com.example.appComponents.roomDatabase

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface DemoDataDao {

    @Insert
    suspend fun insert(demoData: DemoData) : Int

    @RawQuery
    suspend fun query(query: SupportSQLiteQuery): Cursor

    @RawQuery
    suspend fun singleQuery(query: SupportSQLiteQuery): DemoData

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    suspend fun getById(id: Int): Cursor

    @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Update
    suspend fun update(demoData: DemoData) : Int
}
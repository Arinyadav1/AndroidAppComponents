package com.example.appComponents.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DemoData::class], version = 1)
abstract class DemoRoomDatabase : RoomDatabase() {

    abstract fun demoDataDao() : DemoDataDao

    companion object{
        fun getDatabase(context: Context): DemoRoomDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                DemoRoomDatabase::class.java,
                "demo_database"
            ).build()
        }
    }
}
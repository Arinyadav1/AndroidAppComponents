package com.example.appComponents.roomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TABLE_NAME)
data class DemoData(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val mobileNo: String

)

const val TABLE_NAME = "demo_table"

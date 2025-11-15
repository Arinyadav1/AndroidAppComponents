package com.example.appComponents

import android.content.Loader
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.loader.app.LoaderManager
import com.example.appComponents.contentResolver.ContentResolver

class MainActivity : ComponentActivity(){
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            )
        )
        setContent {
//            RunService()
//            RunBroadCast()
            ContentResolver(this)
//            RunWorkManager(this)

       }
    }
}



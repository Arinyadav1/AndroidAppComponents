package com.example.appComponents.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startForegroundService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RunService(){
    val context = LocalContext.current
    val intent = remember {  Intent(context, MyServices::class.java)}
    var data by remember { mutableIntStateOf(0) }

    val permissionState = rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)

    val broadcastReceiver = remember {
        object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                data = intent?.getIntExtra("counter", 0) ?: 0
            }

        }
    }
    LaunchedEffect(key1 =  true) {
       IntentFilter("event").apply {
           context.registerReceiver(broadcastReceiver, this, Context.RECEIVER_EXPORTED)
       }

    }


    Box(
        modifier = Modifier.fillMaxSize(1f),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.fillMaxSize(.7f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            Text(
                text = "How foreground service work",
                fontSize = 20.sp
            )

            Box(
                modifier = Modifier.padding(top = 100.dp)
            ) {
                Button(
                    onClick = {

                        if(!permissionState.hasPermission){
                            permissionState.launchPermissionRequest()
                        }else{
                            startForegroundService(context, intent)
                        }
                    },
                ) {
                    Text(
                        text = "Start Count"
                    )
                }
            }

            Button(onClick = {
                context.stopService(intent)
            }
            ) {
                Text(
                    text = "Stop Count"
                )
            }

            Box(
                modifier = Modifier.padding(top = 100.dp)
            ) {
                Text(
                    text = "$data",
                    fontSize = 25.sp
                )
            }
        }
    }

}
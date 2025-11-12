package com.example.AppComponents.BroadCast_Reciever

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RunBroadCast() {

    val data = remember { mutableStateOf("") }
    val context = LocalContext.current

    val broadcastReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val bundle = intent?.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN )
                data.value = if(bundle == WifiManager.WIFI_STATE_ENABLED){
                    "Wifi is Enable"
                }else{
                    "Wifi is Disable"
                }
            }

        }
    }
    DisposableEffect(key1 = true) {
        IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION).apply {
            context.registerReceiver(broadcastReceiver, this, Context.RECEIVER_NOT_EXPORTED)
        }

        onDispose {
            context.unregisterReceiver(broadcastReceiver)
        }
    }



    Box(
        modifier = Modifier.fillMaxSize(1f),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = data.value,
            fontSize = 25.sp
        )
    }
    
}


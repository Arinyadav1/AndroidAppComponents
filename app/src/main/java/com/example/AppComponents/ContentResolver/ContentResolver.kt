package com.example.AppComponents.ContentResolver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

data class ContactData(val number: String, val name: String)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("Range", "Recycle", "UnrememberedMutableState")
@Composable
fun ContentResolver(context: Context) {
    val dataList = mutableSetOf<ContactData>()
    val contentResolver = context.contentResolver
    val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

    val permissionState = rememberPermissionState(android.Manifest.permission.READ_CONTACTS)

    LaunchedEffect(Unit) {
        if (!permissionState.hasPermission) {
            permissionState.launchPermissionRequest()
        }
    }
    if (permissionState.hasPermission) {
        val cursor = contentResolver.query(
            uri, null, null, null, null, null
        )

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

                    val number =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    if(name != null) {
                        dataList.add(ContactData(name,number))
                    }

                } while (cursor.moveToNext())
                cursor.close()
            }
        }
    }else{


    }
    ContactListShowByContentProvider(dataList)
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ContactListShowByContentProvider(dataList: MutableSet<ContactData>) {

    val contactPermission = rememberPermissionState(android.Manifest.permission.CALL_PHONE)

    val context = LocalContext.current
    val list = mutableListOf<ContactData>()
    for (item in dataList){
        list.add(ContactData(item.name, item.number))
    }


    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = 45.dp)
                    .height(50.dp),
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF81D4FA),
                    titleContentColor = Color.Black
                ),
                title = {

                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = "Contact List"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)

                ,
            contentAlignment = Alignment.TopCenter
        ) {
                LazyColumn(
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(list) {
                        Box(
                            modifier = Modifier.clip(
                                RoundedCornerShape(14.dp),
                            )
                                .fillMaxWidth(.94f)
                                .fillMaxHeight(1f)
                                .height(60.dp)
                                .background(Color.Black)
                            ,
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                Column(
                                    modifier = Modifier.height(60.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.Start,
                                ) {
                                    Text(
                                        modifier = Modifier.padding(start = 20.dp),
                                        text = it.name, color = Color.White,
                                        fontSize = 20.sp
                                    )
                                    Spacer(
                                        modifier = Modifier.height(5.dp)
                                    )
                                    Text(
                                        modifier = Modifier.padding(start = 20.dp),
                                        text = it.number, color = Color.White,
                                    )
                                }

                                Box(
                                    modifier = Modifier.fillMaxSize()
                                        .padding(end = 10.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    IconButton(onClick = {
                                        val intent = Intent(Intent.ACTION_CALL).apply {
                                            data = Uri.parse("tel: ${it.number}")
                                        }
                                        val chooser = Intent.createChooser(intent, "")
                                        if(!contactPermission.hasPermission) {
                                            contactPermission.launchPermissionRequest()
                                        }else{
                                            context.startActivity(chooser)
                                        }

                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Call,
                                            contentDescription = "",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }



}
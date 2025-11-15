package com.example.appComponents.contentResolver

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
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
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.example.appComponents.roomDatabase.DemoRoomDatabase
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

data class ContactData(val number: String, val name: String)

// this is use to get large volume of data from content
// provider and don't block the main UI thread.
class DemoContentResolver(context: Context) : LoaderManager.LoaderCallbacks<Cursor> {

    val contentResolver: ContentResolver = context.contentResolver
    val uri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

    val context1 = context

    val dataList = mutableListOf<ContactData?>(null)


    // it is use to get a particular column data in table
    val projection = arrayOf(
        // this point a name row in table
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,

        //this point a number row in table
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )


    // it is like where clause in sql
    val selectionClause =
        "${ContactsContract.Contacts.DISPLAY_NAME} = ? AND ${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?"

    // if @param selectionClause have ? mark then @param selectionArgument value provide
    val selectionArgument = arrayOf("Arin", "902789")

    val sortOrder = "${ContactsContract.Contacts.DISPLAY_NAME} DESC"

    @SuppressLint("Recycle")
    fun retrieveData() : MutableList<ContactData>?{

        val dataList = mutableListOf<ContactData>()

        val retrieve = contentResolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder
        )

        if (retrieve != null && retrieve.count > 0){
            if (retrieve.moveToFirst()){
                do {
                    val name =
                        retrieve.getString(0)

                    val number =
                        retrieve.getString(1)

                    dataList.add(ContactData(name, number))
                }while (retrieve.moveToNext())
            }
        }else{
            return  null
        }

        return dataList
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun updateData(
        name : String? = null,
        phoneNumber : String? = null
    ){

        val contentValue = ContentValues().apply {
            put(ContactsContract.Contacts.DISPLAY_NAME, name)
            put(ContactsContract.Contacts.HAS_PHONE_NUMBER, phoneNumber)
        }

        contentResolver.update(
            uri,
            contentValue,
            null
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun insertData(
        name : String,
        phoneNumber : String
    ){

        val contentValue = ContentValues().apply {
            put(ContactsContract.Contacts.DISPLAY_NAME, name)
            put(ContactsContract.Contacts.HAS_PHONE_NUMBER, phoneNumber)
        }

        contentResolver.insert(
            uri,
            contentValue,
            null
        )

    }

    fun deleteData(
        name : String,
    ){

        contentResolver.delete(
            uri,
            "${ContactsContract.Contacts.DISPLAY_NAME} = $name",
            null
        )
    }



    // Cursor Loader is work with old ui for new ui use coroutine
    // and flow for asynchronous task
    override fun onCreateLoader(
        id: Int,
        args: Bundle?
    ): Loader<Cursor?> {
        TODO("Not yet implemented")
    }

    override fun onLoadFinished(
        loader: Loader<Cursor?>,
        retrieve: Cursor?
    ) {

        TODO("Not yet implemented")
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {
        TODO("Not yet implemented")
    }

}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("Range", "Recycle", "UnrememberedMutableState")
@Composable
fun ContentResolver(context: Context) {

    val permissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)

    LaunchedEffect(Unit) {
        if (!permissionState.hasPermission) {
            permissionState.launchPermissionRequest()
        }
    }

    val demoContentResolver = DemoContentResolver(context)

    if (permissionState.hasPermission) {
        ContactListShowByContentResolver(demoContentResolver.retrieveData())
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ContactListShowByContentResolver(dataList: MutableList<ContactData>?) {

    val contactPermission = rememberPermissionState(Manifest.permission.CALL_PHONE)

    val context = LocalContext.current
    val list = mutableListOf<ContactData>()
    if (dataList != null) {
        for (item in dataList) {
            list.add(ContactData(item.name, item.number))
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(top = 45.dp)
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (dataList != null) {
                LazyColumn(
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(list) {
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(14.dp),
                                )
                                .fillMaxWidth(.94f)
                                .fillMaxHeight(1f)
                                .height(60.dp)
                                .background(Color.Black),
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
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(end = 10.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    IconButton(onClick = {
                                        val intent = Intent(Intent.ACTION_CALL).apply {
                                            data = Uri.parse("tel: ${it.number}")
                                        }
                                        val chooser = Intent.createChooser(intent, "")
                                        if (!contactPermission.hasPermission) {
                                            contactPermission.launchPermissionRequest()
                                        } else {
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
            }else{
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = "No Contact Found",
                    fontSize = 20.sp
                )
            }
        }
    }


}
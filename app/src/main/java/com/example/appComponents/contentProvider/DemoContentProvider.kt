package com.example.appComponents.contentProvider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.appComponents.roomDatabase.DemoRoomDatabase
import androidx.core.net.toUri
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.example.appComponents.roomDatabase.DemoData
import com.example.appComponents.roomDatabase.TABLE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class DemoContentProvider : ContentProvider() {

    private lateinit var database: DemoRoomDatabase

    companion object {
        const val AUTHORITY = "com.example.contentProvider"
        const val PATH_ALL_DATA = "allData"
        const val PATH_DELETE_ALL = "deleteAll"
        const val PATH_DELETE_SINGLE = "deleteAll"

        // this is use when we use contentResolver
        val CONTENT_URI_PATH_ALL_DATA: Uri = "content://$AUTHORITY/$PATH_ALL_DATA".toUri()

        // for pass id use like this
        val uriWithId: Uri = ContentUris.withAppendedId(
            CONTENT_URI_PATH_ALL_DATA,
            1 // this is id
        )

        private const val ALL_DATA = 1
        private const val ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            // Match all data
            addURI(AUTHORITY, PATH_ALL_DATA, ALL_DATA)
            // Match single note by ID
            addURI(AUTHORITY, "$PATH_ALL_DATA/#", ID)

        }
    }


    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        return when (uriMatcher.match(uri)) {
            ALL_DATA -> {
                val queryBuilder = SupportSQLiteQueryBuilder.builder(TABLE_NAME)
                    .selection(selection, selectionArgs)
                    .create()
                val demoData =
                    runBlocking(Dispatchers.IO) { database.demoDataDao().singleQuery(queryBuilder) }

                runBlocking(Dispatchers.IO) { database.demoDataDao().deleteById(demoData.id) }

            }

            ID -> {
                val id = ContentUris.parseId(uri)

                runBlocking { database.demoDataDao().deleteById(id.toInt()) }
            }

            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {

            ALL_DATA -> {
                "vnd.android.cursor.dir/${AUTHORITY}.${PATH_ALL_DATA}"
            }

            ID -> {
                "vnd.android.cursor.item/${AUTHORITY}.${PATH_ALL_DATA}"
            }

            else -> null
        }
    }

    override fun insert(uri: Uri, value: ContentValues?): Uri? {
        return when (uriMatcher.match(uri)) {
            ALL_DATA -> {
                val demoData = DemoData(
                    name = value?.getAsString("name").orEmpty(),
                    mobileNo = value?.getAsString("number").orEmpty(),
                )

                val id = runBlocking(Dispatchers.IO) { database.demoDataDao().insert(demoData) }

                ContentUris.withAppendedId(
                    CONTENT_URI_PATH_ALL_DATA,
                    id.toLong()
                )

            }

            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun onCreate(): Boolean {
        val context = context ?: return false

        database = DemoRoomDatabase.getDatabase(context)

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        order: String?
    ): Cursor {
        return when (uriMatcher.match(uri)) {
            ALL_DATA -> {
                val queryBuilder = SupportSQLiteQueryBuilder.builder(TABLE_NAME)
                    .columns(projection)
                    .selection(selection, selectionArgs)
                    .orderBy(order)
                    .create()
                runBlocking(Dispatchers.IO) { database.demoDataDao().query(queryBuilder) }
            }

            ID -> {
                val id = ContentUris.parseId(uri)

                runBlocking { database.demoDataDao().getById(id.toInt()) }
            }

            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        return when (uriMatcher.match(uri)) {
            ALL_DATA -> {
                val queryBuilder = SupportSQLiteQueryBuilder.builder(TABLE_NAME)
                    .selection(selection, selectionArgs)
                    .create()

                val query =
                    runBlocking(Dispatchers.IO) { database.demoDataDao().singleQuery(queryBuilder) }

                val demoData = DemoData(
                    name = contentValues?.getAsString("name").orEmpty(),
                    mobileNo = contentValues?.getAsString("number").orEmpty(),
                    id = query.id
                )

                runBlocking(Dispatchers.IO) { database.demoDataDao().update(demoData) }
            }

            ID -> {
                val id = ContentUris.parseId(uri)

                val demoData = DemoData(
                    name = contentValues?.getAsString("name").orEmpty(),
                    mobileNo = contentValues?.getAsString("number").orEmpty(),
                    id = id.toInt()
                )

                runBlocking(Dispatchers.IO) { database.demoDataDao().update(demoData) }
            }

            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

}
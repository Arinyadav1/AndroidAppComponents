package com.example.appComponents.services

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid.Companion.random

class DemoWorkManager(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun doWork(): Result {

        Log.d("ARIN1", "Task Started...")

        delay(2000)

        Log.d("ARIN1", "Task Completed ${random()}")

        return Result.retry()
    }


}

fun createWorkManager(context: Context) {
    val workManager = WorkManager.getInstance(context)

    // this will run periodically in every 10 second even the app has closed or kill.
    val uploadWorkManager =
//        OneTimeWorkRequestBuilder<DemoWorkManager>()
//            .setInitialDelay(1, TimeUnit.SECONDS)
//            .build()
        PeriodicWorkRequestBuilder<DemoWorkManager>(
            repeatInterval = 15,
            TimeUnit.MINUTES
        )
            .build()

    workManager.enqueue(uploadWorkManager)
}


@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RunWorkManager(context: Context) {

    Box(
        modifier = Modifier.fillMaxSize(1f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(.7f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Work Manager",
                fontSize = 20.sp
            )

            Box(
                modifier = Modifier.padding(top = 100.dp)
            ) {
                Button(
                    onClick = {
                        createWorkManager(context)
                    },
                ) {
                    Text(
                        text = "Run Work Manager"
                    )
                }
            }

        }
    }

}
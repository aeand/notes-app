package com.example.notes

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/* TODO
- check how Writer Journal handles root folder access and permission requests. Very nice.
- king idea. open app into a files list. start a new activity when clicking on a note. to go back, use the back functionality. will make the structure clean and good. one note activity is directly tied to the note that is opened. the note will be auto saved as soon as the activity is closed in any way. Could move title to top center and make it part of the scrollable document. also bottom button would overlay the textfield. same with title. to fit more text on screen. by putting everything in a column and increasing height, i can scroll title and content. and maybe fix writing behind keyboard issues

Performance:
- laggy and slow dir on low battery
- check recompositions
*/

object AppInit: Application() {
    val fileManager = FileManager(this)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        window.statusBarColor = Color.Black.toArgb()
        window.navigationBarColor = Color.Black.toArgb()
        val fileManager = AppInit.fileManager

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                val showDir = remember { mutableStateOf(false) }
                Notes(fileManager) {
                    showDir.value = true
                    fileManager.updateFiles()
                }
                if (showDir.value) {
                    Directory(
                        fileManager,
                        closeDir = {
                            showDir.value = false
                        }
                    )
                }
            }
        }
    }

    private fun requestPermissions() {
        if (Environment.isExternalStorageManager()) {
            return
        }

        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).also {
            it.data = Uri.parse("package:${packageName}")
        }
        startActivity(intent)
    }
}
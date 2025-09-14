package com.example.notes

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* TODO
Bugs:
- bug rotating app deletes text
- bug file name cannot include ? and more characters. be sure to regex the title before saving
- bug content textfield does not match with the divider. text gets cut off before divider
- bug save hides after saving to file with empty title
- bug holding erase removed 1 char in front. like delete. Might be how android works. backspaces and deletes over and over when on low battery.
- bug navigating out and in of app removed all content. happened once
- bug close and open new keyboard when clicking new input, with other input focused

Notes:
- check how Writer Journal handles root folder access and permission requests. Very nice.
- king idea. open app into a files list. start a new activity when clicking on a note. to go back, use the back functionality. will make the structure clean and good. one note activity is directly tied to the note that is opened. the note will be auto saved as soon as the activity is closed in any way. Could move title to top center and make it part of the scrollable document. also bottom button would overlay the textfield. same with title. to fit more text on screen. by putting everything in a column and increasing height, i can scroll title and content. and maybe fix writing behind keyboard issues

Might be fixed:
- opening dir takes a 'long' time. too long for my patience tho
- make sure file ends with new lines when long enough

Performance:
- laggy dir on low battery
- check recompositions
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()
        val fileManager = FileManager(this)

        window.statusBarColor = Color.Black.toArgb()
        window.navigationBarColor = Color.Black.toArgb()

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
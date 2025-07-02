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
rotation deletes content in textfield
make title field scrollable to the right. so it don't wrap itself
opening dir takes a 'long' time. too long for my patience tho
delete auto saving
file name cannot include ? and more characters. be sure to regex the title before saving
add a scrollbar to text field
close and open new keyboard when clicking new input, with other input focused

laggy dir on low battery
check recompositions
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()
        val fileManager = FileManager(this)

        window.statusBarColor = Color.Black.toArgb()
        window.navigationBarColor = Color.Black.toArgb()

        setContent {
            // AUTOSAVE
            LaunchedEffect(fileManager.currentFile.content.value) {
                this.launch {
                    delay(3000)
                    fileManager.autoSave()
                }
            }

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
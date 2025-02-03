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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/* TODO
reloading files on move (order looks ok but hit boxes are old)
reloading files with dir open sometimes removes all files from UI
redo UI for saving folders
redo NotesUI when deleting file your on
move auto save to fileManager
folder icons for hidden folders (might be more)

close and open new keyboard when clicking new input, with other input focused
start folders as folded

make me able to edit text behind keyboard. When text is just not long enough to be scrollable and keyboard is in the way
want a undo/redo feature (in case I accidentally delete something)

check recompositions
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()
        val fileManager = FileManager(this)

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
                    Directory(fileManager) {
                        showDir.value = false
                    }
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
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* TODO
reloading files on move (order looks ok but hit boxes are old)
recompose NotesUI when deleting file your on

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
            // AUTOSAVE
            LaunchedEffect(fileManager.currentFile.value.content) {
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
                val showSaveFileDialog = remember { mutableStateOf(false) }
                if (showSaveFileDialog.value) {
                    DialogSaveFolder(
                        confirm = { folderName ->
                            fileManager.saveFolder(folderName)
                            showSaveFileDialog.value = false
                        },
                        cancel = {
                            showSaveFileDialog.value = false
                        }
                    )
                }

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
                        },
                        openSaveFolderDialog = {
                            showSaveFileDialog.value = true
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
package se.anton.notes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/* TODO
Performance:
- laggy and slow dir on low battery
- check recompositions
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        window.statusBarColor = Color.Black.toArgb()
        window.navigationBarColor = Color.Black.toArgb()
        val fileManager = FileManager(this)

        setContent {
            val showDir = remember { mutableStateOf(true) }
            BackHandler(!showDir.value) {
                if ((SelectedFile.content.value.isNotEmpty() || SelectedFile.title.value.isNotEmpty()) && fileManager.fileHasChanges()) {
                    return@BackHandler
                }

                fileManager.updateFiles()
                showDir.value = true
            }

            if (showDir.value) {
                Directory(
                    fileManager,
                    closeDir = {
                        showDir.value = false
                    }
                )
            }
            else {
                Notes(fileManager)
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

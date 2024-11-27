package com.example.notes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

/* TODO
- check recompositions
- it needs to be clearer what each action does
- make me able to edit text behind keyboard. When text is just not long enough to be scrollable and keyboard is in the way
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        val fileManager = FileManager()

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Notes(
                    getFiles = fileManager::getFiles,
                    saveFile = fileManager::saveFile,
                    saveFileOverride = fileManager::overrideFile,
                    readFile = fileManager::readFile,
                    saveFolder = fileManager::saveFolder,
                    moveFile = fileManager::moveFile,
                    deleteFiles = fileManager::deleteFiles,
                    rootFolderName = fileManager.rootFolderName,
                    rootPath = fileManager.root,
                )

                Directory()
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
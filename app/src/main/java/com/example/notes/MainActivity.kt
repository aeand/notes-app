package com.example.notes

import android.os.Bundle
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
- bug: save crashed app
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
        )

        val fileManager = FileManager()
        val topBar = 32f
        val bottomBar = 48f

        setContent {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black))

            Notes(
                Modifier
                    .padding(top = topBar.dp, bottom = bottomBar.dp),
                getFiles = fileManager::getFiles,
                saveFile = fileManager::saveFile,
                saveFileOverride = fileManager::overrideFile,
                readFile = fileManager::readFile,
                saveFolder = fileManager::saveFolder,
                moveFile = fileManager::moveFile,
                deleteFiles = fileManager::deleteFile,
                rootFolderName = fileManager.rootFolderName,
                rootPath = fileManager.root,
            )

            Directory()
        }
    }
}
package com.example.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/* TODO
- add app logo
- add splash screen
- open dir menu in a new way
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val notes = Notes()
        val topBar = 32f
        val bottomBar = 48f

        setContent {
            NotesPage(
                Modifier
                    .padding(top = topBar.dp, bottom = bottomBar.dp),
                getFiles = notes::getFiles,
                saveFile = notes::saveFile,
                saveFileOverride = notes::overrideFile,
                readFile = notes::readFile,
                saveFolder = notes::saveFolder,
                moveFile = notes::moveFile,
                deleteFiles = notes::deleteFile,
                rootFolderName = notes.rootFolderName,
                rootPath = notes.root,
            )
        }
    }
}
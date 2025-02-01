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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/* TODO
ez
when writing title then clicking text with an open keyboard won't actually open a new keyboard visually. pls close and open new one. the button in the bottom right is incorrect
start folders as folded
if I'm on a note that I remove. I need to clear the text or something so I can switch to other one. (will partially be fixed when changing the save flow)

remove the confirmation and override saves and add back toast when saving
when switching to a note, open a save dialog if there are changes to a note or there's new text. And give the options save and continue or don't save and continue.

make me able to edit text behind keyboard. When text is just not long enough to be scrollable and keyboard is in the way
want a undo/redo feature (in case I accidentally delete something)

check recompositions
it needs to be clearer what each action does
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
                Notes(fileManager)
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
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
what happens if you deny the permissions?

1. Make keyboard take up space. so i don't have to add new lines like this and remove new line functionality and new lines from existing notes
2. Stop auto scroll to top when clicking in a scrolled note
I can try this:
https://stackoverflow.com/questions/72813513/android-jetpack-compose-basictextfield-scroll-to-top-when-get-focus-how-to-stop#72825770
or this
https://github.com/JetBrains/compose-multiplatform/issues/4014

i could try and find a way to return to the same scroll position when returning from a note. Not when opening app, then I want to start fro  the top.

never tried adding spacing at bottom of textfield when keyboard is open. That could solve the issue of keyboard taking up space. Cooould also come with its own issues. will solve issue when writing at bottom of note

trim notes on save? go through all lines and run trim

great tips to remove lag from lazy column
https://stackoverflow.com/questions/70592694/laggy-lazy-column-android-compose
improvememts to lazy column in notes app note

use the same padding on note area as the title area. sliightly different
or it is the font that makes it look weird

move title to the top of file and make save a floating button. visable when keyboard is open

i think i do to much when loading the dir. i only need to:
cd ./Notes && ls
to get names for the files and display those. then look at the first row to get tags. seems to me like it could be faster

can i focus the input field when note is opened to fix the scroll to cursor issue?

Bugs:
- Suddenly an old version is installed instead. no clue why..
- it doesn't read in files once you accept the permissions
- remove the effect of deleting text on the wrong side of cursor
- backing out didnt work after entering an empty file. even after saving

Performance:
- reloading dir on delete or navigation is slow enough for me to notice. even without battery saving mode
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

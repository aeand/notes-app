package se.anton.notes

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.io.File
import java.io.FileInputStream

class CustomFile(
    val file: File,
    val tag: String,
)

object SelectedFile {
    var title: MutableState<String> = mutableStateOf("")
    var content: MutableState<String> = mutableStateOf("")
    var originalTitle: String = ""
    var originalContent: String = ""
}

class FileManager(
    private val applicationContext: Context
) {
    val rootFolderName = "Notes"
    private val root = "/storage/emulated/0/${rootFolderName}"
    var files = mutableStateListOf<CustomFile>()

    init {
        getAllFiles()
    }

    fun updateFiles() {
        getAllFiles()
    }

    fun openFile(fileName: String) {
        val file = files.find { it.file.nameWithoutExtension == fileName }
        if (file == null) {
            Toast.makeText(applicationContext, "file not found", Toast.LENGTH_SHORT).show()
            return
        }

        val content = FileInputStream(file.file).bufferedReader().use {
            it.readText()
        }

        SelectedFile.title.value = file.file.nameWithoutExtension
        SelectedFile.content.value = content
        SelectedFile.originalTitle = file.file.nameWithoutExtension
        SelectedFile.originalContent = content
    }

    fun resetCurrentFile() {
        SelectedFile.title.value = ""
        SelectedFile.content.value = ""
        SelectedFile.originalTitle = ""
        SelectedFile.originalContent = ""
    }

    fun fileHasChanges(): Boolean {
        if (SelectedFile.title.value.isEmpty() && SelectedFile.content.value.isEmpty())
            return false

        if (SelectedFile.originalContent.isEmpty() || SelectedFile.originalTitle.isEmpty())
            return true

        return SelectedFile.title.value != SelectedFile.originalTitle || SelectedFile.content.value != SelectedFile.originalContent
    }

    fun saveCurrentFile() {
        if (SelectedFile.title.value.isEmpty()) {
            Toast
                .makeText(applicationContext, "missing title or content", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val illegalCharacters: List<String> = mutableListOf("\\", "/", ":", "*", "?", "\"", "<", ">", "|")
        illegalCharacters.forEach { illegalChar ->
            if (SelectedFile.title.value.contains(illegalChar)) {
                Toast
                    .makeText(applicationContext, "illegal characters in title", Toast.LENGTH_SHORT)
                    .show()
                return
            }
        }

        val letDirectory = File(root, "")
        letDirectory.mkdirs()
        val file = File(letDirectory, "${SelectedFile.title.value}.txt")
        file.writeText(SelectedFile.content.value)

        SelectedFile.originalTitle = SelectedFile.title.value
        SelectedFile.originalContent = SelectedFile.content.value
        SelectedFile.title.value = SelectedFile.title.value
        SelectedFile.content.value = SelectedFile.content.value

        Toast.makeText(applicationContext, "file saved", Toast.LENGTH_SHORT).show()
    }

    fun deleteFiles(list: List<CustomFile>) {
        list.forEach { file ->
            try {
                if (file.file.nameWithoutExtension == SelectedFile.title.value) {
                    resetCurrentFile()
                }
                file.file.delete()
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "error: delete file failure $e",
                    Toast.LENGTH_SHORT
                ).show()
                println("error: delete file failure $e")
            }
        }
    }

    private fun getAllFiles() {
        val files = File(root, "").listFiles()

        files?.sortWith { a, b ->
            a.name.uppercase().compareTo(b.name.uppercase())
        }

        files?.sortWith { a, b ->
            a.isFile.compareTo(b.isFile)
        }

        val customFiles = mutableListOf<CustomFile>()
        files?.forEach { file ->
            if (!file.exists())
                return@forEach

            val tag = if (
                file.readLines(Charsets.UTF_8).isNotEmpty()
                && file.readLines(Charsets.UTF_8)[0].contains("#")
            )
                file.readLines(Charsets.UTF_8)[0].replace("#", "").trim().lowercase()
            else
                ""

            customFiles.add(CustomFile(file, tag))
        }

        customFiles.sortWith { a, b ->
            a.tag.compareTo(b.tag)
        }

        this.files.clear()
        customFiles.forEach {
            this.files.add(it)
        }
    }
}
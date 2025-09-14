package com.example.notes

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

class FileContent(
    var title: MutableState<String>,
    var content: MutableState<String>,
)

class FileManager(
    private val applicationContext: Context
) {
    val rootFolderName = "Notes"
    private val root = "/storage/emulated/0/${rootFolderName}"
    var files = mutableStateListOf<CustomFile>()
    var currentFile = FileContent(
        mutableStateOf(""),
        mutableStateOf("")
    )
    private var originalTitle: String = ""
    private var originalContent: String = ""

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

        currentFile.title.value = file.file.nameWithoutExtension
        currentFile.content.value = content
        originalTitle = file.file.nameWithoutExtension
        originalContent = content
    }

    fun resetCurrentFile() {
        currentFile.title.value = ""
        currentFile.content.value = ""
        originalTitle = ""
        originalContent = ""
    }

    fun fileHasChanges(): Boolean {
        if (currentFile.title.value.isEmpty() && currentFile.content.value.isEmpty())
            return false

        if (originalContent.isEmpty() || originalTitle.isEmpty())
            return true

        return currentFile.title.value != originalTitle || currentFile.content.value != originalContent
    }

    fun saveCurrentFile() {
        if (currentFile.title.value.isEmpty()) {
            Toast
                .makeText(applicationContext, "missing title or content", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val illegalCharacters: List<String> = mutableListOf("\\", "/", ":", "*", "?", "\"", "<", ">", "|")
        illegalCharacters.forEach { illegalChar ->
            if (currentFile.title.value.contains(illegalChar)) {
                Toast
                    .makeText(applicationContext, "illegal characters in title", Toast.LENGTH_SHORT)
                    .show()
                return
            }
        }

        val letDirectory = File(root, "")
        letDirectory.mkdirs()
        val file = File(letDirectory, "${currentFile.title.value}.txt")
        file.writeText(currentFile.content.value)

        originalTitle = currentFile.title.value
        originalContent = currentFile.content.value
        currentFile.title.value = currentFile.title.value
        currentFile.content.value = currentFile.content.value

        Toast.makeText(applicationContext, "file saved", Toast.LENGTH_SHORT).show()
    }

    fun deleteFiles(list: List<CustomFile>) {
        list.forEach { file ->
            try {
                if (file.file.nameWithoutExtension == currentFile.title.value) {
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
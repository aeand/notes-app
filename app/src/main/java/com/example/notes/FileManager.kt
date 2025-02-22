package com.example.notes

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.File
import java.io.FileInputStream

class CustomFile(
    val file: File,
    val tag: String,
)

class FileContent(
    var title: String,
    var path: String,
    var content: String,
)

class FileManager(private val applicationContext: Context) {
    val rootFolderName = "Notes"
    private val root = "/storage/emulated/0/${rootFolderName}"
    var files = mutableStateListOf<CustomFile>()
    private var previousFiles = mutableStateListOf<CustomFile>()
    var currentFile = mutableStateOf(FileContent("", "", ""))

    init {
        files = getFiles("")

        val autoSaveFile = files.find { it.file.nameWithoutExtension == "tmpfileforautosave" }
        if (autoSaveFile == null) {
            createAutoSaveFile()
        }
    }

    fun updateFiles() {
        files.forEach { previousFiles.add(it) }
        files.clear()

        getFiles().forEach {
            if (it.file.exists())
                files.add(it)
        }

        previousFiles.clear()
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

        currentFile.value.title = file.file.nameWithoutExtension
        currentFile.value.path = file.file.path.replace(root, "").replace(file.file.name, "")
        currentFile.value.content = content
    }

    fun resetCurrentFile() {
        currentFile.value.title = ""
        currentFile.value.path = ""
        currentFile.value.content = ""
    }

    private fun createAutoSaveFile() {
        saveFile("tmpfileforautosave", "", "", false)
    }

    fun autoSave() {
        if (currentFile.value.content.isEmpty()) {
            return
        }

        saveFile("tmpfileforautosave", "", currentFile.value.content, false)
    }

    fun saveFile(title: String, path: String = "", content: String, showToast: Boolean) {
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(applicationContext, "no content or title", Toast.LENGTH_SHORT).show()
            return
        }

        val letDirectory = File(root, path)
        letDirectory.mkdirs()
        val file = File(letDirectory, "$title.txt")
        file.writeText(content)

        if (showToast) {
            Toast.makeText(applicationContext, "file saved", Toast.LENGTH_SHORT).show()
        }

        return
    }

    fun deleteFiles(list: List<CustomFile>) {
        list.forEach { file ->
            try {
                if (file.file.path.replace(root, "") == currentFile.value.path) {
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

        updateFiles()
    }

    private fun getFiles(path: String = ""): SnapshotStateList<CustomFile> {
        val files = File(root, path).listFiles()

        files?.sortWith { a, b ->
            a.name.uppercase().compareTo(b.name.uppercase())
        }

        files?.sortWith { a, b ->
            a.isFile.compareTo(b.isFile)
        }

        val result = SnapshotStateList<CustomFile>()
        files?.forEach { file ->
            if (file.nameWithoutExtension == "tmpfileforautosave")
                return@forEach

            var str = file.readLines(Charsets.UTF_8)[0]
            if (str.contains("#"))
                str = str.replace("#", "").trim()
            else
                str = ""

            result.add(CustomFile(file, str))
        }

        result.sortWith { a, b ->
            a.tag.compareTo(b.tag)
        }

        return result
    }
}
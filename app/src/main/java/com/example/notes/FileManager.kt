package com.example.notes

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
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
    var title: MutableState<String>,
    var path: MutableState<String>,
    var content: MutableState<String>,
)

class FileManager(private val applicationContext: Context) {
    val rootFolderName = "Notes"
    private val root = "/storage/emulated/0/${rootFolderName}"
    var files = mutableStateListOf<CustomFile>()
    private var previousFiles = mutableStateListOf<CustomFile>()
    var currentFile = FileContent(
        mutableStateOf(""),
        mutableStateOf(""),
        mutableStateOf("")
    )
    private var originalContent: String? = null

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

        currentFile.title.value = file.file.nameWithoutExtension
        currentFile.path.value = file.file.path.replace(root, "").replace(file.file.name, "")
        currentFile.content.value = content
        originalContent = content
    }

    fun resetCurrentFile() {
        currentFile.title.value = ""
        currentFile.path.value = ""
        currentFile.content.value = ""
        originalContent = null
    }

    fun fileHasChanges(): Boolean {
        if (originalContent == null)
            return true

        return currentFile.content.value != originalContent
    }

    private fun createAutoSaveFile() {
        saveFile("tmpfileforautosave", "", "", false)
    }

    fun autoSave() {
        if (currentFile.content.value.isEmpty()) {
            return
        }

        saveFile("tmpfileforautosave", "", currentFile.content.value, false)
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
        originalContent = content

        if (showToast) {
            Toast.makeText(applicationContext, "file saved", Toast.LENGTH_SHORT).show()
        }

        return
    }

    fun deleteFiles(list: List<CustomFile>) {
        list.forEach { file ->
            try {
                if (file.file.path.replace(root, "") == currentFile.path.value) {
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
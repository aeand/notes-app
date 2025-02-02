package com.example.notes

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import java.io.File
import java.io.FileInputStream

class FileManager(private val applicationContext: Context) {
    class CustomFile(
        val file: File,
        val children: MutableList<CustomFile>?,
        val indent: Int,
        var hidden: Boolean,
    )

    class FileContent(
        var title: String,
        var path: String,
        var content: String,
    )

    val rootFolderName = "Notes"
    val root = "/storage/emulated/0/${rootFolderName}"
    var files = mutableListOf<CustomFile>()
    var previousFiles = mutableListOf<CustomFile>()
    var currentFile = mutableStateOf(FileContent("", "", ""))

    init {
        files = getFiles("")
    }

    fun updateFiles() {
        files.forEach {
            previousFiles.add(
                it
            )
        }
        files.clear()
    }

    fun autoSave(content: String) {
        saveFile("tmpfileforautosave", "", content, false)
    }

    fun saveFolder(title: String, path: String = "") {
        val folder = File(root, path + title)

        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Toast.makeText(
                    applicationContext,
                    "e: Cannot create a directory",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                folder.mkdirs()
            }
        }

        Toast.makeText(applicationContext, "folder saved", Toast.LENGTH_SHORT).show()
    }

    fun saveFile(title: String, path: String = "", content: String, showToast: Boolean) {
        if (title.isEmpty() || content.isEmpty()) {
            return
        }

        val letDirectory = File(root, path)
        letDirectory.mkdirs()
        val file = File(letDirectory, "$title.txt")

        file.writeText(content)

        currentFile.value.title = title
        currentFile.value.path = path
        currentFile.value.content = content

        if (showToast) {
            Toast.makeText(applicationContext, "file saved", Toast.LENGTH_SHORT).show()
        }

        return
    }

    fun readFile(file: File) {
        val match = files.find { it.file.nameWithoutExtension == file.nameWithoutExtension }
        if (match == null) {
            Toast.makeText(applicationContext, "file not found", Toast.LENGTH_SHORT).show()
            return
        }

        val content = FileInputStream(file).bufferedReader().use {
            it.readText()
        }
        println(content)

        currentFile.value.title = file.nameWithoutExtension
        currentFile.value.path = file.path.replace(root, "")
        currentFile.value.content = content
    }

    fun moveFile(sourceFilePaths: String, targetFile: CustomFile) {
        val pathList = mutableListOf<String>()
        if (sourceFilePaths.contains("_-middle-_")) {
            sourceFilePaths.split("_-middle-_").forEach { path ->
                pathList.add(path)
            }
        } else {
            pathList.add(sourceFilePaths)
        }

        val sourceFileList = mutableListOf<CustomFile>()
        pathList.forEach { path ->
            val file = getFiles().find { file ->
                file.file.name == path.takeLastWhile { s -> s != '/' } && file.file.path == path
            }
            if (file != null) {
                sourceFileList.add(file)
            }
        }

        sourceFileList.forEach { sourceFile ->
            if (sourceFile.file.path == targetFile.file.path) {
                Toast.makeText(
                    applicationContext,
                    "error: file is targeting the source file",
                    Toast.LENGTH_SHORT
                ).show()
                println("error: file is targeting the source file")
                return@forEach
            }

            if (root == targetFile.file.path && targetFile.file.path == sourceFile.file.path.replace(
                    "/${sourceFile.file.name}",
                    ""
                )
            ) {
                Toast.makeText(
                    applicationContext,
                    "error: file is already in root",
                    Toast.LENGTH_SHORT
                ).show()
                println("error: file is already in root")
                return@forEach
            }

            if (sourceFile.file.isFile) {
                if (targetFile.file.isFile) {
                    val targetFilePath =
                        targetFile.file.path.replace("/${targetFile.file.name}", "")

                    if (sourceFile.file.name == targetFile.file.name) {
                        Toast.makeText(
                            applicationContext,
                            "error: file with that name already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                        println("error: file with that name already exists")
                        return@forEach
                    }

                    if (sourceFile.file.path.replace(
                            "/${sourceFile.file.name}",
                            ""
                        ) == targetFilePath
                    ) {
                        Toast.makeText(
                            applicationContext,
                            "error: file and file have the same path",
                            Toast.LENGTH_SHORT
                        ).show()
                        println("error: file and file have the same path")
                        return@forEach
                    }

                    val filesInPath = File(
                        root,
                        targetFile.file.path.replace(root, "").replace(targetFile.file.name, "")
                    ).listFiles()
                    if (filesInPath == null) {
                        Toast.makeText(
                            applicationContext, "error: found no files in ${
                                root + targetFile.file.path.replace(
                                    root,
                                    ""
                                ).replace(targetFile.file.name, "")
                            }", Toast.LENGTH_SHORT
                        ).show()
                        println(
                            "error: found no files in ${
                                root + targetFile.file.path.replace(
                                    root,
                                    ""
                                ).replace(targetFile.file.name, "")
                            }"
                        )
                        return@forEach
                    }

                    for (file in filesInPath) {
                        if (sourceFile.file.name == file.name && file.isFile) {
                            Toast.makeText(
                                applicationContext,
                                "error: path has file with the same name as source",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("error: path has file with the same name as source")
                            return@forEach
                        }
                    }

                    copyFile(sourceFile, "$targetFilePath/${sourceFile.file.name}")
                    if (canDeleteFile(sourceFile, targetFile, targetFile.file.path))
                        deleteFile(sourceFile)
                    Toast.makeText(applicationContext, "moved file", Toast.LENGTH_SHORT).show()
                } else if (targetFile.file.isDirectory) {
                    val listOfFilesInDir = targetFile.file.listFiles()
                    if (listOfFilesInDir != null) {
                        for (file in listOfFilesInDir) {
                            if (sourceFile.file.name == file.name && file.isFile) {
                                Toast.makeText(
                                    applicationContext,
                                    "error: found file with same name as source file",
                                    Toast.LENGTH_SHORT
                                ).show()
                                println("error: found file with same name as source file")
                                return@forEach
                            }
                        }
                    }

                    copyFile(sourceFile, "${targetFile.file.path}/${sourceFile.file.name}")
                    if (canDeleteFile(sourceFile, targetFile, targetFile.file.path))
                        deleteFile(sourceFile)
                    Toast.makeText(applicationContext, "moved file", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "error: target file is not file or folder",
                        Toast.LENGTH_SHORT
                    ).show()
                    println("error: target file is not file or folder ${sourceFile.file.exists()} ${targetFile.file.exists()}")
                    return@forEach
                }
            } else if (sourceFile.file.isDirectory) {
                if (targetFile.file.isFile) {
                    val targetFilePath =
                        targetFile.file.path.replace("/${targetFile.file.name}", "")

                    val filesInPath = File(
                        root,
                        targetFile.file.path.replace(root, "").replace(targetFile.file.name, "")
                    ).listFiles()
                    if (filesInPath == null) {
                        Toast.makeText(
                            applicationContext, "error: found no files in ${
                                root + targetFile.file.path.replace(
                                    root,
                                    ""
                                ).replace(targetFile.file.name, "")
                            }", Toast.LENGTH_SHORT
                        ).show()
                        println(
                            "error: found no files in ${
                                root + targetFile.file.path.replace(
                                    root,
                                    ""
                                ).replace(targetFile.file.name, "")
                            }"
                        )
                        return@forEach
                    }

                    for (file in filesInPath) {
                        if (sourceFile.file.name == file.name && file.isDirectory) {
                            Toast.makeText(
                                applicationContext,
                                "error: path has folder with the same name as source",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("error: path has folder with the same name as source")
                            return@forEach
                        }
                    }

                    copyFile(sourceFile, "$targetFilePath/${sourceFile.file.name}")
                    if (canDeleteFile(sourceFile, targetFile, targetFilePath))
                        deleteFile(sourceFile)
                    Toast.makeText(applicationContext, "moved file", Toast.LENGTH_SHORT).show()
                } else if (targetFile.file.isDirectory) {
                    if (targetFile.children != null) {
                        val children = targetFile.file.listFiles()
                        if (children != null) {
                            for (file in children) {
                                if (sourceFile.file.name == file.name && file.isDirectory) {
                                    Toast.makeText(
                                        applicationContext,
                                        "error: folder with that name already exists",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    println("error: folder with that name already exists")
                                    return@forEach
                                }
                            }
                        }
                    }

                    copyFile(sourceFile, "${targetFile.file.path}/${sourceFile.file.name}")
                    if (canDeleteFile(sourceFile, targetFile, targetFile.file.path))
                        deleteFile(sourceFile)
                    Toast.makeText(applicationContext, "moved file", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "error: target file is not file or folder",
                        Toast.LENGTH_SHORT
                    ).show()
                    println("error: target file is not file or folder ${sourceFile.file.exists()} ${targetFile.file.exists()}")
                    return@forEach
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "error: source file is not file or folder",
                    Toast.LENGTH_SHORT
                ).show()
                println("error: source file is not file or folder ${sourceFile.file.exists()} ${targetFile.file.exists()}")
                return@forEach
            }
        }
    }

    private fun copyFile(file: CustomFile, path: String) {
        if (file.file.isFile) {
            try {
                file.file.copyTo(File(path))
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "error: file copy failed $e", Toast.LENGTH_SHORT)
                    .show()
                println("error: file copy failed $e")
            }
        } else if (file.file.isDirectory) {
            try {
                file.file.copyRecursively(File(path))
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "error: folder copy failed $e",
                    Toast.LENGTH_SHORT
                ).show()
                println("error: folder copy failed $e")
            }
        }
    }

    private fun canDeleteFile(
        sourceFile: CustomFile,
        targetFile: CustomFile,
        targetPath: String
    ): Boolean {
        if (sourceFile.file.isFile) {
            return sourceFile.file.exists() && File(targetPath + "/${sourceFile.file.name}").exists()
        } else if (sourceFile.file.isDirectory) {
            if (!sourceFile.file.exists() || !File(targetPath + "/${sourceFile.file.name}").exists()) {
                Toast.makeText(
                    applicationContext,
                    "error: source file and/or target file doesn't exist",
                    Toast.LENGTH_SHORT
                ).show()
                println("error: source file and/or target file doesn't exist")
                return false
            }

            if (sourceFile.children != null) {
                val targetChildren = getFiles(targetPath.replace(root, ""))
                for (sourceChild in sourceFile.children) {
                    var foundCopy = false
                    val relativePath = sourceChild.file.path.replace(sourceFile.file.path, "")

                    for (copiedChild in targetChildren) {
                        val relativePath2 = copiedChild.file.path.replace(
                            "${targetFile.file.path}/${sourceFile.file.name}",
                            ""
                        )

                        if (sourceChild.file.name == copiedChild.file.name && relativePath == relativePath2) {
                            foundCopy = true
                            break
                        }
                    }

                    if (!foundCopy) {
                        Toast.makeText(
                            applicationContext,
                            "error: didn't find corresponding child to: ${sourceChild.file.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        println("error: didn't find corresponding child to: ${sourceChild.file.name}")
                        return false
                    }
                }

                return true
            } else {
                return sourceFile.file.exists() && File(targetPath + "/${sourceFile.file.name}").exists()
            }
        } else {
            return false
        }
    }

    fun deleteFile(file: CustomFile) {
        try {
            if (file.file.isFile) {
                file.file.delete()
            } else if (file.file.isDirectory) {
                file.file.deleteRecursively()
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "error: delete file failure $e", Toast.LENGTH_SHORT)
                .show()
            println("error: delete file failure $e")
        }
    }

    fun deleteFiles(list: List<CustomFile>) {
        list.forEach { file ->
            try {
                if (file.file.isFile) {
                    file.file.delete()
                } else if (file.file.isDirectory) {
                    file.file.deleteRecursively()
                }
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

    fun getFiles(path: String = ""): MutableList<CustomFile> {
        val files = File(root, path).listFiles()
        val directoryLevel = path.count { it == '/' } + 1

        files?.sortWith { a, b ->
            a.name.uppercase().compareTo(b.name.uppercase())
        }

        files?.sortWith { a, b ->
            a.isFile.compareTo(b.isFile)
        }

        val result = mutableListOf<CustomFile>()
        files?.forEach { file ->
            var children: MutableList<CustomFile>? = null
            if (!file.isFile) {
                children = getFiles("$path/${file.name}")
            }
            result.add(CustomFile(file, children, directoryLevel, false))
            children?.forEach { result.add(it) }
        }

        return result
    }
}
package com.example.notes

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Directory(
    fileManager: FileManager,
    closeDir: () -> Unit,
) {
    // RELOAD DIR
    LaunchedEffect(fileManager.files.size == 0) {
        if (fileManager.files.size == 0) {
            fileManager.getFiles().forEach {
                if (it.file.exists())
                    fileManager.files.add(it)
            }
            fileManager.files.forEach { file ->
                val previousFile =
                    fileManager.previousFiles.find { prevFile -> prevFile.file.path == file.file.path }
                file.hidden = previousFile?.hidden ?: false
            }

            fileManager.previousFiles.clear()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .clickable {
                closeDir()
            }
    ) {
        val hiddenItems = remember { mutableListOf<String>() }

        val selectedItems = remember { mutableStateListOf<String>() }

        val interactionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .align(Alignment.BottomEnd) //TODO -> fix alignment issues
                .clip(RoundedCornerShape(10.dp))
                .background(Color.DarkGray)
                .clickable(interactionSource = interactionSource, indication = null) {}
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        event
                            .mimeTypes()
                            .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    },
                    target = object : DragAndDropTarget {
                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            val draggedFilePath =
                                event.toAndroidDragEvent().clipData?.getItemAt(0)?.text.toString()
                            fileManager.moveFile(
                                draggedFilePath, FileManager.CustomFile(
                                    file = File(
                                        fileManager.root,
                                        ""
                                    ),
                                    children = null,
                                    indent = 1,
                                    hidden = true,
                                )
                            )
                            selectedItems.clear()
                            fileManager.files.forEach { fileManager.previousFiles.add(it) }
                            fileManager.files.clear()
                            return true
                        }
                    }
                )
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(start = 5.dp),
                text = fileManager.rootFolderName,
                color = Color.White,
                fontFamily = Typography.titleLarge.fontFamily,
                fontSize = Typography.titleLarge.fontSize,
                fontWeight = Typography.titleLarge.fontWeight,
                lineHeight = Typography.titleLarge.lineHeight,
            )

            val autoSaveFile =
                fileManager.files.find { it.file.nameWithoutExtension == "tmpfileforautosave" }
            if (autoSaveFile != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 60.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable {
                            val file =
                                fileManager.files.find { it.file.nameWithoutExtension == fileManager.currentFile.title }
                            if (fileManager.currentFile.content != "") {
                                if (file == null || fileManager.readFile(file.file) != fileManager.currentFile.content) {
                                    fileManager.saveFile(
                                        fileManager.currentFile.title,
                                        "",
                                        fileManager.currentFile.content
                                    )
                                }
                            }
                            if (fileManager.currentFile.content == "" /*|| !showSaveFileDialog.value*/) { //TODO -> find out what this did
                                fileManager.currentFile.content =
                                    fileManager.readFile(autoSaveFile.file)
                                fileManager.currentFile.title = "tmpfileforautosave"
                                fileManager.currentFile.path = autoSaveFile.file.path
                                    .replace(fileManager.root, "")
                                    .replace(autoSaveFile.file.name, "")
                                //showDirMenu.value = false
                            }
                        },
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(start = 5.dp, top = 10.dp, bottom = 10.dp),
                        painter = painterResource(R.drawable.file),
                        contentDescription = null,
                        tint = Color.Black,
                    )

                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp, top = 10.dp, bottom = 10.dp),
                        text = "Auto save",
                        color = Color.White,
                        fontFamily = Typography.bodyLarge.fontFamily,
                        fontSize = Typography.bodyLarge.fontSize,
                        fontWeight = Typography.bodyLarge.fontWeight,
                        lineHeight = Typography.bodyLarge.lineHeight,
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(top = 120.dp, bottom = 60.dp)
            ) {
                items(fileManager.files) { file ->
                    if (!file.hidden && file.file.nameWithoutExtension != "tmpfileforautosave") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .dragAndDropSource {
                                    detectTapGestures(
                                        onTap = {
                                            if (selectedItems.size > 0) {
                                                if (selectedItems.contains(file.file.path)) selectedItems.remove(
                                                    file.file.path
                                                )
                                                else selectedItems.add(file.file.path)
                                            } else if (file.file.isFile) {
                                                if (fileManager.currentFile.content != "") {
                                                    val match =
                                                        fileManager.files.find { it.file.nameWithoutExtension == fileManager.currentFile.content }
                                                    if (match == null || fileManager.readFile(
                                                            match.file
                                                        ) != fileManager.currentFile.content
                                                    ) {
                                                        fileManager.saveFile(
                                                            fileManager.currentFile.title,
                                                            "",
                                                            fileManager.currentFile.content
                                                        )
                                                    }
                                                }
                                                if (fileManager.currentFile.content == "" /*|| !showSaveFileDialog.value*/) {
                                                    fileManager.currentFile.content =
                                                        fileManager.readFile(file.file)
                                                    fileManager.currentFile.content =
                                                        file.file.nameWithoutExtension
                                                    //showDirMenu.value = false
                                                    fileManager.currentFile.path = file.file.path
                                                        .replace(fileManager.root, "")
                                                        .replace(file.file.name, "")
                                                }
                                            } else if (file.file.isDirectory) {
                                                if (hiddenItems.find { it == file.file.path } != null) {
                                                    hiddenItems.remove(file.file.path)
                                                    file.file
                                                        .listFiles()
                                                        ?.forEach { i ->
                                                            val f =
                                                                fileManager.files.find { j -> i.path == j.file.path }
                                                            if (f != null)
                                                                f.hidden = false
                                                        }
                                                } else {
                                                    hiddenItems.add(file.file.path)
                                                    file.children?.forEach { it.hidden = true }
                                                }

                                                fileManager.files.forEach {
                                                    fileManager.previousFiles.add(
                                                        it
                                                    )
                                                }
                                                fileManager.files.clear()
                                            } else {
                                                fileManager.files.forEach {
                                                    fileManager.previousFiles.add(
                                                        it
                                                    )
                                                }
                                                fileManager.files.clear()
                                            }
                                        },
                                        onLongPress = {
                                            if (selectedItems.find { it == file.file.path } != null) {
                                                var result = ""
                                                selectedItems.forEachIndexed { index, path ->
                                                    result += if (index != selectedItems.size - 1) path + "_-middle-_" else path
                                                }

                                                startTransfer(
                                                    transferData = DragAndDropTransferData(
                                                        clipData = ClipData.newPlainText(
                                                            file.file.name,
                                                            result
                                                        )
                                                    )
                                                )
                                            } else {
                                                selectedItems.add(file.file.path)
                                            }
                                        }
                                    )
                                }
                                .dragAndDropTarget(
                                    shouldStartDragAndDrop = { event ->
                                        event
                                            .mimeTypes()
                                            .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                                    },
                                    target = object : DragAndDropTarget {
                                        override fun onDrop(event: DragAndDropEvent): Boolean {
                                            fileManager.moveFile(
                                                event.toAndroidDragEvent().clipData?.getItemAt(
                                                    0
                                                )?.text.toString(), file
                                            )
                                            selectedItems.clear()
                                            fileManager.files.forEach {
                                                fileManager.previousFiles.add(
                                                    it
                                                )
                                            }
                                            fileManager.files.clear()

                                            return true
                                        }
                                    }
                                )
                                .background(if (selectedItems.find { it == file.file.path } != null) Color.Gray else Color.Transparent)

                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(
                                        start = (5 * file.indent).dp,
                                        top = 10.dp,
                                        bottom = 10.dp
                                    ),
                                painter = painterResource(if (file.file.isFile) R.drawable.file else if (hiddenItems.find { it == file.file.path } == null) R.drawable.folder_open else R.drawable.folder),
                                contentDescription = null,
                                tint = Color.White,
                            )

                            Text(
                                modifier = Modifier
                                    .padding(start = 5.dp, top = 10.dp, bottom = 10.dp),
                                text = if (file.file.isFile) file.file.nameWithoutExtension else file.file.name,
                                color = Color.White,
                                fontFamily = Typography.bodyLarge.fontFamily,
                                fontSize = Typography.bodyLarge.fontSize,
                                fontWeight = Typography.bodyLarge.fontWeight,
                                lineHeight = Typography.bodyLarge.lineHeight,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                if (selectedItems.size != 0) {
                    Icon(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                selectedItems.forEach {
                                    val file =
                                        fileManager.files.find { file -> file.file.path == it }
                                    if (file != null) {
                                        val list = mutableListOf<FileManager.CustomFile>()

                                        selectedItems.forEach { path ->
                                            val f =
                                                fileManager.files.find { f -> f.file.path == path }
                                            if (f != null) {
                                                list.add(f)
                                            }
                                        }

                                        fileManager.deleteFiles(list)
                                        fileManager.files.forEach { fileManager.previousFiles.add(it) }
                                        fileManager.files.clear()
                                    }
                                }
                                selectedItems.clear()
                            },
                        painter = painterResource(R.drawable.bin),
                        contentDescription = null,
                        tint = Color.White,
                    )
                } else {
                    Icon(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                fileManager.currentFile.content = ""
                                fileManager.currentFile.title = ""
                                fileManager.currentFile.path = ""
                                closeDir()
                            },
                        painter = painterResource(R.drawable.plus),
                        contentDescription = null,
                        tint = Color.White,
                    )

                    Icon(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                fileManager.saveFolder(fileManager.currentFile.title)
                                closeDir()
                            },
                        painter = painterResource(R.drawable.folder),
                        contentDescription = null,
                        tint = Color.White,
                    )
                }

                Icon(
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {
                            closeDir()
                        },
                    painter = painterResource(R.drawable.burger_menu),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}
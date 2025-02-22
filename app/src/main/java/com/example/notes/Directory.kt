package com.example.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Directory(
    fileManager: FileManager,
    closeDir: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .clickable {
                closeDir()
            }
    ) {
        val selectedItems = remember { mutableStateListOf<String>() }
        val interactionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.DarkGray)
                .clickable(interactionSource = interactionSource, indication = null) {}
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

            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 5.dp, top = 60.dp, bottom = 10.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        fileManager.openFile("tmpfileforautosave")
                        closeDir()
                    },
                text = "Auto save",
                color = Color.White,
                fontFamily = Typography.bodyLarge.fontFamily,
                fontSize = Typography.bodyLarge.fontSize,
                fontWeight = Typography.bodyLarge.fontWeight,
                lineHeight = Typography.bodyLarge.lineHeight,
            )

            LazyColumn(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(top = 120.dp, bottom = 60.dp)
            ) {
                itemsIndexed(fileManager.files) { index, file ->
                    if (file.tag.isNotEmpty() && index != 0 && fileManager.files[index - 1].tag != file.tag) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 5.dp, top = 10.dp, bottom = 10.dp),
                                text = file.tag,
                                color = Color.White,
                                fontFamily = Typography.bodySmall.fontFamily,
                                fontSize = Typography.bodySmall.fontSize,
                                fontWeight = Typography.bodySmall.fontWeight,
                                lineHeight = Typography.bodySmall.lineHeight,
                            )

                            HorizontalDivider(
                                Modifier
                                    .padding(5.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .combinedClickable(
                                onClick = {
                                    if (selectedItems.size > 0) {
                                        if (selectedItems.contains(file.file.path))
                                            selectedItems.remove(file.file.path)
                                        else
                                            selectedItems.add(file.file.path)
                                    } else {
                                        fileManager.openFile(file.file.nameWithoutExtension)
                                        closeDir()
                                    }
                                },
                                onLongClick = {
                                    selectedItems.add(file.file.path)
                                },
                            )
                            .background(
                                if (selectedItems.find { it == file.file.path } != null)
                                    Color.Gray
                                else
                                    Color.Transparent
                            )

                    ) {
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
                                        val list = mutableListOf<CustomFile>()

                                        selectedItems.forEach { path ->
                                            val f =
                                                fileManager.files.find { f -> f.file.path == path }
                                            if (f != null)
                                                list.add(f)
                                        }

                                        fileManager.deleteFiles(list)
                                        fileManager.updateFiles()
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
                            .padding(5.dp)
                            .size(42.dp)
                            .clickable {
                                fileManager.resetCurrentFile()
                                closeDir()
                            },
                        painter = painterResource(R.drawable.plus),
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
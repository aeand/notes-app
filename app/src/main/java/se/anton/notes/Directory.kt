package se.anton.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Directory(fileManager: FileManager, closeDir: () -> Unit) {
    val selectedItems = remember { mutableStateListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 10.dp, top = 30.dp, end = 10.dp, bottom = 10.dp)
                .fillMaxSize()
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
                                .padding(start = 5.dp, top = 5.dp, end = 30.dp, bottom = 5.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 10.dp, end = 10.dp)
                        .combinedClickable(
                            onClick = {
                                if (selectedItems.isNotEmpty()) {
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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Button(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(50.dp)
                    .size(70.dp),
                onClick = {
                    if (selectedItems.isNotEmpty()) {
                        val list = selectedItems.mapNotNull {
                            fileManager.files.find { file -> file.file.path == it }
                        }
                        fileManager.deleteFiles(list)
                        fileManager.updateFiles()
                        selectedItems.clear()
                    }
                    else {
                        fileManager.resetCurrentFile()
                        closeDir()
                    }
                },
                shape = CircleShape,
                border = BorderStroke(2.dp, Color.White),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                if (selectedItems.isNotEmpty()) {
                    Icon(
                        modifier = Modifier
                            .size(50.dp),
                        painter = painterResource(R.drawable.bin),
                        contentDescription = null,
                        tint = Color.White,
                    )
                } else {
                    Icon(
                        modifier = Modifier
                            .padding(5.dp)
                            .size(42.dp),
                        painter = painterResource(R.drawable.plus),
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

package com.example.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Notes(
    fileManager: FileManager,
    openDir: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(
                handleColor = Color.Gray,
                backgroundColor = Color.DarkGray
            )
        ) {
            BasicTextField(
                modifier = Modifier
                    .padding(bottom = 90.dp)
                    .fillMaxSize(),
                value = fileManager.currentFile.content.value,
                onValueChange = { it: String -> fileManager.currentFile.content.value = it },
                singleLine = false,
                maxLines = Int.MAX_VALUE,
                visualTransformation = VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.None
                ),
                cursorBrush = Brush.verticalGradient(
                    0.00f to Color.White,
                    0.15f to Color.White,
                    0.15f to Color.White,
                    0.75f to Color.White,
                    0.75f to Color.White,
                    1.00f to Color.White,
                ),
                textStyle = TextStyle(
                    textAlign = TextAlign.Start,
                    fontFamily = Typography.bodyLarge.fontFamily,
                    fontSize = Typography.bodyLarge.fontSize,
                    lineHeight = Typography.bodyLarge.lineHeight,
                    letterSpacing = Typography.bodyLarge.letterSpacing,
                    color = Color.White,
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                    ) {
                        if (fileManager.currentFile.content.value.isEmpty()) {
                            Text(
                                modifier = Modifier,
                                text = "Write something",
                                textAlign = TextAlign.Left,
                                fontFamily = FontFamily(Font(R.font.roboto_italic)),
                                fontSize = Typography.bodyLarge.fontSize,
                                fontWeight = Typography.bodyLarge.fontWeight,
                                lineHeight = Typography.bodyLarge.lineHeight,
                                color = Color.Gray
                            )
                        } else {
                            innerTextField()
                        }
                    }
                },
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                HorizontalDivider(color = Color.DarkGray)

                val focusManager = LocalFocusManager.current

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    BasicTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .fillMaxHeight(),
                        value = fileManager.currentFile.title.value,
                        onValueChange = { it: String -> fileManager.currentFile.title.value = it },
                        singleLine = false,
                        maxLines = 1,
                        visualTransformation = VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        cursorBrush = Brush.verticalGradient(
                            0.00f to Color.White,
                            0.15f to Color.White,
                            0.15f to Color.White,
                            0.75f to Color.White,
                            0.75f to Color.White,
                            1.00f to Color.White,
                        ),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Start,
                            fontFamily = Typography.titleMedium.fontFamily,
                            fontSize = Typography.titleMedium.fontSize,
                            lineHeight = Typography.titleMedium.lineHeight,
                            letterSpacing = Typography.titleMedium.letterSpacing,
                            color = Color.White,
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 20.dp, end = 20.dp)
                            ) {
                                if (fileManager.currentFile.title.value.isEmpty()) {
                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart),
                                        text = "Title",
                                        textAlign = TextAlign.Left,
                                        fontFamily = FontFamily(Font(R.font.roboto_italic)),
                                        fontSize = Typography.titleMedium.fontSize,
                                        fontWeight = Typography.titleMedium.fontWeight,
                                        lineHeight = Typography.titleMedium.lineHeight,
                                        color = Color.Gray
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                    ) {
                                        innerTextField()
                                    }
                                }
                            }
                        },
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val showSave = remember { mutableStateOf(false) }

                        LaunchedEffect(
                            fileManager.currentFile.title.value,
                            fileManager.currentFile.content.value
                        ) {
                            if (fileManager.currentFile.content.value.isEmpty() && fileManager.currentFile.title.value.isEmpty())
                                showSave.value = false
                            else
                                showSave.value = fileManager.fileHasChanges()
                        }

                        if (showSave.value) {
                            Text(
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .clickable {
                                        fileManager.saveCurrentFile()
                                        showSave.value = false
                                    },
                                text = "Save",
                                color = Color.White,
                                fontFamily = Typography.bodyLarge.fontFamily,
                                fontSize = Typography.bodyLarge.fontSize,
                                fontWeight = Typography.bodyLarge.fontWeight,
                                lineHeight = Typography.bodyLarge.lineHeight,
                            )
                        }

                        Icon(
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    openDir()
                                },
                            painter = painterResource(R.drawable.burger_menu),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
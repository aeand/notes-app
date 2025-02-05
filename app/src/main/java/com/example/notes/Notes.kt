package com.example.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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
    val textFieldFocused = remember { mutableStateOf(false) }

    val title = remember { mutableStateOf(fileManager.currentFile.value.title) }
    val path = remember { mutableStateOf(fileManager.currentFile.value.path) }
    val content = remember { mutableStateOf(fileManager.currentFile.value.content) }

    // UPDATE FIELDS
    LaunchedEffect(
        fileManager.currentFile.value.title,
        fileManager.currentFile.value.path,
        fileManager.currentFile.value.content
    ) {
        title.value = fileManager.currentFile.value.title
        path.value = fileManager.currentFile.value.path
        content.value = fileManager.currentFile.value.content
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }

        val customTextSelectionColors = TextSelectionColors(
            handleColor = Color.Gray,
            backgroundColor = Color.DarkGray
        )

        CompositionLocalProvider(
            LocalTextSelectionColors provides customTextSelectionColors
        ) {
            val interactionSourceTitle = remember { MutableInteractionSource() }
            val interactionSourceContent = remember { MutableInteractionSource() }

            BasicTextField(
                modifier = Modifier
                    .padding(bottom = 90.dp)
                    .fillMaxSize()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            textFieldFocused.value = true
                        }
                    }
                    .background(Color.Black),
                value = content.value,
                onValueChange = { it: String ->
                    content.value = it
                },
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
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Unspecified
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        textFieldFocused.value = false
                    }
                ),
                singleLine = false,
                maxLines = Int.MAX_VALUE,
                visualTransformation = VisualTransformation.None,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                    ) {
                        if (content.value.isEmpty()) {
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
                onTextLayout = {},
                interactionSource = interactionSourceContent,
                minLines = 1,
            )

            BasicTextField(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp)
                    .fillMaxWidth()
                    .height(70.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            textFieldFocused.value = true
                        }
                    }
                    .background(Color.Black),
                value = title.value,
                onValueChange = { it: String ->
                    title.value = it
                },
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
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        textFieldFocused.value = false
                    }
                ),
                singleLine = false,
                maxLines = 1,
                visualTransformation = VisualTransformation.None,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp, end = 20.dp)
                    ) {
                        if (title.value.isEmpty()) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterStart),
                                text = "Title",
                                textAlign = TextAlign.Left,
                                fontFamily = FontFamily(roboto["italic"]!!),
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
                onTextLayout = {},
                interactionSource = interactionSourceTitle,
                minLines = 1,
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-60).dp, y = (-11).dp)
                .clickable {
                    focusManager.clearFocus()
                    textFieldFocused.value = false

                    if (title.value.isNotEmpty() && content.value.isNotEmpty()) {
                        println("${title.value}, ${content.value}")
                        fileManager.saveFile(title.value, path.value, content.value, true)
                    }
                },
            text = "Save",
            color = if (content.value.isEmpty()) Color.LightGray else Color.White,
            fontFamily = Typography.bodyLarge.fontFamily,
            fontSize = Typography.bodyLarge.fontSize,
            fontWeight = Typography.bodyLarge.fontWeight,
            lineHeight = Typography.bodyLarge.lineHeight,
        )

        Icon(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(50.dp)
                .clickable {
                    fileManager.currentFile.value = FileManager.FileContent(
                        title.value,
                        path.value,
                        content.value
                    )
                    openDir()
                },
            painter = painterResource(R.drawable.burger_menu),
            contentDescription = null,
            tint = Color.White
        )
    }
}
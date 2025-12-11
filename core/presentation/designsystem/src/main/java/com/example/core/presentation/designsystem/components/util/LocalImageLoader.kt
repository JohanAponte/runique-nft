package com.example.core.presentation.designsystem.components.util

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap

@Composable
fun LocalImageLoader(
    imageBytes: ByteArray?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = {},
    error: @Composable () -> Unit = {}
) {
    if (imageBytes != null) {
        val bitmap = remember(imageBytes) {
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = modifier
            )
        } else {
            error()
        }
    } else {
        placeholder()
    }
}
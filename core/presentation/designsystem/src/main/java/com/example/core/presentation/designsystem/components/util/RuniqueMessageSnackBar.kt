package com.example.core.presentation.designsystem.components.util

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.presentation.designsystem.RuniqueDarkRed
import com.example.core.presentation.designsystem.RuniqueWhite

@Composable
fun RuniqueMessageSnackBar(
    snackBarData: SnackbarData,
    modifier: Modifier = Modifier,
    isErrorMessage: Boolean = false
) {
    Snackbar(
        snackbarData = snackBarData,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        containerColor = if (isErrorMessage) RuniqueDarkRed else MaterialTheme.colorScheme.primary,
        contentColor = if (isErrorMessage) RuniqueWhite else MaterialTheme.colorScheme.onPrimary,
        dismissActionContentColor = RuniqueWhite
    )
}
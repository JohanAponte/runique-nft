package com.example.core.presentation.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import coil.compose.SubcomposeAsyncImage
import com.example.core.domain.location.Location
import com.example.core.domain.run.Run
import com.example.core.presentation.designsystem.CalendarIcon
import com.example.core.presentation.designsystem.LocationIcon
import com.example.core.presentation.designsystem.R
import com.example.core.presentation.designsystem.RunOutlinedIcon
import com.example.core.presentation.designsystem.RuniqueTheme
import com.example.core.presentation.ui.getLocationName
import com.example.core.presentation.ui.mapper.toRunUi
import com.example.core.presentation.ui.model.RunDataUi
import com.example.core.presentation.ui.model.RunUi
import java.time.ZonedDateTime
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RunCard(
    run: Run,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit = {},
) {
    val context = LocalContext.current

    val runUi = remember {
        run.toRunUi()
    }

    var showDeleteDropDown by remember {
        mutableStateOf(false)
    }

    var isExpanded by remember {
        mutableStateOf(false)
    }

    val expandIconRotate by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(500),
        label = ""
    )

    var locationName by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(key1 = true) {
        run.getLocationName(
            context = context,
        ) { name ->
            locationName = name
        }
    }

    Box {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                .pointerInput(UInt){
                    detectTapGestures(
                        onLongPress = {
                            showDeleteDropDown = true
                        },
                        onTap = {
                            isExpanded = !isExpanded
                        }
                    )
                }
                /*.combinedClickable(
                    onClick = {},
                    onLongClick = {
                        showDeleteDropDown = true
                    }
                )*/
                .padding(16.dp),
        ) {
            MapImage(imageUrl = runUi.mapPictureUrl)
            Spacer(modifier = Modifier.height(16.dp))

            RunningTimeSection(
                duration = runUi.duration,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RunningDateSection(dateTime = runUi.dateTime)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    isExpanded = !isExpanded
                }) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.rotate(expandIconRotate)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(300)
                ),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(300)
                )
            ) {
                Column {
                    DataGrid(
                        run = runUi,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            locationName?.let { location ->
                LocationNameSection(
                    locationName = location,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        DeleteRunDropdown(
            isVisible = showDeleteDropDown,
            onDismissRequest = { showDeleteDropDown = false },
            onDeleteClick = onDeleteClick
        )
    }
}

@Composable
fun DeleteRunDropdown(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val topOffset = with(LocalDensity.current) { 48.dp.toPx() }
    if (isVisible) {
        Popup(
            alignment = Alignment.BottomCenter,
            offset = IntOffset(0, topOffset.roundToInt()),
            onDismissRequest = onDismissRequest
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(top = 2.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onError)
                    .clickable {
                        onDeleteClick()
                        onDismissRequest()
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.delete),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun RunningDateSection(
    dateTime: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = CalendarIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = dateTime,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DataGrid(
    run: RunUi,
    modifier: Modifier = Modifier
) {
    val runDataList = listOf(
        RunDataUi(
            name = stringResource(id = R.string.distance),
            value = run.distance,
        ),
        RunDataUi(
            name = stringResource(id = R.string.pace),
            value = run.pace,
        ),
        RunDataUi(
            name = stringResource(id = R.string.avg_speed),
            value = run.avgSpeed,
        ),
        RunDataUi(
            name = stringResource(id = R.string.max_speed),
            value = run.maxSpeed,
        ),
        RunDataUi(
            name = stringResource(id = R.string.totel_elevation),
            value = run.totalElevation,
        )
    )
    var maxWidth by remember {
        mutableIntStateOf(0)
    }
    val maxWidthDp = with(LocalDensity.current) { maxWidth.toDp() }
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        runDataList.forEach { run ->
            DataGridCell(
                runData = run,
                modifier = Modifier
                    .defaultMinSize(minWidth = maxWidthDp)
                    .onSizeChanged {
                        maxWidth = max(maxWidth, it.width)
                    }
            )
        }
    }
}

@Composable
private fun MapImage(imageUrl: String?, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = stringResource(R.string.run_map),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .clip(RoundedCornerShape(15.dp)),
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.couldnt_load_image),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@Composable
private fun RunningTimeSection(
    duration: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = RunOutlinedIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.total_running_time),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = duration,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun DataGridCell(
    runData: RunDataUi,
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = runData.name,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = runData.value,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun LocationNameSection(
    locationName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = LocationIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = locationName,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }
}

@Preview
@Composable
private fun RunCardPreview() {
    RuniqueTheme {
        RunCard(
            run = Run(
                id = "123",
                duration = 10.minutes + 30.seconds,
                dateTimeUtc = ZonedDateTime.now(),
                distanceMeters = 5500,
                location = Location(0.0, 0.0),
                maxSpeedKmh = 15.0,
                totalElevationMeters = 123,
                mapPictureUrl = null,
            ),
            onDeleteClick = { /*TODO*/ }
        )
    }
}
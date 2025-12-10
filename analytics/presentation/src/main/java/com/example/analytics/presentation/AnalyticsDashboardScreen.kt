package com.example.analytics.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.analytics.domain.AnalyticsGraphData
import com.example.analytics.presentation.components.AnalyticsCard
import com.example.analytics.presentation.components.AnalyticsGraphCard
import com.example.core.domain.location.Location
import com.example.core.domain.run.Run
import com.example.core.presentation.designsystem.RuniqueTheme
import com.example.core.presentation.designsystem.components.GradientBackground
import com.example.core.presentation.designsystem.components.RunCard
import com.example.core.presentation.designsystem.components.RuniqueScaffold
import com.example.core.presentation.designsystem.components.RuniqueToolbar
import org.koin.androidx.compose.koinViewModel
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun AnalyticsDashboardScreenRoot(
    onBackClick: () -> Unit,
    viewModel: AnalyticsDashboardViewModel = koinViewModel()
) {
    AnalyticsDashboardScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                AnalyticsDashboardAction.OnBackClick -> onBackClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboardScreen(
    state: AnalyticsDashboardState?,
    onAction: (AnalyticsDashboardAction) -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )

    var selectedRun by remember {
        mutableStateOf<Run?>(null)
    }

    var isRunExpanded by remember {
        mutableStateOf(false)
    }

    var previousSelectedDay by remember {
        mutableStateOf(state?.selectedDay)
    }

    var dayChangeDifference by remember {
        mutableIntStateOf((state?.selectedDay ?: 0) - (previousSelectedDay ?: 0))
    }

    SideEffect {
        if (previousSelectedDay != state?.selectedDay) {
            dayChangeDifference = (state?.selectedDay ?: 0) - (previousSelectedDay ?: 0)
            previousSelectedDay = state?.selectedDay
        }
    }

    LaunchedEffect(key1 = state?.selectedDay) {
        state?.selectedDay?.let {
            selectedRun = state.graphData.runByDay[it]
        }
    }

    GradientBackground {
        RuniqueScaffold(
            topAppBar = {
                RuniqueToolbar(
                    title = stringResource(id = R.string.analytics),
                    scrollBehavior = scrollBehavior,
                    showBackButton = true,
                    onBackClick = { onAction(AnalyticsDashboardAction.OnBackClick) }
                )
            },
        ) { padding ->
            if (state == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        ) {
                            AnalyticsCard(
                                title = stringResource(id = R.string.total_distance),
                                value = state.totalDistance,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            AnalyticsCard(
                                title = stringResource(id = R.string.total_time_run),
                                value = state.totalDuration,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        ) {
                            AnalyticsCard(
                                title = stringResource(id = R.string.fastes_ever_run),
                                value = state.maxSpeed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        AnalyticsGraphCard(
                            graphData = state.graphData,
                            selectedDay = state.selectedDay,
                            onTypeSelect = { type ->
                                onAction(AnalyticsDashboardAction.OnGraphTypeSelect(type))
                            },
                            onDaySelect = { day ->
                                dayChangeDifference = (state.selectedDay ?: 0) - (day)
                                onAction(AnalyticsDashboardAction.OnDaySelect(day))
                            },
                            onMonthSelect = { month ->
                                onAction(AnalyticsDashboardAction.OnMonthSelect(month))
                            }
                        )
                    }
                    item {
                        AnimatedContent(
                            targetState = selectedRun,
                            transitionSpec = {
                                slideInHorizontally(
                                    initialOffsetX = {
                                        if (dayChangeDifference > 0) it else -it
                                    }
                                ).togetherWith(
                                    slideOutHorizontally(
                                        targetOffsetX = {
                                            if (dayChangeDifference > 0) -it else it
                                        }
                                    )
                                )
                            },
                            label = ""
                        ) { selectedRun ->
                            key(selectedRun?.id) {
                                selectedRun?.let {
                                    RunCard(
                                        run = it,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun AnalyticsDashboardScreenPreview() {
    RuniqueTheme {
        AnalyticsDashboardScreen(
            state = AnalyticsDashboardState(
                totalDistance = "10 km",
                totalDuration = "0d 1h 30m",
                maxSpeed = "15 km/h",
                graphData = AnalyticsGraphData(
                    runs = listOf(
                        Run(
                            id = "123",
                            duration = 10.minutes + 30.seconds,
                            dateTimeUtc = ZonedDateTime.now(),
                            distanceMeters = 5500,
                            location = Location(0.0, 0.0),
                            maxSpeedKmh = 15.0,
                            totalElevationMeters = 123,
                            mapPictureUrl = null
                        )
                    )
                ),
                selectedDay = ZonedDateTime.now().dayOfMonth
            ),
            onAction = {}
        )
    }
}
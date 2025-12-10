package com.example.analytics.presentation

import android.annotation.SuppressLint
import com.example.analytics.domain.AnalyticsData
import com.example.core.presentation.ui.toFormattedKm
import com.example.core.presentation.ui.toFormattedKmh
import kotlin.time.Duration
import kotlin.time.DurationUnit

@SuppressLint("DefaultLocale")
fun Duration.toFormattedTotalTime(): String {
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24
    val minutes = toLong(DurationUnit.MINUTES) % 60

    return "${days}d ${hours}h ${minutes}m"
}

fun AnalyticsData.toAnalyticsDashboardState(): AnalyticsDashboardState {
    return AnalyticsDashboardState(
        totalDistance = (totalDistanceRun / 1000.0).toFormattedKm(),
        totalDuration = totalTimeRun.toFormattedTotalTime(),
        maxSpeed = fastestEverRun.toFormattedKmh(),
        graphData = graphData
    )
}
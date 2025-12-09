package com.example.analytics.domain

import com.example.analytics.domain.util.toFormattedMonth
import com.example.core.domain.run.Run

enum class AnalyticsGraphType(val title: String) {
    DISTANCE("Distance"),
    SPEED("Avg. Speed"),
    AVG_PACE("Avg Pace")
}

data class AnalyticsGraphData(
    val runs: List<Run> = emptyList(),
    val distinctMonths: List<String> = runs.map { it.dateTimeUtc.toFormattedMonth() }.distinct(),
    val selectedMonth: String? = distinctMonths.firstOrNull(),
    val datatype: AnalyticsGraphType = AnalyticsGraphType.DISTANCE
) {
    val runsForSelectedMonth: List<Run>
        get() = runs.filter { it.dateTimeUtc.toFormattedMonth() == selectedMonth }

    private val maxValue: Number
        get() = when (datatype) {
            AnalyticsGraphType.DISTANCE -> runsForSelectedMonth.maxOf { it.distanceMeters }
            AnalyticsGraphType.SPEED -> runsForSelectedMonth.maxOf { it.avgSpeedKmh }
            AnalyticsGraphType.AVG_PACE -> runsForSelectedMonth.minOf { it.pace }
        }

    private val minValue: Number
        get() = when (datatype) {
            AnalyticsGraphType.DISTANCE -> runsForSelectedMonth.minOf { it.distanceMeters }
            AnalyticsGraphType.SPEED -> runsForSelectedMonth.minOf { it.avgSpeedKmh }
            AnalyticsGraphType.AVG_PACE -> runsForSelectedMonth.maxOf { it.pace }
        }

    val yAxisValues: List<Number>
        get() = listOf(
            minValue,
            maxValue
        ).distinct()

    private val firstDay: Int
        get() = runsForSelectedMonth.firstOrNull()?.dateTimeUtc?.dayOfMonth ?: 1

    private val lastDay: Int
        get() = runsForSelectedMonth.lastOrNull()?.dateTimeUtc?.dayOfMonth ?: 1

    val runByDay: Map<Int, Run?>
        get() = (firstDay..lastDay step 2).associateWith { day ->
            runsForSelectedMonth.find { it.dateTimeUtc.dayOfMonth == day }
        }
}
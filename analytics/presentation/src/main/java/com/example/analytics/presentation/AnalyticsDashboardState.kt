package com.example.analytics.presentation

import com.example.analytics.domain.AnalyticsGraphData

data class AnalyticsDashboardState(
    val totalDistance: String,
    val totalDuration: String,
    val maxSpeed: String,
    val graphData: AnalyticsGraphData = AnalyticsGraphData(),
    val selectedDay: Int? = null,
)

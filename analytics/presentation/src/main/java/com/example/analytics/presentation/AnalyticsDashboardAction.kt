package com.example.analytics.presentation

import com.example.analytics.domain.AnalyticsGraphType

sealed interface AnalyticsDashboardAction {
    data object OnBackClick : AnalyticsDashboardAction
    data class OnMonthSelect(val month: String) : AnalyticsDashboardAction
    data class OnDaySelect(val day: Int) : AnalyticsDashboardAction
    data class OnGraphTypeSelect(val type: AnalyticsGraphType): AnalyticsDashboardAction
}
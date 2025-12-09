package com.example.analytics.domain

interface AnalyticsRepository {
    suspend fun getAnalyticsData(): AnalyticsData
}
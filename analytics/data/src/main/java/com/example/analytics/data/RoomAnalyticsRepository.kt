package com.example.analytics.data

import com.example.analytics.domain.AnalyticsRepository
import com.example.analytics.domain.AnalyticsData
import com.example.analytics.domain.AnalyticsGraphData
import com.example.core.database.dao.AnalyticsDao
import com.example.core.domain.run.LocalRunDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class RoomAnalyticsRepository(
    private val analyticsDao: AnalyticsDao,
    private val localRunDataSource: LocalRunDataSource
): AnalyticsRepository {
    override suspend fun getAnalyticsData(): AnalyticsData {
        return withContext(Dispatchers.IO) {
            val totalDistance = async { analyticsDao.getTotalDistanceRun() }
            val totalTime = async { analyticsDao.getTotalTimeRun() }
            val maxRunSpeed = async { analyticsDao.getMaxRunSpeed() }
            val avgDistance = async { analyticsDao.getAvgDistancePerRun() }
            val avgPace = async { analyticsDao.getAvgPacePerRun() }
            val runs = async { localRunDataSource.getUnsortedRuns() }

            AnalyticsData(
                totalDistanceRun = totalDistance.await(),
                totalTimeRun = totalTime.await().milliseconds,
                fastestEverRun = maxRunSpeed.await(),
                graphData = AnalyticsGraphData(runs.await()),
            )
        }
    }
}
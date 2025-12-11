package com.example.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.core.database.dao.AnalyticsDao
import com.example.core.database.dao.RunDao
import com.example.core.database.dao.RunPendingSyncDao
import com.example.core.database.entities.DeletedRunSyncEntity
import com.example.core.database.entities.RunEntity
import com.example.core.database.entities.RunPendingSyncEntity

@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        DeletedRunSyncEntity::class
    ],
    version = 5
)

abstract class RunDataBase : RoomDatabase() {
    abstract val runDao: RunDao
    abstract val runPendingSyncDao: RunPendingSyncDao
    abstract val analyticsDao: AnalyticsDao
}
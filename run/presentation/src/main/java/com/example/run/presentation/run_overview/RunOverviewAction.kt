package com.example.run.presentation.run_overview

import com.example.core.domain.run.Run

sealed interface RunOverviewAction {
    data object OnStartClick : RunOverviewAction
    data object OnLogoutClick : RunOverviewAction
    data object OnAnalyticsClick : RunOverviewAction
    data class DeleteRun(val run: Run) : RunOverviewAction
}
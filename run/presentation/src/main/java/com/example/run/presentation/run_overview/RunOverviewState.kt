package com.example.run.presentation.run_overview

import com.example.core.domain.run.Run

data class RunOverviewState(
    val runs: List<Run> = emptyList()
)

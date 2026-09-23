package com.example.data.model

data class AntigravityStats(
    val latencySavedMs: Long = 0,
    val memoryFreedKb: Long = 0,
    val tokenEfficiencyPercent: Int = 18,
    val isZeroGActive: Boolean = true,
    val pipelineStatus: String = "Normal Orbit (Nominal)",
    val activeNodes: Int = 4,
    val totalOptimizedRequests: Int = 0
)

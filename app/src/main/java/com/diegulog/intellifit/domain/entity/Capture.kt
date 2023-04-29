package com.diegulog.intellifit.domain.entity

data class Capture(
    val id: String? = null,
    val persons: List<Person>,
    val videoPath: String,
    val moveType: MoveType,
    val timestamp: Long = System.currentTimeMillis()
)

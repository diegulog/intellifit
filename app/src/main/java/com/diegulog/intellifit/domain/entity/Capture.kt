package com.diegulog.intellifit.domain.entity

data class Capture(
    var id: Long = 0,
    val samples: List<Sample>,
    val videoPath: String,
    var moveType: MoveType,
    val timestamp: Long = System.currentTimeMillis()
)

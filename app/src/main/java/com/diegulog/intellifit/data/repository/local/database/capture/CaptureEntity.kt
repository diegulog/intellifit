package com.diegulog.intellifit.data.repository.local.database.capture

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.diegulog.intellifit.data.repository.local.database.DomainTranslatable
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.MoveType
@Entity(tableName = "capture")
data class CaptureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoPath: String = "",
    val moveType: MoveType,
    val timestamp: Long = 0
    ): DomainTranslatable<Capture>{
    @Ignore
    var samples: List<SampleEntity> = emptyList()
    override fun toDomain(): Capture {
        return Capture(
            id = id,
            samples = samples.map { it.toDomain() },
            videoPath = videoPath,
            moveType = moveType,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromDomain(capture: Capture): CaptureEntity {
            return CaptureEntity(
                id = capture.id,
                videoPath = capture.videoPath,
                moveType = capture.moveType,
                timestamp = capture.timestamp
            ).apply {
                samples = capture.samples.map { SampleEntity.fromDomain(it) }
            }
        }
    }
}
